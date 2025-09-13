package dev.onelili.unichat.velocity.module;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.velocitypowered.api.proxy.Player;
import dev.onelili.unichat.velocity.gui.GUIData;
import dev.onelili.unichat.velocity.util.ChaceData;
import dev.onelili.unichat.velocity.util.PlayerData;
import dev.onelili.unichat.velocity.message.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ShowItemModule extends PatternModule {
    @Override
    public @Nonnull Component handle(@Nonnull Player sender, boolean doProcess) {
        PlayerData data = PlayerData.getPlayerData(sender);
        if (data == null || data.getHandItem() < 0)
            return new Message("&7[&fNone&7]").toComponent();
        ItemStack item = data.getInventory().get(data.getHandItem() + 36);
        if (item == null)
            return new Message("&7[&fNone&7]").toComponent();

        UUID uuid = UUID.randomUUID();
        ChaceData.getChacedInventories().put(uuid, GUIData.ofSingleItem(item, null/*改为title*/));

        return Component.empty()
                .append(Component.text("[").color(NamedTextColor.GRAY))
                .append(
                        item.getComponentOr(ComponentTypes.CUSTOM_NAME, item.getComponentOr(ComponentTypes.ITEM_NAME, Component.text("Unknown")))
                                .hoverEvent(HoverEvent.showText(Message.getMessage("show-item.hover-text").toComponent()))
                                .clickEvent(ClickEvent.runCommand("/check-item " + uuid))
                )
                .append(Component.text("]").color(NamedTextColor.GRAY));
    }
}
