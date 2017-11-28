 package com.nemegaming.playervaults.command;

import com.nemegaming.playervaults.PlayerVaults;
import com.nemegaming.playervaults.util.VaultUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ethan on 11/28/2017.
 */
public class DeleteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if(args.length == 1 || args.length == 2) {
            int vault_number = 0;
            try {
                vault_number = Integer.parseInt(args[0]);
            } catch (Exception e) {
                if(!player.hasPermission("playervaults.delete")) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/pvdel <vault#>");
                    return false;
                }

                String user = args[0];
                try {
                    int number = Integer.parseInt(args[1]);
                    String uuid = PlayerVaults.getInstance().getSQL().getUUID(user);
                    if(uuid == null) {
                        player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "That user does not exist");
                        return false;
                    }

                    int target_id = PlayerVaults.getInstance().getSQL().getID(uuid);
                    int vault_id = VaultUtil.getVaultID(target_id, number);
                    VaultUtil.deleteVault(vault_id);
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You deleted Vault #" + vault_id);
                    return true;
                } catch (Exception e1) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/pvdel <user> #");
                    return false;
                }
            }

            int user_id = PlayerVaults.getInstance().getSQL().getID(player.getUniqueId().toString());
            int vault_id = VaultUtil.getVaultID(user_id, vault_number);
            VaultUtil.deleteVault(vault_id);
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You deleted Vault-" + vault_number);
        } else {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid arguments");
        }
        return false;
    }
}
