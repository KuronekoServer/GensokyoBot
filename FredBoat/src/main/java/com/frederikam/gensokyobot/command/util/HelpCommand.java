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

package com.frederikam.gensokyobot.command.util;

import com.frederikam.gensokyobot.Config;
import com.frederikam.gensokyobot.commandmeta.abs.Command;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class HelpCommand extends Command  {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendMessage(getHelpMessage(guild.getJDA())).queue();
    }

    public static String getHelpMessage(JDA jda) {
        String out =  "```md\n" +
                "< \u97f3\u697d\u30b3\u30de\u30f3\u30c9 >\n" +
                "^join\n" +
                "#\u30dc\u30a4\u30b9\u30c1\u30e3\u30c3\u30c8\u306b\u53c2\u52a0\u3057\u3066\u30d7\u30ec\u30a4\u3092\u958b\u59cb\u3057\u307e\u3059\u3002\n" +
                "^leave\n" +
                "#\u97f3\u58f0\u30c1\u30e3\u30c3\u30c8\u3092\u7d42\u4e86\u3057\u3001\u97f3\u697d\u3092\u505c\u6b62\u3057\u307e\u3059\u3002\n" +
                "^np\n" +
                "#\u7d20\u6575\u306a\u57cb\u3081\u8fbc\u307f\u3067\u73fe\u5728\u518d\u751f\u4e2d\u306e\u66f2\u3092\u8868\u793a\u3057\u307e\u3059\n" +
                "^stats\n" +
                "#\u3053\u306e\u30dc\u30c3\u30c8\u306b\u95a2\u3059\u308b\u7d71\u8a08\u60c5\u5831\u3092\u8868\u793a\u3057\u307e\u3059\u3002\n" +
                "^help\n" +
                "#\u3053\u306e\u30d8\u30eb\u30d7\u30e1\u30c3\u30bb\u30fc\u30b8\u3092\u8868\u793a\u3057\u307e\u3059\u3002\n" +
                "\n\n" +
                "\u3053\u306e\u30dc\u30c3\u30c8\u3092\u62db\u5f85: https://discordapp.com/oauth2/authorize?&client_id=" + jda.getSelfUser().getId() + "&scope=bot\n" +
                "\u30bd\u30fc\u30b9\u30b3\u30fc\u30c9: https://github.com/KuronekoServer/GensokyoBot\n\n{0}" +
                "```";

        out = out.replaceAll("^", Config.CONFIG.getPrefix());

        if(Config.CONFIG.getStreamUrl().equals(Config.GENSOKYO_RADIO_STREAM_URL)) {
            out = out.replaceFirst("\\{0}", "\u30b3\u30f3\u30c6\u30f3\u30c4\u306fgensokyoradio.net\u306b\u3088\u3063\u3066\u63d0\u4f9b\u3055\u308c\u3066\u3044\u307e\u3059\u3002\n GR\u30ed\u30b4\u306fGensokyo Radio\u306e\u5546\u6a19\u3067\u3059\u3002\n \u8457\u4f5c\u6a29:\n Copyright (C) LunarSpotlight.\nGensokyo Bot JP Copyright (C) Cosgy Dev\n");
        } else {
            out = out.replaceFirst("\\{0}", "");
        }

        return out;
    }

    @Override
    public String help(Guild guild) {
        return null;
    }
}
