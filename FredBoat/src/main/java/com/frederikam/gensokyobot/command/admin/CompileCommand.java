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

import com.frederikam.gensokyobot.commandmeta.abs.ICommandOwnerRestricted;
import com.frederikam.gensokyobot.util.log.SLF4JInputStreamErrorLogger;
import com.frederikam.gensokyobot.commandmeta.abs.Command;
import com.frederikam.gensokyobot.util.log.SLF4JInputStreamLogger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CompileCommand extends Command implements ICommandOwnerRestricted {

    private static final Logger log = LoggerFactory.getLogger(CompileCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            Runtime rt = Runtime.getRuntime();
            Message msg;

            msg = channel.sendMessage("* \u30a2\u30c3\u30d7\u30c7\u30fc\u30c8\u4e2d...*\n\nRunning `git clone`... ").complete(true);

            String branch = "master";
            if (args.length > 1) {
                branch = args[1];
            }
            String githubUser = "Cosgy-Dev";
            if (args.length > 2) {
                githubUser = args[2];
            }

            //Clear any old update folder if it is still present
            try {
                Process rm = rt.exec("rm -rf update");
                rm.waitFor(5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            Process gitClone = rt.exec("git clone https://github.com/" + githubUser + "/FredBoat.git --branch " + branch + " --recursive --single-branch update");
            new SLF4JInputStreamLogger(log, gitClone.getInputStream()).start();
            new SLF4JInputStreamErrorLogger(log, gitClone.getInputStream()).start();

            if (!gitClone.waitFor(120, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: timed out]\n\n").complete(true);
                throw new RuntimeException("Operation timed out: git clone");
            } else if (gitClone.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: returned code " + gitClone.exitValue() + "]\n\n").complete(true);
                throw new RuntimeException("Bad response code");
            }

            msg = msg.editMessage(msg.getRawContent() + "????????\n\nRunning `mvn package shade:shade`... ").complete(true);
            File updateDir = new File("update/FredBoat");

            Process mvnBuild = rt.exec("mvn -f " + updateDir.getAbsolutePath() + "/pom.xml package shade:shade");
            new SLF4JInputStreamLogger(log, mvnBuild.getInputStream()).start();
            new SLF4JInputStreamErrorLogger(log, mvnBuild.getInputStream()).start();

            if (!mvnBuild.waitFor(600, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: timed out]\n\n").complete(true);
                throw new RuntimeException("Operation timed out: mvn package shade:shade");
            } else if (mvnBuild.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: returned code " + mvnBuild.exitValue() + "]\n\n").complete(true);
                throw new RuntimeException("\u5fdc\u7b54\u30b3\u30fc\u30c9\u304c\u6b63\u3057\u304f\u3042\u308a\u307e\u305b\u3093");
            }

            msg.editMessage(msg.getRawContent() + "????????").queue();

            if(!new File("./update/FredBoat/target/FredBoat-1.0.jar").renameTo(new File(System.getProperty("user.home") + "/FredBoat-1.0.jar"))){
                throw new RuntimeException("Failed to move jar to home");
            }
        } catch (InterruptedException | IOException | RateLimitedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} [branch [repo]]\n#Update the bot by checking out the provided branch from the provided github repo and compiling it. Default github repo is Frederikam, default branch is master. Does not restart the bot.";
    }
}
