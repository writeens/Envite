package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "envite_table")
public class Envite {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String title;

    @NonNull
    private String note;

    @NonNull
    private int price;

    @NonNull
    private String placeId;

    @NonNull
    private String location;

    @NonNull
    private String imageUrl;

    @NonNull
    private String createdBy;

    @NonNull
    @ColumnInfo
    private int createdAt;

    @NonNull
    private int updatedAt;

    @NonNull
    private String displayTag;


    public Envite(@NonNull String id, @NonNull String title, @NonNull String note, @NonNull int price,
    @NonNull String placeId, @NonNull String location, @NonNull String imageUrl,
    @NonNull String createdBy, @NonNull int createdAt, @NonNull int updatedAt, @NonNull String displayTag) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.price = price;
        this.placeId = placeId;
        this.location = location;
        this.imageUrl = imageUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.displayTag = displayTag;
    }

    public String getId(){return this.id;}


    public String getTitle(){return this.title;}

    public String getNote(){return this.note;}

    public int getPrice(){return this.price;}


    public String getFormattedPrice(){return "£" + this.price + "/month";}

    public String getPlaceId() {
        return placeId;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public Date getCreatedAtFormatted() {
        final Date date = new Date(Long.parseLong(createdAt + ""));
        return date;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    @NonNull
    public String getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(String tag) {
        this.displayTag = tag;
    }

}
