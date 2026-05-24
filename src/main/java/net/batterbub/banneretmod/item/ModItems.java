package net.batterbub.banneretmod.item;

import net.batterbub.banneretmod.BanneretMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BanneretMod.MOD_ID);



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
