package net.borawski.api;

import net.borawski.api.db.NetworkDatabase;
import net.borawski.api.server.NetworkServer;

public class NetworkAPI {

    public static void main(String[] args) {
        NetworkServer server = new NetworkServer() {
            public String getDatabaseName() {
                return "network";
            }

            public NetworkDatabase getData() {
                return new NetworkDatabase(this);
            }
        };
    }

}
