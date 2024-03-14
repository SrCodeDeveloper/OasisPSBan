package me.srcode.oasispsban.command;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.espi.protectionstones.PSL;
import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.commands.PSCommandArg;
import dev.espi.protectionstones.utils.UUIDCache;
import dev.espi.protectionstones.utils.WGUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.srcode.oasispsban.OasisPsBan;
import me.srcode.oasispsban.flag.BannedPlayersFlag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnbanCommand implements PSCommandArg {
   private final String unbannedMessage;
   private final OasisPsBan oasisPsBan;

   public UnbanCommand(String unbannedMessage, OasisPsBan oasisPsBan) {
      this.unbannedMessage = unbannedMessage;
      this.oasisPsBan = oasisPsBan;
   }

   public List<String> getNames() {
      return Collections.singletonList("unban");
   }

   public boolean allowNonPlayersToExecute() {
      return false;
   }

   public List<String> getPermissionsToExecute() {
      return null;
   }

   public HashMap<String, Boolean> getRegisteredFlags() {
      return null;
   }

   public boolean executeArgument(CommandSender s, String[] args, HashMap<String, String> flags) {
      Player sender = (Player)s;
      if (args.length < 2) {
         return PSL.msg(sender, PSL.COMMAND_REQUIRES_PLAYER_NAME.msg());
      } else if (!UUIDCache.containsName(args[1])) {
         return PSL.msg(sender, PSL.PLAYER_NOT_FOUND.msg());
      } else {
         UUID playerId = UUIDCache.getUUIDFromName(args[1]);
         String playerName = UUIDCache.getNameFromUUID(playerId);
         OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
         PSRegion region = PSRegion.fromLocationGroup(sender.getLocation());
         if (region == null) {
            PSL.msg(sender, PSL.NOT_IN_REGION.msg());
            return true;
         } else {
            ProtectedRegion wgRegion = region.getWGRegion();
            if (WGUtils.hasNoAccess(wgRegion, sender, WorldGuardPlugin.inst().wrapPlayer(sender), this.oasisPsBan.getConfig().getBoolean("members-can-unban", false))) {
               PSL.msg(sender, PSL.NO_ACCESS.msg());
               return true;
            } else {
               Set<UUID> bannedPlayers = (Set)wgRegion.getFlag(BannedPlayersFlag.FLAG);
               if (bannedPlayers == null) {
                  sender.sendMessage(this.unbannedMessage.replace("%player%", playerName));
                  return true;
               } else {
                  bannedPlayers.remove(playerId);
                  wgRegion.setFlag(BannedPlayersFlag.FLAG, bannedPlayers);
                  sender.sendMessage(this.unbannedMessage.replace("%player%", playerName));
                  return false;
               }
            }
         }
      }
   }

   public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return Collections.emptyList();
      } else {
         Player playerSender = (Player)sender;
         String prefix = args[1];
         List<String> matchedPlayers = new ArrayList();
         Iterator var7 = Bukkit.matchPlayer(prefix).iterator();

         while(var7.hasNext()) {
            Player player = (Player)var7.next();
            if (playerSender.canSee(player)) {
               matchedPlayers.add(player.getName());
            }
         }

         return matchedPlayers;
      }
   }
}
