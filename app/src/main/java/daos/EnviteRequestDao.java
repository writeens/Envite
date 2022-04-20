package daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;

import entities.Envite;
import entities.EnviteRequest;

@Dao
public interface EnviteRequestDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    public void insertEnviteRequest(EnviteRequest enviteRequest);
//
//    @Query("SELECT * FROM envite_request_table WHERE envite_request_table.`to` = :uid")
//    public ListenableFuture<List<EnviteRequest>> fetchReceivedEnviteRequests(String uid);
//
//    @Query("SELECT * FROM envite_request_table WHERE envite_request_table.`from` = :uid")
//    public ListenableFuture<List<EnviteRequest>> fetchSentEnviteRequests(String uid);
//
//    @Query("SELECT * FROM envite_request_table JOIN envite_table ON envite_request_table.eid = envite_table.id WHERE envite_request_table.`to` = :uid")
//    public LiveData<Map<EnviteRequest, List<Envite>>> fetchReceivedEnvites(String uid);
//
//    @Query("SELECT * FROM envite_request_table JOIN envite_table ON envite_request_table.eid = envite_table.id WHERE envite_request_table.`from` = :uid")
//    public LiveData<Map<EnviteRequest, List<Envite>>> fetchSentEnvites(String uid);

}
