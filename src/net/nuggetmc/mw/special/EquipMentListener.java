package net.nuggetmc.mw.special;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.nuggetmc.mw.mwclass.classes.MWAsn;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EquipMentListener extends PacketAdapter {
    public EquipMentListener(Plugin plugin) {
        super(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        int entityId = packet.getIntegers().getValues().get(0);
        if (MWAsn.hiddenPlayers.contains(player)){
            if (packet.getItemSlots().getValues().get(0)== EnumWrappers.ItemSlot.MAINHAND) {
                packet.getItemModifier().write(0, null);
            }
        }
    }
}
