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
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraftforge.common.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	public static StructureData getEdificeStructureData(Edifice edifice) {
		return structureDataRegistry.get(edifice);
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
	
	public static Pair<Validity, Map<Item, Pair<Integer, List<Pair<Integer, int[]>>>>> getMissingBlocks(World world, Edifice edifice, BlockPos pos, Rotation rotation) {
		StructureData structure = structureDataRegistry.get(edifice);
		Map<Item, Pair<Integer, List<Pair<Integer, int[]>>>> missing = new HashMap<>();
		boolean obstructed = false;
		int[] size = structure.size;
		for (int i = 0; i < structure.blocks.size(); i ++) {
			Pair<int[], Integer> block = structure.blocks.get(i);
			int[] position = null;
			int x = block.getFirst()[0];
			int y = block.getFirst()[1];
			int z = block.getFirst()[2];
			switch (rotation) {
				case NONE:
					position = new int[] {x, y, z};
					break;
				case CLOCKWISE_90:
					position = new int[] {z, y, size[0] - x - 1};
					break;
				case CLOCKWISE_180:
					position = new int[] {size[0] - x - 1, y, size[2] - z - 1};
					break;
				case COUNTERCLOCKWISE_90:
					position = new int[] {size[2] - z - 1, y, x};
					break;
			}
			BlockPos foundPos = pos.offset(position[0], position[1], position[2]).offset(edifice.getOffset());
			BlockState found = world.getBlockState(foundPos);
			BlockState ideal = structure.palette.get(block.getSecond());
			if (!found.getBlock().equals(ideal.getBlock()) || !found.getValues().equals(ideal.getValues())) {
				if (!found.isAir()) {
					obstructed = true;
				}
				if (!ideal.isAir()) {
					Item item = ideal.getBlock().asItem();
					Pair<Integer, List<Pair<Integer, int[]>>> current = missing.getOrDefault(item, new Pair<>(0, new ArrayList<>()));
					current.getSecond().add(new Pair<>(block.getSecond(), position));
					Pair<Integer, List<Pair<Integer, int[]>>> newValue = new Pair<>(current.getFirst() + 1, current.getSecond());
					missing.put(item, newValue);
				}
			}
		}
		if (obstructed) {
			return new Pair<>(Validity.OBSTRUCTED, missing);
		}
		if (missing.isEmpty()) {
			return new Pair<>(Validity.VALID, missing);
		}
		return new Pair<>(Validity.INCOMPLETE, missing);
	}
	
	public enum Validity {
		VALID,
		OBSTRUCTED,
		INCOMPLETE
	}
	
	public static class StructureData {
		
		private final int[] size;
		private List<Pair<int[], Integer>> blocks;
		private List<BlockState> palette;
		
		public List<BlockState> getPalette() {
			return palette;
		}
		
		public StructureData(CompoundNBT nbt) {
			ListNBT sizeList = nbt.getList("size", Constants.NBT.TAG_INT);
			size = new int[] {sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2)};
			ListNBT blocksList = nbt.getList("blocks", Constants.NBT.TAG_COMPOUND);
			blocks = blocksList.stream().map(e -> {
				ListNBT pos = ((CompoundNBT) e).getList("pos", Constants.NBT.TAG_INT);
				return new Pair<>(new int[] {pos.getInt(0), pos.getInt(1), pos.getInt(2)}, ((CompoundNBT) e).getInt("state"));
			}).collect(Collectors.toList());
			palette = nbt.getList("palette", Constants.NBT.TAG_COMPOUND).stream().map(e -> NBTUtil.readBlockState((CompoundNBT) e)).collect(Collectors.toList());
		}
	}
}