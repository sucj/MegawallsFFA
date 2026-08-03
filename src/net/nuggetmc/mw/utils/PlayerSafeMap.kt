package net.nuggetmc.mw.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class PlayerSafeMap<V> : Iterable<Map.Entry<Player, V>> {

    @PublishedApi
    internal val storage = hashMapOf<UUID, V>()

    // --- Key 支持 Player? ---

    fun put(player: Player?, value: V): V? {
        if (player == null) return null
        return storage.put(player.uniqueId, value)
    }

    // 加上 operator 关键字，同时支持 `map.get(player)` 和 `map[player]`
    operator fun get(player: Player?): V? {
        if (player == null) return null
        return storage[player.uniqueId]
    }

    fun remove(player: Player?): V? {
        if (player == null) return null
        return storage.remove(player.uniqueId)
    }

    fun containsKey(player: Player?): Boolean {
        if (player == null) return false
        return storage.containsKey(player.uniqueId)
    }

    // --- Key 支持 UUID ---

    fun put(uuid: UUID, value: V): V? = storage.put(uuid, value)

    // 加上 operator 关键字，支持 `map.get(uuid)` 和 `map[uuid]`
    operator fun get(uuid: UUID): V? = storage[uuid]

    fun remove(uuid: UUID): V? = storage.remove(uuid)
    fun containsKey(uuid: UUID): Boolean = storage.containsKey(uuid)

    // --- Kotlin 下标赋值运算符支持：map[player] = value / map[uuid] = value ---

    operator fun set(player: Player?, value: V) {
        put(player, value)
    }

    operator fun set(uuid: UUID, value: V) {
        storage[uuid] = value
    }

    // --- 基础集合操作 ---

    fun containsValue(value: V): Boolean = storage.containsValue(value)
    fun clear() = storage.clear()
    fun size() = storage.size
    fun isEmpty() = storage.isEmpty()

    // 清理掉内部对应玩家已经离线的 Key
    fun purgeOffline(): Boolean {
        return storage.keys.removeIf { Bukkit.getPlayer(it) == null }
    }

    // 获取所有当前在线玩家与其对应的 Value 映射列表
    fun getOnlineEntries(): List<AbstractMap.SimpleImmutableEntry<Player, V>> {
        val list = mutableListOf<AbstractMap.SimpleImmutableEntry<Player, V>>()
        for ((uuid, value) in storage) {
            val player = Bukkit.getPlayer(uuid) ?: continue
            list.add(AbstractMap.SimpleImmutableEntry(player, value))
        }
        return list
    }

    // 高效遍历在线玩家及其绑定的 Value
    inline fun forEachOnline(block: (Player, V) -> Unit) {
        for ((uuid, value) in storage) {
            val player = Bukkit.getPlayer(uuid) ?: continue
            block(player, value)
        }
    }

    // 继承 Iterable<Map.Entry<Player, V>>，方便 Java 和 Kotlin 直接用 for-each 遍历在线 Entry
    override fun iterator(): Iterator<Map.Entry<Player, V>> {
        return getOnlineEntries().iterator()
    }
}