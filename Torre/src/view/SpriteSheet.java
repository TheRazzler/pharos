/**
 * 
 */
package view;

import java.awt.image.BufferedImage;

/**
 * A class representing a group of textures, each occupying a fixed width and height
 * @author Spencer Yoder
 *
 */
public class SpriteSheet {
    /** The width of a texture in the SpriteSheet */
    private int width;
    /** The height of a texture in the SpriteSheet */
    private int height;
    /** The image the textures are contained within (png preferred) */
    private BufferedImage sheet;
    /** The number of sprites going across the sheet */
    public int columns;
    /** The number of sprites going down the sheet */
    public int rows;
    
    /**
     * Constructs the SpriteSheet with the given width, height and image
     * @param width The width of a single texture in the sheet
     * @param height The height of a single texture in the sheet
     */
    public SpriteSheet(int width, int height, BufferedImage sheet) {
        this.width = width;
        this.height = height;
        this.sheet = sheet;
        columns = sheet.getWidth() / width;
        rows = sheet.getHeight() / height;
    }
    
    /**
     * @param horizIDX the horizontal position (in sprites, not pixels) of the texture
     * @param vertIdx the vertical position (in sprites, not pixels) of the texture
     * @return a single texture from the sprite sheet
     */
    public BufferedImage getSprite(int horizIdx, int vertIdx) {
        int x = horizIdx * width;
        int y = vertIdx * height;
        if(x >= sheet.getWidth() || y >= sheet.getHeight()) {
            throw new IndexOutOfBoundsException();
        }
        return sheet.getSubimage(x, y, width, height);
    }
}
