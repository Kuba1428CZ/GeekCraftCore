package cz.kuba1428.coincraftcore.coincraftcore.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PravitkoCancelBreak implements Listener {
    @EventHandler
    public static void onPlacerBreakBlock(BlockBreakEvent event) {
        if (event.getPlayer().getItemInHand().getType() == Material.BONE) {
            event.setCancelled(true);
        }
    }
}
