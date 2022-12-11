package io.github.eisoptrophobia.kubryzltcraft.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.container.ContainerEdificeCore;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenEdificeCore extends ContainerScreen<ContainerEdificeCore> {
	
	private final ResourceLocation GUI = new ResourceLocation(Kubryzltcraft.MOD_ID, "textures/gui/container/edifice_core.png");
	
	public ScreenEdificeCore(ContainerEdificeCore container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		minecraft.getTextureManager().bind(GUI);
		blit(matrixStack, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());
	}
}