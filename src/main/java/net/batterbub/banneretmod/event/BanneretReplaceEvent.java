package net.batterbub.banneretmod.event;

import net.batterbub.banneretmod.BanneretMod;
import net.batterbub.banneretmod.block.BanneretBlock;
import net.batterbub.banneretmod.block.ModBlocks;
import net.batterbub.banneretmod.block.entity.BanneretBlockEntity;
import net.batterbub.banneretmod.config.BanneretConfig;
import net.batterbub.banneretmod.config.BanneretConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import xaero.pac.OpenPartiesAndClaims;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.claims.result.api.AreaClaimResult;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.claims.api.IServerClaimsManagerAPI;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
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
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        Block belowBlock = belowState.getBlock();

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
        claimAround(level,pos,event.getEntity(),
                BanneretConfig.COMMON.baseClaimRadius.get() + BanneretConfigHandler.BLOCK_RADIUS_MAP.getOrDefault(belowBlock, 0));

        // Copy banner data
        banneret.setBaseColor(oldBanner.getBaseColor());
        banneret.setPatterns(oldBanner.getPatterns());
        banneret.setChanged();
        level.sendBlockUpdated(pos, banneret.getBlockState(), banneret.getBlockState(), 3);

        if (!player.isCreative()) {
            stack.shrink(1);
        }

        level.addParticle(ParticleTypes.NOTE,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                1, 0, 1);

        level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 64f, 1f);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    public static void claimAround(Level level, BlockPos pos, Player player, int radius){
        IServerClaimsManagerAPI api = OpenPACServerAPI.get(level.getServer()).getServerClaimsManager();

        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        var dim = level.dimension().location();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {

                if(api.get(dim, x,z) != null) continue;

                var result = api.claim(
                        dim,
                        player.getUUID(),
                        0,
                        //cx, cz,   // "from" chunk (center) to be used in tryToClaim
                        x, z,     // chunk to claim
                        false
                );
            }
        }
        //int debug = 1; //Only uncomment if you need to check result.
    }
}
