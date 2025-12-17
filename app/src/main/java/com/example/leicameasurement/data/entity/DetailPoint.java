package com.example.leicameasurement.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.leicameasurement.device.adapter.InstrumentAdapter;

/**
 * 碎步点实体（点号/X/Y/Z/所属任务）
 */
@Entity(tableName = "detail_points",
        foreignKeys = @ForeignKey(entity = TraverseTask.class,
                parentColumns = "taskId",
                childColumns = "taskId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("taskId")})
public class DetailPoint {

    @PrimaryKey(autoGenerate = true)
    public long pointId;

    public long taskId;

    public String pointName;

    public double x;

    public double y;

    public double z;

    // 新增测量相关字段
    public double horizontalAngle;  // 水平角 (弧度)
    public double verticalAngle;    // 垂直角 (弧度)
    public double slopeDistance;    // 斜距 (米)
    public double prismHeight;      // 棱镜高 (米)

    // 测量模式 - 需要存储为字符串，因为Room不支持枚举直接存储
    public String measureMode;

    public long timestamp;          // 测量时间戳

    /**
     * 这是Room会使用的主构造函数。
     * 它包含了所有需要从数据库读取并填充的字段。
     */
    public DetailPoint(long taskId, String pointName, double x, double y, double z,
                       double horizontalAngle, double verticalAngle, double slopeDistance,
                       double prismHeight, String measureMode, long timestamp) {
        this.taskId = taskId;
        this.pointName = pointName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontalAngle = horizontalAngle;
        this.verticalAngle = verticalAngle;
        this.slopeDistance = slopeDistance;
        this.prismHeight = prismHeight;
        this.measureMode = measureMode;
        this.timestamp = timestamp;
    }

    /**
     * 告诉Room忽略这个构造函数。
     * 它现在是一个方便你在代码中创建新对象的工具。
     */
    @Ignore
    public DetailPoint() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 告诉Room忽略这个构造函数。
     * 这是一个用于创建仅有坐标信息的点的便捷构造函数。
     */
    @Ignore
    public DetailPoint(String pointName, double x, double y, double z, long taskId) {
        this(); // 调用无参构造函数以设置时间戳
        this.pointName = pointName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.taskId = taskId;
    }

    /**
     * 告诉Room忽略这个构造函数。
     * 这是一个用于创建包含完整测量信息的点的便捷构造函数。
     */
    @Ignore
    public DetailPoint(String pointName, double x, double y, double z,
                       double horizontalAngle, double verticalAngle, double slopeDistance,
                       double prismHeight, InstrumentAdapter.MeasureMode measureMode, long taskId) {
        this(pointName, x, y, z, taskId); // 调用上一个便捷构造函数
        this.horizontalAngle = horizontalAngle;
        this.verticalAngle = verticalAngle;
        this.slopeDistance = slopeDistance;
        this.prismHeight = prismHeight;
        this.setMeasureModeEnum(measureMode); // 使用setter方法来保证逻辑统一
    }

    // --- Getter 和 Setter 方法 ---
    public long getPointId() { return pointId; }
    public void setPointId(long pointId) { this.pointId = pointId; }

    public long getTaskId() { return taskId; }
    public void setTaskId(long taskId) { this.taskId = taskId; }

    public String getPointName() { return pointName; }
    public void setPointName(String pointName) { this.pointName = pointName; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public double getHorizontalAngle() { return horizontalAngle; }
    public void setHorizontalAngle(double horizontalAngle) { this.horizontalAngle = horizontalAngle; }

    public double getVerticalAngle() { return verticalAngle; }
    public void setVerticalAngle(double verticalAngle) { this.verticalAngle = verticalAngle; }

    public double getSlopeDistance() { return slopeDistance; }
    public void setSlopeDistance(double slopeDistance) { this.slopeDistance = slopeDistance; }

    public double getPrismHeight() { return prismHeight; }
    public void setPrismHeight(double prismHeight) { this.prismHeight = prismHeight; }

    public String getMeasureMode() { return measureMode; }
    public void setMeasureMode(String measureMode) { this.measureMode = measureMode; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    /**
     * 获取测量模式枚举（从字符串转换）
     */
    public InstrumentAdapter.MeasureMode getMeasureModeEnum() {
        if (measureMode == null) {
            return InstrumentAdapter.MeasureMode.STANDARD;
        }
        try {
            return InstrumentAdapter.MeasureMode.valueOf(measureMode);
        } catch (IllegalArgumentException e) {
            return InstrumentAdapter.MeasureMode.STANDARD;
        }
    }

    /**
     * 设置测量模式枚举（转换为字符串存储）
     */
    public void setMeasureModeEnum(InstrumentAdapter.MeasureMode mode) {
        this.measureMode = (mode == null) ? null : mode.name();
    }

    /**
     * 获取坐标字符串
     */
    public String getCoordinateString() {
        return String.format("X: %.3f, Y: %.3f, Z: %.3f", x, y, z);
    }

    /**
     * 获取测量数据字符串
     */
    public String getMeasurementString() {
        return String.format("Hz: %.4f°, V: %.4f°, SD: %.3fm",
                Math.toDegrees(horizontalAngle),
                Math.toDegrees(verticalAngle),
                slopeDistance);
    }

    /**
     * 获取完整信息字符串
     */
    public String getFullInfoString() {
        // ✅ [已修复] 使用 '+' 号将跨越多行的字符串拼接起来，以符合Java语法
        return String.format("点号: %s " +
                "坐标: X=%.3f, Y=%.3f, Z=%.3f " +
                "测量: Hz=%.4f°, V=%.4f°, SD=%.3fm " +
                "棱镜高: %.3fm, 模式: %s",
                pointName, x, y, z,
                Math.toDegrees(horizontalAngle), Math.toDegrees(verticalAngle), slopeDistance,
                prismHeight, getMeasureModeEnum());
    }

    /**
     * 重写toString方法，用于调试和日志输出
     */
    @Override
    public String toString() {
        // ✅ [已修复] 确保了标准的toString()格式，避免了语法错误
        return "DetailPoint{" +
                "pointId=" + pointId +
                ", pointName='" + pointName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", taskId=" + taskId +
                '}';
    }
}
