// Station.java 的完整代码
package com.example.leicameasurement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 代表一个测站 (Station) 的实体类，对应数据库中的 "stations" 表。
 * 它通过外键与 Project 相关联。
 */
@Entity(tableName = "stations",
        foreignKeys = @ForeignKey(entity = Project.class,
                parentColumns = "project_id",
                childColumns = "project_id_fk",
                onDelete = ForeignKey.CASCADE)) // CASCADE 表示删除项目时，其下的所有测站也一并删除
public class Station {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "station_id")
    public long stationId;

    @ColumnInfo(name = "station_name")
    public String stationName;

    // 这是外键列，它引用了 Project 表的 project_id
    // index = true 可以加快查询速度
    @ColumnInfo(name = "project_id_fk", index = true)
    public long projectIdFk;

    // Room 需要一个无参构造函数
    public Station() {}

    // 方便我们自己创建对象的构造函数，用 @Ignore 告诉 Room 忽略它
    @Ignore
    public Station(String stationName, long projectIdFk) {
        this.stationName = stationName;
        this.projectIdFk = projectIdFk;
    }
}
