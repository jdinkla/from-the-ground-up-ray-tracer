package net.dinkla.raytracer.samplers;

import net.dinkla.raytracer.math.Point2DF;
import net.dinkla.raytracer.math.Point3DF;
import net.dinkla.raytracer.math.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 21.05.2010
 * Time: 18:11:32
 * To change this template use File | Settings | File Templates.
 */
public class Sampler {

    protected Generator sampler;

    protected int numSamples;
    protected int numSets;

	protected ArrayList<Integer> shuffledIndices;
    protected ArrayList<Point2DF> samples;
	protected ArrayList<Point2DF> diskSamples;
	protected ArrayList<Point3DF> hemisphereSamples;
	protected ArrayList<Point3DF> sphereSamples;

    protected int count;
    protected int jump;

    public Sampler(Generator sampler, int numSamples, int numSets) {
        this.sampler = sampler;
        this.numSamples = numSamples;
        this.numSets = numSets;
        count = 0;
        jump = 0;
        setupShuffledIndices();
        samples = new ArrayList<Point2DF>();
        samples.ensureCapacity(numSamples * numSets);
        sampler.generateSamples(numSamples, numSets, samples);
    }

    public void setupShuffledIndices() {
        shuffledIndices = new ArrayList<Integer>();
        shuffledIndices.ensureCapacity(numSamples * numSets);

        // Create temporary array 
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int j = 0; j < numSamples; j++) {
            indices.add(j);
        }

        for (int p = 0; p < numSets; p++) {
            Random.randomShuffle(indices);
            shuffledIndices.addAll(indices);
//            for (int j = 0; j < indices.size(); j++) {
//                shuffledIndices.add(indices.get(j));
//            }
        }
    }

    public void shuffleSamples() {

    }

    public Point2DF sampleUnitSquare() {
	    if (count % numSamples == 0) {
		    jump = Random.randInt(numSets) * numSamples;
        }
        int index1 = jump + count++ % numSamples;
        int index2 = jump + shuffledIndices.get(index1);
	    return (samples.get(index2));
    }
    
    public Point2DF sampleUnitDisk() {
        if (count % numSamples == 0) {
            jump = Random.randInt(numSets) * numSamples;
        }
        return (diskSamples.get(jump + shuffledIndices.get(jump + count++ % numSamples)));
    }

    public Point3DF sampleHemisphere() {
        if (count % numSamples == 0) {
            jump = Random.randInt(numSets) * numSamples;
        }
        return (hemisphereSamples.get(jump + shuffledIndices.get(jump + count++ % numSamples)));
    }

    public Point3DF sampleSphere() {
        if (count % numSamples == 0) {
            jump = Random.randInt(numSets) * numSamples;
        }
        return (sphereSamples.get(jump + shuffledIndices.get(jump + count++ % numSamples)));
    }
    
    public Point2DF sampleOneSet() {
        return samples.get(count++ % numSamples);  
    }

    public void mapSamplesToUnitDisk() {
	    int size = samples.size();
	    float r, phi;		
	    diskSamples = new ArrayList<Point2DF>(size);
        for (int j = 0; j < size; j++) {
            Point2DF p = samples.get(j);
            Point2DF sp = new Point2DF(2.0f * p.x() - 1.0f, 2.0f * p.y() - 1.0f);
            if (sp.x() > -sp.y()) {			// sectors 1 and 2
                if (sp.x() > sp.y()) {		// sector 1
                    r = sp.x();
                    phi = sp.y() / sp.x();
                }
                else {					// sector 2
                    r = sp.y();
                    phi = 2 - sp.x() / sp.y();
                }
            }
            else {						// sectors 3 and 4
                if (sp.x() < sp.y()) {		// sector 3
                    r = -sp.x();
                    phi = 4 + sp.y() / sp.x();
                }
                else {					// sector 4
                    r = -sp.y();
                    if (sp.y() != 0.0)	// avoid division by zero at origin
                        phi = 6 - sp.x() / sp.y();
                    else
                        phi  = 0.0f;
                }
            }
            phi *= Math.PI / 4.0f;
            diskSamples.add(new Point2DF((float) (r * Math.cos(phi)), (float) (r * Math.sin(phi))));
        }
    }

    public void mapSamplesToHemiSphere(final float exp) {
    	int size = samples.size();
        hemisphereSamples = new ArrayList<Point3DF>(numSamples * numSets);
        for (int j = 0; j < size; j++) {
            float cos_phi = (float) Math.cos(2.0 * Math.PI * samples.get(j).x());
            float sin_phi = (float) Math.sin(2.0 * Math.PI * samples.get(j).x());
            float cos_theta = (float) Math.pow((1.0 - samples.get(j).y()), 1.0 / (exp + 1.0));
            float sin_theta = (float) Math.sqrt (1.0 - cos_theta * cos_theta);
            float pu = sin_theta * cos_phi;
            float pv = sin_theta * sin_phi;
            float pw = cos_theta;
            hemisphereSamples.add(new Point3DF(pu, pv, pw));
        }
    }

    public void mapSamplesToSphere() {
        float x, y, z;
        float r, phi;
        sphereSamples = new ArrayList<Point3DF>(numSamples * numSets);
	    for (int j = 0; j < numSamples * numSets; j++) {
            Point2DF p = samples.get(j);
            z 	= 1.0f - 2.0f * p.x();
            r 	= (float) Math.sqrt(1.0 - z * z);
            phi = (float) (2 * Math.PI * p.y());
            x 	= (float) (r * Math.cos(phi));
            y 	= (float) (r * Math.sin(phi));
            sphereSamples.add(new Point3DF(x, y, z));
        }
    }

    protected static void shuffleXCoordinates(int numSamples, int numSets, List<Point2DF> samples) {
        for (int p = 0; p < numSets; p++) {
            for (int i = 0; i <  numSamples - 1; i++) {
                int target = Random.randInt(numSamples) + p * numSamples;
                int source = i + p * numSamples + 1;
                float temp = samples.get(source).x();
                samples.set(source, new Point2DF(samples.get(target).x(), samples.get(source).y()));
                samples.set(target, new Point2DF(temp, samples.get(target).y()));
            }
        }
    }

    protected static void shuffleYCoordinates(int numSamples, int numSets, List<Point2DF> samples) {
        for (int p = 0; p < numSets; p++) {
            for (int i = 0; i <  numSamples - 1; i++) {
                int target = Random.randInt(numSamples) + p * numSamples;
                int source = i + p * numSamples + 1;
                float temp = samples.get(i + p * numSamples + 1).y();
                samples.set(source, new Point2DF(samples.get(source).x(), samples.get(target).y()));
                samples.set(target, new Point2DF(samples.get(target).x(), temp));
            }
        }
    }
}


