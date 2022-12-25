package io.github.eisoptrophobia.kubryzltcraft.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.ModTileEntities;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.TileEntityEdificeCore;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.Edifice;
import io.github.eisoptrophobia.kubryzltcraft.warfare.edifice.EdificeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;
import java.util.Map;

public class RendererTileEntityEdificeCore extends TileEntityRenderer<TileEntityEdificeCore> {
	
	public RendererTileEntityEdificeCore(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@Override
	public void render(TileEntityEdificeCore tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
		tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			Edifice edifice = EdificeUtils.getEdificeByBlueprint(handler.getStackInSlot(0).getItem());
			if (edifice != null) {
				BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
				EdificeUtils.StatusData missing = EdificeUtils.getMissingBlocks(tileEntity.getLevel(), edifice, tileEntity.getBlockPos(), Rotation.NONE);
				if (missing.getValidity() == EdificeUtils.Validity.INCOMPLETE) {
					EdificeUtils.StructureData structureData = EdificeUtils.getEdificeStructureData(edifice);
					for (Map.Entry<Item, List<EdificeUtils.StatusData.BlockData>> item : missing.getMissingBlocks().entrySet()) {
						for (EdificeUtils.StatusData.BlockData block : item.getValue()) {
							int[] pos = block.getPos();
							Vector3i offset = edifice.getOffset();
							renderBlock(blockRenderer, matrixStack, buffer, overlay, structureData.getPalette().get(block.getPaletteIndex()), pos[0] + offset.getX(), pos[1] + offset.getY(), pos[2] + offset.getZ());
						}
					}
				}
			}
		});
	}
	
	private void renderBlock(BlockRendererDispatcher blockRenderer, MatrixStack matrixStack, IRenderTypeBuffer buffer, int overlay, BlockState blockState, double x, double y, double z) {
		matrixStack.pushPose();
		matrixStack.translate(x + 0.35, y + 0.35, z + 0.35);
		matrixStack.scale(0.3f, 0.3f, 0.3f);
		blockRenderer.renderBlock(blockState, matrixStack, buffer, 240, overlay, new ModelDataMap.Builder().build());
		matrixStack.popPose();
	}
	
	@Override
	public boolean shouldRenderOffScreen(TileEntityEdificeCore tileEntity) {
		return true;
	}
	
	public static void register() {
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.EDIFICE_CORE.get(), RendererTileEntityEdificeCore::new);
	}
}