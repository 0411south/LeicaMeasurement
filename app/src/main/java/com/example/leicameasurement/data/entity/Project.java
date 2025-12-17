package com.example.leicameasurement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore; // ✅ [新增] 导入 Ignore 注解
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "projects")
public class Project {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "project_id")
    public long projectId;

    @ColumnInfo(name = "project_name")
    public String projectName;

    @ColumnInfo(name = "creation_date")
    public Date creationDate;

    /**
     * Room 会使用这个无参构造函数来创建从数据库读取的对象。
     */
    public Project() {}

    /**
     * 这个构造函数方便我们在代码中创建新项目。
     * 使用 @Ignore 注解告诉 Room 在处理数据库时不使用此构造函数。
     */
    @Ignore // ✅✅✅ [已修复] 添加 @Ignore 注解，解决 Kapt 构造函数混淆问题
    public Project(String projectName) {
        this.projectName = projectName;
        this.creationDate = new Date(); // 自动设置当前时间为创建时间
    }
}
