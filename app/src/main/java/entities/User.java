package entities;

import androidx.annotation.NonNull;

public class User {
    private String uid;

    private String email;

    private String firstName;

    private String lastName;

    private String profileUrl;

    private String q1;

    private String q2;

    private Integer createdAt;

    private String createdBy;

    public User(String uid, String firstName, String lastName, String profileUrl,
                  String email, String q1, String q2, String createdBy, int createdAt) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileUrl = profileUrl;
        this.q1 = q1;
        this.q2 = q2;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getQ1() {
        return q1;
    }

    public String getQ2() {
        return q2;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
