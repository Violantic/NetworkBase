/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package com.nemegaming.playervaults.util.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Ethan on 9/5/2016.
 */
public class CustomIS {

    private static final String CUSTOMIS_META_KEY = "CustomISMeta";

    private String name;
    private final ArrayList<String> lore;
    private Material material;
    private short data;
    private int size;
    private HashMap<Enchantment, Integer> enchantments;
    private ArrayList<ItemFlag> flags;

    public CustomIS() {
        this.name = "";
        this.lore = new ArrayList<String>();
        this.material = Material.DIAMOND;
        this.data = 0;
        this.size = 1;
        this.enchantments = new HashMap<Enchantment, Integer>();
        this.flags = new ArrayList<ItemFlag>();
    }

    public CustomIS(ItemStack is) {
        this.name = null;
        this.material = is.getType();
        this.data = is.getDurability();
        this.size = is.getAmount();
        this.enchantments = new HashMap();
        this.enchantments.putAll(is.getEnchantments());
        this.flags = new ArrayList();
        ItemMeta meta = is.getItemMeta();
        if (meta == null) {
            this.lore = new ArrayList();
            return;
        }
        Iterator<ItemFlag> itim = meta.getItemFlags().iterator();
        while (itim.hasNext()) {
            this.flags.add(itim.next());
        }
        if (meta.hasDisplayName()) {
            this.name = meta.getDisplayName();
        }
        if (meta.hasLore()) {
            this.lore = (ArrayList) meta.getLore();
        } else {
            this.lore = new ArrayList();
        }
    }

    public CustomIS(final JSONObject dbo) throws IllegalArgumentException {
        this.name = "";
        this.lore = new ArrayList<String>();
        this.material = Material.DIAMOND;
        this.data = 0;
        this.size = 1;
        this.enchantments = new HashMap<Enchantment, Integer>();
        this.flags = new ArrayList<ItemFlag>();
        try {
            if (dbo.containsKey("name")) {
                setName(ChatColor.translateAlternateColorCodes('&', (String) dbo.get("name")));
            }
            if (dbo.containsKey("typeid")) {
                setMaterial(Material.getMaterial((Integer) dbo.get("typeid")));
            }
            if (dbo.containsKey("data")) {
                setData((Byte) dbo.get("data"));
            }
            if (dbo.containsKey("amount")) {
                setSize((Integer) dbo.get("amount"));
            }
            if (dbo.containsKey("lore")) {
                for (Object loredbo : (JSONArray) dbo.get("lore")) {
                    addLore(ChatColor.translateAlternateColorCodes('&', (String) loredbo));
                }
            }
            if (dbo.containsKey("enchantments")) {
                for (Object edbo : (JSONArray) dbo.get("enchantments")) {
                    addEnchantment(
                            Enchantment.getByName(((String) ((JSONObject) edbo).get("name")).toUpperCase()),
                            (Integer) ((JSONObject) edbo).get("level")
                    );
                }
            }
            if (dbo.containsKey("flags")) {
                for (Object flagdbo : (JSONArray) dbo.get("flags")) {
                    addItemFlag(ItemFlag.valueOf(((String) flagdbo).toUpperCase()));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public JSONObject toJSON() {
        return new JSONObject() {{
            put("name", name);
            put("lore", lore.stream().collect(Collectors.toCollection((Supplier<JSONArray>) new Supplier<JSONArray>() {
                public JSONArray get() {
                    return new JSONArray();
                }
            })));
            put("typeid", material.getId());
            put("data", data);
            put("amount", size);
            put("flags", new JSONArray() {{
                addAll(flags.stream().map(new Function<ItemFlag, String>() {
                    public String apply(ItemFlag itemFlag) {
                        return itemFlag.name();
                    }
                }).collect(Collectors.toList()));
            }});
            put("enchantments", new JSONArray() {{
                addAll(enchantments.keySet().stream().map(new Function<Enchantment, JSONObject>() {
                    public JSONObject apply(final Enchantment enchantment) {
                        return new JSONObject() {{
                            put("name", enchantment.getName());
                            put("level", enchantments.get(enchantment));
                        }};
                    }
                }).collect(Collectors.toList()));
            }});
        }};
    }

    public CustomIS clone() {
        CustomIS cis = new CustomIS();
        cis.setName(name);
        cis.setMaterial(material);
        cis.setData(data);
        cis.setSize(size);
        for (String l : lore) {
            cis.addLore(l);
        }
        for (Enchantment e : enchantments.keySet()) {
            cis.addEnchantment(e, enchantments.get(e));
        }
        for (ItemFlag _if : flags) {
            cis.addItemFlag(_if);
        }
        return cis;
    }

    public CustomIS addItemFlag(ItemFlag _if) {
        if (!flags.contains(_if)) {
            flags.add(_if);
        }
        return this;
    }

    public CustomIS addEnchantment(Enchantment e, int level) {
        enchantments.put(e, level);
        return this;
    }

    public boolean equals(ItemStack is) {
        CustomIS comp = this;
        return (comp.equals(is));
    }

    public CustomIS setName(String name) {
        this.name = name;
        return this;
    }

    public CustomIS addLore(String line) {
        lore.add(line);
        return this;
    }

    public CustomIS addLores(List<String> list) {
        lore.addAll(list);
        return this;
    }

    public CustomIS setMaterial(Material mat) {
        material = mat;
        return this;
    }

    public CustomIS setData(short data) {
        this.data = data;
        return this;
    }

    public CustomIS setSize(int size) {
        this.size = size;
        return this;
    }
    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public short getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<String> getLore() {
        return lore;
    }

    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public ArrayList<ItemFlag> getFlags() {
        return flags;
    }

    public ItemStack get() {
        ItemStack is = new ItemStack(material, size);
        is.setDurability((short) data);
        final ItemMeta im = is.getItemMeta();
        if (name != null && !name.isEmpty()) {
            im.setDisplayName(name);
        }
        flags.forEach(new Consumer<ItemFlag>() {
            public void accept(ItemFlag itemFlags) {
                im.addItemFlags(itemFlags);
            }
        });
        ArrayList<String> lore_wmeta = (ArrayList<String>) lore.clone();
        im.setLore(lore_wmeta);
        is.setItemMeta(im);
        is.addUnsafeEnchantments(enchantments);
        return is;
    }

}
