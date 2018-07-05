/**
 * 
 */
package model;

import java.awt.Canvas;
import java.awt.Point;
import java.util.ArrayList;

import component.ClickableComponent;

/**
 * A class for managing components' interactions with the mouse
 * @author Spencer Yoder
 */
public class MouseWatcher {
    /** The front of the linked list containing the components */
    private Node head;
    /** The canvas to read the mouse position from */
    private Canvas canvas;
    /** The Component over which the mouse is hovering */
    private ClickableComponent activeComponent;
    
    private ArrayList<Node> tempList;
    
    /**
     * Constructs a new MouseWatcher which receives a mouse position from the given canvas
     * @param canvas the canvas which communicates the mouse position
     */
    public MouseWatcher(Canvas canvas) {
        head = null;
        this.canvas = canvas;
        tempList = new ArrayList<Node>();
    }
    
    /**
     * Adds the given ClickableComponent to the MouseWatcher.
     * The Component is added to the LinkedList in order of its distance to the point (0, 0)
     * @see component.ClickableComponent#compareTo(ClickableComponent)
     * @param c the Component to add
     */
    public void addComponent(ClickableComponent c) {
        addNode(new Node(c));
    }
    
    public void temporaryAdd(ClickableComponent c) {
        c.setMouseIndex(tempList.size());
        Node n = new Node(c);
        addNode(n);
        tempList.add(n);
    }
    
    /**
     * Checks each component (within the scope of the mouse)
     * @see component.Bound#withinScope(Point)
     * @see component.ClickableComponent#reactToMouse(Point)
     */
    public void checkComponents() {
        Point mousePos = canvas.getMousePosition();
        if(activeComponent == null) {
            Node current = head;
            while(current != null && current.c.bound.withinScope(mousePos)) {
                int status = current.c.reactToMouse(mousePos);
                if(status == ClickableComponent.MOUSE_ON) {
                    activeComponent = current.c;
                    Debug.println("Mouse on");
                }
                current = current.next;
            }
        } else {
            if(activeComponent.reactToMouse(mousePos) == ClickableComponent.MOUSE_LEFT) {
                activeComponent = null;
            }
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
     * A Node in the LinkedList containing the Components
     * @author Spencer Yoder
     */
    private class Node {
        private Node next;
        private Node prev;
        private ClickableComponent c;
        private Node(ClickableComponent c) {
            this.c = c;
        }
    }

    /**
     * Delegate the mouse click to the component the mouse is currently hovering over.
     * @see component.ClickableComponent#onClick()
     */
    public void handleClick() {
        if(activeComponent != null) {
            activeComponent.onClick();
        }
    }
    
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
    
    public void remove(ClickableComponent c) {
        if(!tempList.isEmpty()) {
            Node n = tempList.remove(c.getMouseIndex());
            for(int i = 0; i < tempList.size(); i++) {
                if(tempList.get(i).c.getMouseIndex() != i) {
                    tempList.get(i).c.setMouseIndex(i);
                }
            }
            if(n.prev != null)
                n.prev.next = n.next;
            else
                head = head.next;
            if(n.next != null)
                n.next.prev = n.prev;
        } else {
            Debug.println("MouseWatcher attempt to remove from empty list");
        }
    }
}
