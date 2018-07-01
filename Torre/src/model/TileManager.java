/**
 * 
 */
package model;

import java.awt.Point;

import component.Animator;
import component.Component;
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
public class TileManager {
    /** The SpriteSheet which contains the textures for each tile */
    private SpriteSheet tileSheet;
    /** The {@link view.LayerManager} to which the TileManager will draw the Tiles */
    private LayerManager layerManager;
    /** The TileGrid which occupies the screen 
     * TEMPORARY: the final game is planned to contain smooth scrolling so multiple TileGrids may be
     * on screen at a given time*/
    private TileGrid home;
    
    private Point mousePress;
    private Component breakIndicator;
    
    /** The width (in Tiles) of the screen */
    public static final int TILE_GRID_WIDTH = 30;
    /** The width (in Tiles) of the screen */
    public static final int TILE_GRID_HEIGHT = 20;
    
    /**
     * Constructs a new TileManager which draws to the given LayerManager
     * @param layerManager the LayerManager
     */
    public TileManager(LayerManager layerManager) {
        this.layerManager = layerManager;
        tileSheet = new SpriteSheet(50, 50, Loader.loadTexture("/textures/tiles/tile_sheet.png"));
        home = new TileGrid(-14, -10);
    }
    
    /**
     * Calculates the state of every Tile on screen and adjusts accordingly
     */
    public void tick() {
        //TODO
    }
    
    public void handleRightClick(int x, int y) {
        if(home.grid[x / Tile.LENGTH][y / Tile.LENGTH] != null) {
            home.grid[x / Tile.LENGTH][y / Tile.LENGTH].onRightClick();
        }
    }
    
    public void handlePress(Point p) {
        
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
                    if(globalY != 0) {
                        addTile(new LockedTile(), globalX, globalY);
                    }
                }
            }
            addTile(new Crystal(), 0, 0);
            for(int i = 0; i < TILE_GRID_WIDTH; i++) {
                for(int j = 0; j < TILE_GRID_HEIGHT; j++) {
                    link(i, j);
                }
            }
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
                if(j > 0) {
                    current.setNeighbor(grid[i][j - 1], Tile.BOTTOM);
                }
                if(j < TILE_GRID_HEIGHT - 1) {
                    current.setNeighbor(grid[i][j + 1], Tile.TOP);
                }
            }
        }
        
        private void addTile(Tile t, int x, int y) {
            int i = x - this.x;
            int j = y - this.y;
            grid[i][j] = t;
            t.place(i * 50, j * 50);
            layerManager.addComponent(t, 1);
        }
    }
    
    /**
     * The tiles outside the range of the crystal at the top of the tower
     * Cannot be interacted with, serve as a fog of war
     * @author Spencer Yoder
     */
    private class LockedTile extends Tile {
        public LockedTile() {
            super(tileSheet.getSprite(0, 0), false, -1, false, -1, 0);
        }
        @Override
        public void onRightClick() {
            //Do nothing
            Debug.println("LockedTile");
        }
    }
    
    private class Crystal extends Tile {
        public Crystal() {
            super(null, false, -1, true, -1, 0);
            animator = new Animator(new SpriteSheet(50, 50, Loader.loadTexture("/textures/tiles/crystal.png")), 2);
        }

        /* (non-Javadoc)
         * @see component.Tile#onRightClick()
         */
        @Override
        public void onRightClick() {
            // TODO Auto-generated method stub
            
        }
    }
    
    private class GrassTile extends Tile {
        private GrassTile() {
            super(tileSheet.getSprite(1, 0), true, 1, true, -1, 4);
        }

        /* (non-Javadoc)
         * @see component.Tile#onRightClick()
         */
        @Override
        public void onRightClick() {
            
        }
    }
}
