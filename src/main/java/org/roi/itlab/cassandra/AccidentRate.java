package org.roi.itlab.cassandra;

import org.apache.commons.math3.random.RandomGenerator;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.random_attributes.IntensityNormalGenerator;
import org.roi.itlab.cassandra.random_attributes.NormalGenerator;

import java.time.LocalTime;

/**
 * @author Anna Striganova
 */
public class AccidentRate {

    private IntensityMap intensityMap;
    private NormalGenerator normalGenerator;
    private final static double averageAccidentProbability = 1.0 / 80_000_000;
    private final static double onewwayFactor = 0.75;
    private RandomGenerator rng;

    public AccidentRate(IntensityMap intensityMap, RandomGenerator rng) {
        this.intensityMap = intensityMap;
        this.rng = rng;
        this.normalGenerator = new IntensityNormalGenerator(intensityMap.getMaxIntensity(), rng);
    }

    public double getProbability(Person person) {
        double normalizedDistance = getNormalizedDistance(person.getToWork(), person.getWorkStart()) + getNormalizedDistance(person.getToHome(), person.getWorkEnd());
        return averageAccidentProbability * normalizedDistance * person.getRushFactor() / person.getSkill();
    }

    public int calculateAccidents(Person person, int days) {
        int accidents = 0;
        double probability = getProbability(person);
        for (int i = 0; i < days; i++) {
            accidents += rng.nextDouble() < probability ? 1 : 0;
        }
        person.setProbability(1 - Math.pow(1 - probability, days));
        return accidents;
    }

    private double getNormalizedDistance(Route route, LocalTime startTime) {
        long time = startTime.toSecondOfDay() * 1000;
        double result = 0;
        Edge[] edges = route.getEdges();
        boolean[] directions = route.getDirections();
        for (int i = 0; i < edges.length; i++) {
            boolean direction = directions[i];
            Edge e = edges[i];
            if (e.isOneWay()) {
                result += e.getDistance() * normalGenerator.getRandomDouble(intensityMap.getIntensity(e, time, direction)) * onewwayFactor;
            } else {
                result += e.getDistance() * normalGenerator.getRandomDouble(intensityMap.getIntensity(e, time, direction));
            }
            time += e.getTime();
        }
        return result;
    }

    private void calculateRouteAccidents(Person  person, Route route, long time, int days){
        Edge[] edges = route.getEdges();
        boolean[] directions = route.getDirections();
        double probability = 0.0;
        for (int i = 0; i < edges.length; i++) {
            double d = rng.nextDouble();
            probability =  1 - Math.pow(1 - averageAccidentProbability *
                    getNormalizedEdgeDistance(edges[i], time, directions[i]) * person.getRushFactor() /
                    person.getSkill(), days);
            if(d < probability){
                person.setEdgeWithAccidentc(edges[i]);
                person.setAccidents(person.getAccidents()+1);
            }
            time += edges[i].getTime();
        }
    }

    public int calculateAccidentsAlter(Person person, int days) {
        calculateRouteAccidents(person,person.getToWork(), person.getWorkStart().getHour()*1000, days);
        calculateRouteAccidents(person,person.getToHome(), person.getWorkEnd().getHour()*1000, days);
        return person.getAccidents();
    }

    private double getNormalizedEdgeDistance(Edge edge, long time, boolean direction){
        if (edge.isOneWay()) {
            return edge.getDistance() * normalGenerator.getRandomDouble(intensityMap.getIntensity(edge, time, direction)) * onewwayFactor;
        } else {
            return edge.getDistance() * normalGenerator.getRandomDouble(intensityMap.getIntensity(edge, time, direction));
        }
    }
}
