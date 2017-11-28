package com.nemegaming.playervaults.command;

import com.nemegaming.playervaults.PlayerVaults;
import com.nemegaming.playervaults.util.VaultUtil;
import com.nemegaming.playervaults.util.gui.ItemGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Ethan on 11/27/2017.
 */
public class VaultCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        int user_id = PlayerVaults.getInstance().getSQL().getID(player.getUniqueId().toString());

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Invalid arguments");
            return false;
        } else if (args.length == 1) {
            int number = 0;
            try {
                number = Integer.valueOf(args[0]);
            } catch (Exception e) {
                String user = args[0];
                String uuid = PlayerVaults.getInstance().getSQL().getUUID(user);

                if (uuid == null) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Could not find vault data for " + user);
                    return false;
                }

                int target_user_id = PlayerVaults.getInstance().getSQL().getID(uuid);
                List<Integer[]> info = VaultUtil.getVaultInfo(target_user_id);

                String[] infoMsg = new String[1 + info.size()];
                infoMsg[0] = ChatColor.GRAY + "-----[ " + user + " ]-----";
                for (int x = 0; x < info.size(); x++) {
                    int copy = x;
                    infoMsg[x + 1] = "" + ChatColor.GRAY + "" + ChatColor.ITALIC + "" + (copy += 1) + ". " + ChatColor.RESET + "" + ChatColor.GRAY + "Vault-" + info.get(x)[0] + " " + ChatColor.DARK_GRAY + "(#" + info.get(x)[1] + ")";
                }

                player.sendMessage(infoMsg);
                return true;
            }

            int max = VaultUtil.getMaxVaults(player);
            if ((number > max && max != -1) || number <= 0) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "The only vaults you have access to are: (" + (max > 1 ? "1-" + max : max == -1 ? "Infinite" : "n/a") + ")");
                return false;
            } else if (!VaultUtil.hasVault(PlayerVaults.getInstance().getSQL().getID(player.getUniqueId().toString()), number)) {
                VaultUtil.registerVault(PlayerVaults.getInstance().getSQL().getID(player.getUniqueId().toString()), number);
            }

            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Opening vault " + number + "...");
            ItemGUI gui = VaultUtil.getVaultMenu(player, user_id, number);
            gui.getItemMenu().setAccessAllowed(true);
            gui.show();
            return true;
        } else if (args.length == 2) {
            if(!player.hasPermission("playervaults.admin")) {
                player.sendMessage(ChatColor.RED + "Insufficient permission");
                return false;
            }

            String user = args[0];
            int vault_number = Integer.valueOf(args[1]);
            String uuid = PlayerVaults.getInstance().getSQL().getUUID(user);
            int target_id = PlayerVaults.getInstance().getSQL().getID(uuid);

            if (!VaultUtil.hasVault(target_id, vault_number)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "They do not have that vault");
                return false;
            }

            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Opening vault #" + VaultUtil.getVaultID(target_id, vault_number) + " (" + user + ")...");
            ItemGUI gui = VaultUtil.getVaultMenu(player, target_id, vault_number);
            gui.getItemMenu().setAccessAllowed(true);
            gui.show();
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "Invalid arguments");
            return false;
        }
    }

}
