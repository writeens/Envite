package entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "my_envite_table")
public class MyEnvite {
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
    private String createdByImageUrl;


    public MyEnvite(@NonNull String id, @NonNull String title,
                    @NonNull String location, @NonNull String placeId,
                    @NonNull String price, @NonNull String note,
                    @NonNull Integer createdAt, @NonNull Integer updatedAt,
                    @NonNull String imageUrl, @NonNull String createdBy,
                    @NonNull String createdByImageUrl) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.placeId = placeId;
        this.price = price;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imageUrl = imageUrl;
        this.createdBy = createdBy;
        this.createdByImageUrl = createdByImageUrl;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    public String getPrice() {
        return price;
    }

    @NonNull
    public String getNote() {
        return note;
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
    public String getImageUrl() {
        return imageUrl;
    }

    @NonNull
    public String getCreatedBy() {
        return createdBy;
    }

    @NonNull
    public String getCreatedByImageUrl() {
        return createdByImageUrl;
    }

    public String getFormattedPrice(){return "£" + this.price + "/month";}
}
