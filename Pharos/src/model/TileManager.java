/**
 * 
 */
package model;

import java.awt.Graphics;
import java.awt.Point;

import component.Animator;
import component.Component;
import component.Item;
import component.Tile;
import view.LayerManager;
import view.SpriteSheet;

/**
 * A class which manages the behavior of Tiles in the game
 * Tile Assets are loaded in this class instead of in Assets for better organization
 * Tile behavior is handled in this class instead of in the GameState class
 * because the TileManager contains data about every Tile
 * @author Spencer Yoder
 */
public class TileManager extends Component {
    /** The TileGrid which occupies the screen 
     * TEMPORARY: the final game is planned to contain smooth scrolling so multiple TileGrids may be
     * on screen at a given time*/
    private TileGrid home;
    
    private Tile crystal;
    public int crystalHeight;
    
    /** The width (in Tiles) of the screen */
    public static final int TILE_GRID_WIDTH = 30;
    /** The width (in Tiles) of the screen */
    public static final int TILE_GRID_HEIGHT = 20;
    
    /**
     * Constructs a new TileManager
     */
    public TileManager() {
        super(null);
        home = new TileGrid(-14, -10);
        crystal = new Tile.Crystal();
        crystalHeight = -5;
        for(int i = 0; i > crystalHeight; i--) {
            home.addTile(new Tile.DirtTile(), 0, i);
        }
        for(int i = 0; i > -5; i--) {
            home.addTile(new Tile.ScaffoldTile(), 3, i);
        }
        home.addTile(crystal, 0, crystalHeight);
    }
    
    /**
     * Calculates the state of every Tile on screen and adjusts accordingly
     */
    public void tick() {
        for(int i = 0; i < TILE_GRID_WIDTH; i++) {
            for(int j = 0; j < TILE_GRID_HEIGHT; j++) {
                Point p = convertToGlobalTileCoords(new Point(i, j));
                Tile t = home.grid[i][j];
                if(t != null) {
                    if(t.willFall()) {
                        if(j < TILE_GRID_HEIGHT - 1) {
                            if(t == crystal) {
                                if(p.y != 0) {
                                    crystalHeight++;
                                    home.grid[i][j] = null;
                                    home.grid[i][j + 1] = t;
                                }
                            } else {
                                home.grid[i][j] = null;
                                home.grid[i][j + 1] = t;
                            }
                            home.link(i, j+ 1);
                            home.link(i, j);
                        }
                        break;
                    }
                    if(t.willCollapse()) {
                        Game.gameState.spawnItem(breakTile(i * 50, j * 50));
                    }
                    if(Math.abs(p.y) > Math.abs(crystalHeight)) {
                        t.lock();
                    } else {
                        t.unlock();
                    }
                }
            }
        }
    }
    
    /**
     * @return the given point in local tile coordinates converted to global tile coordinates
     * (where the crystal starts at (0, 0))
     */
    private Point convertToGlobalTileCoords(Point localTileCoords) {
        return new Point(localTileCoords.x + home.x, localTileCoords.y + home.y);
    }
    
    /**
     * Places a tile if the conditions are correct
     * @param x the x location of the mouse in pixels
     * @param y the y location of the mouse in pixels
     * @param tile the Tile to place
     * @return true if the Tile is placed correctly
     */
    public boolean handleRightClick(int x, int y, Tile tile) {
        Point pL = convertToLocalTileCoords(x, y);
        Point pA = convertToGlobalTileCoords(x, y);
        if(home.grid[pL.x][pL.y] != null) {
            if(home.grid[pL.x][pL.y] == crystal && pL.y > 0) {
                home.addTile(crystal, 0, --crystalHeight);
                home.addTile(tile, pA.x, pA.y);
                return true;
            }
            home.grid[pL.x][pL.y].onRightClick();
        } else if(tile != null){
            home.addTile(tile, pA.x, pA.y);
            return true;
        }
        return false;
    }
    
    /**
     * @return the Tile at the given x and y coordinates (in pixels)
     */
    public Tile getTile(int xPixel, int yPixel) {
        Point p = convertToLocalTileCoords(xPixel, yPixel);
        return home.grid[p.x][p.y];
    }
    
    /**
     * @return the given x and y coordinates (in pixels) converted to local Tile coordinates
     */
    public Point convertToLocalTileCoords(int xPixel, int yPixel) {
        return new Point(xPixel / Tile.LENGTH, yPixel / Tile.LENGTH);
    }
    
    /**
     * @return the given Point which is in global coordinates converted to local tile coordinates
     */
    public Point convertToLocalTileCoords(Point p) {
        return new Point(p.x - home.x, p.y - home.y);
    }
    
    /**
     * @return the given x and y coordinates (in pixels) converted to global Tile coordinates
     */
    public Point convertToGlobalTileCoords(int xPixel, int yPixel) {
        Point p = convertToLocalTileCoords(xPixel, yPixel);
        return new Point(p.x + home.x, p.y + home.y);
    }
    
    /**
     * Breaks the Tile at the given x and y pixel coordinates
     * @param mouseX the given x
     * @param mouseY the given y
     * @return the Item the Tile drops when broken
     */
    public Item breakTile(int mouseX, int mouseY) {
        Point p = convertToLocalTileCoords(mouseX, mouseY);
        Item item = home.grid[p.x][p.y].getItem();
        home.grid[p.x][p.y] = null;
        home.link(p.x, p.y);
        return item;
    }
    
    /**
     * Draws all Tiles to the screen
     */
    @Override
    public void render(Graphics g) {
        for(int i = 0; i < TILE_GRID_WIDTH; i++) {
            for(int j = 0; j < TILE_GRID_HEIGHT; j++) {
                if(home.grid[i][j] != null) {
                    home.grid[i][j].place(i * 50, j * 50);
                    home.grid[i][j].render(g);
                }
            }
        }
    }
    
    /**
     * @param mouseP the position of the mouse
     * @return the corners of the Tile the mouse currently occupies
     * @see state.GameState#tick()
     */
    public Point[] getActiveRange(Point mouseP) {
        int left = (int) (Math.floor(mouseP.x / 50.0) * 50);
        int right = (int) (Math.ceil(mouseP.x / 50.0) * 50);
        int top = (int) (Math.floor(mouseP.y / 50.0) * 50);
        int bottom = (int) (Math.ceil(mouseP.y / 50.0) * 50);
        return new Point[] {new Point(left, top), new Point(right, bottom)};
    }
    
    /**
     * @param mousePos the position of the mouse in pixels
     * @return true if the mouse is inside the bounds set by the crystal.
     */
    public boolean mouseInBounds(Point mousePos) {
        Point absPos = convertToGlobalTileCoords(mousePos.x, mousePos.y);
        return Math.abs(absPos.y) <= Math.abs(crystalHeight);
    }
    
    /**
     * @return the range at which to start drawing the fog of war
     */
    public int[] getVisibleRange() {
        Point p = convertToLocalTileCoords(new Point(crystalHeight, -crystalHeight));
        return new int[] {(p.x - 4) * 50, (p.y + 1) * 50};
    }
    
    /**
     * A 30x20 grid of Tiles.
     * These are the groups of tiles which will be loaded in memory (i.e. if the user navigates far
     * enough away the Tile will be unloaded)
     * @author Spencer Yoder
     */
    private class TileGrid {
        /** The global x-coordinate (in Tiles) of the top-left Tile in the grid */
        private int x;
        /** The global y-coordinate (in Tiles) of the top-left Tile in the grid */
        private int y;
        /** The grid of Tiles (stored in the format [x][y]) */
        private Tile[][] grid;
        
        /**
         * Constructs a new TileGrid
         * @param x The global x-coordinate (in Tiles) of the top-left Tile in the grid
         * @param y The global y-coordinate (in Tiles) of the top-left Tile in the grid
         */
        private TileGrid(int x, int y) {
            this.x = x;
            this.y = y;
            grid = new Tile[TILE_GRID_WIDTH][TILE_GRID_HEIGHT];
            for(int i = x; i < x + TILE_GRID_WIDTH; i++) {
                for(int j = y; j < y + TILE_GRID_HEIGHT; j++) {
                    if(j == 1) {
                        addTile(new Tile.GrassTile(), i, j);
                    } else if(j > 1 && j <= 5) {
                        addTile(new Tile.DirtTile(), i, j);
                    } else if(j > 1) {
                        addTile(new Tile.StoneTile(), i, j);
                    }
                }
            }
        }
        
        /**
         * Links the given Tile so that it knows about the adjacent Tiles in the grid
         * @param i the column (x) of the Tile
         * @param j the row (y) of the Tile
         */
        private void link(int i, int j) {
            if(i < TILE_GRID_WIDTH && j < TILE_GRID_HEIGHT) {
                Tile current = grid[i][j];
                if(current != null) {
                    if(i > 0) {
                        current.setNeighbor(grid[i - 1][j], Tile.LEFT);
                    }
                    if(i < TILE_GRID_WIDTH - 1) {
                        current.setNeighbor(grid[i + 1][j], Tile.RIGHT);
                    }
                    if(j < TILE_GRID_HEIGHT - 1) {
                        current.setNeighbor(grid[i][j + 1], Tile.BOTTOM);
                    }
                    if(j > 0) {
                        current.setNeighbor(grid[i][j - 1], Tile.TOP);
                    }
                }
                for(int k = Tile.RIGHT; k <= Tile.BOTTOM; k++) {
                    linkHelper(current, k, i, j);
                }
            }
        }
        
        /**
         * A helper method for {@link model.TileManager.TileGrid#link(int, int)}
         * The former links a tile to the tiles around it, this links the other way around
         * Very useful if the Tile is null
         * @param current the Tile to link
         * @param direction the direction to link
         * @param i the x position of the Tile in local coordinates
         * @param j the y position of the Tile in local coordinate
         */
        private void linkHelper(Tile current, int direction, int i, int j) {
            switch (direction) {
                case Tile.LEFT:
                    i -= 1;
                    direction = Tile.RIGHT;
                    break;
                case Tile.RIGHT:
                    i += 1;
                    direction = Tile.LEFT;
                    break;
                case Tile.BOTTOM:
                    j += 1;
                    direction = Tile.TOP;
                    break;
                case Tile.TOP:
                    j -= 1;
                    direction = Tile.BOTTOM;
                    break;
            }
            if(i >= 0 && j >=0 && i < TILE_GRID_WIDTH && j < TILE_GRID_HEIGHT) {
                Tile neighbor = grid[i][j];
                if(neighbor != null)
                    neighbor.setNeighbor(current, direction);
            }
        }
        
        /**
         * Adds the Tile to the grid and screen, and links and places it
         * @param t the given Tile
         * @param x the x position of the tile in pixels
         * @param y the y position of the tile in pixels
         */
        private void addTile(Tile t, int x, int y) {
            int i = x - this.x;
            int j = y - this.y;
            grid[i][j] = t;
            link(i, j);
            t.place(i * 50, j * 50);
        }
    }
}
