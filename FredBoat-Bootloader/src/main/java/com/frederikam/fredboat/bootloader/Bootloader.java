/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.frederikam.fredboat.bootloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Bootloader {

    private static JSONArray command;
    private static String jarName;
    private static int recentBoots = 0;
    private static long lastBoot = 0L;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        OUTER:
        while (true) {
            InputStream is = new FileInputStream(new File("./bootloader.json"));
            Scanner scanner = new Scanner(is);
            JSONObject json = new JSONObject(scanner.useDelimiter("\\A").next());
            scanner.close();
        
            command = json.getJSONArray("command");
            jarName = json.getString("jarName");

            Process process = boot();
            process.waitFor();
            System.out.println("[BOOTLOADER] Bot exited with code " + process.exitValue());
            
            switch (process.exitValue()) {
                case ExitCodes.EXIT_CODE_UPDATE:
                    System.out.println("[BOOTLOADER] \u4eca\u66f4\u65b0\u4e2d...");
                    update();
                    break;
                case 130:
                case ExitCodes.EXIT_CODE_NORMAL:
                    System.out.println("[BOOTLOADER] \u4eca\u30b7\u30e3\u30c3\u30c8\u30c0\u30a6\u30f3...");
                    break OUTER;
                    //SIGINT received or clean exit
                default:
                    System.out.println("[BOOTLOADER] \u4eca\u518d\u8d77\u52d5\u3057\u307e\u3059..");
                    break;
            }
        }
    }

    private static Process boot() throws IOException {
        //Check that we are not booting too quick (we could be stuck in a login loop)
        if(System.currentTimeMillis() - lastBoot > 3000 * 1000){
            recentBoots = 0;
        }
        
        recentBoots++;
        lastBoot = System.currentTimeMillis();
        
        if(recentBoots >= 4){
            System.out.println("[BOOTLOADER] \u304a\u305d\u3089\u304f\u30ed\u30b0\u30a4\u30f3\u30a8\u30e9\u30fc\u306e\u305f\u3081\u306b3\u56de\u518d\u8d77\u52d5\u306b\u5931\u6557\u3057\u307e\u3057\u305f\u3002\u7d42\u4e86...");
            System.exit(-1);
        }
        
        //ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + "/bin/java -jar "+new File("FredBoat-1.0.jar").getAbsolutePath())
        ProcessBuilder pb = new ProcessBuilder()
                .inheritIO();
        ArrayList<String> list = new ArrayList<>();
        command.forEach((Object str) -> {
            list.add((String) str);
        });
        
        pb.command(list);
        
        Process process = pb.start();
        return process;
    }

    private static void update() {
        //The main program has already prepared the shaded jar. We just need to replace the jars.
        File oldJar = new File("./" + jarName);
        oldJar.delete();
        File newJar = new File("./update/target/" + jarName);
        newJar.renameTo(oldJar);

        //Now clean up the workspace
        boolean deleted = new File("./update").delete();
        System.out.println("[BOOTLOADER] \u66f4\u65b0\u3057\u307e\u3057\u305f\u3002\u524a\u9664\u3055\u308c\u305f\u66f4\u65b0\u30c7\u30a3\u30ec\u30af\u30c8\u30ea\uff1a " + deleted);
    }

}
