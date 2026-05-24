package net.batterbub.banneretmod.block.entity;

import net.batterbub.banneretmod.BanneretMod;
import net.batterbub.banneretmod.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BanneretMod.MOD_ID);

    public static final Supplier<BlockEntityType<BanneretBlockEntity>> BANNERET_BE =
            BLOCK_ENTITIES.register("banneret_be", () -> BlockEntityType.Builder.of(
                    BanneretBlockEntity::new, ModBlocks.BANNERET.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register((eventBus));
    }
}
