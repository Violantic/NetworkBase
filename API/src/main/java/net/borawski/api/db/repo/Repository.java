package net.borawski.api.db.repo;

import net.borawski.api.user.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

public interface Repository extends DAO<User, ObjectId> {
    public User getByUUID(UUID uuid);
    public List<User> getByRank(String rank);
    public List<User> getBeforeDate(Long time);
}
