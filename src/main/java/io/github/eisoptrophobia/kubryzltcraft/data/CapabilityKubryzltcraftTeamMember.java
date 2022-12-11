package io.github.eisoptrophobia.kubryzltcraft.data;

import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityKubryzltcraftTeamMember {
	
	@CapabilityInject(IKubryzltcraftTeamMember.class)
	public static Capability<IKubryzltcraftTeamMember> INSTANCE = null;
	
	public static void register() {
		CapabilityManager.INSTANCE.register(IKubryzltcraftTeamMember.class, new Storage(), DefaultKubryzltcraftTeamMember::new);
	}
	
	public static class Storage implements Capability.IStorage<IKubryzltcraftTeamMember> {
		
		@Nullable
		@Override
		public INBT writeNBT(Capability<IKubryzltcraftTeamMember> capability, IKubryzltcraftTeamMember instance, Direction side) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("kubryzltcraft_team", instance.getTeam().getId());
			return nbt;
		}
		
		@Override
		public void readNBT(Capability<IKubryzltcraftTeamMember> capability, IKubryzltcraftTeamMember instance, Direction side, INBT nbt) {
			String teamId = ((CompoundNBT) nbt).getString("kubryzltcraft_team");
			instance.setTeam(KubryzltcraftTeamManager.getTeamById(teamId));
		}
	}
}