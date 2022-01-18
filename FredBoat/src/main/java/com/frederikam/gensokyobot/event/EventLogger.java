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

package com.frederikam.gensokyobot.event;

import com.frederikam.gensokyobot.FredBoat;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLogger extends ListenerAdapter {

    public static final Logger log = LoggerFactory.getLogger(EventLogger.class);

    private final String logChannelId;
    public JDA jda;

    public EventLogger(String logChannelId) {
        this.logChannelId = logChannelId;
        Runtime.getRuntime().addShutdownHook(new Thread(ON_SHUTDOWN));
    }

    private void send(Message msg) {
        send(msg.getRawContent());
    }

    private void send(String msg) {
        /*DiscordUtil.sendShardlessMessage(jda, logChannelId,
                FredBoat.getInstance(jda).getShardInfo().getShardString()
                + " "
                + msg
        );*/
        log.info(msg);
    }

    @Override
    public void onReady(ReadyEvent event) {
        jda = event.getJDA();
        send(new MessageBuilder()
                .append("[:rocket:] \u53d7\u4fe1\u6e08\u307f\u306e\u6e96\u5099\u30a4\u30d9\u30f3\u30c8\u3002")
                .build()
        );
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        send(
                "[:white_check_mark:] \u5165\u4f1a\u3057\u305f\u30ae\u30eb\u30c9\u306f\u3001 `" + event.getGuild() + "`\u3067\u3059\u3002 \u30e6\u30fc\u30b6\u30fc: `" + event.getGuild().getMembers().size() + "`"
        );
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        send(
                "[:x:] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`."
        );
    }

    private final Runnable ON_SHUTDOWN = () -> {
        Runtime rt = Runtime.getRuntime();
        if(FredBoat.shutdownCode != FredBoat.UNKNOWN_SHUTDOWN_CODE){
            send("[:door:] \u7d42\u4e86\u30b3\u30fc\u30c9: " + FredBoat.shutdownCode + "");
        } else {
            send("[:door:] \u4e0d\u660e\u306a\u30b3\u30fc\u30c9\u3067\u7d42\u4e86\u3059\u308b\u3002");
        }
    };

}
