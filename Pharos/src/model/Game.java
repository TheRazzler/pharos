/**
 * 
 */
package model;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import assets.Assets;
import component.Component;
import state.GameState;
import state.MenuState;
import state.SettingsState;
import state.State;
import view.Display;

/**
 * The class responsible for running the entire game
 * @author Spencer Yoder
 * @author Some YouTube guy
 */
public class Game implements Runnable {
    /** The {@link view.Display} for the game*/
    private Display display;
    /** The width and height of the display window */
    public int width, height;
    /** The title for the Display */
    public String title;
    /** The separate Thread on which this game runs */
    private Thread thread;
    /** Whether or not the game is currently running (i.e. is done initializing */
    private boolean running = false;
    /** The {@link java.awt.image.BufferStrategy} for displaying each frame of the game */
    private BufferStrategy bs;
    /** The {@link java.awt.Graphics} the game will draw to */
    private Graphics g;
    
    /**
     * These Components are kept loaded in the Game for display when the game is changing States
     */
    private Component loadingBackground;
    private Component loadingText;
    
    /** The State for the main menu */
    public static MenuState menuState;
    /** The State for the settings screen */
    //TODO currently empty
    public static SettingsState settingsState;
    /** The state for the game part of the game */
    public static GameState gameState;
    
    /**
     * Constructs a new Game with the given title, width, and height
     * @param title the title for the Display
     * @param width the width of the Display
     * @param height the height of the Display
     */
    public Game(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;
    }
    
    /**
     * Loads the game
     */
    private void init() {
        display = new Display(title, width, height);
        display.getCanvas().addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
             State.getState().handleRelease(e);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                State.getState().handlePress(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub 
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                State.getState().handleClick(e);
            }
        });
        
        display.getCanvas().addMouseWheelListener(new MouseWheelListener() {
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                State.getState().handleScroll(e);
            }
        });
        menuState = new MenuState(display.getCanvas());
        settingsState = new SettingsState(display.getCanvas());
        gameState = new GameState(display.getCanvas());
        
        Assets.loadLoadingScreenAssets();
        loadingBackground = Assets.loadingBackground;
        loadingText = Assets.loadingText;
        loadingText.place(550, 450);
        
        State.setState(menuState);
    }
    
    /**
     * Runs the game at a framerate of 60 fps.
     */
    @Override
    public void run() {
        init();
        int fps = 60;
        double timePerTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        while(running) {
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            lastTime = now;
            if(delta >= 1) {
                tick();
                render();
                delta--;
            }
        }
        stop();
    }
    
    /**
     * Calculate the internal state of the game
     */
    private void tick() {
        if(State.getState() != null) {
            State.getState().tick();
        }
    }
    
    /**
     * Draw the game to the screen
     */
    private void render() {
        Canvas c = display.getCanvas();
        bs = c.getBufferStrategy();
        if(bs == null) {
            c.createBufferStrategy(3);
        } else {
            g = bs.getDrawGraphics();
            g.clearRect(0, 0, width, height);
            if(State.getState() == null) {
                loadingBackground.render(g);
                loadingText.render(g);
            } else {
                State.getState().render(g);
            }
            bs.show();
            g.dispose();
        }
    }
    
    /**
     * Start running the game
     */
    public synchronized void start() {
        if(!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }
    
    /**
     * Stop running the game
     */
    public synchronized void stop() {
        if(running) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                //Do nothing
            }
        }
    }
}
