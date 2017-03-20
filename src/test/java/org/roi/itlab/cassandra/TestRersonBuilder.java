package org.roi.itlab.cassandra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.Frequency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.person.PersonBuilder;
import org.roi.itlab.cassandra.person.PersonBuilderImpl;
import org.roi.itlab.cassandra.person.PersonDirector;
import org.roi.itlab.cassandra.random_attributes.RandomGenerator;


/**
 * Created by Vadim on 26.02.2017.
 */
public class TestRersonBuilder {
    List<Person> list = new ArrayList<>();
    int mean_mean = 0;

    Frequency workStartTime = new Frequency();
    Frequency age = new Frequency();
    List<Comparable<?>> mode;

    @Before
    public  void init()
    {
        PersonBuilder rab = new PersonBuilderImpl();
        PersonDirector rad = new PersonDirector(rab);

        for(int j = 0 ; j < 1000000;++j) {
            list.add(rad.constract());
            age.addValue(list.get(j).getAge());
            mean_mean += list.get(j).getAge();
            workStartTime.addValue(list.get(j).getWorkStart());
        }
        mean_mean = mean_mean / list.size();

        mode = age.getMode();
/*

        Iterator<Comparable<?>> iter = age.valuesIterator();
        StringBuilder outBuffer = new StringBuilder();
        while (iter.hasNext())
        {
            Comparable<?> value = iter.next();
            outBuffer.append(value);
            outBuffer.append('\t');
            outBuffer.append(age.getCount(value));
            outBuffer.append('\n');
        }
        System.out.println(outBuffer.toString());

        for(int i = 0 ; i < 15; ++i){
            System.out.println(list.get(i));
        }
*/

    }

    @Test
    public void TestBuilder() throws Exception {
        Assert.assertEquals(mean_mean, 30, 15);
        Assert.assertEquals(age.getUniqueCount(), 73);
        Assert.assertEquals(mode.get(0).toString(), "30");
        Assert.assertEquals(workStartTime.getUniqueCount(), 13);
    }
}
