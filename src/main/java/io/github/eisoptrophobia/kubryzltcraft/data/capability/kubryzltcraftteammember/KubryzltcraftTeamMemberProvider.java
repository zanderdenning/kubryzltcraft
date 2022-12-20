package io.github.eisoptrophobia.kubryzltcraft.data.capability.kubryzltcraftteammember;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KubryzltcraftTeamMemberProvider implements ICapabilitySerializable<CompoundNBT> {
	
	private final DefaultKubryzltcraftTeamMember kubryzltcraftTeamMember = new DefaultKubryzltcraftTeamMember();
	private final LazyOptional<DefaultKubryzltcraftTeamMember> kubryzltcraftTeamMemberOptional = LazyOptional.of(() -> kubryzltcraftTeamMember);
	
	public void invalidate() {
		kubryzltcraftTeamMemberOptional.invalidate();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return kubryzltcraftTeamMemberOptional.cast();
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		if (CapabilityKubryzltcraftTeamMember.INSTANCE == null) {
			return new CompoundNBT();
		}
		return (CompoundNBT) CapabilityKubryzltcraftTeamMember.INSTANCE.writeNBT(kubryzltcraftTeamMember, null);
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (CapabilityKubryzltcraftTeamMember.INSTANCE != null) {
			CapabilityKubryzltcraftTeamMember.INSTANCE.readNBT(kubryzltcraftTeamMember, null, nbt);
		}
	}
}