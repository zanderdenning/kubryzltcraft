package io.github.eisoptrophobia.kubryzltcraft.warfare.edifice;

import com.mojang.datafixers.util.Pair;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EdificeUtils {
	
	private static final HashMap<Edifice, StructureData> structureDataRegistry = new HashMap<>();
	private static final HashMap<Item, Edifice> blueprintRegistry = new HashMap<>();
	
	public static void initRegistry() {
		for (Edifice edifice : Kubryzltcraft.EDIFICE_REGISTRY.getValues()) {
			structureDataRegistry.put(edifice, new StructureData(getEdificeNBT(edifice)));
			blueprintRegistry.put(edifice.getBlueprint(), edifice);
		}
	}
	
	public static Edifice getEdificeByBlueprint(Item blueprint) {
		return blueprintRegistry.get(blueprint);
	}
	
	public static CompoundNBT getEdificeNBT(Edifice edifice) {
		ResourceLocation edificeResource = edifice.getRegistryName();
		String path = "data/" + edificeResource.getNamespace() + "/structures/edifice/" + edificeResource.getPath() + ".nbt";
		try {
			return CompressedStreamTools.readCompressed(EdificeUtils.class.getClassLoader().getResourceAsStream(path));
		}
		catch (IOException e) {
			return null;
		}
	}
	
	public static List<Template.BlockInfo> getEdificeBlocks(World world, Edifice edifice, BlockPos pos, Rotation rotation) {
		return null;
	}
	
	public static class StructureData {
		
		private final int[] size;
		private List<Pair<int[], Integer>> blocks;
		private List<BlockState> palette;
		
		public StructureData(CompoundNBT nbt) {
			size = nbt.getIntArray("size");
			ListNBT blocksList = nbt.getList("blocks", 10);
			blocks = blocksList.stream().map(e -> new Pair<>(((CompoundNBT) e).getIntArray("pos"), ((CompoundNBT) e).getInt("state"))).collect(Collectors.toList());
			palette = nbt.getList("palette", 10).stream().map(e -> NBTUtil.readBlockState((CompoundNBT) e)).collect(Collectors.toList());
		}
	}
}