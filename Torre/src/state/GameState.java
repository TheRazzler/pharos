/**
 * 
 */
package state;

import java.awt.Canvas;
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
                    mouseWatcher.addComponent(item);
                    layerManager.temporaryAdd(item, 3);
                }
                layerManager.remove(breakIndicator);
                breakIndicator = null;
            }
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
        tileManager = new TileManager(layerManager);
        Assets.loadGameAssets();
        background = Assets.gameBackground;
        layerManager.addComponent(background, 0);
        layerManager.addComponent(tileManager, 1);
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
        }
    }
}
