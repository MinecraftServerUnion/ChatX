package dev.onelili.unichat.velocity.module;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.component.ComponentType;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.velocitypowered.api.proxy.Player;
import dev.onelili.unichat.velocity.handler.PlayerData;
import dev.onelili.unichat.velocity.message.Message;
import dev.onelili.unichat.velocity.util.Logger;
import dev.onelili.unichat.velocity.util.Utils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.DataComponentValueConverterRegistry;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ShowItemModule extends PatternModule {
    @Override
    public @Nonnull Component handle(@Nonnull Player sender, boolean doProcess) {
        PlayerData data = PlayerData.getPlayerData(sender);
        if (data == null || data.getHandItem() < 0)
            return new Message("&7[&fNone&7]").toComponent();
        ItemStack item = data.getInventory().get(data.getHandItem() + 36);
        if (item == null)
            return new Message("&7[&fNone&7]").toComponent();

        Map<Key, DataComponentValue> dataComponents = new HashMap<>();
        JsonObject attributeModifiers = new JsonObject();
        attributeModifiers.add("modifiers", new JsonArray());
        attributeModifiers.addProperty("show_in_tooltip", item.getComponent(ComponentTypes.ATTRIBUTE_MODIFIERS).isPresent() && item.getComponent(ComponentTypes.ATTRIBUTE_MODIFIERS).get().isShowInTooltip());
        dataComponents.put(Key.key("minecraft:attribute_modifiers"), GsonDataComponentValue.gsonDataComponentValue(attributeModifiers));
        dataComponents.put(Key.key("minecraft:lore"), GsonDataComponentValue.gsonDataComponentValue(
                Utils.getNBTListJson(
                        (NBTList<?>) ((NBTCompound) item.getOrCreateTag().getTagOrNull("display")).getTagOrNull("Lore")
                )
        ));
        dataComponents.put(Key.key("minecraft:custom_name"), GsonDataComponentValue.gsonDataComponentValue(
                new JsonPrimitive(
                        ((NBTString) ((NBTCompound) item.getOrCreateTag().getTagOrNull("display")).getTagOrNull("Name")).getValue()
                )
        ));

        JsonObject out = new JsonObject();
        dataComponents.forEach((key1, val) -> {
            out.add(key1.asString(), ((GsonDataComponentValue) val).element());
        });
        Logger.error(new Gson().toJson(out));

        return Component.empty()
                .append(Component.text("[").color(NamedTextColor.GRAY))
                .append(
                        Component.text("test")
                                .hoverEvent(HoverEvent.showItem(item.getType().getName(), item.getAmount(), dataComponents))
                )
                .append(Component.text("]").color(NamedTextColor.GRAY));
    }
}
