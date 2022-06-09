package de.xnonymous.languagesystem.paper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
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

import java.lang.reflect.Field;
import java.util.List;
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
                .prefix("§7LanguageSystem =>")
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
        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT) {

            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Field messageField = packet.getModifier().getField(0);
                messageField.setAccessible(true);
                String msg = "";
                try {
                    msg = (String) messageField.get(packet);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                while(msg.contains("1lang:")){
                    if(msg.contains("1lang:")){
                        String key = msg.split("1lang:")[1];
                        key = key.split(":lang1")[0];
                        msg = msg.replaceFirst("1lang:", "");
                        msg = msg.replaceFirst(":lang1", Lang.requestData(getApi(), event.getPlayer().getUniqueId(), key));
                    }
                }
                try {
                    messageField.set(packet, msg);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                event.setPacket(packet);
            }
        });

    }

    @Override
    public void postDisable() {

    }
}
