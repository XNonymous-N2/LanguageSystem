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
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class PickLangInventory implements InventoryProvider {
    public static SmartInventory INVENTORY = SmartInventory.builder()
            .provider(new PickLangInventory())
            .manager(((LanguageSystem) LanguageSystem.getInstance()).getInventoryManager())
            .title("§0Please choose your language")
            .size(3, 9)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        PaperUsefulAPI api = LanguageSystem.getInstance().getApi();
        ArrayList<Locale> locales = Lang.requestLocales(api);

        for (int i = 0; i < locales.size(); i++) {
            Locale lang = locales.get(i);
            contents.set(1, 2, ClickableItem.of(ItemBuilder.builder()
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

            contents.set(1, 6, ClickableItem.of(ItemBuilder.builder()
                    .displayName("§e2lang:name:lang2")
                    .custom(true)
                    .skull("2lang:name:lang2")
                    .material(Material.PLAYER_HEAD)
                    .build().toItemStack(), event -> {
                MySQL mySQL = api.getMySQL();

                mySQL.delete("lang", "uuid", player.getUniqueId().toString());
                mySQL.insertData("lang", "`uuid`, `locale`", player.getUniqueId().toString(), lang.toString());

                player.closeInventory();
                ChatUtil.sendMessage(player, "1lang:change:lang1");
            }));
        }
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Bitte wähle deine Sprache");
        contents.set(0, 0, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 1, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 2, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 3, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 4, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 5, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 6, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 7, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 8, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(0, 9, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(1, 0, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(1, 1, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.BLACK_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        //Lang 1
        contents.set(1, 3, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.BLACK_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(1, 4, ClickableItem.of(ItemBuilder.builder().displayName("§7Please choose your language").lores(lore).material(Material.PAPER).build().toItemStack(), event -> {}));
        contents.set(1, 5, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.BLACK_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        //Lang 2
        contents.set(1, 7, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.BLACK_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(1, 8, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 0, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 1, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 2, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 3, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 4, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 5, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 6, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 7, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.WHITE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
        contents.set(2, 8, ClickableItem.of(ItemBuilder.builder().displayName(" ").material(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build().toItemStack(), event -> {}));
     }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
