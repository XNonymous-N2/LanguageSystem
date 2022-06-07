package de.xnonymous.languagesystem.paper.inventories;

import de.xnonymous.languagesystem.paper.LanguageSystem;
import de.xnonymous.languagesystem.utils.Lang;
import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
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
                    .displayName("§e" + Lang.requestData(api, lang, "name"))
                    .custom(true)
                    .skull(Lang.requestData(api, lang, "head"))
                    .material(Material.PLAYER_HEAD)
                    .build().toItemStack(), event -> {

            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
