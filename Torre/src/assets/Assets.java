/**
 * 
 */
package assets;

import java.awt.image.BufferedImage;

import component.Animator;
import component.ClickableComponent;
import component.Component;
import component.Tile;
import model.Debug;
import model.Game;
import model.Loader;
import model.TileManager;
import state.State;
import view.SpriteSheet;

/**
 * A class for holding, loading and unloading game assets
 * Since each asset potentially has different behavior, each needs its own class definition.
 * All asset class definitions are stored as inner classes of this class to prevent bloat
 * in the number of classes.
 * The {@link state.State#load()} and {@link state.State#unload()} methods delegate to methods
 * stored in this class.
 * The assets in each state are loaded and unloaded to prevent overuse of memory
 * @author Spencer Yoder
 */
public class Assets {
    //Loading Screen Assets
    /** The background of the loading screen */
    public static LoadingBackground loadingBackground;
    /** The "Loading.." text on the loading screen */
    public static LoadingText loadingText;
    
    //Menu Assets
    /** The sheet containing the sprites for the main menu buttons */
    private static SpriteSheet menuButtonSheet;
    /** The light on top of the tower in the main menu */
    public static LightAnimation lightAnimation;
    /** The background of the main menu */
    public static MenuBackground menuBackground;
    /** The settings button */
    public static SettingsButton settingsButton;
    /** The start game button */
    public static StartGameButton startGameButton;
    
    //Game Assets
    public static GameBackground gameBackground;

    //Loading Screen Assets (never unloaded)
    /**
     * Loads the assets for the loading screen. These are stored in the Game class, not a State.
     * This means they are never unloaded, they persist for the runtime of the game.
     */
    public static void loadLoadingScreenAssets() {
        loadingBackground = new LoadingBackground();
        loadingText = new LoadingText();
    }
    
    //Menu Assets
    /**
     * Loads the assets for the main menu state
     */
    public static void loadMenuAssets() {
        lightAnimation = new LightAnimation();
        menuBackground = new MenuBackground();
        menuButtonSheet = new SpriteSheet(400, 80, Loader.loadTexture("/textures/menu_state_sheet.png"));
        settingsButton = new SettingsButton();
        startGameButton = new StartGameButton();
    }
    /**
     * Unloads the assets for the main menu state
     */
    public static void unloadMenuAssets() {
        lightAnimation = null;
        menuBackground = null;
        menuButtonSheet = null;
        settingsButton = null;
    }
    
    //Game Assets
    public static void loadGameAssets() {
        gameBackground = new GameBackground();
    }
    public static void unloadGameAssets() {
        gameBackground = null;
    }
    
    //Loading Screen Assets
    /**
     * A class for the background on the loading screen.
     * @author Spencer Yoder
     */
    private static class LoadingBackground extends Component {
        public LoadingBackground() {
            super(Loader.loadTexture("/textures/loading_background.png"));
        }
    }
    /**
     * A class for the "Loading..." text in the loading screen.
     * @author Spencer Yoder
     */
    private static class LoadingText extends Component {
        public LoadingText() {
            super(null);
            animator = new Animator(new SpriteSheet(450, 100, Loader.loadTexture("/textures/loading_text_animated.png")), 20);
        }
    }
    
    //Menu Assets
    /**
     * A class for the light on top of the tower in the menu screen
     * @author Spencer Yoder
     */
    private static class LightAnimation extends Component {
        /**
         * Constructs a new LightAnimation
         */
        public LightAnimation() {
            super(null);
            animator = new Animator(new SpriteSheet(40, 40, Loader.loadTexture("/textures/light_animation.png")), 5);
        }
    }
    /**
     * A class for the background of the menu screen
     * @author Spencer Yoder
     */
    private static class MenuBackground extends Component {
        public MenuBackground() {
            super(Loader.loadTexture("/textures/menu_background.png"));
        }
    }
    /**
     * A class for the settings button on the menu screen
     * @author Spencer Yoder
     */
    private static class SettingsButton extends ClickableComponent {
        private BufferedImage unlit;
        private BufferedImage lit;

        public SettingsButton() {
            super(null);
            this.unlit = menuButtonSheet.getSprite(0, 2);
            this.lit = menuButtonSheet.getSprite(0, 3);
            updateTexture(unlit);
        }
        
        @Override
        public void onMouseOver() {
            texture = lit;
        }

        @Override
        public void onMouseLeave() {
            texture = unlit;
        }

        @Override
        public void onClick() {
//            State.setState(Game.settingsState);
        }
    }
    /**
     * A class for the start game button
     * @author Spencer Yoder
     */
    private static class StartGameButton extends ClickableComponent {
        private BufferedImage unlit;
        private BufferedImage lit;

        /**
         * Constructs the StartGameButton
         */
        public StartGameButton() {
            super(null);
            this.unlit = menuButtonSheet.getSprite(0, 0);
            this.lit = menuButtonSheet.getSprite(0, 1);
            updateTexture(unlit);
        }
        
        /***/
        @Override
        public void onMouseOver() {
            texture = lit;
        }

        /***/
        @Override
        public void onClick() {
            State.setState(Game.gameState);
        }

        /***/
        @Override
        public void onMouseLeave() {
            texture = unlit;
        }
    }
    
    //Game Assets
    /**
     * A class for the game background (currently a grid to assist debugging)
     * @author Spencer Yoder
     */
    private static class GameBackground extends Component {
        private GameBackground() {
            super(Loader.loadTexture("/textures/debug_background.png"));
        }
    }
}
