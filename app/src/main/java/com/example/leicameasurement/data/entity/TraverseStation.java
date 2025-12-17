package com.example.leicameasurement.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 测站实体（任务ID/测站号/坐标/仪器高）
 */
@Entity(tableName = "traverse_stations",
        foreignKeys = @ForeignKey(entity = TraverseTask.class,
                                  parentColumns = "taskId",
                                  childColumns = "taskId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("taskId")})
public class TraverseStation {

    @PrimaryKey(autoGenerate = true)
    public long stationId;

    public long taskId;

    public String stationName;

    public double x;

    public double y;

    public double h;

    public double instrumentHeight;
}
