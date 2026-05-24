package net.batterbub.banneretmod.datagen;

import net.batterbub.banneretmod.BanneretMod;
import net.batterbub.banneretmod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, BanneretMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        banneretBSP(ModBlocks.BANNERET);
    }

    private void banneretBSP(DeferredBlock<?> deferredBlock) {
        directionalBlock(deferredBlock.get(), models().getExistingFile(ResourceLocation.parse("minecraft:block/air")));

    }
}
