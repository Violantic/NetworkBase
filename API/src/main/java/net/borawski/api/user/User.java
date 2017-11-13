package net.borawski.api.user;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;

import java.util.Date;
import java.util.UUID;

@Entity(value = "user", noClassnameStored = true)
public class User {

    @Id
    protected ObjectId objectId;

    private UUID uuid;
    private String lastName;
    private String rank;
    private Integer tokens;

    private Date createdDate;
    private Date lastUpdate;

    public User(){
    }

    public User(UUID uuid, String lastName, String rank, Integer tokens) {
        super();
        this.uuid = uuid;
        this.lastName = lastName;
        this.rank = rank;
        this.tokens = tokens;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        setLastUpdate(new Date());
        this.objectId = objectId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        setLastUpdate(new Date());
        this.uuid = uuid;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        setLastUpdate(new Date());
        this.lastName = lastName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        setLastUpdate(new Date());
        this.rank = rank;
    }

    public Integer getTokens() {
        return tokens;
    }

    public void setTokens(Integer tokens) {
        setLastUpdate(new Date());
        this.tokens = tokens;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        setLastUpdate(new Date());
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = (createdDate == null) ? new Date() : createdDate;
        this.lastUpdate = (lastUpdate == null) ? createdDate : new Date();
    }

    @Override
    public String toString() {
        return "{uuid='" + getUuid().toString() + "', name='" + getLastName() + "', rank='" + getRank() + "', tokens='" + getTokens() + "', firstLogin='" + createdDate + "', lastUpdate='" + getLastUpdate().toString() + "'}";
    }
}
