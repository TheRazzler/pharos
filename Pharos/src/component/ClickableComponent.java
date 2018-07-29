/**
 * 
 */
package component;

import java.awt.Point;
import java.awt.image.BufferedImage;

import model.Debug;

/**
 * A class representing a component which can be clicked on or moused over
 * @author Spencer Yoder
 */
public abstract class ClickableComponent extends Component implements Comparable<ClickableComponent> {
    /** The mouse has stayed in the same state since the last tick */
    public static final int NO_CHANGE = 0;
    /** The mouse went from hovering over the Component to not since the last Tick */
    public static final int MOUSE_LEFT = 1;
    /** The mouse went from not hovering over the Component to hovering since the last Tick */
    public static final int MOUSE_ON = 2;
    /** The bound which the ClickableComponent occupies */
    public Bound bound;
    /** Whether or not the mouse is currently over the Component */
    public boolean mouseOver = false;
    /** The index of this Component in a helper List inside of the {@link model.MouseWatcher} class
     *  (For use in removal from the list) */
    protected int mouseIndex;
    
    /** @see component.Component#Component(BufferedImage) */
    public ClickableComponent(BufferedImage texture) {
        super(texture);
        bound = new Bound(new Point(x, y), new Point(x + width - 1, y + height - 1));
    }
    
    /**
     * Calls the appropriate method depending on whether or not the mouse is over the component
     * @return how the mouse has acted in relation to this component
     * @see model.MouseWatcher#checkComponents()
     */
    public int reactToMouse(Point mousePos) {
        if(bound.withinBound(mousePos)) {
            if(!mouseOver) {
                mouseOver = true;
                onMouseOver();
                return MOUSE_ON;
            }
        } else {
            if(mouseOver) {
                mouseOver = false;
                onMouseLeave();
                return MOUSE_LEFT;
            }
        }
        return NO_CHANGE;
    }
    
    /**
     * @see component.Bound#compareTo(Bound)
     */
    @Override
    public int compareTo(ClickableComponent other) {
        return bound.compareTo(other.bound);
    }
    
    /**
     * @see component.Component#place(int, int)
     */
    @Override
    public void place(int x, int y) {
        int dx = x - this.x;
        int dy = y - this.y;
        super.place(x, y);
        bound.pt1.x += dx;
        bound.pt1.y += dy;
        bound.pt2.x += dx;
        bound.pt2.y += dy;
    }
    
    /**
     * @see component.Component#updateTexture(BufferedImage)
     */
    @Override
    public void updateTexture(BufferedImage texture) {
        super.updateTexture(texture);
        bound = new Bound(new Point(x, y), new Point(x + width - 1, y + height - 1));
    }
    
    /**
     * @return the mouseIndex
     */
    public int getMouseIndex() {
        return mouseIndex;
    }
    
    public void setMouseIndex(int mouseIndex) {
        this.mouseIndex = mouseIndex;
    }
    
    /** The code that gets run when the component is moused over */
    public void onMouseOver() {
        //Override this method with behavior, otherwise, this method does nothing
    }
    /** The code that gets run when the mouse stops hovering over the component */
    public void onMouseLeave() {
      //Override this method with behavior, otherwise, this method does nothing
    }
    /** The code that gets run when the component is clicked */
    public void onClick() {
      //Override this method with behavior, otherwise, this method does nothing
    }
}
