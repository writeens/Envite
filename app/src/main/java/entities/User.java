package entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "envite_user_table")
public class User {

    @PrimaryKey
    @NonNull
    private String uid;

    @NonNull
    private String email;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String profileUrl;

    @NonNull
    private String q1;

    @NonNull
    private String q2;

    @NonNull
    private Integer createdAt;

    @NonNull
    private String updatedAt;

    public User(@NonNull String uid, @NonNull String firstName, @NonNull String lastName,
                @NonNull String profileUrl, @NonNull String email, @NonNull String q1,
                @NonNull String q2, @NonNull int createdAt, @NonNull String updatedAt) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileUrl = profileUrl;
        this.q1 = q1;
        this.q2 = q2;
        this.updatedAt = updatedAt;
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

    public String getUpdatedAt() {
        return updatedAt;
    }
}
