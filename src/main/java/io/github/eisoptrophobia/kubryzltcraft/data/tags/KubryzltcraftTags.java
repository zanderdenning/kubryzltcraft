package io.github.eisoptrophobia.kubryzltcraft.data.tags;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class KubryzltcraftTags {
	
	public static class Items {
		
		public static final Tags.IOptionalNamedTag<Item> BLUEPRINT = createTag("blueprints");
		
		private static Tags.IOptionalNamedTag<Item> createTag(String name) {
			return ItemTags.createOptional(new ResourceLocation(Kubryzltcraft.MOD_ID, name));
		}
	}
}