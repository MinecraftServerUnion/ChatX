package dev.onelili.unichat.velocity.module;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.util.adventure.NbtTagHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import dev.onelili.unichat.velocity.handler.PlayerData;
import dev.onelili.unichat.velocity.message.Message;
import dev.onelili.unichat.velocity.util.ShitMountainException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShowItemModule extends PatternModule {
    @Override
    public @Nonnull Component handle(@Nonnull Player sender, boolean doProcess) {
        PlayerData data = PlayerData.getPlayerData(sender);
        if(data == null || data.getHandItem() < 0)
            return new Message("&7[None]").toComponent();
        ItemStack item = data.getInventory().get(data.getHandItem() + 36);
        if(item == null)
            return new Message("&7[None]").toComponent();
//        Map<Key, DataComponentValue> tags = new ConcurrentHashMap<>();
//        JsonObject obj = new JsonObject();
//        JsonObject objDisplay = new JsonObject();
//        JsonArray objDisplayLore = new JsonArray();
//        if(item.getComponent(ComponentTypes.LORE).isPresent())
//            for(Component loreLine : item.getComponent(ComponentTypes.LORE).get().getLines())
//                objDisplayLore.add(GsonComponentSerializer.gson().serialize(loreLine));
//
//        objDisplay.add("Lore", objDisplayLore);
//        obj.addProperty("Unbreakable", 1);
//        obj.add("display", objDisplay);
//
//        tags.put(Key.key("minecraft:custom_data"), GsonDataComponentValue.gsonDataComponentValue(obj));
//        tags.put(Key.key("minecraft:unbreakable"), GsonDataComponentValue.gsonDataComponentValue(new JsonObject()));
//        //        tags.put(Key.key("minecraft", "custom_name"), GsonDataComponentValue.gsonDataComponentValue(temp));
//
//
//        HoverEvent.ShowItem showItem = HoverEvent.ShowItem.showItem(() -> item.getType().getName().key(), item.getAmount(), (Map<Key, ? extends DataComponentValue>) null);
        return Component.empty()
                .append(Component.text("[").color(NamedTextColor.GRAY))
                .append(
                        item.getComponent(ComponentTypes.CUSTOM_NAME)
                                .orElseGet(()->item.getComponent(ComponentTypes.ITEM_NAME)
                                        .orElseThrow(()->new ShitMountainException("Item name not present!")))
//                                .hoverEvent(HoverEvent.showItem(showItem))
//                                .clickEvent(ClickEvent.)
                )
                .append(Component.text("]").color(NamedTextColor.GRAY));
    }
}
