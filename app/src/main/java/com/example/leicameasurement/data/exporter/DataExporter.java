package com.example.leicameasurement.data.exporter;

import com.example.leicameasurement.data.entity.DetailPoint;
import java.util.List;

/**
 * 导出总控
 */
public interface DataExporter {

    boolean exportCsv(List<DetailPoint> points, String path);

    boolean exportDxf(List<DetailPoint> points, String path);
}
