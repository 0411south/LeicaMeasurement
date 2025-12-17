// AppDatabase.java (最终修复版本)
package com.example.leicameasurement.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// ✅ [已修复] 导入项目中所有已知的 DAO 接口
import com.example.leicameasurement.data.dao.DetailPointDao;
import com.example.leicameasurement.data.dao.MeasurementDao;
import com.example.leicameasurement.data.dao.ProjectDao;
import com.example.leicameasurement.data.dao.StationDao;
import com.example.leicameasurement.data.dao.TraverseStationDao;
import com.example.leicameasurement.data.dao.TraverseTaskDao;
// 注意：如果 MeasurementParam 有对应的 DAO，也需要在这里导入

// ✅ [已修复] 导入项目中所有已知的 Entity 类
import com.example.leicameasurement.data.entity.DetailPoint;
import com.example.leicameasurement.data.entity.MeasurementPoint;
import com.example.leicameasurement.data.entity.Project;
import com.example.leicameasurement.data.entity.Station;
import com.example.leicameasurement.data.entity.TraverseStation;
import com.example.leicameasurement.data.entity.TraverseTask;

// 导入 Converters，但你的项目结构显示 Converters 在 database 包下，我已更正路径


/**
 * 应用程序的 Room 数据库主类。
 * ✅ [关键修复] 在 entities 数组中注册了项目里所有的实体类。
 * ✅ [关键修复] 数据库版本号增加到 2，因为我们改变了数据库结构（增加了表）。
 */
@Database(entities = {
        Project.class,
        Station.class,
        MeasurementPoint.class,
        DetailPoint.class,
        TraverseStation.class,
        TraverseTask.class
}, version = 2, exportSchema = false) // 版本号增加到 2
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // ✅ [已修复] 为所有在 App 中使用的 DAO 提供抽象的 "getter" 方法
    public abstract ProjectDao projectDao();
    public abstract StationDao stationDao();
    public abstract MeasurementDao measurementDao();
    public abstract DetailPointDao detailPointDao();
    public abstract TraverseStationDao traverseStationDao();
    public abstract TraverseTaskDao traverseTaskDao();
    // 注意：如果 MeasurementParam 有对应的 DAO，也需要在这里添加

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "survey_database")
                            // 数据库结构发生变化，需要添加迁移策略。这里使用最简单的破坏性迁移。
                            // 这会清除所有旧数据，在开发阶段是安全的。
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

