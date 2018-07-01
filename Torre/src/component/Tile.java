/**
 * 
 */
package component;

import java.awt.image.BufferedImage;

/**
 * A class representing a Tile drawn on the screen.
 * @author Spencer Yoder
 */
public abstract class Tile extends Component {
    /** The index in the neighbors array of the Tile to the right of this one */
    public static final int RIGHT = 0;
    /** The index in the neighbors array of the Tile above this one */
    public static final int TOP = 1;
    /** The index in the neighbors array of the Tile to the left of this one */
    public static final int LEFT = 2;
    /** The index in the neighbors array of the Tile below this one */
    public static final int BOTTOM = 3;
    
    public static final int LENGTH = 50;
    
    /** Whether or not the user can break this Tile */
    protected boolean canBreak;
    /** How long it takes the user to break this Tile (in seconds)*/
    protected double breakTime;
    /** Whether or not this Tile is affected by gravity */
    protected boolean canFall;
    /** The neighboring Tiles to this one */
    protected Tile[] neighbors;
    /** How many tiles can be above this one before it collapses and turns into an item
     * -1 if the tile cannot collapse */
    protected int strength;
    /** How many tiles this one can hold in place to prevent falling */
    protected int stickiness;
    
    /**
     * Constructs a new Tile with the given states
     * @param texture the texture for the display of this tile (Must be 50x50 pixels)
     * @param canBreak Whether or not this tile can be broken by the user
     * @param breakTime How long it takes this tile to be broken by the use (in seconds)
     * @param canFall Whether or not this tile is affected by gravity
     * @param strength How many tiles can be on top of this one before it breaks (-1 if indestructable)
     * @param stickiness How many tiles to either side this one can prevent from falling
     */
    public Tile(BufferedImage texture, boolean canBreak, double breakTime,
            boolean canFall, int strength, int stickiness) {
        super(texture);
        this.canBreak = canBreak;
        this.breakTime = breakTime;
        this.canFall = canFall;
        this.strength = strength;
        this.stickiness = stickiness;
        neighbors = new Tile[4];
    }
    
    /**
     * The behavior of this tile when it is right clicked
     */
    public abstract void onRightClick();
    
    /**
     * Sets a neighbor of this tile using {@link component.Tile#RIGHT}, {@link component.Tile#LEFT}, 
     * {@link component.Tile#TOP}, or {@link component.Tile#BOTTOM}
     * @param neighbor the Tile this will be neighboring
     * @param idx the index of the neighbors array the new neighbor will occupy
     */
    public void setNeighbor(Tile neighbor, int idx) {
        neighbors[idx] = neighbor;
    }
    
    /**
     * Removes the neighbor at the given index of this Tile
     * @param idx {@link component.Tile#RIGHT}, {@link component.Tile#LEFT}, 
     * {@link component.Tile#TOP}, or {@link component.Tile#BOTTOM}
     */
    public void removeNeighbor(int idx) {
        neighbors[idx] = null;
    }
    
    /**
     * @return true if this Tile should fall given its state and the state of its neighbors
     */
    public boolean willFall() {
        if(canFall) {
            if(neighbors[BOTTOM] == null) {
                if(checkSide(LEFT, stickiness)) {
                    return false;
                } 
                if(checkSide(RIGHT, stickiness)) {
                    return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }
    
    /**
     * Checks the side of this Tile to see if it has enough Tiles to its one seide keeping it from falling
     * @param direction {@link component.Tile#RIGHT} or {@link component.Tile#LEFT}, 
     * @param stickFactor How many tiles before this one will fall
     * @return true if this Tile is secure
     */
    private boolean checkSide(int direction, int stickFactor) {
        if(neighbors[BOTTOM] == null) {
            if(stickFactor == 0) {
                return false;
            }
            return neighbors[direction].checkSide(direction, stickFactor - 1);
        }
        return true;
    }
    
    /**
     * @return true if this Tile will collapse given the number of Tiles above it
     */
    public boolean willCollapse() {
        if(strength > -1) {
            return loadGreaterThanStrength(strength);
        }
        return false;
    }
    
    /**
     * @return true if the number of Tiles above this Tile is greater than its strength
     */
    private boolean loadGreaterThanStrength(int strengthFactor) {
        if(strengthFactor <= 0) {
            return true;
        }
        if(neighbors[TOP] == null) {
            return false;
        }
        return neighbors[TOP].loadGreaterThanStrength(strengthFactor - 1);
    }
    
    /**
     * @param index {@link component.Tile#RIGHT}, {@link component.Tile#LEFT}, 
     * {@link component.Tile#TOP}, or {@link component.Tile#BOTTOM}
     * @return the neighbor at the given index
     */
    public Tile getNeighbor(int index) {
        return neighbors[index];
    }
    
    public boolean isAccessible() {
        for(int i = 0; i < neighbors.length; i++) {
            if(neighbors[i] == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    public double getBreakTime() {
        return breakTime;
    }

    /**
     * @return
     */
    public boolean canBreak() {
        return canBreak;
    }
}
