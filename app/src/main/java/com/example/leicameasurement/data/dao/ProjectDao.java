package com.example.leicameasurement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.leicameasurement.data.entity.Project;

import java.util.List;

@Dao
public interface ProjectDao {
    @Insert
    void insert(Project project);

    @Query("SELECT * FROM projects ORDER BY creation_date DESC")
    LiveData<List<Project>> getAllProjects();

    @Query("SELECT * FROM projects WHERE project_id = :projectId")
    Project getProjectById(long projectId);
}
