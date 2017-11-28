package com.nemegaming.playervaults.listener;

import com.nemegaming.playervaults.PlayerVaults;
import com.nemegaming.playervaults.util.ItemUtil;
import com.nemegaming.playervaults.util.VaultUtil;
import com.nemegaming.playervaults.vault.Vault;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ethan on 11/27/2017.
 */
public class VaultListener implements Listener {

    private PlayerVaults instance;

    public VaultListener(PlayerVaults instance) {
        this.instance = instance;
    }

    public PlayerVaults getInstance() {
        return instance;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if(!isViewingVault(event.getPlayer())) {
            return;
        }
        Vault vault = getInstance().getVaultManager().getCurrentEdit().get(event.getPlayer().getUniqueId());
        for(int i = 0; i < 54; i++) {
            ItemStack stack = event.getInventory().getItem(i);
            if(!vault.getPositionMap().containsKey(i)) {
                if(stack != null && !stack.getType().equals(Material.AIR)) {
                    VaultUtil.registerSlot(vault.getVaultId(), i, ItemUtil.itemToString(stack));
                }
                continue;
            }

            int item_id = vault.getPositionMap().get(i).id();
            if(stack == null) {
                VaultUtil.drop(item_id);
            } else {
                String serialization = ItemUtil.itemToString(stack);
                VaultUtil.update(item_id, serialization);
            }
        }

        getInstance().getVaultManager().getCurrentEdit().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        getInstance().getSQL().registerUser(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
    }

    public boolean isViewingVault(HumanEntity player) {
        return getInstance().getVaultManager().getCurrentEdit().containsKey(player.getUniqueId());
    }
}
