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

package com.frederikam.gensokyobot.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class ArgumentUtil {

    private ArgumentUtil() {}

    public static List<Member> fuzzyMemberSearch(Guild guild, String term) {
        ArrayList<Member> list = new ArrayList<>();

        term = term.toLowerCase();

        for(Member mem : guild.getMembers()) {
            if((mem.getUser().getName().toLowerCase() + "#" + mem.getUser().getDiscriminator()).contains(term)
                    | (mem.getEffectiveName().toLowerCase().contains(term))
                    | term.contains(mem.getUser().getId())) {
                list.add(mem);
            }
        }

        return list;
    }

    public static Member checkSingleFuzzySearchResult(TextChannel tc, String term) {
        List<Member> list = fuzzyMemberSearch(tc.getGuild(), term);

        switch (list.size()) {
            case 0:
                tc.sendMessage("`" + term + "`\u306e\u30e1\u30f3\u30d0\u30fc\u306f\u898b\u3064\u304b\u308a\u307e\u305b\u3093\u3067\u3057\u305f\u3002").queue();
                return null;
            case 1:
                return list.get(0);
            default:
                String msg = "\u8907\u6570\u306e\u30e6\u30fc\u30b6\u30fc\u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f\u3002\u3053\u308c\u3089\u306e\u30e6\u30fc\u30b6\u30fc\u306e\u3044\u305a\u308c\u304b\u3092\u610f\u5473\u3057\u307e\u3057\u305f\u304b\uff1f\n```";

                for (int i = 0; i < 5; i++){
                    if(list.size() == i) break;
                    msg = msg + "\n" + list.get(i).getUser().getName() + "#" + list.get(i).getUser().getDiscriminator();
                }

                msg = list.size() > 5 ? msg + "\n[...]" : msg;
                msg = msg + "```";

                tc.sendMessage(msg).queue();
                return null;
        }
    }
}
