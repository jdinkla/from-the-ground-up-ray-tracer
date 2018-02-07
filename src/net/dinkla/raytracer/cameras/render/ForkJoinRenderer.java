package net.dinkla.raytracer.cameras.render;/*
 * Copyright (c) 2012 by Jörn Dinkla, www.dinkla.com, All rights reserved.
 */

import net.dinkla.raytracer.cameras.IColorCorrector;
import net.dinkla.raytracer.colors.Color;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.utilities.Resolution;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinRenderer implements IRenderer {

    final protected ISingleRayRenderer render;
    final protected IColorCorrector corrector;

    protected int numThreads;
    protected int sizeGrid;
    private ForkJoinPool pool = null;

    private static final int STEP_X = 1;
    private static final int STEP_Y = 1;

    public double exposureTime = 1.0;

    protected IFilm film;

    public ForkJoinRenderer(final ISingleRayRenderer render, final IColorCorrector corrector) {
        this.render = render;
        this.corrector = corrector;
        numThreads = 16;
        sizeGrid = 4;
        final int procs = Runtime.getRuntime().availableProcessors();
        pool = new ForkJoinPool(procs);
    }

    @Override
    public void render(IFilm film, int frame) {
        this.film = film;
        final Resolution res = film.getResolution();

        Master master = new Master(sizeGrid, res.hres, res.vres);
        pool.invoke(master);
        pool.shutdown();
        this.film = null;
    }

    class Master extends RecursiveAction {

        final int numBlocks;
        final int blockHeight;
        final int blockWidth;
        final int blocks;
        final int offset;

        Worker[] actions;

        public Master(final int numBlocks, final int width, final int height) {
            this.numBlocks = numBlocks;
            blockHeight = height / numBlocks;
            blockWidth = width / numBlocks;
            blocks = numBlocks * numBlocks;
            offset = 256 / blocks;
            actions = new Worker[blocks];
            //System.out.println("blocks=" + blocks);
        }

        @Override
        protected void compute() {
            for (int j=0; j<numBlocks; j++) {
                for (int i=0; i<numBlocks; i++) {
                    final int idx = j * numBlocks + i;
                    final int x = i * blockWidth;
                    final int y = j * blockHeight;
                    Worker t = new Worker(x, x+blockWidth, y, y+blockHeight);
                    actions[idx] = t;
                    t.fork();
                }
            }
            for (int i=0; i<blocks; i++) {
                actions[i].join();
            }
        }

    }

    /**
     *
     */
    class Worker extends RecursiveAction {

        private final int xStart, xEnd, yStart, yEnd;

        public Worker(final int xStart, final int xEnd, final int yStart, final int yEnd) {
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
        }

        @Override
        protected void compute() {
            int count = 0;
            final int numLogLines = Math.max(25, (yEnd - yStart) / 10);
            for (int r = yStart; r < yEnd; r += STEP_Y) {
                for (int c = xStart; c < xEnd; c += STEP_X) {
                    Color color = render.render(r, c);
                    color = color.mult(exposureTime);
                    color = corrector.correct(color);
                    film.setPixel(0, c, r, color);
                }
                count++;
            }
        }
    }
}
