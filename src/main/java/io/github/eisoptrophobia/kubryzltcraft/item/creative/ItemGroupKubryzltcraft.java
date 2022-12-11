package io.github.eisoptrophobia.kubryzltcraft.item.creative;

import io.github.eisoptrophobia.kubryzltcraft.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupKubryzltcraft extends ItemGroup {
	
	public static final ItemGroupKubryzltcraft INSTANCE = new ItemGroupKubryzltcraft(ItemGroup.TABS.length, "kubryzltcraft");
	
	public ItemGroupKubryzltcraft(int index, String label) {
		super(index, label);
	}
	
	@Override
	public ItemStack makeIcon() {
		return new ItemStack(ModBlocks.KUBRYZLT.get());
	}
}