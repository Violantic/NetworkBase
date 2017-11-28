package com.nemegaming.playervaults;

import com.nemegaming.playervaults.command.DeleteCommand;
import com.nemegaming.playervaults.command.VaultCommand;
import com.nemegaming.playervaults.database.ConnectionTracker;
import com.nemegaming.playervaults.database.MySQL;
import com.nemegaming.playervaults.listener.VaultListener;
import com.nemegaming.playervaults.vault.VaultManager;
import org.bukkit.plugin.java.JavaPlugin;



/**
 * Created by Ethan on 11/26/2017.
 */
public class PlayerVaults extends JavaPlugin {

    private static PlayerVaults instance;
    private MySQL sql;
    private ConnectionTracker connectionTracker;
    private VaultManager vaultManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        sql = new MySQL(this,
                getConfig().getString("host"),
                getConfig().getString("name"),
                getConfig().getString("user"),
                getConfig().getString("pass"));
        connectionTracker = new ConnectionTracker(this, sql.getConnection());
        vaultManager = new VaultManager();

        getServer().getPluginManager().registerEvents(new VaultListener(this), this);
        getCommand("pv").setExecutor(new VaultCommand());
        getCommand("pvdel").setExecutor(new DeleteCommand());
    }

    public MySQL getSQL() {
        return sql;
    }

    public ConnectionTracker getTracker() {
        return connectionTracker;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public static PlayerVaults getInstance() {
        return instance;
    }
}
