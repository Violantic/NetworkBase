package com.nemegaming.playervaults.vault;

import com.nemegaming.playervaults.PlayerVaults;
import com.nemegaming.playervaults.util.VaultUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by Ethan on 11/26/2017.
 */
public class Vault {

    private int userId;
    private int vaultId;

    private List<VaultItem> content;
    private Map<Integer, VaultItem> positionMap;
    private Map<Integer, Integer> positionIdMap;

    public Vault(int userId, int vaultId) {
        this.userId = userId;
        this.vaultId = vaultId;
        this.positionMap = new ConcurrentHashMap<Integer, VaultItem>();
        this.positionIdMap = new ConcurrentHashMap<Integer, Integer>();
        this.content = VaultUtil.get(vaultId);

        getContent().forEach(new Consumer<VaultItem>() {
            @Override
            public void accept(VaultItem i) {
                positionMap.put(i.vault_pos(), i);
                positionIdMap.put(i.vault_pos(), i.id());
            }
        });

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVaultId() {
        return vaultId;
    }

    public void setVaultId(int vaultId) {
        this.vaultId = vaultId;
    }

    public Map<Integer, VaultItem> getPositionMap() {
        return positionMap;
    }

    public void setPositionMap(Map<Integer, VaultItem> contents) {
        this.positionMap = contents;
    }

    public List<VaultItem> getContent() {
        return content;
    }

    public void setContent(List<VaultItem> content) {
        this.content = content;
    }

    public Map<Integer, Integer> getPositionIdMap() {
        return positionIdMap;
    }

    public void setPositionIdMap(Map<Integer, Integer> positionIdMap) {
        this.positionIdMap = positionIdMap;
    }

    public PlayerVaults getInstance() {
        return PlayerVaults.getInstance();
    }

}
