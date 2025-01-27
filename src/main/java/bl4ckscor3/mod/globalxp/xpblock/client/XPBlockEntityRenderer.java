package bl4ckscor3.mod.globalxp.xpblock.client;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import bl4ckscor3.mod.globalxp.Configuration;
import bl4ckscor3.mod.globalxp.xpblock.XPBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;

public class XPBlockEntityRenderer implements BlockEntityRenderer<XPBlockEntity> {
	private ItemStack emerald = new ItemStack(Items.EMERALD, 1);

	public XPBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(XPBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (be == null)
			return;

		Minecraft mc = Minecraft.getInstance();

		poseStack.pushPose();

		if (be.getBlockPos() != null && mc.hitResult instanceof BlockHitResult hitResult && be.getBlockPos().equals(hitResult.getBlockPos())) {
			double y = 1.5D;

			if (Configuration.CLIENT.renderXPInfo.get()) {
				renderTextAboveBlock(mc, poseStack, buffer, combinedLight, Component.literal((int) be.getStoredLevels() + " (" + be.getStoredXP() + ")"), y);
				y += 0.25D;
			}

			if (be.hasCustomName() && Configuration.CLIENT.renderCustomName.get())
				renderTextAboveBlock(mc, poseStack, buffer, combinedLight, be.getCustomName(), y);
		}

		float time = be.getLevel().getLevelData().getGameTime() + partialTicks;
		double offset = Math.sin(time * Configuration.CLIENT.bobSpeed.get() / 8.0D) / 10.0D;
		BakedModel model = mc.getItemRenderer().getModel(emerald, be.getLevel(), null, 0);

		poseStack.translate(0.5D, 0.4D + offset, 0.5D);
		poseStack.mulPose(Axis.YP.rotationDegrees(time * 4.0F * Configuration.CLIENT.spinSpeed.get().floatValue()));
		mc.getItemRenderer().render(emerald, ItemDisplayContext.GROUND, false, poseStack, buffer, combinedLight, combinedOverlay, model);
		poseStack.popPose();
	}

	public void renderTextAboveBlock(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, Component text, double y) {
		float opacity = mc.options.getBackgroundOpacity(0.25F);
		int backgroundColor = (int) (opacity * 255.0F) << 24;
		float halfWidth = -mc.font.width(text) / 2;
		Matrix4f positionMatrix;

		poseStack.pushPose();
		poseStack.translate(0.5D, y, 0.5D);
		poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
		poseStack.scale(-0.025F, -0.025F, 0.025F);
		positionMatrix = poseStack.last().pose();
		mc.font.drawInBatch(text, halfWidth, 0, 553648127, false, positionMatrix, buffer, DisplayMode.SEE_THROUGH, backgroundColor, combinedLight);
		mc.font.drawInBatch(text, halfWidth, 0, -1, false, positionMatrix, buffer, DisplayMode.NORMAL, 0, combinedLight);
		poseStack.popPose();
	}
}
