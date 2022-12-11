package io.github.eisoptrophobia.kubryzltcraft.data;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventAttachCapabilities {
	
	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof CreatureEntity) {
			KubryzltcraftTeamMemberProvider provider = new KubryzltcraftTeamMemberProvider();
			event.addCapability(new ResourceLocation(Kubryzltcraft.MOD_ID, "kubryzltcraft_team"), provider);
			event.addListener(provider::invalidate);
		}
	}
}