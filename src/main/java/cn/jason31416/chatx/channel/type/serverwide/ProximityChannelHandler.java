package cn.jason31416.chatx.channel.type.serverwide;

import cn.jason31416.chatx.channel.Channel;
import cn.jason31416.chatx.channel.type.ServerWideChannelHandler;
import cn.jason31416.chatx.message.Message;
import cn.jason31416.chatx.util.PlayerData;
import cn.jason31416.chatx.util.SimplePlayer;
import com.github.retrooper.packetevents.util.Vector3d;
import net.kyori.adventure.text.Component;

import java.util.List;

public class ProximityChannelHandler extends ServerWideChannelHandler {
    // todo: NOT FINISHED. World not fetched.
    Channel channel;
    public ProximityChannelHandler(Channel channel) {
        this.channel = channel;
    }

    @Override
    public List<SimplePlayer> getReceivers(SimplePlayer sender) {
        if (sender.getCurrentServer()==null) {
            return List.of();
        }
        Vector3d position = PlayerData.getPlayerData(sender.getPlayer()).getPosition();
        return sender.getServerPlayers().stream().filter(player->
                position.distance(PlayerData.getPlayerData(player).getPosition()) <= channel.getRawConfig().getDouble("range",64)
        ).map(SimplePlayer::new).toList();
    }

    @Override
    public Component getPrefix(String text, SimplePlayer sender) {
        return new Message(text)
                .add("player", sender.getName())
                .add("channel", channel.getConfig(sender.getCurrentServer()).getDisplayName())
                .toComponent();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }
}
