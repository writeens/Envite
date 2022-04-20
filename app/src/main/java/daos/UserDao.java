package daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import entities.User;

@Dao
public interface UserDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    public void insertUser(User user);
}
