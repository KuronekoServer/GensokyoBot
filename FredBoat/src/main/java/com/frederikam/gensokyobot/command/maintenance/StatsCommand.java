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

package com.frederikam.gensokyobot.command.maintenance;

import com.frederikam.gensokyobot.Config;
import com.frederikam.gensokyobot.FredBoat;
import com.frederikam.gensokyobot.audio.PlayerRegistry;
import com.frederikam.gensokyobot.commandmeta.CommandManager;
import com.frederikam.gensokyobot.commandmeta.abs.Command;
import com.frederikam.gensokyobot.commandmeta.abs.IMaintenanceCommand;
import com.frederikam.gensokyobot.feature.I18n;
import com.frederikam.gensokyobot.util.TextUtils;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

public class StatsCommand extends Command implements IMaintenanceCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        long totalSecs = (System.currentTimeMillis() - FredBoat.START_TIME) / 1000;
        int days = (int) (totalSecs / (60 * 60 * 24));
        int hours = (int) ((totalSecs / (60 * 60)) % 24);
        int mins = (int) ((totalSecs / 60) % 60);
        int secs = (int) (totalSecs % 60);
        
        String str = MessageFormat.format(
                I18n.get(guild).getString("statsParagraph"),
                days, hours, mins, secs, CommandManager.commandsExecuted - 1)
                + "\n";

        str = MessageFormat.format(I18n.get(guild).getString("statsRate"), str, (float) (CommandManager.commandsExecuted - 1) / ((float) totalSecs / (float) (60 * 60)));

        str = str + "\n\n```";

        str = str + "\u4E88\u7D04\u30E1\u30E2\u30EA:                " + Runtime.getRuntime().totalMemory() / 1000000 + "MB\n";
        str = str + "-> \u3069\u308C\u304C\u4F7F\u7528\u3055\u308C\u3066\u3044\u308B\u304B:            " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000 + "MB\n";
        str = str + "-> \u3046\u3061\u306F\u30D5\u30EA\u30FC\u3067\u3059:            " + Runtime.getRuntime().freeMemory() / 1000000 + "MB\n";
        str = str + "\u6700\u5927\u4E88\u7D04\u53EF\u80FD\uFF1A                " + Runtime.getRuntime().maxMemory() / 1000000 + "MB\n";

        str = str + "\n----------\n\n";

        str = str + "\u30b7\u30e3\u30fc\u30c7\u30a3\u30f3\u30b0:                       " + FredBoat.getInstance(guild.getJDA()).getShardInfo().getShardString() + "\n";
        str = str + "\u518d\u751f\u4e2d\u306e\u30d7\u30ec\u30a4\u30e4\u30fc:                " + PlayerRegistry.getPlayingPlayers().size() + "\n";

        str = str + "\u65e2\u77e5\u306e\u30b5\u30fc\u30d0\u30fc:                  " + FredBoat.getAllGuilds().size() + "\n";
        str = str + "\u30b5\u30fc\u30d0\u30fc\u306e\u65e2\u77e5\u306e\u30e6\u30fc\u30b6\u30fc:         " + FredBoat.getAllUsersAsMap().size() + "\n";
        str = str + "\u914d\u5e03:                  " + Config.CONFIG.getDistribution() + "\n";
        str = str + "JDA\u306e\u56de\u7b54\u7dcf\u6570:            " + guild.getJDA().getResponseTotal() + "\n";
        str = str + "JDA \u30d0\u30fc\u30b8\u30e7\u30f3:                    " + JDAInfo.VERSION;

        str = str + "```";

        channel.sendMessage(TextUtils.prefaceWithName(invoker, str)).queue();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
