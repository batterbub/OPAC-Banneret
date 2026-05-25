package net.batterbub.banneretmod.block.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.batterbub.banneretmod.block.BanneretBlock;
import net.batterbub.banneretmod.block.entity.BanneretBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Field;

public class BanneretRenderer implements BlockEntityRenderer<BanneretBlockEntity> {
    private final BannerRenderer bannerRenderer;
    public BanneretRenderer(BlockEntityRendererProvider.Context context){
        this.bannerRenderer = new BannerRenderer(context);
    }

    @Override
    public void render(BanneretBlockEntity banneretBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);

        BlockState state = banneretBlockEntity.getBlockState();

        Direction dir = state.getValue(BanneretBlock.FACING);
        poseStack.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));

        poseStack.translate(-0.5F, 0.0F, -0.5F);

        BannerBlockEntity dummy = new BannerBlockEntity(
                banneretBlockEntity.getBlockPos(),
                Blocks.WHITE_BANNER.defaultBlockState(),
                DyeColor.byId(banneretBlockEntity.baseColor)
        );

        try {
            Field f = BannerBlockEntity.class.getDeclaredField("patterns");
            f.setAccessible(true);
            f.set(dummy, banneretBlockEntity.patterns);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject banner patterns", e);
        }

        this.bannerRenderer.render(dummy, partialTick, poseStack, multiBufferSource, packedLight, packedOverlay);

        poseStack.popPose();
    }

}
