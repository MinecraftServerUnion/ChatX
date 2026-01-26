package dev.onelili.unichat.velocity.util;

import com.velocitypowered.api.proxy.Player;
import dev.onelili.unichat.velocity.UniChat;
import dev.onelili.unichat.velocity.util.hook.PAPIProxyBridgeHook;

import java.util.concurrent.CompletableFuture;

public class PlaceholderUtil {
    public static boolean isPAPIAvailable;
    public static void init(){
        if(UniChat.getProxy().getPluginManager().getPlugin("papiproxybridge").isPresent()) {
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
