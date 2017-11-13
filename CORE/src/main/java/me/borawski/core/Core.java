package me.borawski.core;

import net.borawski.api.db.NetworkDatabase;
import net.borawski.api.server.NetworkServer;
import net.borawski.api.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ethan on 10/15/2017.
 */
public class Core extends JavaPlugin implements NetworkServer, Listener {

    private static Core instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    public static Core getInstance() {
        return instance;
    }

    public String getDatabaseName() {
        return "network";
    }

    public NetworkDatabase getData() {
        return new NetworkDatabase(this);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        getData().getUserRepository().computeIfAbsent(player.getUniqueId(), player.getName());
        User user = getData().getUserRepository().getByUUID(player.getUniqueId());
        System.out.println(user.toString());
    }


}
