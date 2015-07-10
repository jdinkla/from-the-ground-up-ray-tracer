package net.dinkla.raytracer.cameras.render;

import net.dinkla.raytracer.cameras.IColorCorrector;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.utilities.Resolution;
import org.apache.log4j.Logger;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 08.06.2010
 * Time: 20:02:17
 * To change this template use File | Settings | File Templates.
 */
public class ParallelRenderer implements IRenderer {

    static final Logger LOGGER = Logger.getLogger(ParallelRenderer.class);

    final protected ISingleRayRenderer render;
    final protected IColorCorrector corrector;

    public float exposureTime = 1.0f;
    
    public boolean parallel = false;
    protected int numThreads;
    protected Worker[] worker;
    protected CyclicBarrier barrier;

    private static final int STEP_X = 1;
    private static final int STEP_Y = 1;

    public ParallelRenderer(final ISingleRayRenderer render, final IColorCorrector corrector) {
        this.render = render;
        this.corrector = corrector;
        numThreads = 16;
    }

    public void render(IFilm film, int frame) {
        // Init
        createWorkers(film);
        barrier = new CyclicBarrier(numThreads + 1);
        parallel = numThreads > 1;

        // Work
        for (int i = 0; i < worker.length; i++) {
            worker[i].film = film;
            new Thread(worker[i]).start();
        }

        // Wait for workers to finish
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    protected void createWorkers(IFilm film) {
        final Resolution res = film.getResolution();
        int vertFactor = numThreads/4;
        if (res.vres() % vertFactor == 0) {
            worker = new Worker[numThreads];
            int yStep = film.getResolution().vres() / vertFactor;
            for (int i = 0; i < numThreads / 4; i++) {
                worker[4*i]   = new Worker(0,            1*res.hres()/4, i * yStep, (i + 1) * yStep);
                worker[4*i+1] = new Worker(1*res.hres()/4, 2*res.hres()/4, i * yStep, (i + 1) * yStep);
                worker[4*i+2] = new Worker(2*res.hres()/4, 3*res.hres()/4, i * yStep, (i + 1) * yStep);
                worker[4*i+3] = new Worker(3*res.hres()/4, 4*res.hres()/4, i * yStep, (i + 1) * yStep);
            }
        } else {
            throw new RuntimeException("viewPlane.vres % numThreads != 0");
        }
    }

    /*
    protected void createWorkers(IFilm film) {
        final Resolution res = film.getResolution();
        if (res.vres % numThreads == 0) {
            worker = new Worker[numThreads];
            int yStep = film.getResolution().vres / (numThreads/2);
            for (int i = 0; i < numThreads / 2; i++) {
                worker[2*i] = new Worker(0, res.hres/2, i * yStep, (i + 1) * yStep);
                worker[2*i+1] = new Worker(res.hres/2, res.hres, i * yStep, (i + 1) * yStep);
            }
        } else {
            throw new RuntimeException("viewPlane.vres % numThreads != 0");
        }
    }
    */

/*
    protected void createWorkers(IFilm film) {
        final Resolution res = film.getResolution();
        if (res.vres % numThreads == 0) {
            worker = new Worker[numThreads];
            int yStep = film.getResolution().vres / numThreads;
            for (int i = 0; i < numThreads; i++) {
                worker[i] = new Worker(0, res.hres, i * yStep, (i + 1) * yStep);
            }
        } else {
            throw new RuntimeException("viewPlane.vres % numThreads != 0");
        }
    }
*/

    public void setNumThreads(final int numThreads) {
        this.numThreads = numThreads;
    }

    protected class Worker implements Runnable {

        private final int xStart, xEnd, yStart, yEnd;
        public IFilm film;
        
        public Worker(final int xStart, final int xEnd, final int yStart, final int yEnd) {
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
        }

        public void run() {
            int count = 0;
            final int numLogLines = Math.max(25, (yEnd - yStart) / 10);
            for (int r = yStart; r < yEnd; r += STEP_Y) {
                if (count % numLogLines == 0) {
                    LOGGER.info("ParallelRender: " + count + " of " + (yEnd - yStart));
                }
                for (int c = xStart; c < xEnd; c += STEP_X) {
                    Color color = render.render(r, c);
                    color = color.mult(exposureTime);
                    color = corrector.correct(color);
                    film.setPixel(0, c, r, color);
                }
                count++;
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
    
}
