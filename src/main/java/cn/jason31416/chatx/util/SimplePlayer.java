package cn.jason31416.chatx.util;

import com.velocitypowered.api.proxy.Player;
import cn.jason31416.chatx.message.Message;
import cn.jason31416.chatx.message.MessageLoader;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SimplePlayer {
    @Getter
    private final Player player;

    public String getCurrentServer() {
        if(player == null) return "Console";
        return player.getCurrentServer().isPresent()? player.getCurrentServer().get().getServerInfo().getName() : "Unknown";
    }
    public Collection<Player> getServerPlayers() {
        if(player == null) return List.of();
        return player.getCurrentServer().isPresent()? player.getCurrentServer().get().getServer().getPlayersConnected() : List.of();
    }

    public SimplePlayer(Player player){
        this.player = player;
    }

    public String getName() {
        if(player == null) return MessageLoader.getMessage("chat.console-name", "CONSOLE").toString();
        return player.getUsername();
    }

    public UUID getPlayerUUID() {
        return player.getUniqueId();
    }

    public void sendMessage(@Nonnull Message message) {
        player.sendMessage(message.toComponent());
    }

    public void finishLogin() {
        throw new UnsupportedOperationException();
    }

    public void kick(@Nonnull Message reason) {
        player.disconnect(reason.toComponent());
    }

    public void sendActionBar(@Nonnull Message message) {
        player.sendActionBar(message.toComponent());
    }

    public void showBossbar(@Nonnull BossBar bossBar) {
        player.showBossBar(bossBar);
    }

    public void hideBossbar(@Nonnull BossBar bossBar) {
        player.hideBossBar(bossBar);
    }

    public boolean hasPermission(@Nonnull String permission) {
        return player.hasPermission(permission);
    }

    public boolean isOp() {
        return player.hasPermission("chatx.admin");
    }

    public boolean equals(Object obj) {
        if(obj instanceof SimplePlayer pl) return pl.getPlayerUUID().equals(this.getPlayerUUID());
        return false;
    }
    public int hashCode(){
        return this.getPlayerUUID().hashCode();
    }
}
