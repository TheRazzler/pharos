/**
 * 
 */
package view;

import java.awt.Graphics;
import java.util.ArrayList;

import component.Animator;
import component.Component;

/**
 * A class for handling layers (drawing Components on top of other Components)
 * maintains a LinkedList of Components in order of their layer
 * @author Spencer Yoder
 */
public class LayerManager {
    /** The front of the LinkedList of Components */
    private Node head;
    private Node beforeHead;
    private ArrayList<Node> tempList;
    
    /**
     * Constructs a new LayerManager
     */
    public LayerManager() {
        head = null;
        beforeHead = new Node(null);
        tempList = new ArrayList<Node>();
    }
    
    /**
     * Adds a Component to the LinkedList in order of the layer in the Component
     * @param c the Component
     * @param layer the layer of the Component
     */
    public void addComponent(Component c, int layer) {
        c.setLayer(layer);
        if(head == null) {
            head = new Node(c);
        } else if(c.layer <= head.c.layer) {
            Node n = new Node(c);
            n.next = head;
            head = n;
        } else {
            Node current = head;
            while(current.next != null && current.next.c.layer < c.layer) {
                current = current.next;
            }
            Node n = new Node(c);
            n.next = current.next;
            current.next = n;
        }
    }
    
    public int temporaryAdd(Component c, int layer) {
        c.setLayer(layer);
        int toReturn = tempList.size();
        Node n = new Node(c);
        if(head == null) {
            head = n;
            beforeHead.next = head;
            tempList.add(beforeHead);
        } else if(c.layer <= head.c.layer) {
            n.next = head;
            n = head;
            beforeHead.next = head;
            tempList.add(beforeHead);
        } else {
            Node current = head;
            while(current.next != null && current.next.c.layer < c.layer) {
                current = current.next;
            }
            n.next = current.next;
            current.next = n;
            tempList.add(current);
        }
        return toReturn;
    }
    
    public void remove(int idx) {
        Node n = tempList.remove(idx);
        if(n == beforeHead) {
            head = head.next;
        } else {
            n.next = n.next.next;
        }
    }
    
    /**
     * Draws all Components to the screen
     * @param g the graphics to which the Components will be drawn
     */
    public void render(Graphics g) {
        Node current = head;
        while(current != null) {
            current.c.render(g);
            current = current.next;
        }
    }
    
    /**
     * A Node in the LinkedList
     * @author Spencer Yoder
     */
    private class Node {
        /** The next Node in the List */
        private Node next;
        /** The Component */
        private Component c;
        
        /**
         * Constructs a new Node with the given Component
         * @param c the given Component
         */
        private Node(Component c) {
            this.c = c;
        }
    }
}
