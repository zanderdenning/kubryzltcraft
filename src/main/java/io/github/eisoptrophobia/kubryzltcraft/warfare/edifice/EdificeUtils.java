package io.github.eisoptrophobia.kubryzltcraft.warfare.edifice;

import com.mojang.datafixers.util.Pair;
import io.github.eisoptrophobia.kubryzltcraft.ConfigCommon;
import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.block.entity.TileEntityEdificeCore;
import io.github.eisoptrophobia.kubryzltcraft.data.WorldSavedDataKubryzltcraftMap;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.io.IOException;
import java.util.*;
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
	
	public static void updateEdificesAround(World world, BlockPos pos) {
		Set<Map.Entry<UUID, Pair<World, BlockPos>>> toUpdate = WorldSavedDataKubryzltcraftMap.searchEdificeLocations(ServerLifecycleHooks.getCurrentServer().overworld(), world, pos, ConfigCommon.EDIFICE_UPDATE_RADIUS.get());
		for (Map.Entry<UUID, Pair<World, BlockPos>> edifice : toUpdate) {
			updateEdifice(edifice.getValue().getFirst(), edifice.getValue().getSecond());
		}
	}
	
	public static void updateEdifice(World world, BlockPos pos) {
		try {
			ServerWorld overworld = ServerLifecycleHooks.getCurrentServer().overworld();
			TileEntity tile = world.getBlockEntity(pos);
			if (tile instanceof TileEntityEdificeCore) {
				UUID uuid = ((TileEntityEdificeCore) tile).getUuid();
				Rotation rotation = ((TileEntityEdificeCore) tile).getRotation();
				IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(Exception::new);
				Edifice edifice = getEdificeByBlueprint(handler.getStackInSlot(0).getItem());
				if (edifice == null) {
					return;
				}
				StatusData statusData = getMissingBlocks(world, edifice, pos, rotation);
				WorldSavedDataKubryzltcraftMap.writeEdificeLocation(overworld, uuid, world, pos);
				WorldSavedDataKubryzltcraftMap.writeEdificeStatus(overworld, uuid, statusData);
				String territory = TerritoryManager.getManager().getTerritoryByBlockPos(world, pos).getId();
				WorldSavedDataKubryzltcraftMap.cleanKubryzltEdifices(overworld, uuid);
				WorldSavedDataKubryzltcraftMap.writeKubryzltEdifice(overworld, territory, uuid);
			}
		}
		catch (Exception e) {}
	}
	
	public static StatusData getMissingBlocksServer(World world, Edifice edifice, BlockPos pos) {
		TileEntity tile = world.getBlockEntity(pos);
		if (tile instanceof TileEntityEdificeCore) {
			UUID uuid = ((TileEntityEdificeCore) tile).getUuid();
			return WorldSavedDataKubryzltcraftMap.readEdificeStatus(ServerLifecycleHooks.getCurrentServer().overworld(), uuid);
		}
		StatusData defaultOut = new StatusData(edifice);
		defaultOut.setObstructed();
		return defaultOut;
	}
	
	public static StatusData getMissingBlocks(World world, Edifice edifice, BlockPos pos, Rotation rotation) {
		StructureData structure = structureDataRegistry.get(edifice);
		StatusData status = new StatusData(edifice);
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
					status.setObstructed();
				}
				if (!ideal.isAir()) {
					status.addMissingBlock(ideal, position, block.getSecond());
				}
			}
		}
		return status;
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
	
	public static class StatusData {
		
		private final Map<Item, List<BlockData>> missingBlocks = new HashMap<>();
		private boolean obstructed = false;
		private final Edifice edifice;
		
		public StatusData(Edifice edifice) {
			this.edifice = edifice;
		}
		
		public void addMissingBlock(BlockState state, int[] pos, int paletteIndex) {
			Item item = state.getBlock().asItem();
			List<BlockData> current = missingBlocks.getOrDefault(item, new ArrayList<>());
			current.add(new BlockData(paletteIndex, pos));
			missingBlocks.put(item, current);
		}
		
		public void setObstructed() {
			obstructed = true;
		}
		
		public Validity getValidity() {
			if (obstructed) {
				return Validity.OBSTRUCTED;
			}
			if (missingBlocks.size() == 0) {
				return Validity.VALID;
			}
			return Validity.INCOMPLETE;
		}
		
		public Map<Item, List<BlockData>> getMissingBlocks() {
			return missingBlocks;
		}
		
		public CompoundNBT save() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putBoolean("obstructed", false);
			nbt.putString("type", edifice.getRegistryName().toString());
			ListNBT missing = new ListNBT();
			for (Map.Entry<Item, List<BlockData>> missingItem : missingBlocks.entrySet()) {
				CompoundNBT item = new CompoundNBT();
				ListNBT blocks = new ListNBT();
				for (BlockData blockData : missingItem.getValue()) {
					CompoundNBT data = new CompoundNBT();
					data.putInt("paletteIndex", blockData.getPaletteIndex());
					int[] pos = blockData.getPos();
					data.putInt("x", pos[0]);
					data.putInt("y", pos[1]);
					data.putInt("z", pos[2]);
					blocks.add(data);
				}
				item.put("blocks", blocks);
				missing.add(item);
			}
			nbt.put("missing", missing);
			return nbt;
		}
		
		public static StatusData load(CompoundNBT nbt) {
			Edifice type = Kubryzltcraft.EDIFICE_REGISTRY.getValue(new ResourceLocation(nbt.getString("type")));
			StatusData statusData = new StatusData(type);
			List<BlockState> palette = structureDataRegistry.get(type).getPalette();
			if (nbt.getBoolean("obstructed")) {
				statusData.setObstructed();
			}
			ListNBT missing = nbt.getList("missing", Constants.NBT.TAG_COMPOUND);
			for (INBT missingItem : missing) {
				CompoundNBT item = (CompoundNBT) missingItem;
				ListNBT blocks = item.getList("blocks", Constants.NBT.TAG_COMPOUND);
				for (INBT block : blocks) {
					CompoundNBT blockCompound = (CompoundNBT) block;
					int[] pos = new int[] { blockCompound.getInt("x"), blockCompound.getInt("y"), blockCompound.getInt("z") };
					int paletteIndex = blockCompound.getInt("paletteIndex");
					statusData.addMissingBlock(palette.get(paletteIndex), pos, paletteIndex);
				}
			}
			return statusData;
		}
		
		public static class BlockData {
			
			private final int paletteIndex;
			private final int[] pos;
			
			public BlockData(int paletteIndex, int[] pos) {
				this.paletteIndex = paletteIndex;
				this.pos = pos;
			}
			
			public int getPaletteIndex() {
				return paletteIndex;
			}
			
			public int[] getPos() {
				return pos;
			}
		}
	}
}