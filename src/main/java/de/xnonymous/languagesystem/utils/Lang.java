package de.xnonymous.languagesystem.utils;

import de.xnonymous.api.mysql.MySQL;
import de.xnonymous.api.mysql.utils.Row;
import de.xnonymous.languagesystem.Main;
import de.xnonymous.usefulapi.UsefulAPI;
import lombok.SneakyThrows;
import net.bytebuddy.asm.Advice;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class Lang {

    private static void publishRedis(UsefulAPI api, String channel, String data) {
        try (Jedis resource = api.getJedisPool().getResource()) {
            api.log("Redis publishing on channel " + channel + " data: " + data);
            resource.publish(channel, data);
        }
    }

    private static void subscribeRedis(UsefulAPI api, JedisPubSub sub, String... channels) {
        try (Jedis resource = api.getJedisPool().getResource()) {
            api.log("Redis subscribing on " + Arrays.toString(channels));
            resource.subscribe(sub, channels);
        }
    }

    public static String requestData(UsefulAPI usefulAPI, UUID uuid, String key) {
        String text = ReplaceUtils.replaceColon(key);
        int identify = new Random().nextInt(50000);
        usefulAPI.log("Requestion language data with identify: " + identify);
        final String[] string = {"langerror"};
        subscribeRedis(usefulAPI, new JedisPubSub() {
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                publishRedis(usefulAPI, "lang", uuid.toString() + ":" + text + ":" + identify);
                usefulAPI.log("Waiting for language data...");
            }

            @Override
            public void onMessage(String channel, String message) {
                usefulAPI.log("Receiving " + message);
                String[] split = message.split(":");
                if (!split[0].equals(uuid.toString())) {
                    usefulAPI.log("but its wrong... " + uuid);
                    return;
                }
                if (!split[1].equals(String.valueOf(identify))) {
                    usefulAPI.log("but its wrong... " + identify);
                    return;
                }

                usefulAPI.log("Correct language! Unsubscribing...");
                string[0] = ReplaceUtils.replaceColonBack(split[2]);
                unsubscribe();
            }
        }, "langr");
        return string[0];
    }

    public static String requestData(UsefulAPI usefulAPI, Locale lang, String key) {
        String text = ReplaceUtils.replaceColon(key);
        int identify = new Random().nextInt(50000);
        usefulAPI.log("Requestion language data with identify: " + identify);
        final String[] string = {"langerror"};
        subscribeRedis(usefulAPI, new JedisPubSub() {
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                publishRedis(usefulAPI, "lang", lang + ":" + text + ":" + identify);
                usefulAPI.log("Waiting for language data...");
            }

            @Override
            public void onMessage(String channel, String message) {
                usefulAPI.log("Receiving " + message);
                String[] split = message.split(":");
                if (!split[0].equals(lang.toString())) {
                    usefulAPI.log("but its wrong... " + lang);
                    return;
                }
                if (!split[1].equals(String.valueOf(identify))) {
                    usefulAPI.log("but its wrong... " + identify);
                    return;
                }

                usefulAPI.log("Correct language! Unsubscribing...");
                string[0] = ReplaceUtils.replaceColonBack(split[2]);
                unsubscribe();
            }
        }, "langr");
        return string[0];
    }

    public static void getLang(MySQL mySQL, UUID uuid, Consumer<Locale> langConsumer) {
        mySQL.getData("lang", null, "`uuid`='" + uuid.toString() + "'", data -> {
            ArrayList<Row> objects = data.getObjects();

            if (objects == null || objects.isEmpty()) {
                langConsumer.accept(null);
                return;
            }

            Object locale = objects.get(0).get("locale");
            String[] language = String.valueOf(locale).split("_");

            langConsumer.accept(new Locale(language[0], language[1]));
        });
    }

    public static ArrayList<Locale> requestLocales(UsefulAPI usefulAPI) {
        int identify = new Random().nextInt(50000);
        usefulAPI.log("Requestion all language data with identify: " + identify);
        ArrayList<Locale> locales = new ArrayList<>();
        subscribeRedis(usefulAPI, new JedisPubSub() {
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                publishRedis(usefulAPI, "langall", "requestlocale:" + identify);
                usefulAPI.log("Requestion locales with identify: " + identify);
            }

            @Override
            public void onMessage(String channel, String message) {
                usefulAPI.log("Receiving locales " + message);

                String[] split = message.split(":");

                if (!split[0].equals(String.valueOf(identify))) {
                    usefulAPI.log("Wrong locales data... Waiting again...");
                    return;
                }

                for (int i = 1; i < split.length; i++) {
                    String[] s = split[i].split("_");
                    locales.add(new Locale(s[0], s[1]));
                }

                usefulAPI.log("Correct locales! Unsubscribing...");
                unsubscribe();
            }
        }, "langall");
        return locales;
    }

    public static HashMap<Locale, HashMap<String, String>> requestAllLangData(UsefulAPI usefulAPI) {
        int identify = new Random().nextInt(50000);
        usefulAPI.log("Requestion all language data with identify: " + identify);
        final HashMap[] hash = new HashMap[]{new HashMap<>()};
        subscribeRedis(usefulAPI, new JedisPubSub() {
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                publishRedis(usefulAPI, "langall", "requestall:" + identify);
                usefulAPI.log("Waiting for all language data...");
            }

            @Override
            public void onMessage(String channel, String message) {
                usefulAPI.log("Receiving all " + message);

                if (message.startsWith("request"))
                    return;

                String[] split = message.split(":");
                byte[] bytes = new byte[split.length];
                for (int i = 0; i < split.length; i++) {
                    bytes[i] = Byte.parseByte(split[i]);
                }

                hash[0] = deserializeLang(bytes, String.valueOf(identify));

                if (hash[0] == null) {
                    usefulAPI.log("Wrong language data... Waiting again...");
                    return;
                }

                usefulAPI.log("Correct language data! Unsubscribing...");
                unsubscribe();
            }
        }, "langall");
        return hash[0];
    }

    @SneakyThrows
    private static HashMap<Locale, HashMap<String, String>> deserializeLang(byte[] message, String identify) {
        try (MessageUnpacker messageUnpacker = MessagePack.newDefaultUnpacker(message)) {
            HashMap<Locale, HashMap<String, String>> map = new HashMap<>();

            String string = messageUnpacker.unpackString();
            if (!string.equals(identify))
                return null;

            int i2 = messageUnpacker.unpackMapHeader();
            for (int i = 0; i < i2; i++) {
                String[] string1 = messageUnpacker.unpackString().split("_");
                Locale locale = new Locale(string1[0], string1[1]);

                int i3 = messageUnpacker.unpackMapHeader();
                for (int i1 = 0; i1 < i3; i1++) {
                    String key = messageUnpacker.unpackString();
                    String value = messageUnpacker.unpackString();

                    Main.put(map, locale, key, value);
                }
            }

            return map;
        }
    }

}
