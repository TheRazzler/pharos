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
        Point p = canvas.getMousePosition();
        if(breakIndicator != null && p != null) {
            breakIndicator.place(p.x - 7, p.y - 7);
            if(p.x < activeTileRange[0].x || p.x > activeTileRange[1].x || p.y < activeTileRange[0].y ||
                    p.y > activeTileRange[1].y) {
                layerManager.remove(breakIndicator);
                Tile activeTile = tileManager.getTile(p.x, p.y);
                if(activeTile != null && activeTile.canBreak()) {
                    breakIndicator = new BreakIndicator(activeTile.getBreakTime());
                    layerManager.temporaryAdd(breakIndicator, 2);
                    activeTileRange = tileManager.getActiveRange(p);
                } else {
                    breakIndicator = null;
                }
            }
        }
        if(breakIndicator != null && p != null) {
            if(breakIndicator.progress() >= 60) {
                Item item = tileManager.breakTile(p.x, p.y);
                if(item != null) {
                    mouseWatcher.temporaryAdd(item);
                    layerManager.temporaryAdd(item, 3);
                }
                layerManager.remove(breakIndicator);
                breakIndicator = null;
            }
        }
        tileManager.tick();
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
            Point p = canvas.getMousePosition();
            tileManager.handleRightClick(p.x, p.y);
        } else if(SwingUtilities.isLeftMouseButton(e)) {
            Debug.println("Screen clicked");
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
        hotbar.place(523, 938);
        layerManager.addComponent(hotbar, 5);
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
        if(SwingUtilities.isLeftMouseButton(e)) {
            Point p = canvas.getMousePosition();
            activeTileRange = tileManager.getActiveRange(p);
            Tile t = tileManager.getTile(p.x, p.y);
            if(t != null && t.canBreak()) {
                breakIndicator = new BreakIndicator(t.getBreakTime());
                layerManager.temporaryAdd(breakIndicator, 2);
            }
        }
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
        private Hotbar() {
            super(Loader.loadTexture("/textures/hotbar.png"));
            slots = new InventorySlot[8];
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
            }
            return found;
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
            g.drawString("" + amount, x + 34, y + 42);
        }
    }
}
