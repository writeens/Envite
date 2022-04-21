package daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import entities.MyEnvite;

@Dao
public interface MyEnviteDao {
    @Query("SELECT * FROM my_envite_table")
    public LiveData<List<MyEnvite>> fetchAll();

    @Query("SELECT * FROM my_envite_table WHERE id = :enviteId")
    public ListenableFuture<MyEnvite> getById(String enviteId);

    @Query("DELETE FROM my_envite_table")
    public void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<List<Long>> insert(List<MyEnvite> myEnvites);

    @Query("SELECT * FROM my_envite_table ORDER BY createdAt asc LIMIT 1")
    public ListenableFuture<MyEnvite> getLastItem();

    @Query("SELECT COUNT(id) FROM my_envite_table")
    public ListenableFuture<Integer> getRowCount();


}
