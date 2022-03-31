package entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "envite_table")
public class Envite {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "word")
    private String mTitle;

    @NonNull
    private String mNote;


    public Envite(@NonNull String title, @NonNull String note) {
        this.mTitle = title;
        this.mNote = note;
    }

    public String getTitle(){return this.mTitle;}

    public String getNote(){return this.mNote;}
}
