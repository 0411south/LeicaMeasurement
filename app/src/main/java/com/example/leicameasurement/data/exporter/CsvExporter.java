package com.example.leicameasurement.data.exporter;

import com.example.leicameasurement.data.entity.DetailPoint;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * CSV格式导出
 */
public class CsvExporter implements DataExporter {

    @Override
    public boolean exportCsv(List<DetailPoint> points, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.append("PointName,X,Y,Z\n");
            for (DetailPoint point : points) {
                writer.append(point.pointName)
                      .append(',')
                      .append(String.valueOf(point.x))
                      .append(',')
                      .append(String.valueOf(point.y))
                      .append(',')
                      .append(String.valueOf(point.z))
                      .append('\n');
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean exportDxf(List<DetailPoint> points, String path) {
        // Not implemented
        return false;
    }
}
