package net.batterbub.banneretmod.datagen;

import net.batterbub.banneretmod.BanneretMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BanneretMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
