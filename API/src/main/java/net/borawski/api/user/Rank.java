package net.borawski.api.user;

import org.bukkit.ChatColor;

public enum Rank {

    //    id  name    color               permissions
    ADMIN(3, "admin", ChatColor.DARK_RED, new String[]{"*"}),
    MOD(2, "mod", ChatColor.RED, new String[]{"worldedit.*"}),
    DEFAULT(1, "def", ChatColor.GRAY, new String[]{""});

    int id;
    String name;
    ChatColor color;
    String[] perms;

    Rank(int id, String name, ChatColor color, String[] perms) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.perms = perms;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public String[] getPerms() {
        return perms;
    }

    public void setPerms(String[] perms) {
        this.perms = perms;
    }
}
