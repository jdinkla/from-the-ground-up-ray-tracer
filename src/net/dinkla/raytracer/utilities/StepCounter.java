package net.dinkla.raytracer.utilities;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 10.06.2015
 * Time: 09:30:40
 */
public class StepCounter {

  final int start;
  final int end;
  final int step;
  final boolean cyclic;
  int current;

  public StepCounter(final int start, final int end, final int step, final boolean cyclic) {
    assert(start < end);
    this.start = start;
    this.end = end;
    this.step = step;
    this.cyclic = cyclic;
    current = start;
  }

  public boolean hasNext() {
    return current < end;
  }

  public int getCurrent() {
    return current;
  }

  public void step() {
    current += step;
    if (current > end && cyclic) {
      current = start;
    }
  }

}
