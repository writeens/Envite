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

import daos.MyEnvitesDao;
import daos.ReceivedRequestDao;
import entities.MyEnvites;
import entities.ReceivedRequest;

@Database(entities = {MyEnvites.class, ReceivedRequest.class}, version = 1, exportSchema = false)
public abstract class EnviteRoomDatabase extends RoomDatabase {
    public abstract MyEnvitesDao myEnvitesDao();
    public abstract ReceivedRequestDao receivedRequestDao();


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
