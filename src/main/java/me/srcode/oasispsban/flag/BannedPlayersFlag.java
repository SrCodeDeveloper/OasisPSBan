package me.srcode.oasispsban.flag;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.UUIDFlag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import java.util.Set;
import java.util.UUID;
import me.srcode.oasispsban.OasisPsBan;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class BannedPlayersFlag extends SetFlag<UUID> {
   public static BannedPlayersFlag FLAG = new BannedPlayersFlag();

   private BannedPlayersFlag() {
      super("ps-banned", new UUIDFlag("uuid-flag"));
   }

   private static String c(String message) {
      return ChatColor.translateAlternateColorCodes('&', message);
   }

   public static class Handler extends com.sk89q.worldguard.session.handler.Handler {
      private final JavaPlugin plugin = JavaPlugin.getPlugin(OasisPsBan.class);

      protected Handler(Session session) {
         super(session);
      }

      public boolean testMoveTo(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, MoveType moveType) {
         Set<UUID> bannedPlayers = (Set)toSet.queryValue((RegionAssociable)null, BannedPlayersFlag.FLAG);
         if (bannedPlayers != null && !player.hasPermission("oasispsban.bypass")) {
            if (bannedPlayers.contains(player.getUniqueId())) {
               player.print(BannedPlayersFlag.c(this.plugin.getConfig().getString("cant-enter-banned", "")));
               return false;
            } else {
               return true;
            }
         } else {
            return true;
         }
      }

      public static class Factory extends com.sk89q.worldguard.session.handler.Handler.Factory<BannedPlayersFlag.Handler> {
         public BannedPlayersFlag.Handler create(Session session) {
            return new BannedPlayersFlag.Handler(session);
         }
      }
   }
}
