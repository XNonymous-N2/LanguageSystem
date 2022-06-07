package de.xnonymous.languagesystem.paper.listeners;

import de.xnonymous.api.mysql.MySQL;
import de.xnonymous.languagesystem.paper.LanguageSystem;
import de.xnonymous.languagesystem.paper.inventories.PickLangInventory;
import de.xnonymous.languagesystem.utils.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MySQL mySQL = LanguageSystem.getInstance().getApi().getMySQL();
        Lang.getLang(mySQL, player.getUniqueId(), locale -> {
            if (locale != null)
                return;

            PickLangInventory.INVENTORY.open(player);
        });
    }

}
