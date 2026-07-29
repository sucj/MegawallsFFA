package net.nuggetmc.mw.mixins;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.Bukkit;

public class TestMixin {
    public static void getLoot(EntityEnderman entityEnderman, CallbackInfo callBackInfo) {
        Bukkit.broadcastMessage("MAMA");

        callBackInfo.setReturned(true);   // 是否拦截原方法返回
        callBackInfo.setReturnValue(Items.APPLE);
    }
}
