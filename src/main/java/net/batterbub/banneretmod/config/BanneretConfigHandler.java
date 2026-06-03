package net.batterbub.banneretmod.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.Map;

public class BanneretConfigHandler {

    public static final Map<Block, Integer> BLOCK_RADIUS_MAP = new HashMap<>();

    public static void reloadMappings() {
        BLOCK_RADIUS_MAP.clear();

        for (String entry : BanneretConfig.COMMON.blockRadiusMappings.get()) {
            String[] parts = entry.split("=");
            ResourceLocation id = ResourceLocation.parse(parts[0]);
            Block block = BuiltInRegistries.BLOCK.get(id);

            if (block != Blocks.AIR) {
                int radius = Integer.parseInt(parts[1]);
                BLOCK_RADIUS_MAP.put(block, radius);
            }
        }
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == BanneretConfig.COMMON_SPEC) {
            BanneretConfigHandler.reloadMappings();
        }
    }
}

