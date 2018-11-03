package de.tum.in.tumcampusapp.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.tum.in.tumcampusapp.model.studyroom.StudyRoom;

@Dao
public interface StudyRoomDao {

    @Query("SELECT * FROM study_rooms WHERE group_id = :groupId")
    List<StudyRoom> getAll(int groupId);

    @Query("DELETE FROM study_rooms")
    void removeCache();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StudyRoom... studyRooms);

}
