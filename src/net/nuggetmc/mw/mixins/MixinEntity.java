package net.nuggetmc.mw.mixins;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import net.minecraft.server.v1_8_R3.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MixinEntity {
    public static void a(Entity entity, Item item, int i, CallbackInfo callBackInfo) {
        if (entity instanceof EntityHorse &&blockedDrops.contains(item)){
            callBackInfo.setReturned(true);
            callBackInfo.setReturnValue(null);
        }
    }
    static List<Item> blockedDrops = Arrays.asList(Items.SADDLE,Items.DIAMOND_HORSE_ARMOR,Items.IRON_HORSE_ARMOR,Items.GOLDEN_HORSE_ARMOR);
}
