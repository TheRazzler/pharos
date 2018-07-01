/**
 * 
 */
package state;

import java.awt.Canvas;
import java.awt.Graphics;

import model.TileManager;

/**
 * The State of the game that handles the actual game
 * @author Spencer Yoder
 */
public class GameState extends State {
    private TileManager tileManager;

    /**
     * @param canvas
     */
    public GameState(Canvas canvas) {
        super(canvas);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see state.State#tick()
     */
    @Override
    public void tick() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see state.State#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics g) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see state.State#handleClick()
     */
    @Override
    public void handleClick() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see state.State#load()
     */
    @Override
    protected void load() {
        tileManager = new TileManager(layerManager);
    }

    /* (non-Javadoc)
     * @see state.State#unload()
     */
    @Override
    protected void unload() {
        tileManager = null;
    }
}
