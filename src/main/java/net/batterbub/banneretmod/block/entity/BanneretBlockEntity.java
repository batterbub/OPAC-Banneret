package net.batterbub.banneretmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.claims.api.IServerClaimsManagerAPI;

import java.util.UUID;

public class BanneretBlockEntity extends BlockEntity {
    private BlockPos blockPos = this.getBlockPos();
    private HolderLookup.Provider registries;
    public int baseColor;
    public BannerPatternLayers patterns = BannerPatternLayers.EMPTY;
    public UUID player;

    public BanneretBlockEntity(BlockPos pos, BlockState blockState) {
        //BlockEntityThings
        super(ModBlockEntities.BANNERET_BE.get(), pos, blockState);
    }

    public void setBaseColor(DyeColor bColor) {
        if (bColor == null) baseColor = DyeColor.BLACK.getId();
        else baseColor = bColor.getId();
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setPatterns(BannerPatternLayers bPatterns) {
        this.patterns = bPatterns;
        this.setChanged();
    }

    public void setPlayer(UUID bPlayer){
        this.player = bPlayer;
        this.setChanged();
    }

    public void unClaimAround(Level level, BlockPos pos, int radius){
        IServerClaimsManagerAPI api = OpenPACServerAPI.get(level.getServer()).getServerClaimsManager();
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {

                var result = api.tryToUnclaim(
                        level.dimension().location(),
                        player,
                        cx, cz,   // "from" chunk (center)
                        x, z,     // chunk to claim
                        false
                );
            }
        }
        //int debug = 1; //Only uncomment if you need to check result.
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.baseColor = tag.getInt("Base");
        if (tag.contains("Patterns")) {
            var ops = registries.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
            BannerPatternLayers.CODEC
                    .parse(ops, tag.get("Patterns"))
                    .result()
                    .ifPresent(layers -> this.patterns = layers);
        } else {
            this.patterns = BannerPatternLayers.EMPTY;
        }
        this.player = UUID.fromString(tag.getString("Owner"));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Base", this.baseColor);
        var ops = registries.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
        BannerPatternLayers.CODEC
                .encodeStart(ops, this.patterns)
                .result()
                .ifPresent(nbt -> tag.put("Patterns", nbt));
        tag.putString("Owner", this.player.toString());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
