package dev.onelili.unichat.velocity.util;

import dev.onelili.unichat.velocity.gui.GUIData;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChaceData {
    @Getter
    private static final TimedMap<UUID, GUIData> chacedInventories = new TimedConcurrentHashMap<>(5, TimeUnit.MINUTES);
}
