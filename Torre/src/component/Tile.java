/**
 * 
 */
package component;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import model.Debug;
import model.Loader;
import view.SpriteSheet;

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
    
    private static SpriteSheet tileSheet = new SpriteSheet(50, 50, Loader.loadTexture("/textures/tiles/tile_sheet.png"));
    
    private static SpriteSheet lockedSheet = new SpriteSheet(50, 50, Loader.loadTexture("/textures/tiles/locked_tile_sheet.png"));
    
    public boolean locked;
    
    private BufferedImage lockedTexture;
    
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
    public abstract void onRightClick();
    
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
            if(neighbors[direction] != null)
                return neighbors[direction].checkSide(direction, stickFactor - 1);
            return false;
        }
        return true;
    }
    
    /**
     * @return true if this Tile will collapse given the number of Tiles above it
     */
    public boolean willCollapse() {
        if(strength > -1) {
            if(load(-1) > strength)
                return true;
            return false;
        }
        return false;
    }
    
    private int load(int direction) {
        int load = 0;
        for(int i = RIGHT; i <= LEFT; i++) {
            if(neighbors[i] != null && i != (direction - 2) % 4) {
                load += 1;
                load += neighbors[i].load(i);
            }
        }
        return load;
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
    
    public void lock() {
        locked = true;
        texture = lockedTexture;
    }
    
    public void unlock() {
        locked = false;
        texture = unlockedTexture;
    }
    
    @Override
    public String toString() {
        return getClass().toString();
    }
    
    public static class Crystal extends Tile {
        public Crystal() {
            super(null, null, false, -1, true, -1, 0);
            animator = new Animator(new SpriteSheet(50, 50, Loader.loadTexture("/textures/tiles/crystal.png")), 2);
        }

        /* (non-Javadoc)
         * @see component.Tile#onRightClick()
         */
        @Override
        public void onRightClick() {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            // TODO Auto-generated method stub
            return null;
        }
    }
    
    public static class GrassTile extends Tile {
        public GrassTile() {
            super(tileSheet.getSprite(1, 0), lockedSheet.getSprite(1, 0), true, .5, true, -1, 4);
        }

        /* (non-Javadoc)
         * @see component.Tile#onRightClick()
         */
        @Override
        public void onRightClick() {
            
        }

        /* (non-Javadoc)
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.MudItem(x, y);
        }
    }
    
    public static class StoneTile extends Tile {
        public StoneTile() {
            super(tileSheet.getSprite(2, 0), lockedSheet.getSprite(2, 0), true, 1, true, -1, 6);
        }

        /* (non-Javadoc)
         * @see component.Tile#onRightClick()
         */
        @Override
        public void onRightClick() {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.StoneItem(x, y);
        }
    }
    
    public static class DirtTile extends Tile {
        public DirtTile() {
            super(tileSheet.getSprite(3, 0), lockedSheet.getSprite(3, 0), true, .5, true, -1, 4);
        }

        /* (non-Javadoc)
         * @see component.Tile#onRightClick()
         */
        @Override
        public void onRightClick() {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.MudItem(x, y);
        }
    }
    public static class LogTile extends Tile {
        public LogTile() {
            super(tileSheet.getSprite(4, 0), lockedSheet.getSprite(4, 0), true, 0.7, true, 10, 3);
        }

        /* (non-Javadoc)
         * @see component.Tile#onRightClick()
         */
        @Override
        public void onRightClick() {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.ScaffoldItem(x, y);
        }
    }
    
    public static class ScaffoldTile extends Tile {
        public ScaffoldTile() {
            super(tileSheet.getSprite(5, 0), lockedSheet.getSprite(5, 0), true, 0.3, true, 5, 10);
        }

        /* (non-Javadoc)
         * @see component.Tile#onRightClick()
         */
        @Override
        public void onRightClick() {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see component.Tile#getItem()
         */
        @Override
        public Item getItem() {
            return new Item.ScaffoldItem(x, y);
        }
    }
}
