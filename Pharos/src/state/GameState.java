/**
 * 
 */
package state;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import assets.Assets;
import component.Animator;
import component.Component;
import component.Item;
import component.Tile;
import model.Debug;
import model.Loader;
import model.TileManager;
import view.SpriteSheet;

/**
 * The State of the game that handles the actual game
 * @author Spencer Yoder
 */
public class GameState extends State {
    /** See {@link model.TileManager} */
    private TileManager tileManager;
    /** The background for the GameState */
    private Component background;
    /** See {@link state.GameState.BreakIndicator} */
    private BreakIndicator breakIndicator;
    /** The range (in pixels) of the Tile that contains the mouse pointer */
    private Point[] activeTileRange;
    /** See {@link state.GameState.Hotbar} */
    private Hotbar hotbar;
    /** See {@link state.GameState.PlaceIndicator} */
    private PlaceIndicator placeIndicator;
    /** The position of the mouse (null if the mouse is not on screen) */
    private Point mousePos;
    /** See {@link state.GameState.WarFog} */
    private WarFog warFog;

    /**
     * @see state.State#State(Canvas)
     */
    public GameState(Canvas canvas) {
        super(canvas);
    }

    /**
     * @see state.State#tick()
     */
    @Override
    public void tick() {
        mouseWatcher.checkComponents();
        mousePos = canvas.getMousePosition();
        if(breakIndicator != null && mousePos != null && tileManager.mouseInBounds(mousePos)) {
            breakIndicator.place(mousePos.x - 7, mousePos.y - 7);
            if(mousePos.x < activeTileRange[0].x || mousePos.x > activeTileRange[1].x || mousePos.y < activeTileRange[0].y ||
                    mousePos.y > activeTileRange[1].y) {
                layerManager.remove(breakIndicator);
                Tile activeTile = tileManager.getTile(mousePos.x, mousePos.y);
                if(activeTile != null && activeTile.canBreak()) {
                    breakIndicator = new BreakIndicator(activeTile.getBreakTime());
                    layerManager.temporaryAdd(breakIndicator, 3);
                    activeTileRange = tileManager.getActiveRange(mousePos);
                } else {
                    breakIndicator = null;
                }
            }
        }
        if(breakIndicator != null && mousePos != null) {
            if(breakIndicator.progress() >= 60) {
                Item item = tileManager.breakTile(mousePos.x, mousePos.y);
                spawnItem(item);
                layerManager.remove(breakIndicator);
                breakIndicator = null;
            }
        }
        tileManager.tick();
        hotbar.tick();
    }
    
    /**
     * Spawns the given Item and draws it to the screen
     * @param item the given Item
     */
    public void spawnItem(Item item) {
        if(item != null) {
            mouseWatcher.temporaryAdd(item);
            layerManager.temporaryAdd(item, 3);
        }
    }

    /**
     * @see state.State#render(java.awt.Graphics)
     * @see view.LayerManager#render(Graphics)
     */
    @Override
    public void render(Graphics g) {
        layerManager.render(g);
    }

    /**
     * @see state.State#handleClick(MouseEvent)
     * @see model.TileManager#handleRightClick(int, int, Tile)
     * @see model.MouseWatcher#handleClick()
     */
    @Override
    public void handleClick(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)) {
            if(tileManager.mouseInBounds(mousePos) && 
                    tileManager.handleRightClick(mousePos.x, mousePos.y, placeIndicator.slot == null ? null : placeIndicator.slot.item.getTile())) {
                hotbar.slots[hotbar.index].amount--;
            }
        } else if(SwingUtilities.isLeftMouseButton(e)) {
            mouseWatcher.handleClick();
        }
    }

    /**
     * @see state.State#load()
     */
    @Override
    protected void load() {
        tileManager = new TileManager();
        Assets.loadGameAssets();
        background = Assets.gameBackground;
        layerManager.addComponent(background, 0);
        layerManager.addComponent(tileManager, 1);
        hotbar = new Hotbar();
        hotbar.place(523, 924);
        layerManager.addComponent(hotbar, 5);
        placeIndicator = new PlaceIndicator(hotbar.slots[hotbar.index]);
        layerManager.addComponent(placeIndicator, 2);
        
        hotbar.slots[0] = new InventorySlot(new Item.MudItem(0, 0));
        hotbar.slots[0].amount = 100;
        hotbar.slots[1] = new InventorySlot(new Item.StoneItem(0, 0));
        hotbar.slots[1].amount = 100;
        
        hotbar.slots[2] = new InventorySlot(new Item.ScaffoldItem(0, 0));
        hotbar.slots[2].amount = 100;
        if(hotbar.slots[hotbar.index] != null) {
            placeIndicator.setInventorySlot(hotbar.slots[hotbar.index]);
        }
        
        warFog = new WarFog();
        layerManager.addComponent(warFog, 4);
    }

    /**
     * @see state.State#unload()
     */
    @Override
    protected void unload() {
        //TODO incomplete
        tileManager = null;
    }

    /**
     * @see state.State#handlePress(java.awt.event.MouseEvent)
     */
    @Override
    public void handlePress(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e) && tileManager.mouseInBounds(mousePos)) {
            Point p = canvas.getMousePosition();
            activeTileRange = tileManager.getActiveRange(p);
            Tile t = tileManager.getTile(p.x, p.y);
            if(t != null && t.canBreak()) {
                breakIndicator = new BreakIndicator(t.getBreakTime());
                layerManager.temporaryAdd(breakIndicator, 3);
            }
        }
    }
    
    /**
     * @see state.GameState.Hotbar#changeSelection(int)
     */
    @Override
    public void handleScroll(MouseWheelEvent e) {
        hotbar.changeSelection(e.getWheelRotation());
        placeIndicator.setInventorySlot(hotbar.slots[hotbar.index]);
    }
    
    /**
     * Informs the user of the status of the current tile to be broken
     * Displays a little wheel at the mouse cursor that fills. When the wheel is full, the Tile breaks.
     * The indicator fills at a rate identical to the Tile break time.
     * When the user mouses off the current Tile, the indicator resets.
     * @author Spencer Yoder
     */
    private static class BreakIndicator extends Component {
        /** Starts at 0, at 1, the Tile breaks */
        private double progress;
        /** See {@link component.Tile#breakTime} */
        private double breakTime;
        /**
         * Constructs a new BreakIndicator from the given breakTime
         * @param breakTime See {@link component.Tile#breakTime}
         */
        private BreakIndicator(double breakTime) {
            super(null);
            this.breakTime = breakTime;
            progress = 0;
            animator = new Animator(new SpriteSheet(15, 15, Loader.loadTexture("/textures/break_indicator.png")), breakTime);
        }
        
        /**
         * Progress the wheel by 1 / breakTime (This works because the game ticks at 60 fps)
         * @return the current progress
         */
        private double progress() {
            progress += (1 / breakTime);
            return progress;
        }
    }

    /**
     * @see state.State#handleRelease(java.awt.event.MouseEvent)
     */
    @Override
    public void handleRelease(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            if(breakIndicator != null)
                layerManager.remove(breakIndicator);
            activeTileRange = null;
            breakIndicator = null;
        }
    }

    /**
     * Handles the collection of the given item
     * @param item the given item
     * @see state.GameState.Hotbar#addItem(Item)
     */
    public void handleItemCollect(Item item) {
        if(item != null) {
            layerManager.remove(item);
            mouseWatcher.remove(item);
            hotbar.addItem(item);
        }
    }
    
    /**
     * A class for the hotbar which stores Items that the user can place.
     * It is displayed at the bottom of the screen and will later interact with the inventory
     * @author Spencer Yoder
     */
    private class Hotbar extends Component {
        /** The array of {@link state.GameState.InventorySlot}s */
        private InventorySlot[] slots;
        /** The index of current slot the user has selected */
        private int index;
        /** See {@link state.GameState.Hotbar.Selection} */
        private Selection selection;
        
        /**
         * Constructs a new Hotbar
         */
        private Hotbar() {
            super(Loader.loadTexture("/textures/hotbar.png"));
            slots = new InventorySlot[8];
            selection = new Selection();
        }
        
        /**
         * Adds the given Item to the Hotbar.
         * If the item already has a slot in the Hotbar, that slot gets incremented, otherwise, 
         * the item is placed in the first empty slot from the left
         * @param item the given Item
         */
        public void addItem(Item item) {
            int idx = -1;
            boolean found = false;
            for(int i = 0; i < slots.length; i++) {
                if(slots[i] == null) {
                    if(!found) {
                        idx = i;
                        found = true;
                    }
                } else if(slots[i].item.getClass() == item.getClass()){
                    slots[i].amount++;
                    break;
                }
            }
            if(found) {
                slots[idx] = new InventorySlot(item);
                if(idx == index) {
                    placeIndicator.setInventorySlot(slots[idx]);
                }
            }
        }
        
        /**
         * Increment the selection by the given amount
         * @param i the given amount
         */
        public void changeSelection(int i) {
            index = (index + i) % 8;
            if(index < 0)
                index = 7;
        }
        
        /***/
        @Override
        public void render(Graphics g) {
            super.render(g);
            for(int i = 0; i < slots.length; i++) {
                if(slots[i] != null) {
                    slots[i].place(x + 56 * i + 9, y + 9);
                    slots[i].render(g);
                }
            }
            selection.place(2 + x + 56 * index, 2 + y);
            selection.render(g);
        }
        
        /**
         * Calculate the state of the Hotbar
         */
        public void tick() {
            for(int i = 0; i < slots.length; i++) {
                if(slots[i] != null && slots[i].amount == 0) {
                    slots[i] = null;
                    if(i == index) {
                        placeIndicator.setInventorySlot(null);
                    }
                }
            }
        }
        
        /**
         * Indicates which slot of the Hotbar is selected
         * @author Spencer Yoder
         */
        private class Selection extends Component {
            private Selection() {
                super(Loader.loadTexture("/textures/selection.png"));
            }
        }
    }
    
    /**
     * A class for holding one or many Items in the Hotbar
     * @author Spencer Yoder
     */
    private class InventorySlot extends Component {
        /** The number of Items in the slot */
        private int amount;
        /** The type of Item being held in the slot */
        private Item item;
        
        /**
         * Construct a new Inventory slot with the given Item
         * @param i the given Item
         */
        private InventorySlot(Item i) {
            super(null);
            animator = i.getAnimator();
            amount = 1;
            item = i;
        }
        
        /***/
        @Override
        public void render(Graphics g) {
            super.render(g);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 24));
            int width = g.getFontMetrics().stringWidth("" + amount);
            g.drawString("" + amount, x + 46 - width, y + 42);
        }
    }
    
    /**
     * Shows an Item texture at the cursor's position which indicates the item that will be placed 
     * @author Spencer Yoder
     */
    private class PlaceIndicator extends Component {
        /** The slot from which the item will be taken */
        private InventorySlot slot;
        
        /**
         * Constructs a new PlaceIndicator with the given InventorySlot
         * @param slot the given InventorySlot
         */
        public PlaceIndicator(InventorySlot  slot) {
            super(null);
            setInventorySlot(slot);
        }
        
        /**
         * Sets the InventorySlot to the given InvetorySlot
         * @param slot the given InventorySlot
         */
        private void setInventorySlot(InventorySlot slot) {
            if(this.slot == null || slot == null || slot.item.getClass() != this.slot.item.getClass()) {
                if(slot != null)
                    animator = slot.getAnimator();
                else {
                    animator = null;
                    texture = null;
                }
                this.slot = slot;
            }
        }
        
        /***/
        @Override
        public void render(Graphics g) {
            super.render(g);
            Point p = canvas.getMousePosition();
            if(p != null) {
                p = tileManager.convertToLocalTileCoords(p.x, p.y);
                p.x *= 50;
                p.y *= 50;
                place(p.x + 3, p.y + 3);
            }
        }
    }
    
    /**
     * Indicates which parts of the screen cannot be edited due to the tower
     * @author Spencer Yoder
     */
    private class WarFog extends Component {
        /** The faded edges of the WarFog */
        private SpriteSheet edges;
        
        /**
         * Constructs a new WarFog
         */
        private WarFog() {
            super(null);
            edges = new SpriteSheet(1500, 150, Loader.loadTexture("/textures/fog_edges.png"));
        }
        
        /***/
        @Override
        public void render(Graphics g) {
            g.setColor(Color.BLACK);
            int[] heights = tileManager.getVisibleRange();
            g.drawImage(edges.getSprite(0, 0), 0, heights[0] - 150, null);
            g.fillRect(0, 0, canvas.getWidth(), heights[0] - 150);
            g.drawImage(edges.getSprite(0, 1), 0, heights[1], null);
            g.fillRect(0, heights[1] + 150, canvas.getWidth(), canvas.getHeight() - heights[1]);
        }
    }
}
