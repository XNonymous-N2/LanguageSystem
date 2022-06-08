package de.xnonymous.languagesystem.paper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import de.xnonymous.languagesystem.utils.Lang;
import de.xnonymous.usefulapi.paper.PaperInstance;
import de.xnonymous.usefulapi.paper.PaperUsefulAPI;
import de.xnonymous.usefulapi.paper.util.ChatUtil;
import fr.minuskube.inv.InventoryManager;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
                .name("LanguageSystem")
                .commandCooldownMessage(consumerUtil -> {
                    CommandSender player = consumerUtil.getPlayer();
                    UUID uuid = player instanceof Player ? ((Player) player).getUniqueId() : UUID.randomUUID();
                    ChatUtil.sendMessage(player, Lang.requestData(LanguageSystem.getInstance().getApi(), uuid, "cooldown", consumerUtil.getReplace()));
                })
                .commandNoPermMessage(consumerUtil -> {
                    CommandSender player = consumerUtil.getPlayer();
                    UUID uuid = player instanceof Player ? ((Player) player).getUniqueId() : UUID.randomUUID();
                    ChatUtil.sendMessage(player, Lang.requestData(LanguageSystem.getInstance().getApi(), uuid, "noperm"));
                })
                .commandNoPlayerMessage(consumerUtil -> {
                    CommandSender player = consumerUtil.getPlayer();
                    UUID uuid = player instanceof Player ? ((Player) player).getUniqueId() : UUID.randomUUID();
                    ChatUtil.sendMessage(player, Lang.requestData(LanguageSystem.getInstance().getApi(), uuid, "noplayer"));
                })
                .commandSyntaxMessage(consumerUtil -> {
                    CommandSender player = consumerUtil.getPlayer();
                    UUID uuid = player instanceof Player ? ((Player) player).getUniqueId() : UUID.randomUUID();
                    ChatUtil.sendMessage(player, Lang.requestData(LanguageSystem.getInstance().getApi(), uuid, "syntax", consumerUtil.getReplace()));
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

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.values()) {
            @Override
            public void onPacketReceiving(PacketEvent event) {

            }

            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                StructureModifier<String> strings = event.getPacket().getStrings();
                for (int i = 0; i < strings.getValues().size(); i++) {
                    String read = strings.read(i);
                    StringBuilder finish = new StringBuilder();
                    if (read.contains("1lang:")) {
                        for (String s : read.split("1lang:")) {
                            if (!s.contains(":lang1")) {
                                finish.append(s);
                                continue;
                            }

                            String s1 = s.split(":lang1")[0];

                            finish.append(Lang.requestData(getApi(), player.getUniqueId(), s1));
                            finish.append(s.replaceFirst(s1 + ":lang1", ""));
                        }
                    }
                    strings.write(i, finish.toString());
                }
            }
        });
    }

    @Override
    public void postDisable() {

    }
}
