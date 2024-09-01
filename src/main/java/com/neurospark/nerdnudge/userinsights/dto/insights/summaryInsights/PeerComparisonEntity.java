package com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights;

import lombok.Data;

import java.util.List;

@Data
public class PeerComparisonEntity {
    private List<Double> easy;
    private List<Double> medium;
    private List<Double> hard;

    private double userAverage;
    private double peersAverage;
}
