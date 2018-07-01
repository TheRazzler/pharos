/**
 * 
 */
package component;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * A class representing a visual component of the game.
 * @author Spencer Yoder
 */
public abstract class Component {
    /** The x position of the top-left corner of the Component (defaults to zero) */
    public int x = 0;
    /** The y position of the top-left corner of the Component (defaults to zero) */
    public int y = 0;
    /** The width of the component */
    public int width;
    /** The height of the component */
    public int height;
    /** The visual appearance of the component */
    public BufferedImage texture;
    public int layer;
    protected Animator animator;
    
    /**
     * Constructs a new Component with the given texture
     * @param texture the visual appearance of the Component
     */
    public Component(BufferedImage texture) {
        updateTexture(texture);
        animator = null;
    }
    
    /**
     * Updates this Component's x and y positions to reflect the given x and y
     * @param x the new x position
     * @param y the new y position
     */
    public void place(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Draws this component to the screen.
     * @see component.Animator
     * @param g the Graphics to which this method will draw
     */
    public void render(Graphics g) {
        if(animator != null) {
            texture = animator.nextFrame();
        }
        g.drawImage(texture, x, y, null);
    }
    
    /**
     * Sets the layer of this component
     * @see view.LayerManager
     * @param layer the new Layer
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }
    
    /**
     * Sets the texture of this component to be the given texture
     * @param texture the new texture
     */
    public void updateTexture(BufferedImage texture) {
        if(texture != null) {
            this.texture = texture;
            this.width = texture.getWidth();
            this.height = texture.getHeight();
        }
    }
}
