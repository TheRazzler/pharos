/**
 * 
 */
package component;

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.LinkedList;

import model.Loader;
import view.SpriteSheet;

/**
 * A class representing a Tile drawn on the screen.
 * A Tile can be broken and placed by the mouse, can collapse due to the load of supporting other Tiles,
 * and can hold Tiles which are "stuck" to it on the left and right up to a certain distance.
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
    /** The height and width of a Tile */
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
    /** The SpriteSheet of textures for tiles that are editable (in the range of the tower) */
    private static SpriteSheet tileSheet = new SpriteSheet(50, 50, Loader.loadTexture("/textures/tiles/tile_sheet.png"));
    /** The SpriteSheet of textures for tiles that are not editable */
    private static SpriteSheet lockedSheet = new SpriteSheet(50, 50, Loader.loadTexture("/textures/tiles/locked_tile_sheet.png"));
    /** Whether or not the Tile is in the range of the tower and is editable */
    public boolean locked;
    /** The texture for this Tile when it is locked */
    private BufferedImage lockedTexture;
    /** The texture for this Tile when it is unlocked */
    private BufferedImage unlockedTexture;
    
    /**
     * Constructs a new Tile with the given states
     * @param texture the texture for the display of this tile (Must be 50x50 pixels)
     * @param canBreak Whether or not this tile can be broken by the user
     * @param breakTime How long it takes this tile to be broken by the use (in seconds)
     * @param canFall Whether or not this tile is affected by gravity
     * @param strength How many tiles can be on top of this one before it breaks (-1 if indestructable)
     * @param stickiness How many tiles to either side this one can prevent from falling
     */
    public Tile(BufferedImage texture, BufferedImage lockedTexture, boolean canBreak, double breakTime,
            boolean canFall, int strength, int stickiness) {
        super(texture);
        this.canBreak = canBreak;
        this.breakTime = breakTime;
        this.canFall = canFall;
        this.strength = strength;
        this.stickiness = stickiness;
        neighbors = new Tile[4];
        lock();
        this.unlockedTexture = texture;
        this.lockedTexture = lockedTexture;
    }
    
    /**
     * The behavior of this tile when it is right clicked
     */
    public void onRightClick() {
        //Override if behavior exists, otherwise, this method does nothing
    }
    
    public abstract Item getItem();
    
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
        if(canFall && !locked) {
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
     * Checks the side of this Tile to see if it has enough Tiles to its one side keeping it from falling
     * @param direction {@link component.Tile#RIGHT} or {@link component.Tile#LEFT}, 
     * @param stickFactor How many tiles before this one will fall
     * @return true if this Tile is secure
     */
    private boolean checkSide(int direction, int stickFactor) {
        if(neighbors[BOTTOM] == null) {
            if(stickFactor == 0) {
                return false;
            }
            if(neighbors[direction] != null)
                return neighbors[direction].checkSide(direction, stickFactor - 1);
            return false;
        }
        return true;
    }
    
    /**
     * @return true if this Tile will collapse given the number of Tiles it is supporting
     */
    public boolean willCollapse() {
        if(!canCollapse() || neighbors[BOTTOM] == null || neighbors[BOTTOM].canCollapse())
            return false;
        if(weight() > strength)
            return true;
        return false;
    }
    
    /**
     * @return the number of Tiles this one is supporting
     */
    private int weight() {
        Hashtable<Tile, Tile> table = new Hashtable<Tile, Tile>();
        LinkedList<Tile> queue = new LinkedList<Tile>();
        queue.add(neighbors[TOP]);
        int weight = 0;
        if(neighbors[TOP] == null)
            return 0;
        do {
            Tile current = queue.remove();
            weight++;
            if(weight > strength)
                return weight;
            for(int i = RIGHT; i < BOTTOM; i++) {
                Tile neighbor = current.neighbors[i];
                if(neighbor != null && !table.contains(neighbor))
                    if(i == TOP || neighbor.neighbors[BOTTOM] == null)
                        queue.add(neighbor);
            }
            table.put(current, current);
        } while(!queue.isEmpty());
        return weight;
    }

    public boolean canCollapse() {
        return strength > -1;
    }
    
    /**
     * @param index {@link component.Tile#RIGHT}, {@link component.Tile#LEFT}, 
     * {@link component.Tile#TOP}, or {@link component.Tile#BOTTOM}
     * @return the neighbor at the given index
     */
    public Tile getNeighbor(int index) {
        return neighbors[index];
    }

    /**
     * @return The amount of time (in seconds) it takes this tile to be broken by the mouse
     */
    public double getBreakTime() {
        return breakTime;
    }

    /**
     * @return whether or not this Tile can be broken by the mouse
     */
    public boolean canBreak() {
        return canBreak;
    }
    
    /**
     * This tile can no longer be interacted with by the mouse
     */
    public void lock() {
        locked = true;
        texture = lockedTexture;
    }
    
    /**
     * This Tile can now be interacted with by the mouse
     */
    public void unlock() {
        locked = false;
        texture = unlockedTexture;
    }
    
    /**
     * @return a String representation of this Tile for use in debugging
     */
    @Override
    public String toString() {
        return getClass().toString();
    }
    
    /**
     * For use in the Hashtable used by {@link component.Tile#weight()}
     */
    @Override
    public int hashCode() {
        return Integer.hashCode((x + y) * y * x);
    }
    
    /**
     * A class for the Crystal at the top of the tower
     * @author Spencer Yoder
     */
    public static class Crystal extends Tile {
        /**
         * See {@link component.Tile#Tile(BufferedImage, BufferedImage, boolean, double, boolean, int, int)}
         */
        public Crystal() {
            super(null, null, false, -1, true, -1, 0);
            animator = new Animator(new SpriteSheet(50, 50, Loader.loadTexture("/textures/tiles/crystal.png")), 2);
        }
        
        /**
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return null;
        }
    }
    
    /**
     * A class for grass. Grass cannot collapse and gives a {@link component.Item.MudItem} when broken.
     * Grass can fall and appears at the very top of the ground.
     * @author Spencer Yoder
     */
    public static class GrassTile extends Tile {
        /**
         * See {@link component.Tile#Tile(BufferedImage, BufferedImage, boolean, double, boolean, int, int)}
         */
        public GrassTile() {
            super(tileSheet.getSprite(1, 0), lockedSheet.getSprite(1, 0), true, .5, true, -1, 4);
        }

        /**
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.MudItem(x, y);
        }
    }
    
    /**
     * A class for stone. Stone appears under dirt and has the same properties as dirt and grass
     * with a longer break time.
     * @author Spencer Yoder
     */
    public static class StoneTile extends Tile {
        /**
         * See {@link component.Tile#Tile(BufferedImage, BufferedImage, boolean, double, boolean, int, int)}
         */
        public StoneTile() {
            super(tileSheet.getSprite(2, 0), lockedSheet.getSprite(2, 0), true, 1, true, -1, 6);
        }

        /**
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.StoneItem(x, y);
        }
    }
    
    /**
     * A class for dirt. Dirt appears below grass and has the same properties.
     * @author Spencer Yoder
     */
    public static class DirtTile extends Tile {
        public DirtTile() {
            super(tileSheet.getSprite(3, 0), lockedSheet.getSprite(3, 0), true, .5, true, -1, 4);
        }

        /* (non-Javadoc)
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.MudItem(x, y);
        }
    }
    
    /**
     * A class for logs. Meant to emulate Minecraft and similar games wherein logs of trees
     * are broken and yield wood for crafting
     * @author Spencer Yoder
     */
    public static class LogTile extends Tile {
        /**
         * See {@link component.Tile#Tile(BufferedImage, BufferedImage, boolean, double, boolean, int, int)}
         */
        public LogTile() {
            super(tileSheet.getSprite(4, 0), lockedSheet.getSprite(4, 0), true, 0.7, true, 10, 3);
        }

        /**
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.ScaffoldItem(x, y);
        }
    }
    
    /**
     * As of now, the only Tile that can collapse under the weight of other tiles
     * @author Spencer Yoder
     */
    public static class ScaffoldTile extends Tile {
        /**
         * See {@link component.Tile#Tile(BufferedImage, BufferedImage, boolean, double, boolean, int, int)}
         */
        public ScaffoldTile() {
            super(tileSheet.getSprite(5, 0), lockedSheet.getSprite(5, 0), true, 0.3, true, 3, 10);
        }

        /**
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.ScaffoldItem(x, y);
        }
    }
}
