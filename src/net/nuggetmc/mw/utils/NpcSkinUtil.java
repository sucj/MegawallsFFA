package net.nuggetmc.mw.utils;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.nuggetmc.mw.MegaWalls;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty; // 旧版接口，部分版本可能直接是 Mojang 的 Property 类

public class NpcSkinUtil {

    /**
     * 将 SkinsRestorer 缓存中的皮肤应用到 Citizens NPC 上
     *
     * @param npc      目标 Citizens NPC
     * @param skinName 皮肤名称（例如玩家名或自定义皮肤名）
     * @return result 执行结果
     */
    public static boolean applySkinsRestorerSkin(NPC npc, String skinName) {

        // 1. 获取 SkinsRestorer API 实例
        SkinsRestorerAPI api = SkinsRestorerAPI.getApi();
        if (api==null){
            System.out.println("Warning:SkinsResorter is not loaded!");
            return false;
        }
        try {
            // 2. 从 SkinsRestorer 获取皮肤的 Property 对象
            // 注意：旧版中常用的方法是 getSkinData(skinName) 或 getSkinProperty(skinName)
            IProperty skinData = api.getSkinData(skinName);

            if (skinData != null) {
                String texture;
                String signature;

                // 3. 处理返回的 Property 数据结构（旧版通常为 IProperty 或反射/原生 Property）
                {
                    texture = skinData.getValue();
                    signature = skinData.getSignature();
                }

                // 4. 将获取到的 Texture 和 Signature 设置给 Citizens 的 SkinTrait
                SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
                
                // setSkinPersistent 会将皮肤持久化保存到 NPC 数据中
                skinTrait.setSkinPersistent(skinName, signature, texture);

                /*// 5. 如果 NPC 当前已生成在世界上，重新 Spawn 以刷新皮肤显示
                if (npc.isSpawned()) {
                    npc.despawn();
                    npc.spawn(npc.getStoredLocation());
                }*/
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}