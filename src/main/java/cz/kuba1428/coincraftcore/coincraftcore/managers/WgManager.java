package cz.kuba1428.coincraftcore.coincraftcore.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class WgManager {
    public static WorldGuard worldGuard = WorldGuard.getInstance();

    public static boolean isBlockInRegion(int x, int y, int z, String worldName) {
        World world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(worldName)));
        Location loc = new Location(world, x, y, z);
        ApplicableRegionSet regions = worldGuard.getPlatform().getRegionContainer().createQuery().getApplicableRegions(loc);
        for (ProtectedRegion region : regions) {
            if (region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                return true; // Block is inside a region
            }
        }
        return false;
    }
    public static boolean doesExist(String regionId) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            ProtectedRegion region = regionContainer.get(BukkitAdapter.adapt(world)).getRegion(regionId);
            if (region != null) {
                return true;
            }
        }

        return false;
    }
    public static void createRegion(Player player, String regionId, int x1, int y1, int z1, int x2, int y2, int z2) throws StorageException {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        // Get the world
        World world = BukkitAdapter.adapt(player.getWorld());
        // Calculate the min and max coordinates for the region
        BlockVector3 min = BlockVector3.at(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        BlockVector3 max = BlockVector3.at(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
        // Create the region
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);
        // Set any region properties if needed
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        RegionManager regionManager = regionContainer.get(world);
        assert regionManager != null;
        regionManager.addRegion(region);
        regionManager.save();
        player.sendMessage("Region " + regionId + " has been created!");
    }
    public static void deleteRegion(Player player, String regionId) throws StorageException {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        // Get the world
        World world = BukkitAdapter.adapt(player.getWorld());

        RegionManager regionManager = regionContainer.get(world);
        assert regionManager != null;
        regionManager.removeRegion(regionId);
        regionManager.save();
        player.sendMessage("Region " + regionId + " has been created!");
    }
    public static boolean isOwner(Player player, String region_name){
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(player.getWorld());
        RegionManager regionManager = regionContainer.get(world);
        assert regionManager != null;
        ProtectedRegion region = regionManager.getRegion(region_name);
        assert region != null;
        return region.getOwners().contains(player.getUniqueId());
    }
    public static void addOwner(Player sender,Player player, String region_name) throws StorageException {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(sender.getWorld());
        RegionManager regionManager = regionContainer.get(world);
        assert regionManager != null;
        ProtectedRegion region = regionManager.getRegion(region_name);
        assert region != null;
        region.getOwners().addPlayer(player.getUniqueId());
        regionManager.save();
    }

    public static void addMember(Player sender, Player player, String region_name) throws StorageException {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(sender.getWorld());
        RegionManager regionManager = regionContainer.get(world);
        assert regionManager != null;
        ProtectedRegion region = regionManager.getRegion(region_name);
        assert region != null;
        region.getOwners().addPlayer(player.getUniqueId());
        regionManager.save();
    }

    public static void removePlayerFromRegion(Player sender,Player player, String region_name) throws StorageException {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(sender.getWorld());
        RegionManager regionManager = regionContainer.get(world);
        assert regionManager != null;
        ProtectedRegion region = regionManager.getRegion(region_name);
        assert region != null;
        if (region.getOwners().contains(player.getUniqueId())){
            region.getOwners().removePlayer(player.getUniqueId());
        }
        if (region.getMembers().contains(player.getUniqueId())){
            region.getMembers().removePlayer(player.getUniqueId());
        }
        regionManager.save();
    }
}
