package cn.jason31416.chatx.channel.type;

import cn.jason31416.chatx.ChatX;
import cn.jason31416.chatx.channel.Channel;
import cn.jason31416.chatx.channel.ChannelHandler;
import cn.jason31416.chatx.handler.ChatHistoryManager;
import cn.jason31416.chatx.message.Message;
import cn.jason31416.chatx.module.PatternModule;
import cn.jason31416.chatx.util.PlaceholderUtil;
import cn.jason31416.chatx.util.SimplePlayer;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public abstract class ServerWideChannelHandler implements ChannelHandler {
    public abstract List<SimplePlayer> getReceivers(SimplePlayer sender);
    public abstract Component getPrefix(String text, SimplePlayer sender);
    public abstract Channel getChannel();

    @Override
    public void logToConsole(@Nonnull SimplePlayer player, @Nonnull String message){
        PlaceholderUtil.replacePlaceholders(getChannel().getConfig(player.getCurrentServer()).getFormat(), player.getPlayer())
                .thenAccept(text->{
                    Component cmp = PatternModule.handleMessage(player.getPlayer(), message, List.of());
                    Component component = getPrefix(text, player).append(cmp);

                    ChatX.getProxy().getConsoleCommandSource().sendMessage(component);
                });
    }

    @Override
    public void handle(@Nonnull SimplePlayer player, @Nonnull String message) {
        PlaceholderUtil.replacePlaceholders(getChannel().getConfig(player.getCurrentServer()).getFormat(), player.getPlayer())
                .thenAccept(text->{
                    List<SimplePlayer> receivers = getReceivers(player);
                    Component cmp = PatternModule.handleMessage(player.getPlayer(), message, receivers);
                    Component component = getPrefix(text, player).append(cmp);

                    for(SimplePlayer receiver : receivers) {
                        if(getChannel().getConfig(player.getCurrentServer()).getReceivePermission() != null&&!receiver.hasPermission(Objects.requireNonNull(getChannel().getConfig(player.getCurrentServer()).getReceivePermission())))
                            continue;
                        receiver.getPlayer().sendMessage(component, ChatType.CHAT.bind(component));
                    }
                    if(getChannel().getConfig(player.getCurrentServer()).isLogToConsole()) ChatX.getProxy().getConsoleCommandSource().sendMessage(component);
                });
    }
}
