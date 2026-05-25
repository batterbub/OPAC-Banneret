package net.batterbub.banneretmod.event;

import net.batterbub.banneretmod.BanneretMod;
import net.batterbub.banneretmod.block.BanneretBlock;
import net.batterbub.banneretmod.block.ModBlocks;
import net.batterbub.banneretmod.block.entity.BanneretBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import xaero.pac.OpenPartiesAndClaims;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.claims.api.IServerClaimsManagerAPI;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

@EventBusSubscriber(modid = BanneretMod.MOD_ID)
public class BanneretReplaceEvent {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        Level level = event.getLevel();
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (level.isClientSide()) return;
        if(!stack.is(Items.GLOW_INK_SAC)) return;
        if (!(state.getBlock() instanceof BannerBlock)) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BannerBlockEntity oldBanner)) return;

        float rot = RotationSegment.convertToDegrees(state.getValue(BannerBlock.ROTATION));
        Direction facing = Direction.fromYRot(rot);

        level.setBlock(
                pos,
                ModBlocks.BANNERET.get().defaultBlockState()
                        .setValue(BanneretBlock.FACING, facing),
                3
        );
        BlockEntity newBe = level.getBlockEntity(pos);
        if (!(newBe instanceof BanneretBlockEntity banneret)) return;

        banneret.player = UUID.fromString(event.getEntity().getStringUUID());
        claimAround(level,pos,event.getEntity());

        // Copy banner data
        banneret.setBaseColor(oldBanner.getBaseColor());
        banneret.setPatterns(oldBanner.getPatterns());
        banneret.setChanged();
        level.sendBlockUpdated(pos, banneret.getBlockState(), banneret.getBlockState(), 3);

        if (!player.isCreative()) {
            stack.shrink(1);
        }

        level.playSound(null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1f, 1f);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    public static void claimAround(Level level, BlockPos pos, Player player){
        IServerClaimsManagerAPI api = OpenPACServerAPI.get(level.getServer()).getServerClaimsManager();
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        api.tryToClaimArea(
                level.dimension().location(),
                player.getUUID(),
                0,
                player.chunkPosition().x,
                player.chunkPosition().z,
                cx-1,
                cz-1,
                cx+1,
                cz+1,
                false);
    }
}
