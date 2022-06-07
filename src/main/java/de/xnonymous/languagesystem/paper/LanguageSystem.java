package de.xnonymous.languagesystem.paper;

import de.xnonymous.usefulapi.paper.PaperInstance;
import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import fr.minuskube.inv.InventoryManager;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

public final class LanguageSystem extends PaperInstance {

    private static final Dotenv env = Dotenv.configure()
            .directory("plugins/LanguageSystem")
            .load();
    @Getter
    private InventoryManager inventoryManager;

    public LanguageSystem() {
        super(PaperUsefulAPI.builder()
                .listenerPackage("de.xnonymous.languagesystem.paper.listeners")
                .commandPackage("de.xnonymous.languagesystem.paper.commands")
                .commandCooldownMessage(consumerUtil -> {

                })
                .commandNoPermMessage(consumerUtil -> {

                })
                .commandNoPlayerMessage(consumerUtil -> {

                })
                .commandSyntaxMessage(consumerUtil -> {

                })
                .mySQLHost(env.get("mysqlhost"))
                .mySQLDb(env.get("mysqldb"))
                .mySQLPort(Integer.valueOf(env.get("mysqlport")))
                .mySQLPassword(env.get("mysqlpw"))
                .mySQLUsername(env.get("mysqlusername"))
                .useRedis(true)
                .build());
    }

    @Override
    public void postEnable() {
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
    }

    @Override
    public void postDisable() {

    }
}
