package daos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import entities.Envite;

@Dao
public interface EnviteDao {
    @Query("SELECT * FROM envite_table ORDER BY createdAt desc")
    public LiveData<List<Envite>> fetchAllEnvites();

    @Query("SELECT * FROM envite_table WHERE displayTag = :tag ORDER BY createdAt desc")
    public LiveData<List<Envite>> fetchEnviteByDisplayTag(String tag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEnvite(Envite envite);

    @Query("DELETE FROM envite_table")
    public void deleteAllEnvites();

    @Query("SELECT COUNT(id) FROM envite_table")
    public ListenableFuture<Integer> getRowCount();

    @Query("SELECT * FROM envite_table WHERE displayTag = :tag ORDER BY createdAt asc LIMIT 1")
    public ListenableFuture<Envite> getOldestEnvite(String tag);
}
