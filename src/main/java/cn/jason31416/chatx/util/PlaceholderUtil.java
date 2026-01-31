package cn.jason31416.chatx.util;

import com.velocitypowered.api.proxy.Player;
import cn.jason31416.chatx.ChatX;
import cn.jason31416.chatx.util.hook.PAPIProxyBridgeHook;

import java.util.concurrent.CompletableFuture;

public class PlaceholderUtil {
    public static boolean isPAPIAvailable;
    public static void init(){
        if(ChatX.getProxy().getPluginManager().getPlugin("papiproxybridge").isPresent()) {
            isPAPIAvailable = true;
            PAPIProxyBridgeHook.init();
        }else{
            isPAPIAvailable = false;
        }
    }

    public static CompletableFuture<String> replacePlaceholders(String text, Player player){
        if(isPAPIAvailable){
            return PAPIProxyBridgeHook.replacePlaceholders(text, player);
        }else{
            return CompletableFuture.completedFuture(text);
        }
    }
}
