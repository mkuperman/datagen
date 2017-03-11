package org.roi.itlab.cassandra.random_attributes;

import org.apache.commons.math3.analysis.interpolation.*;

/**
 * author Anush
 */
public class WorkDurationRandomGenerator extends RandomGeneratorBuilder {

    @Override
    public void buildGenerator() {
        double[] x = {4.0,8.0,12.0};
        double[] y  = {1.0,4.0,2.0};
        LinearInterpolator li = new LinearInterpolator();
        randomGenerator = new RandomGenerator();
        randomGenerator.setPsf(li.interpolate(x,y));
        randomGenerator.setProportionalWeight(4);

    }
}
