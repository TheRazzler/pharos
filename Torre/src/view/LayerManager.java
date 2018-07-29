/**
 * 
 */
package view;

import java.awt.Graphics;
import java.util.ArrayList;

import component.Component;
import model.Debug;

/**
 * A class for handling layers (drawing Components on top of other Components)
 * maintains a LinkedList of Components in order of their layer
 * @author Spencer Yoder
 */
public class LayerManager {
    /** The front of the LinkedList of Components */
    private Node head;
    /** A list to assist in the quick removal of Components */
    private ArrayList<Node> tempList;
    
    /**
     * Constructs a new LayerManager
     */
    public LayerManager() {
        head = null;
        tempList = new ArrayList<Node>();
    }
    
    /**
     * Adds a Component to the LinkedList in order of the layer in the Component
     * @param c the Component
     * @param layer the layer of the Component
     */
    public void addComponent(Component c, int layer) {
        c.setLayer(layer);
        addNode(new Node(c));
    }
    
    /**
     * Adds the Node to the linked list
     * @param n the given Node
     */
    private void addNode(Node n) {
        if(head == null) {
            head = n;
        } else if(n.c.layer <= head.c.layer) {
            n.next = head;
            head.prev = n;
            head = n;
        } else {
            Node current = head;
            while(current.next != null && current.next.c.layer < n.c.layer) {
                current = current.next;
            }
            n.next = current.next;
            if(current.next != null)
                current.next.prev = n;
            current.next = n;
            n.prev = current;
        }
    }
    
    /**
     * Adds a Component which will later be removed
     * @param c the given Component
     * @param layer the layer of the Component
     */
    public void temporaryAdd(Component c, int layer) {
        c.setLayer(layer);
        c.setLayerIndex(tempList.size());
        Node n = new Node(c);
        addNode(n);
        tempList.add(n);
    }
    
    /**
     * Removes the given Component from the LayerManager
     * @param c the given Component
     */
    public void remove(Component c) {
        if(!tempList.isEmpty()) {
            Node n = tempList.remove(c.getLayerIndex());
            for(int i = 0; i < tempList.size(); i++) {
                if(tempList.get(i).c.getLayerIndex() != i) {
                    tempList.get(i).c.setLayerIndex(i);
                }
            }
            if(n.prev != null)
                n.prev.next = n.next;
            else
                head = head.next;
            if(n.next != null)
                n.next.prev = n.prev;
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
     * @return a String representation of the LinkedList for debugging
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if(head != null) {
            sb.append(head.c.toString());
            Node current = head.next;
            while(current != null) {
                sb.append(", " + current.c.toString());
                current = current.next;
            }
        }
        sb.append("]");
        return sb.toString();
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
        /** The previous Node in the List */
        private Node prev;
        
        /**
         * Constructs a new Node with the given Component
         * @param c the given Component
         */
        private Node(Component c) {
            this.c = c;
        }
        @Override
        public String toString() {
            return c.toString();
        }
    }
}
