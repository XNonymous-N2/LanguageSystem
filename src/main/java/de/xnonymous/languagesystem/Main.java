package de.xnonymous.languagesystem;

import de.xnonymous.languagesystem.pub.RequestAllPubSub;
import de.xnonymous.languagesystem.pub.RequestPubSub;
import de.xnonymous.usefulapi.UsefulAPI;
import io.github.cdimascio.dotenv.Dotenv;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class Main {

    public static HashMap<Locale, HashMap<String, String>> lang;
    private static UsefulAPI api;

    public static void main(String[] args) throws IOException {
        Dotenv env = Dotenv.load();
        api = UsefulAPI.builder()
                .log(System.out::println)
                .mySQLHost(env.get("mysqlhost"))
                .mySQLDb(env.get("mysqldb"))
                .mySQLPort(Integer.valueOf(env.get("mysqlport")))
                .mySQLPassword(env.get("mysqlpw"))
                .mySQLUsername(env.get("mysqlusername"))
                .useRedis(true)
                .build();
        api.startBeingUseful();
        api.getMySQL().table("`lang` (`uuid` TEXT NOT NULL," +
                "`locale` TEXT NOT NULL )");

        load();
        if (lang.size() == 0)
            return;
        api.log("Loaded " + lang.size() + " locales");

        new RequestPubSub(api);
        new RequestAllPubSub(api);
    }

    private static void load() throws IOException {
        lang = new HashMap<>();

        File folder = new File("lang");

        File[] files = folder.listFiles();
        if (files == null) {
            api.log("No lang files. Aborting.");
            return;
        }

        for (File file : files) {
            YamlFile yamlFile = new YamlFile(file.getPath());
            yamlFile.load();

            String[] s = file.getName().split("_");
            Locale locale = new Locale(s[0], s[1].replace(".yml", ""));

            for (String key : yamlFile.getKeys(false)) {
                put(lang, locale, key, yamlFile.getString(key));
            }
            api.log("Loaded " + lang.get(locale).size() + " keys for " + locale);
        }
    }

    public static void put(HashMap<Locale, HashMap<String, String>> lang, Locale locale, String key, String text) {
        HashMap<String, String> orDefault = lang.getOrDefault(locale, new HashMap<>());

        orDefault.put(key, text);

        lang.put(locale, orDefault);
    }

}
