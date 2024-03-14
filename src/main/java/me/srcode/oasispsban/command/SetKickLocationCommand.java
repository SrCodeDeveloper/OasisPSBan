package me.srcode.oasispsban.command;

import me.srcode.oasispsban.OasisPsBan;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class SetKickLocationCommand implements CommandExecutor {
   private final OasisPsBan oasisPsBan;

   public SetKickLocationCommand(OasisPsBan oasisPsBan) {
      this.oasisPsBan = oasisPsBan;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
         return true;
      } else {
         Player player = (Player)sender;
         Configuration config = this.oasisPsBan.getConfig();
         config.set("kick-location", player.getLocation());
         this.oasisPsBan.saveConfig();
         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lOASISPSBAN &aThe location of protection bans has been established"));
         return true;
      }
   }
}
