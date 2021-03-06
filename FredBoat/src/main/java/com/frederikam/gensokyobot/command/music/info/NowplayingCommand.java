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

package com.frederikam.gensokyobot.command.music.info;

import com.frederikam.gensokyobot.Config;
import com.frederikam.gensokyobot.agent.GensokyoInfoAgent;
import com.frederikam.gensokyobot.commandmeta.abs.Command;
import com.frederikam.gensokyobot.commandmeta.abs.IMusicCommand;
import com.frederikam.gensokyobot.feature.I18n;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONObject;
import org.json.XML;

import java.awt.*;
import java.text.MessageFormat;

public class NowplayingCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {

        if(!Config.CONFIG.getStreamUrl().equals(Config.GENSOKYO_RADIO_STREAM_URL)) {
            channel.sendMessage("\u3053\u306e\u30b9\u30c8\u30ea\u30fc\u30e0\u306b\u306f\u60c5\u5831\u304c\u3042\u308a\u307e\u305b\u3093").queue();
            return;
        }

        sendGensokyoRadioEmbed(channel);
    }

    static void sendGensokyoRadioEmbed(TextChannel channel) {
        JSONObject data = XML.toJSONObject(GensokyoInfoAgent.getInfo()).getJSONObject("GENSOKYORADIODATA");

        String rating = data.getJSONObject("MISC").getInt("TIMESRATED") == 0 ?
                I18n.get(channel.getGuild()).getString("noneYet") :
                MessageFormat.format(I18n.get(channel.getGuild()).getString("npRatingRange"), data.getJSONObject("MISC").getInt("RATING"), data.getJSONObject("MISC").getInt("TIMESRATED"));

        String albumArt = data.getJSONObject("MISC").getString("ALBUMART").equals("") ?
                "https://cdn.discordapp.com/attachments/240116420946427905/373019550725177344/gr-logo-placeholder.png" :
                "https://gensokyoradio.net/images/albums/original/" + data.getJSONObject("MISC").getString("ALBUMART");

        String titleUrl = data.getJSONObject("MISC").getString("CIRCLELINK").equals("") ?
                "https://gensokyoradio.net/" :
                data.getJSONObject("MISC").getString("CIRCLELINK");

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(data.getJSONObject("SONGINFO").getString("TITLE"), titleUrl)
                .addField(I18n.get(channel.getGuild()).getString("album"), data.getJSONObject("SONGINFO").getString("ALBUM"), true)
                .addField(I18n.get(channel.getGuild()).getString("artist"), data.getJSONObject("SONGINFO").getString("ARTIST"), true)
                .addField(I18n.get(channel.getGuild()).getString("circle"), data.getJSONObject("SONGINFO").getString("CIRCLE"), true);

        if (data.getJSONObject("SONGINFO").optInt("YEAR") != 0) {
            eb.addField(I18n.get(channel.getGuild()).getString("year"), Integer.toString(data.getJSONObject("SONGINFO").getInt("YEAR")), true);
        }

        eb.addField(I18n.get(channel.getGuild()).getString("rating"), rating, true)
                .addField(I18n.get(channel.getGuild()).getString("listeners"), Integer.toString(data.getJSONObject("SERVERINFO").getInt("LISTENERS")), true)
                .setImage(albumArt)
                .setColor(new Color(66, 16, 80))
                .setFooter("\u30B3\u30F3\u30C6\u30F3\u30C4\u306F\u0067\u0065\u006E\u0073\u006F\u006B\u0079\u006F\u0072\u0061\u0064\u0069\u006F\u002E\u006E\u0065\u0074\u306B\u3088\u3063\u3066\u63D0\u4F9B\u3055\u308C\u3066\u3044\u307E\u3059\u3002\n" +
                        "\u0047\u0052\u30ED\u30B4\u306F\u0047\u0065\u006E\u0073\u006F\u006B\u0079\u006F\u0020\u0052\u0061\u0064\u0069\u006F\u306E\u5546\u6A19\u3067\u3059\u3002" +
                        "\nGensokyo Radio is ?? LunarSpotlight.", null)
                .build();

        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpNowplayingCommand");
    }
}
