package com.example.leicameasurement.controller;

import com.example.leicameasurement.calculation.TraverseCalculator;
import com.example.leicameasurement.data.entity.TraverseStation;
import com.example.leicameasurement.data.repository.TraverseRepository;
import com.example.leicameasurement.data.wal.WalLogManager;
import com.example.leicameasurement.device.adapter.InstrumentAdapter;
import com.example.leicameasurement.device.protocol.InstrumentException;

/**
 * 导线测量流程（测站→后视→前视→平差）
 */
public class TraverseController {

    private final InstrumentAdapter instrument;
    private final TraverseCalculator calculator;
    private final TraverseRepository repository;
    private final WalLogManager walLogManager;

    public TraverseController(InstrumentAdapter instrument, TraverseCalculator calculator, TraverseRepository repository, WalLogManager walLogManager) {
        this.instrument = instrument;
        this.calculator = calculator;
        this.repository = repository;
        this.walLogManager = walLogManager;
    }

    public void startTraverse(TraverseStation station) {
        try {
            instrument.setStation(station.x, station.y, station.h, station.instrumentHeight);
        } catch (InstrumentException e) {
            throw new RuntimeException(e);
        }
    }

    public void measureBacksight(double prismHeight) {
        try {
            double[] backsightData = instrument.measureBacksight(prismHeight);
        } catch (InstrumentException e) {
            throw new RuntimeException(e);
        }
        // walLogManager.log(backsightData);
        // double azimuth = calculator.calculateBacksightAzimuth(station, backsightData);
        // repository.saveBacksight(azimuth);
    }

    public void measureForesight(double prismHeight) {
        try {
            double[] foresightData = instrument.measureForepoint(prismHeight);
        } catch (InstrumentException e) {
            throw new RuntimeException(e);
        }
        // walLogManager.log(foresightData);
        // double[] coordinates = calculator.calculateForesightCoordinates(station, foresightData);
        // repository.saveForesight(coordinates);
    }
}
