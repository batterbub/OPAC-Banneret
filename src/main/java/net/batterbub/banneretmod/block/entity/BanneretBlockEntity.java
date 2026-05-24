package net.batterbub.banneretmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BanneretBlockEntity extends BlockEntity {
    private BlockPos blockPos = this.getBlockPos();
    private HolderLookup.Provider registries;
    public int baseColor;
    //private BannerPatternLayers patterns;

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

    public BannerPatternLayers setPatterns(BannerPatternLayers patterns) {
        return BannerPatternLayers.EMPTY;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.baseColor = tag.getInt("Base");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Base", this.baseColor);
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
