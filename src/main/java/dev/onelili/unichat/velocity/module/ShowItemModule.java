package dev.onelili.unichat.velocity.module;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.util.adventure.NbtTagHolder;
import com.velocitypowered.api.proxy.Player;
import dev.onelili.unichat.velocity.handler.PlayerData;
import dev.onelili.unichat.velocity.message.Message;
import dev.onelili.unichat.velocity.util.ShitMountainException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShowItemModule extends PatternModule {
    @SuppressWarnings({"PatternValidation"})
    @Override
    public @Nonnull Component handle(@Nonnull Player sender) {
        PlayerData data = PlayerData.getPlayerData(sender);
        if(data == null || data.getHandItem() < 0)
            return new Message("&7[None]").toComponent();
        ItemStack item = data.getInventory().get(data.getHandItem() + 36);
        if(item == null)
            return new Message("&7[None]").toComponent();
        Map<Key, NbtTagHolder> tags = new ConcurrentHashMap<>();
        item.getOrCreateTag().getTags().forEach((key, nbt) -> tags.put(Key.key(key.toLowerCase(Locale.ROOT)), new NbtTagHolder(nbt)));
        System.out.println(tags);
        return Component.empty()
                .append(Component.text("[").color(NamedTextColor.GRAY))
                .append(
                        item.getComponent(ComponentTypes.ITEM_NAME).orElseThrow(() -> new ShitMountainException("Item name not found exceptionally"))
                                .hoverEvent(HoverEvent.showItem(item.getType().getName().key(), item.getAmount(), tags))
//                                .clickEvent(ClickEvent.)
                )
                .append(Component.text("]").color(NamedTextColor.GRAY));
    }
}
