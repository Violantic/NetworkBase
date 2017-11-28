/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package com.nemegaming.playervaults.util.gui;

/**
 * Created by Ethan on 9/5/2016.
 */
public class MenuItem {

    private CustomIS item;
    private Runnable exec;

    public MenuItem(CustomIS item, Runnable exec) {
        this.item = item;
        this.exec = exec;
    }

    public MenuItem(CustomIS item) {
        this.item = item;
        this.exec = null;
    }

    public CustomIS getItem() {
        return item;
    }

    public Runnable getExec() {
        return exec;
    }

    public boolean functions() {
        return (exec != null);
    }

    public static class SubMenuItem extends MenuItem {

        public SubMenuItem(CustomIS item, final ItemMenu child) {
            super(item, new Runnable() {
                public void run() {
                    child.show();
                }
            });
        }

        public SubMenuItem(CustomIS item, ItemGUI child) {
            this(item, child.getItemMenu());
        }

    }

    public static class BackMenuItem extends MenuItem {

        public BackMenuItem(CustomIS item, final ItemMenu current) {
            super(item, new Runnable() {
                public void run() {
                    if (current.hasParent()) {
                        current.getParent().show();
                    }
                }
            });
        }

        public BackMenuItem(CustomIS item, ItemGUI current) {
            this(item, current.getItemMenu());
        }

    }

}
