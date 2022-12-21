package io.github.eisoptrophobia.kubryzltcraft.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KubryzltcraftHUD {
	
	@SubscribeEvent
	public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event) {
		MatrixStack matrixStack = event.getMatrixStack();
		FontRenderer font = Minecraft.getInstance().font;
		BlockPos pos = Minecraft.getInstance().player.blockPosition();
		Territory territory = TerritoryManager.getManager().getTerritoryByBlockPos(pos);
		String toRender = "[" + KubryzltcraftTeamManager.getTerritoryTeamClient(territory).getPrefix() + "] " + territory.getId() + " (" + territory.getMoraleClient() + ")";
		font.drawShadow(matrixStack, toRender, 4, 4, Color.WHITE.getRGB());
	}
}