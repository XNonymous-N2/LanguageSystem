package de.xnonymous.languagesystem.paper.inventories;

import de.xnonymous.api.mysql.MySQL;
import de.xnonymous.languagesystem.paper.LanguageSystem;
import de.xnonymous.languagesystem.utils.Lang;
import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import de.xnonymous.usefulapi.paper.util.ChatUtil;
import de.xnonymous.usefulapi.paper.util.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class PickLangInventory implements InventoryProvider {
    public static SmartInventory INVENTORY = SmartInventory.builder()
            .provider(new PickLangInventory())
            .manager(((LanguageSystem) LanguageSystem.getInstance()).getInventoryManager())
            .title("§0Please choose your language")
            .size(1, 9)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        PaperUsefulAPI api = LanguageSystem.getInstance().getApi();
        ArrayList<Locale> locales = Lang.requestLocales(api);

        for (int i = 0; i < locales.size(); i++) {
            Locale lang = locales.get(i);
            contents.set(0, i, ClickableItem.of(ItemBuilder.builder()
                    .displayName("§e1lang:name:lang1")
                    .custom(true)
                    .skull("1lang:name:lang1")
                    .material(Material.PLAYER_HEAD)
                    .build().toItemStack(), event -> {
                MySQL mySQL = api.getMySQL();

                mySQL.delete("lang", "uuid", player.getUniqueId().toString());
                mySQL.insertData("lang", "`uuid`, `locale`", player.getUniqueId().toString(), lang.toString());

                player.closeInventory();
                ChatUtil.sendMessage(player, "1lang:change:lang1");
            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
