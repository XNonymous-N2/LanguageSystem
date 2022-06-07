package de.xnonymous.languagesystem.pub;

import de.xnonymous.languagesystem.Main;
import de.xnonymous.languagesystem.utils.Lang;
import de.xnonymous.languagesystem.utils.ReplaceUtils;
import de.xnonymous.usefulapi.UsefulAPI;
import redis.clients.jedis.JedisPubSub;

import java.util.Locale;
import java.util.UUID;

public class RequestPubSub {

    public RequestPubSub(UsefulAPI api) {
        new Thread(() -> api.redisSubscribe(new JedisPubSub() {
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                api.log("Listening to language requests...");
            }

            @Override
            public void onMessage(String channel, String message) {
                String[] split = message.split(":");
                try {
                    UUID uuid = UUID.fromString(split[0]);
                    String text = ReplaceUtils.replaceColonBack(split[1]);
                    String identify = split[2];

                    api.log("Request for " + uuid + " with " + text + " and " + identify);
                    Lang.getLang(api.getMySQL(), uuid, locale -> {
                        if (locale == null)
                            locale = Locale.GERMANY;
                        String s = Main.lang.get(locale).get(text);

                        if (s == null)
                            s = "No lang found";

                        String finalS = ReplaceUtils.replaceColon(s);
                        new Thread(() -> api.redisPublish("langr", uuid + ":" + identify + ":" + finalS)).start();
                    });
                    return;
                } catch (Exception ignored) {

                }
                String[] s1 = split[0].split("_");
                Locale locale = new Locale(s1[0], s1[1]);
                String text = ReplaceUtils.replaceColonBack(split[1]);
                String identify = split[2];

                api.log("Request for " + locale + " with " + text + " and " + identify);
                String s = Main.lang.get(locale).get(text);

                if (s == null)
                    s = "No lang found";

                String finalS = ReplaceUtils.replaceColon(s);
                new Thread(() -> api.redisPublish("langr", locale + ":" + identify + ":" + finalS)).start();
            }
        }, "lang")).start();
    }
}
