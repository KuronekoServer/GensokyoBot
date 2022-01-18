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

package com.frederikam.gensokyobot.command.admin;

import com.frederikam.gensokyobot.FredBoat;
import com.frederikam.gensokyobot.commandmeta.abs.Command;
import com.frederikam.gensokyobot.commandmeta.abs.ICommandOwnerRestricted;
import com.frederikam.gensokyobot.util.ExitCodes;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class UpdateCommand extends Command implements ICommandOwnerRestricted {

    private static final Logger log = LoggerFactory.getLogger(UpdateCommand.class);
    private static final CompileCommand COMPILE_COMMAND = new CompileCommand();
    private static final long MAX_JAR_AGE = 10 * 60 * 1000;

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            File homeJar = new File(System.getProperty("user.home") + "/FredBoat-1.0.jar");

            //Must exist and not be too old
            if(homeJar.exists()
                    && (System.currentTimeMillis() - homeJar.lastModified()) < MAX_JAR_AGE){
                update(channel);
                return;
            } else {
                log.info("");
            }

            COMPILE_COMMAND.onInvoke(guild, channel, invoker, message, args);

            update(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(TextChannel channel) throws IOException {
        File homeJar = new File(System.getProperty("user.home") + "/FredBoat-1.0.jar");
        File targetJar = new File("./update/target/FredBoat-1.0.jar");

        targetJar.getParentFile().mkdirs();
        targetJar.delete();
        FileUtils.copyFile(homeJar, targetJar);

        //Shutdown for update
        channel.sendMessage("\u518d\u8d77\u52d5\u3057\u3066\u3044\u307e\u3059\u3002").queue();
        FredBoat.shutdown(ExitCodes.EXIT_CODE_UPDATE);
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} [branch [repo]]\n#\u63d0\u4f9b\u3055\u308c\u305fgithub\u30ea\u30dd\u30b8\u30c8\u30ea\u304b\u3089\u63d0\u4f9b\u3055\u308c\u305f\u30d6\u30e9\u30f3\u30c1\u3092\u30c1\u30a7\u30c3\u30af\u30a2\u30a6\u30c8\u3057\u3001\u30b3\u30f3\u30d1\u30a4\u30eb\u3059\u308b\u3053\u3068\u306b\u3088\u3063\u3066\u30dc\u30c3\u30c8\u3092\u66f4\u65b0\u3057\u307e\u3059\u3002\u30c7\u30d5\u30a9\u30eb\u30c8\u306egithub repo\u306fCosgy Dev\u3067\u3001\u30c7\u30d5\u30a9\u30eb\u30c8\u306e\u30d6\u30e9\u30f3\u30c1\u306fmaster\u3067\u3059\u3002\u65b0\u3057\u3044\u30d3\u30eb\u30c9\u3067\u518d\u8d77\u52d5\u3057\u307e\u3059\u3002";
    }
}
