package cn.jason31416.chatx.handler.packetwrapper;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;

import javax.annotation.Nonnull;

public class WrapperPlayServerSetSlot extends com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot {
    public WrapperPlayServerSetSlot(PacketSendEvent event) {
        super(event);
    }

    @Override
    public @Nonnull ItemStack readItemStack() {
        return ItemStackSerialization.read(this);
    }
}
