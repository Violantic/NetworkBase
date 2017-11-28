package com.nemegaming.playervaults.vault;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ethan on 11/27/2017.
 */
public class VaultManager {

    private Map<UUID, Vault> currentEdit;

    public VaultManager() {
        currentEdit = new ConcurrentHashMap<UUID, Vault>();
    }

    public Map<UUID, Vault> getCurrentEdit() {
        return currentEdit;
    }
}
