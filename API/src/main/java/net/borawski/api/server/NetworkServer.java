package net.borawski.api.server;

import net.borawski.api.db.NetworkDatabase;

public interface NetworkServer {

    String getDatabaseName();
    NetworkDatabase getData();

}
