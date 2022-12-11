package io.github.eisoptrophobia.kubryzltcraft.warfare.edifice;

import net.minecraft.item.ItemStack;

import java.util.List;

public class EdificeRadar extends Edifice {
	
	@Override
	public boolean isValid() {
		return false;
	}
	
	@Override
	public List<ItemStack> getMissingBlocks() {
		return null;
	}
	
	@Override
	public void placeBlock(ItemStack item) {
	
	}
}