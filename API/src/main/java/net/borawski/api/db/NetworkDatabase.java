package net.borawski.api.db;

import com.mongodb.MongoClient;
import net.borawski.api.db.repo.UserRepository;
import net.borawski.api.server.NetworkServer;
import net.borawski.api.user.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;

public class NetworkDatabase {

    private Morphia morphia;
    private Datastore datastore;
    private UserRepository userRepository;

    public NetworkDatabase(NetworkServer server) {
        try {
            MongoClient mongoClient = new MongoClient("127.0.0.1:27017");
            this.morphia = new Morphia();
            String database = server.getDatabaseName();
            this.datastore = morphia.createDatastore(mongoClient, database);
            this.userRepository = new UserRepository(User.class, datastore);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Morphia getMorphia() {
        return morphia;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
