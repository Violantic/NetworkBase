package com.nemegaming.playervaults.event;

import com.nemegaming.playervaults.vault.Vault;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Ethan on 11/27/2017.
 */
public class VaultOpenEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private Vault vault;
    private Player player;

    public VaultOpenEvent(Player player, Vault vault) {
        this.player = player;
        this.vault = vault;
    }

    public Vault getVault() {
        return vault;
    }

    public Player getPlayer() {
        return player;
    }
}
