package io.github.eisoptrophobia.kubryzltcraft.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.ModTileEntities;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.TileEntityKubryzlt;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.*;

public class RendererTileEntityKubryzlt extends TileEntityRenderer<TileEntityKubryzlt> {
	
	public RendererTileEntityKubryzlt(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}
	
	@Override
	public void render(TileEntityKubryzlt tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
		Territory territory = TerritoryManager.getManager().getTerritoryByBlockPos(tileEntity.getBlockPos());
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(KubryzltcraftTeamManager.getTerritoryTeamClient(territory).getKubryzltTexture());
		IVertexBuilder builder = buffer.getBuffer(RenderType.solid());
		
		for (Direction direction : Direction.values()) {
			Vector3f[] vertices = RenderUtils.blockVertices(direction);
			int normal = RenderUtils.packedNormal(vertices[0], vertices[1], vertices[2], vertices[3]);
			int[] vertexData = RenderUtils.vertexData(vertices[0], vertices[1], vertices[2], vertices[3], Color.WHITE.getRGB(), sprite, 0, 16, 0, 16, light, normal);
			BakedQuad quad = new BakedQuad(vertexData, 0, direction, sprite, true);
			
			builder.putBulkData(matrixStack.last(), quad, 1.0f, 1.0f, 1.0f, light, overlay);
		}
	}
	
	public static void register() {
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.KUBRYZLT.get(), RendererTileEntityKubryzlt::new);
	}
}