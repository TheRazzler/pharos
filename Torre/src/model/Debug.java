/**
 * 
 */
package model;

/**
 * A class for printing to the console if the debug mode is turned on
 * @author Spencer Yoder
 */
public class Debug {
    /** Whether or not to print to console */
    public static boolean debugMode = false;
    
    /**
     * @see java.io.PrintStream#print(String)
     */
    public static void print(String s) {
        if(debugMode) {
            System.out.print(s);
        }
    }
    
    /**
     * @see java.io.PrintStream#println(String)
     */
    public static void println(String s) {
        if(debugMode) {
            System.out.println(s);
        }
    }
}
