package net.nuggetmc.mw.mixins;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPig;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.Items;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MixinEntity {
    public static void a(Entity entity, Item item, int i, CallbackInfo callBackInfo) {
        if (entity instanceof EntityPig&&blockedDrops.contains(item)){
            callBackInfo.setReturned(true);
            callBackInfo.setReturnValue(null);
        }
    }
    static List<Item> blockedDrops = Arrays.asList(Items.PORKCHOP,Items.SADDLE,Items.COOKED_PORKCHOP);
}
