package io.github.eisoptrophobia.kubryzltcraft.server;

import io.github.eisoptrophobia.kubryzltcraft.Kubryzltcraft;
import io.github.eisoptrophobia.kubryzltcraft.network.MessageKubryzltcraftMapSync;
import io.github.eisoptrophobia.kubryzltcraft.network.Network;
import io.github.eisoptrophobia.kubryzltcraft.warfare.Territory;
import io.github.eisoptrophobia.kubryzltcraft.warfare.TerritoryManager;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeam;
import io.github.eisoptrophobia.kubryzltcraft.warfare.team.KubryzltcraftTeamManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Kubryzltcraft.MOD_ID)
public class EventPlayerLoggedIn {
	
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity) {
			Map<String, KubryzltcraftTeam> territoryData = TerritoryManager.getManager().getTerritories().stream().collect(Collectors.toMap(Territory::getId, KubryzltcraftTeamManager::getTerritoryTeamServer));
			Map<String, Integer> moraleData = TerritoryManager.getManager().getTerritories().stream().collect(Collectors.toMap(Territory::getId, Territory::getMoraleServer));
			Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> ((ServerPlayerEntity) event.getPlayer())), new MessageKubryzltcraftMapSync(territoryData, moraleData));
		}
	}
}