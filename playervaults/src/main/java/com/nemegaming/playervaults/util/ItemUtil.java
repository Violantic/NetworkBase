/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package com.nemegaming.playervaults.util;

import com.nemegaming.playervaults.util.gui.CustomIS;
import net.minecraft.server.v1_11_R1.Item;
import net.minecraft.server.v1_11_R1.Items;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.*;

/**
 * Created by Ethan on 1/7/2017.
 */
public class ItemUtil {

    public static String itemToString(ItemStack item) {
        if (item != null) {
            String itemString = "";
            String mat = item.getType().toString();
            String amount = ((Integer) item.getAmount()).toString();
            Map<Enchantment, Integer> enchants = item.getEnchantments();
            String fullEnchantmentString = "";
            String displayName = "";
            String loreString = "";
            String dataString = "";
            if (item.hasItemMeta()) {
                try {
                    if (item.getItemMeta().getDisplayName() == null) {
                        displayName = new CustomIS(item).getName();
                    } else {
                        displayName = item.getItemMeta().getDisplayName();
                    }
                    displayName = displayName.replaceAll(" ", "_");
                } catch (NullPointerException e) {
                }
                try {
                    List<String> lore = item.getItemMeta().getLore();
                    String loreString2 = "";
                    for (int x = 0; x < lore.size(); x++) {
                        loreString2 = (loreString2 + lore.get(x) + "\\|").replace(" ", "_");
                    }
                    loreString = loreString2;
                } catch (NullPointerException e) {
                }
            }

            Set<Map.Entry<Enchantment, Integer>> exampleEntry = enchants.entrySet();
            for (Map.Entry<Enchantment, Integer> e : exampleEntry) {
                Enchantment ench = e.getKey();
                String lvl = ((Integer) e.getValue()).toString();
                String enchName = EnchantmentName.enchNameToFriendlyName(ench.getName());
                enchName = enchName.replaceAll(" ", "_");
                fullEnchantmentString = fullEnchantmentString + " " + enchName + ":" + lvl;
            }

            itemString = mat + " " + amount;

            if (item.getData() != null) {
                byte data = item.getData().getData();
                itemString = itemString + " data:" + data;
            }

            if (displayName == null) {
                itemString = itemString + " name:" + ChatColor.RESET + new CustomIS(item).getMaterial().name().toLowerCase().substring(0, 1).toUpperCase() + new CustomIS(item).getMaterial().name().toLowerCase().substring(1);
            } else if (!displayName.equals("")) {
                itemString = itemString + " name:" + displayName;
            }

            if (item.getType().equals(Material.MOB_SPAWNER)) {
                String[] name = item.getItemMeta().getDisplayName().split(" ");
                String entity = ChatColor.stripColor(name[0]);
                short id = 0;

                try {
                    id = sort(entity);
                    itemString = itemString + " entity:" + id;
                } catch (Exception e) {
                    System.out.println("[PlayerVaults] could not parse entity id from mobspawner: " + entity.toUpperCase());
                }
            }

            if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta book = (EnchantmentStorageMeta) item.getItemMeta();
                List<Enchantment> list = new ArrayList<Enchantment>();
                for(Enchantment e : book.getStoredEnchants().keySet()) {
                    list.add(e);
                }
                itemString = itemString + " ebook:" + EnchantmentName.enchNameToFriendlyName(((Enchantment) list.get(0)).getName()).replace(" ", "_") + "=" + book.getStoredEnchants().get((Enchantment) list.get(0));
            }

            if(item.getType().equals(Material.SPLASH_POTION)) {
                itemString = itemString + " splashpotion:" + ((PotionMeta) item.getItemMeta()).getDisplayName();
            } else if(item.getType().equals(Material.POTION)) {
                itemString = itemString + " potion:" + ((PotionMeta) item.getItemMeta()).getDisplayName();
            }

            if (!loreString.equals(""))
                itemString = itemString + " lore:" + loreString;
            if (!fullEnchantmentString.equals(""))
                itemString = itemString + fullEnchantmentString;
            return itemString;
        }
        return "";
    }

    public static short sort(String name) {
        short s = EntityType.valueOf(name.toUpperCase()).getTypeId();
        if (EntityType.valueOf(name.toUpperCase()) == null) {
            for (EntityType t : EntityType.values()) {
                if (t.toString().contains(name.split(" ")[0].toUpperCase())) {
                    return t.getTypeId();
                }
            }
        }

        return s;
    }

    public static ItemStack stringToItem(String item) {
        String[] itemSplit = item.split(" ");
        List<String> itemWordList = Arrays.asList(itemSplit);
        String materialName = itemWordList.get(0);
        Material mat = Material.valueOf(materialName.toUpperCase());
        int amount = 0;
        try {
            amount = Integer.valueOf(itemWordList.get(1));
        } catch (ArrayIndexOutOfBoundsException e) {
            amount = 1;
        }
        String name = null;
        for (String word : itemWordList) {
            if (word.contains("name:")) {
                String[] nameArray = word.split(":");
                name = ChatColor.translateAlternateColorCodes('&', nameArray[1]);
                name = name.replaceAll("_", " ");
            }
        }
        List<String> lore = null;
        for (String word : itemWordList) {
            if (word.contains("lore:")) {
                String[] fullLoreArray = word.split(":");
                String loreString = ChatColor.translateAlternateColorCodes('&', fullLoreArray[1]);
                loreString = loreString.replaceAll("_", " ");
                String[] loreArray = loreString.split("\\|");
                lore = Arrays.asList(loreArray);
            }
        }

        Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
        for (String word : itemWordList) {
            if (word.contains("protection:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, lvl);
            }
            if (word.contains("fire_protection:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.PROTECTION_FIRE, lvl);
            }
            if (word.contains("feather_falling:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.PROTECTION_FALL, lvl);
            }
            if (word.contains("blast_protection:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, lvl);
            }
            if (word.contains("projectile_protection:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.PROTECTION_PROJECTILE, lvl);
            }
            if (word.contains("respiration:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.OXYGEN, lvl);
            }
            if (word.contains("aqua_affinity:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.WATER_WORKER, lvl);
            }
            if (word.contains("thorns:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.THORNS, lvl);
            }
            if (word.contains("sharpness:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.DAMAGE_ALL, lvl);
            }
            if (word.contains("smite:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.DAMAGE_UNDEAD, lvl);
            }
            if (word.contains("bane_of_arthropods:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.DAMAGE_ARTHROPODS, lvl);
            }
            if (word.contains("knockback:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.KNOCKBACK, lvl);
            }
            if (word.contains("fire_aspect:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.FIRE_ASPECT, lvl);
            }
            if (word.contains("looting:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.LOOT_BONUS_MOBS, lvl);
            }
            if (word.contains("efficiency:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.DIG_SPEED, lvl);
            }
            if (word.contains("silk_touch:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.SILK_TOUCH, lvl);
            }
            if (word.contains("unbreaking:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.DURABILITY, lvl);
            }
            if (word.contains("fortune:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.LOOT_BONUS_BLOCKS, lvl);
            }
            if (word.contains("power:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.ARROW_DAMAGE, lvl);
            }
            if (word.contains("punch:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.ARROW_KNOCKBACK, lvl);
            }
            if (word.contains("flame:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.ARROW_FIRE, lvl);
            }
            if (word.contains("infinity:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.ARROW_INFINITE, lvl);
            }
            if (word.contains("luck_of_the_sea:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.LUCK, lvl);
            }
            if (word.contains("lure:")) {
                String[] fullArray = word.split(":");
                int lvl = Integer.valueOf(fullArray[1]);
                enchantments.put(Enchantment.LURE, lvl);
            }
        }

        short id = 0;
        byte data = 0;
        String eName = "";
        int eLvl = 0;
        for (String word : itemWordList) {
            if (word.contains("data:")) {
                String[] nameArray = word.split(":");
                data = (byte) Integer.parseInt(nameArray[1]);
            }

            if (word.contains("entity:")) {
                String[] entityA = word.split(":");
                id = (short) Integer.parseInt(entityA[1]);
            }

            if (word.contains("ebook:")) {
                String[] nameArray = word.split(":");
                String ebook = nameArray[1];
                String[] etemp = ebook.split("=");
                String ename = etemp[0].replace(" ", "_");
                int level = Integer.valueOf(etemp[1]);

                eName = ename;
                eLvl = level;
            }
        }

        ItemStack itemStack = new ItemStack(mat, amount, data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (name != null)
            itemMeta.setDisplayName(ChatColor.RESET + name);
        if (lore != null)
            itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantments(enchantments);

        for (String word : itemWordList) {
            if (word.contains("entity:")) {
                ItemStack itemz = newSpawnerItem(Short.valueOf(word.split(":")[1]));
                return itemz;
            } else if(word.contains("splashpotion:")) {
                itemStack.setType(Material.SPLASH_POTION);
            }
        }

        if (itemStack.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) itemStack.getItemMeta();
            String word = eName;
            int i = eLvl;
            esm.addStoredEnchant(getEnchantmentByCommonName(word), i, false);
            itemStack.setItemMeta(esm);
        }

        return itemStack;
    }

    public static ItemStack newSpawnerItem(short id) {
        try {
            Item spawnerItem = (Item) Items.class.getDeclaredField(Material.MOB_SPAWNER.name()).get(Items.class);
            net.minecraft.server.v1_11_R1.ItemStack stack = new net.minecraft.server.v1_11_R1.ItemStack(spawnerItem, 1);
            NBTTagCompound com = new NBTTagCompound();
            com.setString("EntityId", id + "");
            stack.setTag(com);

            return CraftItemStack.asCraftMirror(stack);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Enchantment getEnchantmentByCommonName(String name){
        name = name.toLowerCase();
        if(name.toLowerCase().equalsIgnoreCase("fire_protection")) return Enchantment.PROTECTION_FIRE;
        if(name.toLowerCase().equalsIgnoreCase("blast_protection")) return Enchantment.PROTECTION_EXPLOSIONS;
        if(name.toLowerCase().equalsIgnoreCase("projectile_protection")) return Enchantment.PROTECTION_PROJECTILE;
        if(name.toLowerCase().equalsIgnoreCase("protection")) return Enchantment.PROTECTION_ENVIRONMENTAL;
        if(name.toLowerCase().equalsIgnoreCase("feather_falling")) return Enchantment.PROTECTION_FALL;
        if(name.toLowerCase().equalsIgnoreCase("respiration")) return Enchantment.OXYGEN;
        if(name.toLowerCase().equalsIgnoreCase("aqua_affinity")) return Enchantment.WATER_WORKER;
        if(name.toLowerCase().equalsIgnoreCase("sharpness")) return Enchantment.DAMAGE_ALL;
        if(name.toLowerCase().equalsIgnoreCase("smite")) return Enchantment.DAMAGE_UNDEAD;
        if(name.toLowerCase().equalsIgnoreCase("bane_of_arthropods")) return Enchantment.DAMAGE_ARTHROPODS;
        if(name.toLowerCase().equalsIgnoreCase("knockback")) return Enchantment.KNOCKBACK;
        if(name.toLowerCase().equalsIgnoreCase("fire_aspect")) return Enchantment.FIRE_ASPECT;
        if(name.toLowerCase().equalsIgnoreCase("looting")) return Enchantment.LOOT_BONUS_MOBS;
        if(name.toLowerCase().equalsIgnoreCase("power")) return Enchantment.ARROW_DAMAGE;
        if(name.toLowerCase().equalsIgnoreCase("punch")) return Enchantment.ARROW_KNOCKBACK;
        if(name.toLowerCase().equalsIgnoreCase("flame")) return Enchantment.ARROW_FIRE;
        if(name.toLowerCase().equalsIgnoreCase("infinity")) return Enchantment.ARROW_INFINITE;
        if(name.toLowerCase().equalsIgnoreCase("efficiency")) return Enchantment.DIG_SPEED;
        if(name.toLowerCase().equalsIgnoreCase("unbreaking")) return Enchantment.DURABILITY;
        if(name.toLowerCase().equalsIgnoreCase("silk_touch")) return Enchantment.SILK_TOUCH;
        if(name.toLowerCase().equalsIgnoreCase("fortune")) return Enchantment.LOOT_BONUS_BLOCKS;
        if(name.toLowerCase().equalsIgnoreCase("thorns")) return Enchantment.THORNS;
        return null;
    }

    public static class EnchantmentName {
        public static String enchNameToFriendlyName(String enchName) {
            if (enchName.equals("PROTECTION_ENVIRONMENTAL")) {
                return "protection";
            } else if (enchName.equals("PROTECTION_FIRE")) {
                return "fire protection";
            } else if (enchName.equals("PROTECTION_FALL")) {
                return "feather falling";
            } else if (enchName.equals("PROTECTION_EXPLOSIONS")) {
                return "blast protection";
            } else if (enchName.equals("PROTECTION_PROJECTILE")) {
                return "projectile protection";
            } else if (enchName.equals("OXYGEN")) {
                return "respiration";
            } else if (enchName.equals("WATER_WORKER")) {
                return "aqua affinity";
            } else if (enchName.equals("THORNS")) {
                return "thorns";
            } else if (enchName.equals("DAMAGE_ALL")) {
                return "sharpness";
            } else if (enchName.equals("DAMAGE_UNDEAD")) {
                return "smite";
            } else if (enchName.equals("DAMAGE_ARTHROPODS")) {
                return "bane of arthropods";
            } else if (enchName.equals("KNOCKBACK")) {
                return "knockback";
            } else if (enchName.equals("FIRE_ASPECT")) {
                return "fire aspect";
            } else if (enchName.equals("LOOT_BONUS_MOBS")) {
                return "looting";
            } else if (enchName.equals("DIG_SPEED")) {
                return "efficiency";
            } else if (enchName.equals("SILK_TOUCH")) {
                return "silk touch";
            } else if (enchName.equals("DURABILITY")) {
                return "unbreaking";
            } else if (enchName.equals("ARROW_DAMAGE")) {
                return "power";
            } else if (enchName.equals("ARROW_KNOCKBACK")) {
                return "punch";
            } else if (enchName.equals("ARROW_FIRE")) {
                return "flame";
            } else if (enchName.equals("ARROW_INFINITE")) {
                return "infinity";
            } else if (enchName.equals("LUCK")) {
                return "luck of the sea";
            } else if (enchName.equals("LURE")) {
                return "lure";
            }
            return "";
        }
    }

}
