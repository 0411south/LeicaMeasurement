package com.example.leicameasurement.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.leicameasurement.R;
import com.example.leicameasurement.controller.TraverseController;
import com.example.leicameasurement.data.entity.TraverseStation;
import com.example.leicameasurement.infrastructure.DependencyInjector;

public class TraverseActivity extends AppCompatActivity {

    private Button btnSetStation;
    private Button btnMeasureBacksight;
    private Button btnMeasureForesight;
    private TextView traverseResults;

    private TraverseController traverseController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traverse);

        btnSetStation = findViewById(R.id.btn_set_station);
        btnMeasureBacksight = findViewById(R.id.btn_measure_backsight);
        btnMeasureForesight = findViewById(R.id.btn_measure_foresight);
        traverseResults = findViewById(R.id.traverse_results);

        traverseController = DependencyInjector.provideTraverseController(this);

        btnSetStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, we will use a dummy station.
                // In a real app, this would come from user input.
                TraverseStation station = new TraverseStation();
                station.x = 1000;
                station.y = 1000;
                station.h = 100;
                station.instrumentHeight = 1.5;
                traverseController.startTraverse(station);
                traverseResults.setText("Station set at (1000, 1000, 100)");
            }
        });

        btnMeasureBacksight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, we will use a dummy prism height.
                traverseController.measureBacksight(1.6);
                traverseResults.setText("Backsight measured.");
            }
        });

        btnMeasureForesight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, we will use a dummy prism height.
                traverseController.measureForesight(1.6);
                traverseResults.setText("Foresight measured.");
            }
        });
    }
}