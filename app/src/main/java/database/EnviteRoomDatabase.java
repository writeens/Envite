package database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import daos.MyEnviteDao;
import daos.ReceivedRequestDao;
import daos.SentRequestDao;
import entities.MyEnvite;
import entities.ReceivedRequest;
import entities.SentRequest;

@Database(entities = {MyEnvite.class, ReceivedRequest.class, SentRequest.class}, version = 1, exportSchema = false)
public abstract class EnviteRoomDatabase extends RoomDatabase {
    public abstract MyEnviteDao myEnviteDao();
    public abstract ReceivedRequestDao receivedRequestDao();
    public abstract SentRequestDao sentRequestDao();


    private static volatile EnviteRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static EnviteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EnviteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EnviteRoomDatabase.class, "envite_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Log.i("THIS", "THIS IS TRIGGERED");
        }
    };
}
