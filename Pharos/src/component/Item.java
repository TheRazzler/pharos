/**
 * 
 */
package component;

import java.awt.Graphics;
import java.util.Random;

import model.Game;
import model.Loader;
import view.SpriteSheet;

/**
 * A class representing a collectible item which can be stored in the inventory.
 * Upon spawning, the item will shoot off in a random direction.
 * Items are collected upon mouse-over
 * @author Spencer Yoder
 */
public abstract class Item extends ClickableComponent {
    /** The initial speed of the Item */
    private int speed;
    /** The direction of the Item */
    private double direction;
    
    /**
     * Constructs a new Item with the given {@link view.SpriteSheet}, x, and y.
     * @param spriteSheet, a SpriteSheet where each sprite is a frame in the Item's animation.
     * @param x the initial x coordinate of the Item
     * @param y the initial y coordinate of the Item
     */
    public Item(SpriteSheet spriteSheet, int x, int y) {
        super(null);
        animator = new Animator(spriteSheet, 5);
        place(x+ 3, y + 3);
        speed = 15;
        Random r = new Random();
        direction = r.nextDouble() * 360;
    }

    /**
     * Upon mouse-over, the item is collected
     * @see state.GameState#handleItemCollect(Item)
     */
    @Override
    public void onMouseOver() {
        Game.gameState.handleItemCollect(this);
    }
    
    /**
     * @return a new {@link component.Tile} which this Item represents
     */
    public abstract Tile getTile();

    /**
     * Shoots the Item in its direction with decelerating speed
     */
    @Override
    public void render(Graphics g) {
        super.render(g);
        if(speed > 0) {
            int dx = (int) (Math.cos(direction) * speed);
            int dy = (int) (Math.sin(direction) * speed);
            int realX = x + dx > Game.width - Tile.LENGTH || x + dx < 0 ? x : x + dx;
            int realY = y + dy > Game.height - Tile.LENGTH || y + dy < 0 ? y : y + dy;
            place(realX, realY);
            speed--;
        }
    }
    
    /**
     * An Item for {@link component.Tile.DirtTile}
     * @author Spencer Yoder
     */
    public static class MudItem extends Item {
        /** See {@link component.Item#Item(SpriteSheet, int, int)} */
        private static final SpriteSheet mudSheet = new SpriteSheet(44, 44, Loader.loadTexture("/textures/item/mud_item_sheet2.png"));

        /** 
         * See {@link component.Item#Item(SpriteSheet, int, int)} 
         */
        public MudItem(int x, int y) {
            super(mudSheet, x, y);
        }
        
        @Override
        public Tile getTile() {
            return new Tile.DirtTile();
        }
    }
    
    /**
     * An Item for {@link component.Tile.StoneTile}
     * @author Spencer Yoder
     */
    public static class StoneItem extends Item {
        /**
         * See {@link component.Item#Item(SpriteSheet, int, int)}
         */
        public StoneItem(int x, int y) {
            super(stoneSheet, x, y);
        }
        
        /** See {@link component.Item#Item(SpriteSheet, int, int)} */
        private static final SpriteSheet stoneSheet = new SpriteSheet(44, 44, Loader.loadTexture("/textures/item/cobblestone_item_sheet.png"));
        
        @Override
        public Tile getTile() {
            return new Tile.StoneTile();
        }
    }
    
    /**
     * An Item for {@link component.Tile.ScaffoldTile}
     * @author Spencer Yoder
     */
    public static class ScaffoldItem extends Item {
        /** See {@link component.Item#Item(SpriteSheet, int, int)} */
        private static final SpriteSheet scaffoldSheet = new SpriteSheet(44, 44, Loader.loadTexture("/textures/item/scaffold_item_sheet.png"));
        
        /**
         * See {@link component.Item#Item(SpriteSheet, int, int)}
         */
        public ScaffoldItem(int x, int y) {
            super(scaffoldSheet, x, y);
        }

        @Override
        public Tile getTile() {
            return new Tile.ScaffoldTile();
        }
    }
}
