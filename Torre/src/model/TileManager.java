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
    
    /** The width (in Tiles) of the screen */
    public static final int TILE_GRID_WIDTH = 30;
    /** The width (in Tiles) of the screen */
    public static final int TILE_GRID_HEIGHT = 20;
    
    /**
     * Constructs a new TileManager which draws to the given LayerManager
     * @param layerManager the LayerManager
     */
    public TileManager() {
        super(null);
        home = new TileGrid(-14, -10);
    }
    
    /**
     * Calculates the state of every Tile on screen and adjusts accordingly
     */
    public void tick() {
        for(int i = 0; i < TILE_GRID_WIDTH; i++) {
            for(int j = 0; j < TILE_GRID_HEIGHT; j++) {
                Tile t = home.grid[i][j];
                if(t != null) {
                    if(t.willFall()) {
                        home.grid[i][j] = null;
                        if(j < TILE_GRID_HEIGHT - 2)
                            home.grid[i][j + 1] = t;
                        home.link(i, j+ 1);
                    }
                }
            }
        }
    }
    
    public void handleRightClick(int x, int y) {
        if(home.grid[x / Tile.LENGTH][y / Tile.LENGTH] != null) {
            home.grid[x / Tile.LENGTH][y / Tile.LENGTH].onRightClick();
        }
    }
    
    public Tile getTile(int xPixel, int yPixel) {
        Point p = convertToLocalTileCoords(xPixel, yPixel);
        return home.grid[p.x][p.y];
    }
    
    private Point convertToLocalTileCoords(int xPixel, int yPixel) {
        return new Point(xPixel / Tile.LENGTH, yPixel / Tile.LENGTH);
    }
    
    public Item breakTile(int mouseX, int mouseY) {
        Point p = convertToLocalTileCoords(mouseX, mouseY);
        Item item = home.grid[p.x][p.y].getItem();
        home.grid[p.x][p.y] = null;
        home.link(p.x, p.y);
        return item;
    }
    
    @Override
    public void render(Graphics g) {
        for(int i = 0; i < TILE_GRID_WIDTH; i++) {
            for(int j = 0; j < TILE_GRID_HEIGHT; j++) {
                if(home.grid[i][j] != null) {
                    home.grid[i][j].render(g);
                }
            }
        }
    }
    
    public Point[] getActiveRange(Point mouseP) {
        int left = (int) (Math.floor(mouseP.x / 50.0) * 50);
        int right = (int) (Math.ceil(mouseP.x / 50.0) * 50);
        int top = (int) (Math.floor(mouseP.y / 50.0) * 50);
        int bottom = (int) (Math.ceil(mouseP.y / 50.0) * 50);
        return new Point[] {new Point(left, top), new Point(right, bottom)};
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
            for(int globalX = x; globalX < x + TILE_GRID_WIDTH; globalX++) {
                for(int globalY = y; globalY < y + TILE_GRID_HEIGHT; globalY++) {
                    if(globalY > 0) {
                        addTile(new Tile.LockedTile(), globalX, globalY);
                    }
                }
            }
            addTile(new Tile.Crystal(), 0, 0);
            addTile(new Tile.GrassTile(), 1, 0);
            addTile(new Tile.GrassTile(), 2, 0);
            addTile(new Tile.StoneTile(), 3, 0);
            addTile(new Tile.StoneTile(), 3, -1);
            addTile(new Tile.StoneTile(), 4, 0);
            addTile(new Tile.StoneTile(), 4, -1);
        }
        
        /**
         * Links the given Tile so that it knows about the adjacent Tiles in the grid
         * @param i the column (x) of the Tile
         * @param j the row (y) of the Tile
         */
        private void link(int i, int j) {
            Tile current = grid[i][j];
            if(current != null) {
                if(i > 0) {
                    current.setNeighbor(grid[i - 1][j], Tile.LEFT);
                }
                if(i < TILE_GRID_WIDTH - 1) {
                    current.setNeighbor(grid[i + 1][j], Tile.RIGHT);
                }
                if(j > TILE_GRID_HEIGHT - 1) {
                    current.setNeighbor(grid[i][j + 1], Tile.BOTTOM);
                }
                if(j < 0) {
                    current.setNeighbor(grid[i][j - 1], Tile.TOP);
                }
            }
            for(int k = Tile.RIGHT; k < Tile.BOTTOM; k++) {
                linkHelper(current, k, i, j);
            }
        }
        
        private void linkHelper(Tile current, int direction, int i, int j) {
            switch (direction) {
                case Tile.LEFT:
                    i -= 1;
                    direction = Tile.RIGHT;
                case Tile.RIGHT:
                    i += 1;
                    direction = Tile.LEFT;
                case Tile.BOTTOM:
                    j += 1;
                    direction = Tile.TOP;
                case Tile.TOP:
                    j -= 1;
                    direction = Tile.BOTTOM;
            }
            if(i >= 0 && j >=0 && i < TILE_GRID_WIDTH && j < TILE_GRID_HEIGHT) {
                Tile neighbor = grid[i][j];
                if(neighbor != null)
                    neighbor.setNeighbor(current, direction);
            }
        }
        
        private void addTile(Tile t, int x, int y) {
            int i = x - this.x;
            int j = y - this.y;
            grid[i][j] = t;
            link(i, j);
            t.place(i * 50, j * 50);
        }
    }
}
