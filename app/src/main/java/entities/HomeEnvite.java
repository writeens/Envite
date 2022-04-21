package entities;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;

public class HomeEnvite {
    @NonNull
    @PrimaryKey
    private String id;
    @NonNull
    private String title;
    @NonNull
    private String location;
    @NonNull
    private String placeId;
    @NonNull
    private String price;
    @NonNull
    private String note;
    @NonNull
    private Integer createdAt;
    @NonNull
    private Integer updatedAt;
    @NonNull
    private String imageUrl;
    @NonNull
    private String createdBy;
    @NonNull
    @Embedded(prefix = "createdByUser")
    private User createdByUser;
}
