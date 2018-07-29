/**
 * 
 */
package state;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import assets.Assets;
import component.ClickableComponent;
import component.Component;

/**
 * The State of the Game that handles the main menu
 * @author Spencer Yoder
 */
public class MenuState extends State {
    /** The start button */
    private ClickableComponent startButton;
    /** The settings button */
    private ClickableComponent settingsButton;
    /** The background */
    private Component background;
    /** A little animation for the light on top of the tower */
    private Component lightAnimation;
    
    /**
     * Loads and places all Components in the State as well as adds the to the proper managers
     * @see state.State#State(Canvas)
     */
    public MenuState(Canvas canvas) {
        super(canvas);
        load();
        
        background = Assets.menuBackground;
        settingsButton = Assets.settingsButton;
        startButton = Assets.startGameButton;
        lightAnimation = Assets.lightAnimation;
        
        mouseWatcher.addComponent(startButton);
        mouseWatcher.addComponent(settingsButton);
        
        layerManager.addComponent(startButton, 1);
        layerManager.addComponent(settingsButton, 1);
        layerManager.addComponent(background, 0);
        layerManager.addComponent(lightAnimation, 1);
        
        startButton.place(550, 410);
        settingsButton.place(550, 500);
        lightAnimation.place(720, -6);
    }
    
    /**
     * @see state.State#tick()
     */
    @Override
    public void tick() {
        mouseWatcher.checkComponents();
    }
    
    /**
     * @see state.State#render(Graphics)
     */
    @Override
    public void render(Graphics g) {
        layerManager.render(g);
    }
    
    /**
     * @see state.State#handleClick(MouseEvent)
     */
    @Override 
    public void handleClick(MouseEvent e) {
        mouseWatcher.handleClick();
    }

    /**
     * @see state.State#load()
     */
    @Override
    public void load() {
        Assets.loadMenuAssets();
    }

    /**
     * @see state.State#unload()
     */
    @Override
    public void unload() {
        Assets.unloadMenuAssets();
    }

    /* (non-Javadoc)
     * @see state.State#handlePress(java.awt.event.MouseEvent)
     */
    @Override
    public void handlePress(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see state.State#handleRelease(java.awt.event.MouseEvent)
     */
    @Override
    public void handleRelease(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
}
