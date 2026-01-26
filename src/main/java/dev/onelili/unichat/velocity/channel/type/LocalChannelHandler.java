package dev.onelili.unichat.velocity.channel.type;

import com.velocitypowered.api.proxy.Player;
import dev.onelili.unichat.velocity.UniChat;
import dev.onelili.unichat.velocity.channel.Channel;
import dev.onelili.unichat.velocity.channel.ChannelHandler;
import dev.onelili.unichat.velocity.handler.ChatHistoryManager;
import dev.onelili.unichat.velocity.message.Message;
import dev.onelili.unichat.velocity.module.PatternModule;
import dev.onelili.unichat.velocity.util.PlaceholderUtil;
import dev.onelili.unichat.velocity.util.SimplePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class LocalChannelHandler implements ChannelHandler {
    Channel channel;
    public LocalChannelHandler(Channel channel) {
        this.channel = channel;
    }
    @Override
    public void handle(@NotNull SimplePlayer player, @NotNull String message) {
        Component msg = PatternModule.handleMessage(player.getPlayer(), message, true);
        PlaceholderUtil.replacePlaceholders(channel.getChannelConfig().getString("format"),player.getPlayer())
                .thenAccept(text->{
                    Component component = new Message(text)
                            .add("player", player.getName())
                            .add("channel", channel.getDisplayName())
                            .toComponent().append(msg);

                    if (player.getCurrentServer()!=null) {
                        for (Player pl : player.getServerPlayers()) {
                            pl.sendMessage(component);
                        }
                    }
                });
    }
}
