/**
 * 
 */
package component;

import java.awt.Point;

import model.Debug;

/**
 * A class representing a rectangular region for use in mouse detection
 * @author Spencer Yoder
 */
public class Bound implements Comparable<Bound> {
    /** The top-left corner of the region */
    public Point pt1;
    /** The bottom-right corner of the region */
    public Point pt2;
    
    /**
     * @param pt1 the top-left corner of the Bound
     * @param pt2 the bottom-right corner of the Bound
     */
    public Bound(Point pt1, Point pt2) {
        this.pt1 = pt1;
        this.pt2 = pt2;
    }
    
    /**
     * @param p a point
     * @return whether the point lies inside the Bound
     */
    public boolean withinBound(Point p) {
        return p != null && p.x >= pt1.x && p.x <= pt2.x && p.y >= pt1.y && p.y <= pt2.y;
    }
    
    /**
     * @param p a Point
     * @return if the point is down-right from the top-left corner
     */
    public boolean withinScope(Point p) {
        return p != null && (p.x >= pt1.x || p.y >= pt1.y);
    }
    
    /**
     * The order in which Bounds are considered in {@link model.MouseWatcher#checkComponents()}
     */
    @Override
    public int compareTo(Bound other) {
        return (int) (pt1.distance(0, 0) - other.pt1.distance(0, 0));
    }
}
