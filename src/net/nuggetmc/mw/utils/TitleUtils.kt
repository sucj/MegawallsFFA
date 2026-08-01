package net.nuggetmc.mw.utils

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

object TitleUtils {

    /**
     * 发送带自定义时间的 Title
     *
     * @param player 目标玩家
     * @param title 主标题文本（支持 § 颜色代码）
     * @param subtitle 副标题文本（支持 § 颜色代码）
     * @param fadeIn 淡入时间 (tick)
     * @param stay 停留时间 (tick)
     * @param fadeOut 淡出时间 (tick)
     */
    fun sendTitle(
        player: Player,
        title: String?,
        subtitle: String?,
        fadeIn: Int,
        stay: Int,
        fadeOut: Int
    ) {
        val connection = (player as CraftPlayer).handle.playerConnection

        // 1. 发送时间 Packet
        val timePacket = PacketPlayOutTitle(
            PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut
        )
        connection.sendPacket(timePacket)

        // 2. 发送主标题
        if (title != null) {
            val titleComponents = TextComponent.fromLegacyText(title)
            val nmsComponent = toNMSComponent(*titleComponents)
            val titlePacket = PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE, nmsComponent
            )
            connection.sendPacket(titlePacket)
        }

        // 3. 发送副标题
        if (subtitle != null) {
            val subComponents = TextComponent.fromLegacyText(subtitle)
            val nmsComponent = toNMSComponent(*subComponents)
            val subPacket = PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.SUBTITLE, nmsComponent
            )
            connection.sendPacket(subPacket)
        }
    }

    /**
     * 将 Spigot / Bungee 的 BaseComponent 数组转为 1.8.8 NMS 的 IChatBaseComponent
     */
    private fun toNMSComponent(vararg components: BaseComponent): IChatBaseComponent {
        val json = net.md_5.bungee.chat.ComponentSerializer.toString(*components)
        return IChatBaseComponent.ChatSerializer.a(json)
    }
}