/**
 * 
 */
package model;

import model.Game;

/**
 * The java file which is run to start the game
 * @author Spencer Yoder
 */
public class Launcher {
    /**
     * Starts the game
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Game game = new Game("Torre", 1500, 1000);
        game.start();
    }
}
