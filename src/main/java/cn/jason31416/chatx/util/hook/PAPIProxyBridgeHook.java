package cn.jason31416.chatx.util.hook;

import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import net.william278.papiproxybridge.api.PlaceholderAPI;

import java.util.concurrent.CompletableFuture;

public class PAPIProxyBridgeHook {
    @Getter
    private static PlaceholderAPI api=null;

    public static void init(){
        api = PlaceholderAPI.createInstance();
    }

    public static CompletableFuture<String> replacePlaceholders(String text, Player player){
        return api.formatPlaceholders(text, player.getUniqueId());
    }
}
