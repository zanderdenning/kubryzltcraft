package io.github.eisoptrophobia.kubryzltcraft.warfare.edifice;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public abstract class Edifice extends ForgeRegistryEntry<Edifice> {
	
	public abstract boolean isValid();
	
	public abstract List<ItemStack> getMissingBlocks();
	
	public abstract void placeBlock(ItemStack item);
	
	public void onTick() {}
	
	public void onSiegeTick(int minute) {}
	
	public void onResourceTick(int hour) {}
	
	public void onDayTick(int day) {}
	
	public void onBuild(PlayerEntity player) {}
	
	public void onDestroy() {}
	
	public void onDestroy(PlayerEntity player) {}
	
	public void onMobSpawn(LivingSpawnEvent event) {}
	
	public void onInteract(PlayerEntity player) {}
}