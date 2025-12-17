// MeasurementPoint.java 的完整代码
package com.example.leicameasurement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 代表一个测量点 (MeasurementPoint) 的实体类，对应数据库中的 "measurement_points" 表。
 * 它通过外键与 Station 相关联。
 */
@Entity(tableName = "measurement_points",
        foreignKeys = @ForeignKey(entity = Station.class,
                parentColumns = "station_id",
                childColumns = "station_id_fk",
                onDelete = ForeignKey.CASCADE)) // 删除测站时，其下的所有测量点也一并删除
public class MeasurementPoint {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "point_id")
    public long pointId;

    @ColumnInfo(name = "point_name")
    public String pointName;

    // 这是外键列，它引用了 Station 表的 station_id
    @ColumnInfo(name = "station_id_fk", index = true)
    public long stationIdFk;

    // Room 需要一个无参构造函数
    public MeasurementPoint() {}

    // 方便我们自己创建对象的构造函数，用 @Ignore 告诉 Room 忽略它
    @Ignore
    public MeasurementPoint(String pointName, long stationIdFk) {
        this.pointName = pointName;
        this.stationIdFk = stationIdFk;
    }
}
