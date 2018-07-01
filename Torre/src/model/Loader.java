/**
 * 
 */
package model;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A class for Loading media files from the computer to the game
 * @author Spencer Yoder
 */
public class Loader {
    /**
     * Loads an image from the given path name
     * @param pathName the path name in the form "/textures/[filename]"
     * @return the image
     */
    public static BufferedImage loadTexture(String pathName) {
        try {
            return ImageIO.read(Loader.class.getResource(pathName));
        } catch (IOException e) {
            throw new IllegalArgumentException("Resource failed to load: " + pathName);
        }
    }
}
