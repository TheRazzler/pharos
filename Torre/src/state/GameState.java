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
    private TileManager tileManager;
    private Component background;
    private BreakIndicator breakIndicator;
    private Point[] activeTileRange;
    private Hotbar hotbar;
    private PlaceIndicator placeIndicator;
    private Point mousePos;
    private WarFog warFog;

    /**
     * @param canvas
     */
    public GameState(Canvas canvas) {
        super(canvas);
    }

    /* (non-Javadoc)
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
    
    public void spawnItem(Item item) {
        if(item != null) {
            mouseWatcher.temporaryAdd(item);
            layerManager.temporaryAdd(item, 3);
        }
    }

    /* (non-Javadoc)
     * @see state.State#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics g) {
        layerManager.render(g);
    }

    /* (non-Javadoc)
     * @see state.State#handleClick()
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

    /* (non-Javadoc)
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

    /* (non-Javadoc)
     * @see state.State#unload()
     */
    @Override
    protected void unload() {
        tileManager = null;
    }

    /* (non-Javadoc)
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
    
    @Override
    public void handleScroll(MouseWheelEvent e) {
        hotbar.changeSelection(e.getWheelRotation());
        placeIndicator.setInventorySlot(hotbar.slots[hotbar.index]);
    }
    
    private static class BreakIndicator extends Component {
        private double progress;
        private double breakTime;
        private BreakIndicator(double breakTime) {
            super(null);
            this.breakTime = breakTime;
            progress = 0;
            animator = new Animator(new SpriteSheet(15, 15, Loader.loadTexture("/textures/break_indicator.png")), breakTime);
        }
        private double progress() {
            progress += (1 / breakTime);
            return progress;
        }
    }

    /* (non-Javadoc)
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
     * @param item
     */
    public void handleItemClick(Item item) {
        if(item != null) {
            layerManager.remove(item);
            mouseWatcher.remove(item);
            hotbar.addItem(item);
        }
    }
    
    private class Hotbar extends Component {
        private InventorySlot[] slots;
        private int index;
        private Selection selection;
        
        private Hotbar() {
            super(Loader.loadTexture("/textures/hotbar.png"));
            slots = new InventorySlot[8];
            selection = new Selection();
        }
        
        public boolean addItem(Item item) {
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
            return found;
        }
        
        public void changeSelection(int i) {
            index = (index + i) % 8;
            if(index < 0)
                index = 7;
        }
        
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
        
        private class Selection extends Component {
            private Selection() {
                super(Loader.loadTexture("/textures/selection.png"));
            }
        }
    }
    
    private class InventorySlot extends Component {
        private int amount;
        private Item item;
        
        private InventorySlot(Item i) {
            super(null);
            animator = i.getAnimator();
            amount = 1;
            item = i;
        }
        
        @Override
        public void render(Graphics g) {
            super.render(g);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 24));
            int width = g.getFontMetrics().stringWidth("" + amount);
            g.drawString("" + amount, x + 46 - width, y + 42);
        }
    }
    
    private class PlaceIndicator extends Component {
        private InventorySlot slot;
        
        public PlaceIndicator(InventorySlot  slot) {
            super(null);
            setInventorySlot(slot);
        }
        
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
    
    private class WarFog extends Component {
        private SpriteSheet edges;
        private WarFog() {
            super(null);
            edges = new SpriteSheet(1500, 150, Loader.loadTexture("/textures/fog_edges.png"));
        }
        
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
