package daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import entities.MyEnvites;

@Dao
public interface MyEnvitesDao {
    @Query("SELECT * FROM my_envites_table")
    public LiveData<List<MyEnvites>> fetchAll();

    @Query("SELECT * FROM my_envites_table WHERE id = :enviteId")
    public ListenableFuture<MyEnvites> getById(String enviteId);

    @Query("DELETE FROM my_envites_table")
    public void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<List<Long>> insert(List<MyEnvites> myEnvites);

    @Query("SELECT * FROM my_envites_table ORDER BY createdAt asc LIMIT 1")
    public ListenableFuture<MyEnvites> getLastItem();

    @Query("SELECT COUNT(id) FROM my_envites_table")
    public ListenableFuture<Integer> getRowCount();


}
