package daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import entities.ReceivedRequest;

@Dao
public interface ReceivedRequestDao {
    @Query("SELECT * FROM envite_received_request_table")
    public LiveData<List<ReceivedRequest>> fetchAll();

    @Query("SELECT * FROM envite_received_request_table WHERE id = :id")
    public ListenableFuture<ReceivedRequest> getById(String id);

    @Query("DELETE FROM envite_received_request_table")
    public void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<List<Long>> insert(List<ReceivedRequest> receivedRequest);

    @Query("SELECT * FROM envite_received_request_table ORDER BY createdAt asc LIMIT 1")
    public ListenableFuture<ReceivedRequest> getLastItem();

    @Query("SELECT COUNT(id) FROM envite_received_request_table")
    public ListenableFuture<Integer> getRowCount();
}
