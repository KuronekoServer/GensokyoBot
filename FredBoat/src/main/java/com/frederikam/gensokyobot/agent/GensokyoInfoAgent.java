package com.frederikam.gensokyobot.agent;

import com.frederikam.gensokyobot.FredBoat;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.entities.Game;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GensokyoInfoAgent extends Thread {

    private static final Logger log = LoggerFactory.getLogger(GensokyoInfoAgent.class);

    private static final int INTERVAL_MILLIS = 5000; // 5 secs
    private static String info = null;
    private static String lastSong = "";

    public GensokyoInfoAgent() {
        setDaemon(true);
        setName("GensokyoInfoAgent");
    }

    @Override
    public void run() {
        log.info("\u0047\u0065\u006E\u0073\u006F\u006B\u0079\u006F\u0049\u006E\u0066\u006F\u0041\u0067\u0065\u006E\u0074\u3092\u958B\u59CB\u3057\u307E\u3057\u305F");

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                fetch();
                sleep(INTERVAL_MILLIS);
            } catch (Exception e) {
                log.error("\u60C5\u5831\u3092\u53D6\u5F97\u4E2D\u306B\u4F8B\u5916\u304C\u767A\u751F\u3057\u307E\u3057\u305F\uFF01", e);
                try {
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("\u30A8\u30FC\u30B8\u30A7\u30F3\u30C8\u306E\u4F8B\u5916\u5F8C\u306B\u30B9\u30EA\u30FC\u30D7\u4E2D\u306B\u4E2D\u65AD\u3055\u308C\u307E\u3057\u305F", e);
                    break;
                }
            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private static String fetch() {
        try {
            info = Unirest.get("https://gensokyoradio.net/xml").asString().getBody();

            JSONObject data = XML.toJSONObject(GensokyoInfoAgent.getInfo()).getJSONObject("GENSOKYORADIODATA");

            String newSong = data.getJSONObject("SONGINFO").getString("TITLE");

            if (!newSong.equals(lastSong)) {
                List<FredBoat> shards = FredBoat.getShards();
                for(FredBoat shard : shards) {
                    shard.getJda().getPresence().setGame(Game.of(newSong));
                }

                log.info("\u518D\u751F\u4E2D " + newSong);
            }

            lastSong = data.getJSONObject("SONGINFO").getString("TITLE");

            return info;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getInfo() {
        return info == null ? fetch() : info;
    }

}
