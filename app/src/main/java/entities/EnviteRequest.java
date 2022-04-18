package entities;


public class EnviteRequest {
    private String eid;

    private String status;

    private String from;

    private String to;

    private Integer createdAt;

    public EnviteRequest(String eid, String status, String from, String to, Integer createdAt){
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
}
