package entities;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "envite_sent_request_table")
public class SentRequest {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String eid;

    @NonNull
    private String status;

    @NonNull
    private String from;

    @NonNull
    private String to;

    @NonNull
    private Integer createdAt;

    @NonNull
    private Integer updatedAt;

    @NonNull
    @Embedded(prefix = "requestedTo")
    private User requestedTo;

    @NonNull
    @Embedded(prefix = "envite")
    private Envite envite;

    public SentRequest(@NonNull String id, @NonNull String eid, @NonNull String status,
                           @NonNull String from, @NonNull String to, @NonNull Integer createdAt,
                           @NonNull Integer updatedAt, @NonNull User requestedTo, @NonNull Envite envite) {
        this.id = id;
        this.eid = eid;
        this.status = status;
        this.from = from;
        this.to = to;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.requestedTo = requestedTo;
        this.envite = envite;
    }

    @Ignore
    public SentRequest(@NonNull String id, @NonNull String eid, @NonNull String status,
                           @NonNull String from, @NonNull String to, @NonNull Integer createdAt,
                           @NonNull Integer updatedAt){
        this.id = id;
        this.eid = eid;
        this.status = status;
        this.from = from;
        this.to = to;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getEid() {
        return eid;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    @NonNull
    public String getFrom() {
        return from;
    }

    @NonNull
    public String getTo() {
        return to;
    }

    @NonNull
    public Integer getCreatedAt() {
        return createdAt;
    }

    @NonNull
    public Integer getUpdatedAt() {
        return updatedAt;
    }

    @NonNull
    public User getRequestedTo() {
        return requestedTo;
    }

    @NonNull
    public Envite getEnvite() {
        return envite;
    }

    @NonNull
    public void setRequestedTo(User user) {
        this.requestedTo = user;
    }

    @NonNull
    public void setEnvite(Envite envite) {
        this.envite = envite;
    }
}
