package cn.jason31416.chatx.util;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.*;
import com.github.retrooper.packetevents.util.adventure.NbtTagHolder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ItemUtil {
    public static @Nonnull HoverEvent.ShowItem showItem(@Nonnull ItemStack item) {
        Map<Key, NbtTagHolder> data = new HashMap<>();
        item.getComponent(ComponentTypes.LORE).ifPresent(lines -> {
            if(lines.getLines().isEmpty())
                return;
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            lines.getLines().forEach(line ->
                    array.add(GsonComponentSerializer.gson().serializeToTree(line)));
            json.add("extra", array);
            json.addProperty("text", "");
            NBTString finalData = new NBTString(new Gson().toJson(json));
            NBTList<@NotNull NBTString> packedData = new NBTList<@NotNull NBTString>(NBTType.STRING);
            packedData.addTag(finalData);
            data.put(Key.key("minecraft", "lore"), new NbtTagHolder(packedData));
        });
        item.getComponent(ComponentTypes.CUSTOM_NAME).ifPresent(name -> {
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            array.add(GsonComponentSerializer.gson().serializeToTree(name));
            json.add("extra", array);
            json.addProperty("text", "");
            NBTString finalData = new NBTString(new Gson().toJson(json));
            data.put(Key.key("minecraft", "custom_name"), new NbtTagHolder(finalData));
        });
        item.getComponent(ComponentTypes.ENCHANTMENTS).ifPresent(enchantments -> {
            if(enchantments.getEnchantments().isEmpty())
                return;

            NBTCompound compound = new NBTCompound();
            NBTCompound content = new NBTCompound();
            enchantments.forEach(enchantment ->
                content.setTag(enchantment.getKey().getName().toString(), new NBTInt(enchantment.getValue()))
            );
            compound.setTag("levels", content);
            data.put(Key.key("minecraft", "enchantments"), new NbtTagHolder(compound));
        });
        return HoverEvent.ShowItem.showItem(item.getType().getName(), item.getAmount(), data);
    }
}
