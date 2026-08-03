package net.nuggetmc.mw.targeting

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.combat.CombatManager
import net.nuggetmc.mw.combat.CombatManager.isInCombat
import org.bukkit.entity.Entity
import org.bukkit.entity.Player


object TargetManager {
    fun Player.isEnemy(entity: Entity?,vararg targetSelector: TargetSelector): Boolean{
        if (entity==null) return false
        if (this.uniqueId==entity.uniqueId) return false
        if (isNPC(entity)&&targetSelector.contains(TargetSelector.NPC)){
            return isEnemyNPC(this,getNPC(entity)!!)
        }else if (entity is Player&&targetSelector.contains(TargetSelector.PLAYER)){
            return isEnemyPlayer(this, entity)
        }else if(targetSelector.contains(TargetSelector.MOBS)){
            return isEnemyEntity(this,entity)
        }
        return false
    }
    private fun isEnemyNPC(player: Player,NPC: NPC): Boolean{
        return false
    }
    private fun isEnemyPlayer(p1: Player,p2: Player): Boolean{
        if (!isInCombat(p1) || !isInCombat(p2)) {
            return false
        }
        if (MegaWalls.getInstance().teamsManager.isOnSameTeam(p1, p2)) {
            return false
        }
        return true
    }
    private fun isEnemyEntity(player: Player,entity: Entity): Boolean{
        return false
    }

    /**
     * 判断实体是否为 NPC
     */
    private fun isNPC(entity: Entity?): Boolean {
        if (entity == null) {
            return false
        }
        // 使用 CitizensAPI 的 NPCRegistry 校验实体
        return CitizensAPI.getNPCRegistry().isNPC(entity)
    }

    /**
     * 获取实体对应的 NPC 实例
     * @return 如果实体是 NPC 则返回 NPC 实例，否则返回 null
     */
    private fun getNPC(entity: Entity?): NPC? {
        if (!isNPC(entity)) {
            return null
        }
        // 获取 NPC 实例
        return CitizensAPI.getNPCRegistry().getNPC(entity)
    }
    enum class TargetSelector{
        PLAYER,
        NPC,
        MOBS
    }
}