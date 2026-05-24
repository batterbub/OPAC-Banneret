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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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

        DyeColor color = DyeColor.byId(banneretBlockEntity.baseColor);

        // 2) Map DyeColor -> correct banner block
        BlockState bannerState = switch (color) {
            case WHITE -> Blocks.WHITE_BANNER.defaultBlockState();
            case ORANGE -> Blocks.ORANGE_BANNER.defaultBlockState();
            case MAGENTA -> Blocks.MAGENTA_BANNER.defaultBlockState();
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_BANNER.defaultBlockState();
            case YELLOW -> Blocks.YELLOW_BANNER.defaultBlockState();
            case LIME -> Blocks.LIME_BANNER.defaultBlockState();
            case PINK -> Blocks.PINK_BANNER.defaultBlockState();
            case GRAY -> Blocks.GRAY_BANNER.defaultBlockState();
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_BANNER.defaultBlockState();
            case CYAN -> Blocks.CYAN_BANNER.defaultBlockState();
            case PURPLE -> Blocks.PURPLE_BANNER.defaultBlockState();
            case BLUE -> Blocks.BLUE_BANNER.defaultBlockState();
            case BROWN -> Blocks.BROWN_BANNER.defaultBlockState();
            case GREEN -> Blocks.GREEN_BANNER.defaultBlockState();
            case RED -> Blocks.RED_BANNER.defaultBlockState();
            case BLACK -> Blocks.BLACK_BANNER.defaultBlockState();
        };

        BannerBlockEntity dummy = new BannerBlockEntity(
                banneretBlockEntity.getBlockPos(),
                bannerState
        );

        this.bannerRenderer.render(dummy, partialTick, poseStack, multiBufferSource, packedLight, packedOverlay);

        poseStack.popPose();
    }

}
