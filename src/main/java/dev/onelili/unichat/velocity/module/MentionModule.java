package dev.onelili.unichat.velocity.module;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.StaticSound;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import com.google.common.util.concurrent.AtomicDouble;
import com.velocitypowered.api.proxy.Player;
import dev.onelili.unichat.velocity.UniChat;
import dev.onelili.unichat.velocity.handler.PlayerData;
import dev.onelili.unichat.velocity.message.Message;
import dev.onelili.unichat.velocity.util.Config;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Objects;

public class MentionModule {
    public static @Nonnull Component mention(@Nonnull Player player, @Nonnull Player sender, boolean doProcess) {
        if(doProcess) {
            BossBar bossBar = BossBar.bossBar(Message.getMessage("mention.bossbar-title").add("sender", sender.getUsername()).toComponent(), 1f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
            bossBar.addViewer(player);
            AtomicDouble progress = new AtomicDouble(1.0d);
            UniChat.getProxy().getScheduler().buildTask(UniChat.getInstance(), task -> {
                if (progress.addAndGet(-Config.getDouble("module.mention.bossbar-progress-decrease")) <= 0) {
                    bossBar.removeViewer(player);
                    task.cancel();
                    return;
                }
                bossBar.progress(progress.floatValue());
            }).repeat(Duration.ofMillis(50)).schedule();
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSoundEffect(
                    new StaticSound(new ResourceLocation(Config.getString("module.mention.sound-effect-id")), null),
                    SoundCategory.MASTER,
                    Objects.requireNonNull(PlayerData.getPlayerData(player)).toPacketPosition(),
                    (float) Config.getDouble("module.mention.sound-effect-volume"),
                    (float) Config.getDouble("module.mention.sound-effect-pitch")
            ));
        }
        return Component.text("@" + player.getUsername()).color(NamedTextColor.YELLOW);
    }
}
