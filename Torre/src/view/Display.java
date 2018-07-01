/**
 * 
 */
package view;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

/**
 * A class for the game window
 * @author Spencer Yoder
 */
public class Display {
    /** The game window */
    private JFrame frame;
    /** The title for the JFrame */
    private String title;
    /** The width and height of the JFrame */
    private int width, height;
    /** The part of the window to which things are drawn */
    private Canvas canvas;
    
    /**
     * Constructs a new Display with the given JFrame title, width, and height
     * @param title the title of the JFrame
     * @param width the width of the JFrame
     * @param height the height of the JFrame
     */
    public Display(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        createDisplay();
    }
    
    /**
     * Create and show the JFrame
     */
    private void createDisplay() {
        frame = new JFrame(title);
        frame.setSize(width,height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        
        frame.add(canvas);
        frame.pack();
    }
    
    /**
     * @return the canvas
     */
    public Canvas getCanvas() {
        return canvas;
    }
}
