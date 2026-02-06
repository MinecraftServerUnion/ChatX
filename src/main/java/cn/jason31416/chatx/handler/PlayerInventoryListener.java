package cn.jason31416.chatx.handler;

import cn.jason31416.chatx.gui.GUIContainer;
import cn.jason31416.chatx.handler.packetwrapper.WrapperPlayClientClickWindow;
import cn.jason31416.chatx.handler.packetwrapper.WrapperPlayClientCreativeInventoryAction;
import cn.jason31416.chatx.handler.packetwrapper.WrapperPlayServerSetSlot;
import cn.jason31416.chatx.handler.packetwrapper.WrapperPlayServerWindowItems;
import cn.jason31416.chatx.util.Config;
import cn.jason31416.chatx.util.Logger;
import cn.jason31416.chatx.util.PlayerData;
import cn.jason31416.chatx.util.ShitMountainException;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHeldItemChange;
import com.velocitypowered.api.proxy.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerInventoryListener extends SimplePacketListenerAbstract {
    public PlayerInventoryListener() {
        super(PacketListenerPriority.NORMAL);
    }
    @Override
    public void onPacketPlaySend(@Nonnull PacketPlaySendEvent event) {
        try {
            if (event.getPlayer() == null || ((Player) event.getPlayer()).getCurrentServer().isEmpty() || event.getConnectionState() == ConnectionState.LOGIN)
                return;
            switch (event.getPacketType()) {
                case HELD_ITEM_CHANGE -> {
                    WrapperPlayServerHeldItemChange packet = new WrapperPlayServerHeldItemChange(event);
                    Player player = event.getPlayer();
                    Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()))
                            .setHandItem(packet.getSlot());
                }
                case SET_SLOT -> { // Not problematic
                    //                listenTo(event, 1);
                    WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
                    Player player = event.getPlayer();
                    var playerData = Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()));
                    int slot = packet.getSlot();
                    int additionalSize;
                    if (packet.getWindowId() == 0) additionalSize = 9;
                    else additionalSize = playerData.getInventoryPreSizes().getOrDefault(packet.getWindowId(), 9);
                    if (slot < additionalSize) {
                        playerData.getTopInventory().put(slot, packet.getItem());
                    } else {
                        playerData.getInventory().put(slot - additionalSize, packet.getItem());
                    }
                }
                case WINDOW_ITEMS -> { // Not problematic
                    WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
                    Player player = event.getPlayer();
                    var playerData = Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()));
                    //                System.out.println("WINDOW ITEMS: "+packet.getWindowId()+" sizeof "+packet.getItems().size());
                    if (packet.getItems().size() >= 36) {
                        for (int i = 0; i < 36; i++) {
                            playerData.getInventory().put(i, packet.getItems().get(i + (packet.getItems().size() - 36 - (packet.getWindowId() == 0 ? 1 : 0))));
                        }
                        playerData.getTopInventory().clear();
                        for (int i = 0; i < packet.getItems().size() - 36 - (packet.getWindowId() == 0 ? 1 : 0); i++) {
                            playerData.getTopInventory().put(i, packet.getItems().get(i));
                        }
                    }
                    if (packet.getWindowId() == 0) {
                        playerData.getInventoryPreSizes().put(packet.getWindowId(), 9);
                    } else {
                        playerData.getInventoryPreSizes().put(packet.getWindowId(), packet.getItems().size() - 36);
                    }
                }
                case CLOSE_WINDOW -> {
                    WrapperPlayServerCloseWindow packet = new WrapperPlayServerCloseWindow(event);
                    try {
                        GUIContainer.getGuis().remove(GUIContainer.getGuis().stream().filter(gui -> gui.getData().windowId() == packet.getWindowId()).findFirst().orElseThrow(() -> new ShitMountainException("null")));
                    } catch (Exception ignored) {
                    }
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
            switch(event.getPacketType()) {
                case HELD_ITEM_CHANGE -> {
                    WrapperPlayClientHeldItemChange packet = new WrapperPlayClientHeldItemChange(event);
                    Player player = event.getPlayer();
                    Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()))
                            .setHandItem(packet.getSlot());
                }
                case CREATIVE_INVENTORY_ACTION -> { // not problematic
                    WrapperPlayClientCreativeInventoryAction packet;
                    try{
                        packet = new WrapperPlayClientCreativeInventoryAction(event);
                    }catch (IllegalArgumentException e){
                        return;
                    }
                    Player player = event.getPlayer();
                    var playerData = Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()));
                    int additionalSize=playerData.getInventoryPreSizes().getOrDefault(0, 9);
                    playerData.getInventory().put(packet.getSlot()-additionalSize, packet.getItemStack());
                }
                case PLAYER_DIGGING -> { // not problematic
                    WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);
                    Player player = event.getPlayer();
                    var playerData = Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()));

                    if(packet.getAction() == DiggingAction.DROP_ITEM){
                        var mainhand = playerData.getInventory().get(playerData.getHandItem() + 27);
                        if(mainhand == null) return;
                        if(mainhand.getAmount()-1<=0) playerData.getInventory().remove(playerData.getHandItem() + 27);
                        else mainhand.setAmount(mainhand.getAmount()-1);
                    }else if(packet.getAction() == DiggingAction.DROP_ITEM_STACK){
                        var mainhand = playerData.getInventory().get(playerData.getHandItem() + 27);
                        if(mainhand == null) return;
                        playerData.getInventory().remove(playerData.getHandItem() + 27);
                    }
                }
                case CLICK_WINDOW -> { // yes problematic and fixed
    //                listenTo(event, debugLoggingDepth);
                    WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
                    Player player = event.getPlayer();
                    var playerData = Objects.requireNonNull(PlayerData.getPlayerDataMap().computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()));
                    GUIContainer[] gui = new GUIContainer[1];
                    GUIContainer.getGuis().stream().filter(obj -> obj.getData().windowId() == packet.getWindowId()).forEach(obj -> gui[0] = obj);
                    if(gui[0] != null) {
                        event.setCancelled(true);
                        List<ItemStack> items = new ArrayList<>();
                        int guiSize=gui[0].getData().slots() / 9 * 9;
                        for(int i = 0; i < guiSize; i++) {
                            if (gui[0].getData().items().get(i) != null)
                                items.add(gui[0].getData().items().get(i));
                            else
                                items.add(ItemStack.EMPTY);
                        }
    //                    for(int i=0;i<36;i++){
    //                        items.add(playerData.getInventory().getOrDefault(i, ItemStack.EMPTY));
    //                    }
                        var wrapper1 = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems(
                                packet.getWindowId(),
                                packet.getStateId().orElse(0),
                                items,
                                null
                        );
                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper1);

                        if(packet.getHashedSlots()!=null) {
                            packet.getHashedSlots().forEach((k, v)->{
                                if(k>=guiSize){
                                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot(
                                            packet.getWindowId(),
                                            packet.getStateId().orElse(0),
                                            k,
                                            playerData.getInventory().getOrDefault(k-guiSize, ItemStack.EMPTY)
                                    ));
                                }
                            });
                        }
                    }else { // When dealing with actions here, items loses NBT

                        int additionalSize = playerData.getInventoryPreSizes().getOrDefault(packet.getWindowId(), 9);

                        // This is to fix the missing NBT in this packet
                        // In exactly one packet, the placed slots MUST come from the cursor, and the cursor MUST come from slot clicked
                        // Except the QUICK_MOVE action, which must be dealt independently

                        ItemStack template;
                        if(packet.getSlot()<additionalSize) {
                            template = playerData.getTopInventory().getOrDefault(packet.getSlot(), ItemStack.EMPTY).copy();
                        }else{
                            template = playerData.getInventory().getOrDefault(packet.getSlot()-additionalSize, ItemStack.EMPTY).copy();
                        }

                        if(template!=ItemStack.EMPTY&&packet.getWindowClickType()== WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE){
                            playerData.setCursor(template.copy());
                        }

                        if (packet.getHashedSlots() != null) {
                            packet.getHashedSlots().forEach((key, value) -> {
                                ItemStack stack;
                                if(value.isPresent()){
                                    if(playerData.getCursor()==null){
                                        if(Config.getBoolean("debug")) Logger.warn("Unexpected exception: Cursor is null");
                                        return;
                                    }
                                    stack = playerData.getCursor().copy();
                                    stack.setAmount(value.get().getCount());
                                }else{
                                    stack = ItemStack.EMPTY;
                                }
                                if (key < additionalSize) {
    //                                System.out.println("Value " + key + " is outside of container");
                                    playerData.getTopInventory().put(key, stack);
                                }else {
    //                                System.out.println("Putting " + value.get().asItemStack() + " to " + (key - additionalSize));
                                    playerData.getInventory().put(key - additionalSize, stack);
                                }
                            });
                        }

                        // Update cursor status
                        if(packet.getCarriedHashedStack().isPresent() &&
                                template!=ItemStack.EMPTY) { // Empty check to exclude when right clicking to partially deposit
                            var cloned = template.copy();
                            cloned.setAmount(packet.getCarriedHashedStack().get().getCount());
                            playerData.setCursor(cloned);
                        }else if(packet.getCarriedHashedStack().isPresent() &&template==ItemStack.EMPTY) {
                            if(playerData.getCursor()==null){
                                if(Config.getBoolean("debug")) Logger.warn("Unexpected exception: Cursor is null");
                                return;
                            }
                            var cloned = playerData.getCursor().copy();
                            cloned.setAmount(packet.getCarriedHashedStack().get().getCount());
                            playerData.setCursor(cloned);
                        }else if(packet.getCarriedHashedStack().isEmpty()){
                            playerData.setCursor(null);
                        }
                    }
                }
                case CLOSE_WINDOW -> {
                    WrapperPlayClientCloseWindow packet = new WrapperPlayClientCloseWindow(event);
                    try {
                        if(packet.getWindowId()!=0) {
                            var playerData = PlayerData.getPlayerData(event.getPlayer());

                            playerData.getInventoryPreSizes().remove(packet.getWindowId());
                        }
                        GUIContainer.getGuis().remove(GUIContainer.getGuis().stream().filter(gui -> gui.getData().windowId() == packet.getWindowId()).findFirst().orElseThrow(() -> new ShitMountainException("null")));
                    } catch(Exception ignored) {}
                }
            }
        }catch (Exception e){
            if(Config.getBoolean("debug")) {
                Logger.error("Error occured when processing: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
