package cn.jason31416.chatx.handler;

import cn.jason31416.chatx.util.*;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.velocitypowered.api.proxy.Player;
import cn.jason31416.chatx.ChatX;
import cn.jason31416.chatx.channel.Channel;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import static com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.*;
import static com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client.*;

public class PacketEventListener extends SimplePacketListenerAbstract {
    private static int debugLoggingDepth = 1;
    public static List<PacketTypeCommon> filtered = List.of(
            ENTITY_MOVEMENT,
            ENTITY_RELATIVE_MOVE,
            PacketType.Play.Client.KEEP_ALIVE,
            PacketType.Play.Server.KEEP_ALIVE,
            PLAYER_POSITION,
            PLAYER_POSITION_AND_ROTATION,
            CLIENT_TICK_END,
            TIME_UPDATE,
            CHUNK_DATA,
            ENTITY_HEAD_LOOK,
            ENTITY_POSITION_SYNC,
            SPAWN_ENTITY,
            BLOCK_ACTION,
            TEAMS,
            PacketType.Play.Client.PLUGIN_MESSAGE,
            PacketType.Play.Server.PLUGIN_MESSAGE,
            BOSS_BAR,
            SCOREBOARD_OBJECTIVE,
            ENTITY_METADATA,
            ENTITY_VELOCITY,
            ENTITY_HEAD_LOOK,
            ENTITY_ACTION,
            ENTITY_ROTATION,
            ENTITY_STATUS,
            PLAYER_POSITION_AND_LOOK,
            BLOCK_ENTITY_DATA,
            ACKNOWLEDGE_BLOCK_CHANGES,
            UPDATE_SCORE,
            PLAYER_INFO_UPDATE,
            DECLARE_COMMANDS,
            ENTITY_EQUIPMENT,
            PLAYER_INFO_REMOVE,
            ENTITY_ANIMATION,
            SET_EXPERIENCE,
            UPDATE_HEALTH,
            UPDATE_ADVANCEMENTS,
            ENTITY_RELATIVE_MOVE_AND_ROTATION,
            ENTITY_TELEPORT,
            BLOCK_CHANGE,
            PacketType.Play.Client.PLAYER_ROTATION,
            PacketType.Play.Server.PLAYER_ROTATION,
            BUNDLE,
            PLAYER_LIST_HEADER_AND_FOOTER
    );
    public PacketEventListener() {
        super(PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketPlaySend(@Nonnull PacketPlaySendEvent event) {
        try {
            if (event.getPlayer() == null || ((Player) event.getPlayer()).getCurrentServer().isEmpty() || event.getConnectionState() == ConnectionState.LOGIN)
                return;
            if(!filtered.contains(event.getPacketType())&&Config.getConfigTree().getBoolean("debug", false)) listenTo(event, debugLoggingDepth);
            switch (event.getPacketType()) {
                case PLAYER_POSITION_AND_LOOK -> {
                    WrapperPlayServerPlayerPositionAndLook packet = new WrapperPlayServerPlayerPositionAndLook(event);
                    Player player = event.getPlayer();
                    Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()))
                            .setPosition(packet.getPosition());
                }
            }
        }catch (Exception e){
            if(Config.getBoolean("debug")) {
                Logger.error("Error occured when processing: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPacketPlayReceive(@Nonnull PacketPlayReceiveEvent event) {
        try{
            if(event.getPlayer()==null||((Player) event.getPlayer()).getCurrentServer().isEmpty()||event.getConnectionState()== ConnectionState.LOGIN) return;
            if(!filtered.contains(event.getPacketType())&&Config.getConfigTree().getBoolean("debug", false)) listenTo(event, debugLoggingDepth);
            switch(event.getPacketType()) {
                case PLAYER_POSITION -> {
                    WrapperPlayClientPlayerPosition packet = new WrapperPlayClientPlayerPosition(event);
                    Player player = event.getPlayer();
                    Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()))
                            .setPosition(packet.getPosition());
                }
            }
        }catch (Exception e){
            if(Config.getBoolean("debug")) {
                Logger.error("Error occured when processing: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private void printObject(Object obj, String prefix, int depth) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (depth - 1 <= 0) {
                    String cont = Objects.toString(field.get(obj));
                    if (cont.length() > 50) {
                        cont = cont.substring(0, 30) + "..." + cont.substring(cont.length() - 20);
                    }
                    System.out.println(prefix + "- " + field.getName() + " : " + cont);
                } else {
                    System.out.println(prefix + "- " + field.getName() + " :");
                    printObject(field.get(obj), prefix + "  ", depth - 1);
                }
            }catch(Exception ignored){}
        }
    }

    private void listenTo(ProtocolPacketEvent event, int depth) { // to debug
        try {
            com.github.retrooper.packetevents.wrapper.PacketWrapper<?> packet = null;
            for(Constructor<?> i : event.getPacketType().getWrapperClass().getConstructors()){
                if(i.getParameterCount()==1 && i.getParameterTypes()[0].isAssignableFrom(event.getClass())){
                    packet = (com.github.retrooper.packetevents.wrapper.PacketWrapper<?>) i.newInstance(event);
                    break;
                }
            }
            System.out.println(event.getClass().getSimpleName() + "("+ ((Player)event.getPlayer()).getUsername() +"): "+event.getPacketType());
            if(packet != null && depth > 0) printObject(packet, "  ", depth);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
