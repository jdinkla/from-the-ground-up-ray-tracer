package net.dinkla.raytracer.utilities;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 14.04.2010
 * Time: 14:19:39
 * To change this template use File | Settings | File Templates.
 */
public class Timer {

    long startTime;
    long endTime;

    public Timer() {
        startTime = endTime = 0L;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        endTime = 0L;
    }

    public void stop() {
        assert(endTime == 0L);
        endTime = System.currentTimeMillis();
    }

    public long getDuration() {
        return endTime - startTime;
    }

}
