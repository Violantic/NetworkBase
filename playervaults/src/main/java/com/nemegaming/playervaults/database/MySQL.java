/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */
package com.nemegaming.playervaults.database;

import com.nemegaming.playervaults.PlayerVaults;
import com.nemegaming.playervaults.util.ItemUtil;
import com.nemegaming.playervaults.vault.Vault;
import com.nemegaming.playervaults.vault.VaultItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Ethan on 1/6/2017.
 */
public class MySQL {

    private PlayerVaults instance;

    private String host;
    private String name;
    private String user;
    private String pass;
    private Connection connection;

    public MySQL(PlayerVaults instance, String host, String name, String user, String pass) {
        try {
            this.instance = instance;
            this.host = host;
            this.name = name;
            this.user = user;
            this.pass = pass;

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + getHost(), getUser(), getPass());
            setupTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setupTables() {
        String users = "CREATE TABLE IF NOT EXISTS vault_owners " +
                "(id INT NOT NULL AUTO_INCREMENT" +
                ", uuid VARCHAR(60), name VARCHAR(16)" +
                ", PRIMARY KEY(id));";

        String vaults = "CREATE TABLE IF NOT EXISTS vaults " +
                "(id INT NOT NULL AUTO_INCREMENT" +
                ", user_id INT" +
                ", vault_number INT, PRIMARY KEY(id));";

        String items = "CREATE TABLE IF NOT EXISTS items " +
                "(id INT NOT NULL AUTO_INCREMENT" +
                ", vault_id INT" +
                ", vault_pos INT" +
                ", serialization TEXT, PRIMARY KEY(id));";

        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(users);
            executeAsync(statement);
            statement = getConnection().prepareStatement(vaults);
            executeAsync(statement);
            statement = getConnection().prepareStatement(items);
            executeAsync(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Callback<Connection> invokeConnection() {
        return new Callback<Connection>() {
            public void call(Connection callback) {
                try {
                    callback = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + getHost(), getUser(), getPass());
                    System.out.println("[PlayerVaults] Refreshing connection @ " + host);
                    getInstance().getTracker().setLastUpdate(System.currentTimeMillis());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void executeAsync(final PreparedStatement statement) {
        instance.getServer().getScheduler().runTaskAsynchronously(getInstance(),
                new Runnable() {
                    public void run() {
                        try {
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void registerUser(String uuid, String name) {
        String query = "SELECT id FROM vault_owners WHERE uuid='" + uuid + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            if(!set.next()) {
                String insert = "INSERT INTO vault_owners (id,uuid,name) VALUES (NULL, '" + uuid + "', '" + name + "');";
                PreparedStatement register = getConnection().prepareStatement(insert);
                executeAsync(register);
            } else {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getInstance().getTracker().setLastUpdate(System.currentTimeMillis());
    }

    public int getID(String uuid) {
        String query = "SELECT id FROM vault_owners WHERE uuid='" + uuid + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                return set.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getInstance().getTracker().setLastUpdate(System.currentTimeMillis());
        return 0;
    }

    public String getUUID(String name) {
        String query = "SELECT uuid FROM vault_owners WHERE name='" + name + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                return set.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getInstance().getTracker().setLastUpdate(System.currentTimeMillis());

        return null;
    }

    public String getName(String uuid) {
        String query = "SELECT name FROM vault_owners WHERE uuid='" + uuid + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                return set.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getInstance().getTracker().setLastUpdate(System.currentTimeMillis());

        return null;
    }

    public PlayerVaults getInstance() {
        return instance;
    }

    public String getHost() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public Connection getConnection() {
        return connection;
    }

}
