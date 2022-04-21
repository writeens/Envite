package entities;


import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "envite_received_request_table")
public class ReceivedRequest {

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
    @Embedded(prefix = "requestedBy")
    private User requestedBy;

    @NonNull
    @Embedded(prefix = "envite")
    private Envite envite;

    public ReceivedRequest(@NonNull String id, @NonNull String eid, @NonNull String status,
                           @NonNull String from, @NonNull String to, @NonNull Integer createdAt,
                           @NonNull Integer updatedAt, @NonNull User requestedBy, @NonNull Envite envite) {
        this.id = id;
        this.eid = eid;
        this.status = status;
        this.from = from;
        this.to = to;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.requestedBy = requestedBy;
        this.envite = envite;
    }

    @Ignore
    public ReceivedRequest(@NonNull String id, @NonNull String eid, @NonNull String status,
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

    public void setStatus(String newStatus) {
        this.status = newStatus;
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

    public void setUpdatedAt(Integer newUpdatedAt) {
        this.updatedAt = newUpdatedAt;
    }

    @NonNull
    public User getRequestedBy() {
        return requestedBy;
    }

    @NonNull
    public Envite getEnvite() {
        return envite;
    }

    @NonNull
    public void setRequestedBy(User user) {
        this.requestedBy = user;
    }

    @NonNull
    public void setEnvite(Envite envite) {
        this.envite = envite;
    }
}
