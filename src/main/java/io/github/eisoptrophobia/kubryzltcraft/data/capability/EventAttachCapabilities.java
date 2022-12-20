package io.github.eisoptrophobia.kubryzltcraft.data.capability;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.data.capability.kubryzltcraftteammember.KubryzltcraftTeamMemberProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventAttachCapabilities {
	
	@SubscribeEvent
	public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof MobEntity || event.getObject() instanceof PlayerEntity) {
			KubryzltcraftTeamMemberProvider provider = new KubryzltcraftTeamMemberProvider();
			event.addCapability(new ResourceLocation(Kubryzltcraft.MOD_ID, "kubryzltcraft_team"), provider);
			event.addListener(provider::invalidate);
		}
	}
}