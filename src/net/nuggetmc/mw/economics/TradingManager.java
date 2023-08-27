package net.nuggetmc.mw.economics;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TradingManager {
    public static void requestTrade(Player requester,Player target){
        requester.sendMessage(ChatColor.GREEN+"You have sent a trade request to"+target.getDisplayName()+".");
        TextComponent textComponent=new TextComponent(ChatColor.YELLOW+requester.getDisplayName()+ChatColor.GREEN+"has sent you a trade request.Click "+ChatColor.YELLOW+"here "+ChatColor.GREEN+"to accept!");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade accept " + requester.getName()));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("CLICK HERE TO ACCEPT!")}));

    }
}
