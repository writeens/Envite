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
    @Query("SELECT * FROM home_envite_table ORDER BY createdAt asc")
    public LiveData<List<HomeEnvite>> fetchAll();

    @Query("SELECT * FROM home_envite_table WHERE id = :enviteId")
    public ListenableFuture<HomeEnvite> getById(String enviteId);

    @Query("DELETE FROM home_envite_table")
    public ListenableFuture<Integer> deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<List<Long>> insert(List<HomeEnvite> homeEnvites);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<Long> insertOne(HomeEnvite homeEnvite);

    @Query("SELECT * FROM home_envite_table ORDER BY createdAt desc LIMIT 1")
    public ListenableFuture<HomeEnvite> getLastItem();

    @Query("SELECT COUNT(id) FROM home_envite_table")
    public ListenableFuture<Integer> getRowCount();
}
