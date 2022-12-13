package io.github.eisoptrophobia.kubryzltcraft.warfare.edifice;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public abstract class Edifice extends ForgeRegistryEntry<Edifice> {
	
	public abstract Item getBlueprint();
	
	public abstract CompoundNBT getNBT();
	
	public abstract Vector3i getOffset();
	
	public abstract boolean isValid(BlockPos pos, World world);
	
	public abstract List<ItemStack> getMissingBlocks(BlockPos pos, World world);
	
	public abstract void placeBlock(BlockPos pos, World world, ItemStack item);
	
	public void onTick(BlockPos pos, World world) {}
	
	public void onSiegeTick(BlockPos pos, World world, int minute) {}
	
	public void onResourceTick(BlockPos pos, World world, int hour) {}
	
	public void onDayTick(BlockPos pos, World world, int day) {}
	
	public void onBuild(BlockPos pos, World world, PlayerEntity player) {}
	
	public void onDestroy(BlockPos pos, World world) {}
	
	public void onDestroy(BlockPos pos, World world, PlayerEntity player) {}
	
	public void onMobSpawn(BlockPos pos, World world, LivingSpawnEvent event) {}
	
	public void onInteract(BlockPos pos, World world, PlayerEntity player) {}
}