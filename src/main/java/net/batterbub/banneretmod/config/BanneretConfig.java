package net.batterbub.banneretmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForgeConfig;

import java.util.List;

public class BanneretConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {

        public final ModConfigSpec.IntValue baseClaimRadius;
        public final ModConfigSpec.ConfigValue<List<? extends String>> blockRadiusMappings;

        public Common(ModConfigSpec.Builder builder) {
            builder.push("banneret");

            baseClaimRadius = builder
                    .comment("Default radius (in chunks) a banneret claims.",
                            "Default: 1",
                            "Range: 1-32")
                    .defineInRange("baseClaimRadius", 1, 1, 32);

            blockRadiusMappings = builder
                    .comment("Blocks that give additional chunk radius when converting bannerets.",
                            "For example: If bannerets give 1 chunk by defualt, but campfires add another to the radius, then the banneret will claim 9 total",
                            "Default: \"minecraft:campfire=1\", \"minecraft:anvil=2\", \"minecraft:note_block=3\", \"minecraft:beacon=4\"",
                            "Format: block_id=radius")
                    .defineListAllowEmpty(
                            List.of("blockRadiusMappings"),
                            () -> List.of("minecraft:campfire=1", "minecraft:anvil=2", "minecraft:note_block=3", "minecraft:beacon=4"),
                            obj -> obj instanceof String
                    );

            builder.pop();
        }
    }
}