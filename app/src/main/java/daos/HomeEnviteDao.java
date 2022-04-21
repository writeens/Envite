package daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import entities.HomeEnvite;

@Dao
public interface HomeEnviteDao {
    @Query("SELECT * FROM home_envite_table")
    public LiveData<List<HomeEnvite>> fetchAll();

    @Query("SELECT * FROM home_envite_table WHERE id = :enviteId")
    public ListenableFuture<HomeEnvite> getById(String enviteId);

    @Query("DELETE FROM home_envite_table")
    public void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<List<Long>> insert(List<HomeEnvite> homeEnvites);

    @Query("SELECT * FROM home_envite_table ORDER BY createdAt asc LIMIT 1")
    public ListenableFuture<HomeEnvite> getLastItem();

    @Query("SELECT COUNT(id) FROM home_envite_table")
    public ListenableFuture<Integer> getRowCount();
}
