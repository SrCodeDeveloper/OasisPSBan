package me.srcode.oasispsban;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.handler.Handler.Factory;
import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import me.srcode.oasispsban.command.BanCommand;
import me.srcode.oasispsban.command.KickCommand;
import me.srcode.oasispsban.command.UnbanCommand;
import me.srcode.oasispsban.command.SetKickLocationCommand;
import me.srcode.oasispsban.flag.BannedPlayersFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;

public class OasisPsBan extends JavaPlugin {
   private String kickedFromRegion;

   public void onLoad() {
      this.saveDefaultConfig();
      WorldGuard.getInstance().getFlagRegistry().register(BannedPlayersFlag.FLAG);
   }

   public void onEnable() {
      Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bOasisPSBan &aThis plugin has been enabled"));
      Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eDeveloped by &aSrCode"));
      Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eVersion: ") + getDescription().getVersion());
      Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eRunning on: ") + Bukkit.getBukkitVersion().toUpperCase());
      this.kickedFromRegion = this.c(this.getConfig().getString("kicked-from-region"));
      String kickedMessage = this.c(this.getConfig().getString("kicked-player"));
      String bannedMessage = this.c(this.getConfig().getString("banned-player"));
      String unbanMessage = this.c(this.getConfig().getString("unban-player"));
      String cantBanYourself = this.c(this.getConfig().getString("cant-ban-yourself"));
      String cantKickYourself = this.c(this.getConfig().getString("cant-kick-yourself"));
      WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(new BannedPlayersFlag.Handler.Factory(), (Factory)null);
      ProtectionStones.getInstance().addCommandArgument(new BanCommand(cantBanYourself, bannedMessage, this));
      ProtectionStones.getInstance().addCommandArgument(new UnbanCommand(unbanMessage, this));
      ProtectionStones.getInstance().addCommandArgument(new KickCommand(cantKickYourself, kickedMessage, this));
      this.getCommand("setbanlocation").setExecutor(new SetKickLocationCommand(this));
   }

   private String c(String message) {
      return ChatColor.translateAlternateColorCodes('&', message);
   }

   public void onDisable() {
      super.onDisable();
   }


   public void kickPlayerFromRegion(PSRegion region, Player player, boolean sendMessage) {
      ProtectedRegion wgRegion = region.getWGRegion();
      if (wgRegion.contains(BukkitAdapter.asBlockVector(player.getLocation()))) {
         Location location = this.getConfig().getLocation("kick-location", new Location((World)Bukkit.getWorlds().get(0), 0.0D, 90.0D, 0.0D));
         player.teleport(location, TeleportCause.PLUGIN);
         if (sendMessage) {
            player.sendMessage(this.kickedFromRegion);
         }

      }
   }
}
