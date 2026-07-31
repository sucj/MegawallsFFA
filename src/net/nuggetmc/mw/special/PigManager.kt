package net.nuggetmc.mw.special

import net.minecraft.server.v1_8_R3.EntityHorse
import net.minecraft.server.v1_8_R3.EntityPig
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPig
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Horse
import org.bukkit.entity.Pig
import org.bukkit.inventory.ItemStack

object PigManager {

    /**
     * 召唤一只“无敌神猪”：
     * - 名称: §c无敌神猪
     * - 名字永久可见
     * - 带鞍
     * - 无 AI (NoAI: 1b)
     * - 自定义 NBT: pig = true
     */
    fun spawnInvinciblePig(location: Location): Horse {
        val pig = location.world.spawnEntity(location, EntityType.HORSE) as Horse
        
        // 1. 设置基本 Bukkit 属性
        pig.customName = "§c暴君 马克猪"
        pig.isCustomNameVisible = true
        pig.inventory.saddle = ItemStack(Material.SADDLE,1)
        pig.inventory.armor = ItemStack(Material.DIAMOND_BARDING)
        pig.maxHealth = 40.0
        pig.health = 40.0

        // 2. 处理 NMS / NBT 标签
        val nmsPig: EntityHorse = (pig as CraftHorse).handle
        val nbt = NBTTagCompound()
        
        // 将当前实体的 NBT 存入 compound 中
        nmsPig.c(nbt)
        
        // 写入 NoAI 标签 (1b 表示禁用 AI)
        //nbt.setInt("NoAI", 1)
        
        // 写入你自定义的 setBoolean("pig", true)
        nbt.setBoolean("pig", true)
        
        // 将修改后的 NBT 重新应用到实体
        nmsPig.f(nbt)


        return pig
    }

}