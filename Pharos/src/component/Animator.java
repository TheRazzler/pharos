/**
 * 
 */
package component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import view.SpriteSheet;

/**
 * A class to include as a field in animated components. 
 * @see component.Component#render(java.awt.Graphics) to see how an Animator is used.
 * @author Spencer Yoder
 */
public class Animator {
    /** The list of frames in the animation */
    private ArrayList<BufferedImage> frames;
    /** The index of the current frame of the animation */
    private int frameIndex;
    /** The duration (in in-game ticks) of each frame. Loops. */
    private double duration;
    /** An index to make each frame last the specified duration */
    private double intraFrameIndex;
    
    /**
     * Constructs a new Animator with the sprites in the given SpriteSheet
     * Reads each sprite from left-to-right, top-to-bottom
     * @param sheet the given SpriteSheet
     * @param duration the duration of each frame in in-game ticks
     */
    public Animator(SpriteSheet sheet, double duration) {
        frameIndex = 0;
        intraFrameIndex = 0;
        this.duration = duration;
        this.frames = new ArrayList<BufferedImage>(sheet.columns * sheet.rows);
        for(int j = 0; j < sheet.rows; j++) {
            for(int i = 0; i < sheet.columns; i++) {
                frames.add(sheet.getSprite(i, j));
            }
        }
    }
    
    /**
     * @return the next frame in the animation.
     */
    public BufferedImage nextFrame() {
        intraFrameIndex += 1;
        while(intraFrameIndex >= duration) {
            frameIndex++;
            frameIndex %= frames.size();
            intraFrameIndex -= duration;
        }
        return frames.get(frameIndex);
    }
}
