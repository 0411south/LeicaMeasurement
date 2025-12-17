package com.example.leicameasurement.data.exporter;

import com.example.leicameasurement.data.entity.DetailPoint;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * DXF格式导出
 */
public class DxfExporter implements DataExporter {

    @Override
    public boolean exportCsv(List<DetailPoint> points, String path) {
        // Not implemented
        return false;
    }

    @Override
    public boolean exportDxf(List<DetailPoint> points, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            // DXF Header
            writer.append("0\nSECTION\n2\nHEADER\n9\n$ACADVER\n1\nAC1009\n0\nENDSEC\n");
            writer.append("0\nSECTION\n2\nTABLES\n0\nTABLE\n2\nLTYPE\n70\n1\n0\nLTYPE\n2\nCONTINUOUS\n70\n64\n3\nSolid line\n72\n65\n73\n0\n40\n0.0\n0\nENDTAB\n0\nENDSEC\n");
            writer.append("0\nSECTION\n2\nENTITIES\n");

            for (DetailPoint point : points) {
                // Point entity
                writer.append("0\nPOINT\n8\n0\n"); // Layer 0
                writer.append("10\n").append(String.valueOf(point.x)).append("\n"); // X
                writer.append("20\n").append(String.valueOf(point.y)).append("\n"); // Y
                writer.append("30\n").append(String.valueOf(point.z)).append("\n"); // Z

                // Text entity for point name
                writer.append("0\nTEXT\n8\n0\n"); // Layer 0
                writer.append("10\n").append(String.valueOf(point.x)).append("\n"); // X
                writer.append("20\n").append(String.valueOf(point.y)).append("\n"); // Y
                writer.append("30\n").append(String.valueOf(point.z)).append("\n"); // Z
                writer.append("40\n1.0\n"); // Text height
                writer.append("1\n").append(point.pointName).append("\n"); // Text string
            }

            writer.append("0\nENDSEC\n0\nEOF\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
