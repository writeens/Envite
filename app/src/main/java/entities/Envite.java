package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "envite_table")
public class Envite {

    @PrimaryKey
    @NonNull
    private String mTitle;

    @NonNull
    private String mNote;

    @NonNull
    private Number mPrice;


    public Envite(@NonNull String title, @NonNull String note, @NonNull Number price) {
        this.mTitle = title;
        this.mNote = note;
        this.mPrice = price;
    }

    public String getTitle(){return this.mTitle;}

    public String getNote(){return this.mNote;}

    public String getFormattedPrice(){return "£" + this.mPrice + "/month";}
}
