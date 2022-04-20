package daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import entities.SentRequest;

@Dao
public interface SentRequestDao {
    @Query("SELECT * FROM envite_sent_request_table")
    public LiveData<List<SentRequest>> fetchAll();

    @Query("SELECT * FROM envite_sent_request_table WHERE id = :id")
    public ListenableFuture<SentRequest> getById(String id);

    @Query("DELETE FROM envite_sent_request_table")
    public void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<List<Long>> insert(List<SentRequest> sentRequests);

    @Query("SELECT * FROM envite_sent_request_table ORDER BY createdAt asc LIMIT 1")
    public ListenableFuture<SentRequest> getLastItem();

    @Query("SELECT COUNT(id) FROM envite_sent_request_table")
    public ListenableFuture<Integer> getRowCount();
}
