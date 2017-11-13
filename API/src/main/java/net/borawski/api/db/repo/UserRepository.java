package net.borawski.api.db.repo;

import net.borawski.api.user.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository extends BasicDAO<User, ObjectId> implements Repository {

    private Map<UUID, User> userCache;

    public UserRepository(Class<User> entityClass, Datastore ds) {
        super(entityClass, ds);
        userCache = new ConcurrentHashMap<UUID, User>();
    }

    public User getByUUID(UUID uuid) {
        if(userCache.containsKey(uuid)) {
            return userCache.get(uuid);
        }

        Query<User> query = createQuery()
                .field("uuid").equal(uuid);

        User user = query.get();
        userCache.put(uuid, user);
        return user;
    }

    public List<User> getByRank(String rank) {
        Query<User> query = createQuery()
                .field("rank").equal(rank);
        return query.asList();
    }

    public List<User> getBeforeDate(Long time) {
        Query<User> query = createQuery().field("firstJoin").lessThanOrEq(time);
        return query.asList();
    }

    public void computeIfAbsent(UUID uuid, String name) {
        Query<User> query = createQuery()
                .field("uuid").equal(uuid);
        if(query.get() == null) {
            User user = new User(uuid, name, "DEFAULT", 0);
            getDatastore().save(user);
        }


    }

}
