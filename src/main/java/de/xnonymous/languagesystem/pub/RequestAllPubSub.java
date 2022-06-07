package de.xnonymous.languagesystem.pub;

import de.xnonymous.languagesystem.Main;
import de.xnonymous.languagesystem.utils.ReplaceUtils;
import de.xnonymous.usefulapi.UsefulAPI;
import lombok.SneakyThrows;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class RequestAllPubSub {

    public RequestAllPubSub(UsefulAPI api) {
        new Thread(() -> api.redisSubscribe(new JedisPubSub() {
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                api.log("Listening to requests [all]...");
            }

            @Override
            public void onMessage(String channel, String message) {
                if (message.startsWith("requestall:"))
                    new Thread(() -> {
                        api.log("Receiving request [all] " + message);
                        String identify = message.split(":")[1];

                        try (Jedis resource = api.getJedisPool().getResource()) {
                            byte[] message1 = serializeLang(identify);
                            api.log("Sending " + Arrays.toString(message1) + " back...");
                            StringBuilder message2 = new StringBuilder();
                            for (byte b : message1) {
                                message2.append(b).append(":");
                            }
                            resource.publish("langall", message2.toString());
                        }
                    }).start();
                if (message.startsWith("requestlocale:"))
                    new Thread(() -> {
                        api.log("Receiving request [locale] " + message);
                        String identify = message.split(":")[1];

                        api.redisPublish("langall", identify + ":" + Main.lang.keySet().stream()
                                .map(Locale::toString)
                                .collect(Collectors.joining(":")));
                    }).start();
            }
        }, "langall")).start();
    }

    @SneakyThrows
    private byte[] serializeLang(String identify) {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            HashMap<Locale, HashMap<String, String>> lang = Main.lang;

            packer.packString(identify);
            packer.packMapHeader(lang.size());

            for (Locale locale : lang.keySet()) {
                HashMap<String, String> stringStringHashMap = lang.get(locale);

                packer.packString(locale.toString());
                packer.packMapHeader(stringStringHashMap.size());
                for (String s : stringStringHashMap.keySet()) {
                    packer.packString(s);
                    packer.packString(stringStringHashMap.get(s));
                }
            }
            return packer.toByteArray();
        }
    }

}
