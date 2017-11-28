package com.nemegaming.playervaults.util;

import com.nemegaming.playervaults.PlayerVaults;
import com.nemegaming.playervaults.util.gui.CustomIS;
import com.nemegaming.playervaults.util.gui.ItemGUI;
import com.nemegaming.playervaults.util.gui.MenuItem;
import com.nemegaming.playervaults.vault.Vault;
import com.nemegaming.playervaults.vault.VaultItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by Ethan on 11/27/2017.
 */
public class VaultUtil {

    public static ItemGUI getVaultMenu(Player player, int user_id, int vault_id) {
        return new ItemGUI(PlayerVaults.getInstance(), null, player, 54) {
            @Override
            public String getName() {
                return "Vault " + vault_id;
            }

            @Override
            public boolean isCloseOnClick() {
                return false;
            }

            @Override
            public void registerItems() {
                int _vault_id = VaultUtil.getVaultID(user_id, vault_id);
                Vault vault = new Vault(user_id, _vault_id);
                PlayerVaults.getInstance().getVaultManager().getCurrentEdit().put(player.getUniqueId(), vault);
                vault.getPositionMap().forEach(new BiConsumer<Integer, VaultItem>() {
                    @Override
                    public void accept(Integer pos, VaultItem item) {
                        set(pos, new MenuItem(new CustomIS(ItemUtil.stringToItem(item.serialization()))));
                    }
                });
            }
        };
    }

    public static List<VaultItem> get(int vault_id) {
        String query = "SELECT * FROM items WHERE vault_id=" + vault_id + ";";
        List list = new ArrayList();
        try {
            PreparedStatement statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                int id = set.getInt("id");
                int vault_pos = set.getInt("vault_pos");
                String serial = set.getString("serialization");
                VaultItem item = new VaultItem() {
                    @Override
                    public int id() {
                        return id;
                    }

                    @Override
                    public int vault_id() {
                        return vault_id;
                    }

                    @Override
                    public int vault_pos() {
                        return vault_pos;
                    }

                    @Override
                    public String serialization() {
                        return serial;
                    }
                };
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerVaults.getInstance().getTracker().setLastUpdate(System.currentTimeMillis());

        return list;
    }

    public static ItemStack getItem(int item_id) {
        String query = "SELECT serialization FROM items WHERE id='" + item_id + "';";
        try {
            PreparedStatement statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            String serial = set.getString("serialization");
            while(set.next()) {
                ItemStack i = ItemUtil.stringToItem(serial);
                return i;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerVaults.getInstance().getTracker().setLastUpdate(System.currentTimeMillis());
        return null;
    }

    /**
     * Validates if the SLOT for the item exists, essentially
     * @param vault_id
     * @param pos
     * @return
     */
    public static boolean validateItem(int vault_id, int pos) {
        String query = "SELECT id FROM items WHERE vault_id='" + vault_id + "' AND vault_pos='" + pos + "';";
        try {
            PreparedStatement statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            return set.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void update(int item_id, String serial) {
        String query = "UPDATE items SET serialization='" + serial + "' WHERE id='" + item_id + "';";
        try {
            PreparedStatement statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query);
            PlayerVaults.getInstance().getSQL().executeAsync(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void drop(int item_id) {
        String query = "DELETE FROM items WHERE id='" + item_id + "';";
        try {
            PlayerVaults.getInstance().getSQL().executeAsync(PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registerSlot(int vault_id, int vault_pos, String serialization) {
        String query = "INSERT INTO items (id,vault_id,vault_pos,serialization) VALUES (NULL, " + vault_id + ", " + vault_pos + ", '" + serialization + "');";
        try {
            PlayerVaults.getInstance().getSQL().executeAsync(PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registerVault(int user_id, int vault_number) {
        String query = "INSERT INTO vaults (id,user_id,vault_number) VALUES (NULL, " + user_id + ", " + vault_number + ");";
        try {
            PlayerVaults.getInstance().getSQL().executeAsync(PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasVault(int user_id, int vault_number) {
        String query = "SELECT id FROM vaults WHERE user_id='" + user_id + "' AND vault_number='" + vault_number + "';";
        try {
            PreparedStatement statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int getVaultID(int user_id, int vault) {
        String query = "SELECT id FROM vaults WHERE user_id='" + user_id + "' AND vault_number='" + vault + "';";
        try {
            PreparedStatement statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                return set.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerVaults.getInstance().getTracker().setLastUpdate(System.currentTimeMillis());
        return 0;
    }

    public static int getMaxVaults(Player player) {
        int max = 0;
        for(PermissionAttachmentInfo i : player.getEffectivePermissions()) {
            if(i.getPermission().contains("playervaults.amount.")) {
                String[] args = i.getPermission().split(".");
                Integer multiplier = Integer.parseInt(args[2]);
                max = multiplier;
                break;
            }
        }

        if(player.hasPermission("playervaults.*")) {
            max = -1;
        }
        return max;
    }

    public static List<Integer[]> getVaultInfo(int user_id) {
        String query = "SELECT * FROM vaults WHERE user_id='" + user_id + "';";
        List<Integer[]> list = new ArrayList<Integer[]>();
        try {
            PreparedStatement statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                list.add(new Integer[]{set.getInt("vault_number"), set.getInt("id")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void deleteVault(int vault_id) {
        String query = "DELETE FROM vaults WHERE id=" + vault_id + ";";
        String items = "DELETE FROM items WHERE vault_id=" + vault_id + ";";
        try {
            PreparedStatement statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(query);
            PlayerVaults.getInstance().getSQL().executeAsync(statement);
            statement = PlayerVaults.getInstance().getSQL().getConnection().prepareStatement(items);
            PlayerVaults.getInstance().getSQL().executeAsync(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
