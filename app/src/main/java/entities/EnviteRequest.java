package entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "envite_request_table")
public class EnviteRequest {

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

    public EnviteRequest(@NonNull String id, @NonNull String eid, @NonNull String status,
                         @NonNull String from, @NonNull String to, @NonNull Integer createdAt){
        this.id = id;
        this.eid = eid;
        this.status = status;
        this.from = from;
        this.to = to;
        this.createdAt = createdAt;
    }

    public String getEid() {
        return eid;
    }

    public String getStatus() {
        return status;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnviteRequest that = (EnviteRequest) o;
        return id.equals(that.id) && eid.equals(that.eid) && status.equals(that.status) && from.equals(that.from) && to.equals(that.to) && createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eid, status, from, to, createdAt);
    }
}
