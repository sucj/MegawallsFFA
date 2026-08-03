package net.nuggetmc.mw.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class PlayerSafeSet : Iterable<Player>{
    @PublishedApi
    internal val storage = hashSetOf<UUID>()

    fun add(player: Player?): Boolean {
        if (player == null) return false
        return storage.add(player.uniqueId)
    }

    // 将参数类型改为 Player?
    fun remove(player: Player?): Boolean {
        if (player == null) return false
        return storage.remove(player.uniqueId)
    }

    // 将参数类型改为 Player?
    operator fun contains(player: Player?): Boolean {
        if (player == null) return false
        return storage.contains(player.uniqueId)
    }

    // 支持 UUID
    fun add(uuid: UUID): Boolean = storage.add(uuid)
    fun remove(uuid: UUID): Boolean = storage.remove(uuid)
    operator fun contains(uuid: UUID): Boolean = storage.contains(uuid)

    fun clear() = storage.clear()
    fun size() = storage.size
    fun isEmpty() = storage.isEmpty()

    // 清理掉内部已经离线的 UUID
    fun purgeOffline(): Boolean = storage.removeIf { Bukkit.getPlayer(it) == null }

    // 获取所有当前在线的玩家
    fun getOnlinePlayers(): List<Player> {
        return storage.mapNotNull { Bukkit.getPlayer(it) }
    }

    // 高效遍历在线玩家
    inline fun forEachOnline(block: (Player) -> Unit) {
        for (uuid in storage) {
            val player = Bukkit.getPlayer(uuid) ?: continue
            block(player)
        }
    }
    override fun iterator(): Iterator<Player> {
        return getOnlinePlayers().iterator()
    }
    /**
     * 无参 toArray：直接获取在线玩家的 Array<Player> / Player[] 数组
     */
    fun toArray(): Array<Player> {
        return getOnlinePlayers().toTypedArray()
    }

    /**
     * 兼容 Java Collection.toArray(T[] a) 规范的重载
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> toArray(array: Array<T>): Array<T> {
        val onlineList = getOnlinePlayers()
        // 借助 Java 集合的 API 原生转换
        return (onlineList as java.util.Collection<*>).toArray(array) as Array<T>
    }
}