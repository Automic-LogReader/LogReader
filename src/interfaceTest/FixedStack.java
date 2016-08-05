/**
 * @file FixedStack.java
 * @authors Leah Talkov, Jerry Tsui
 * @date 8/3/2016
 * FixedStack is an implementation of a stack that has a fixed size. 
 * The user will specify the maximum size of the stack. If something is 
 * pushed to the stack when it is over capacity, it will remove the item 
 * on the bottom of the stack and it will push the new item to maintain its size
 * restraint. 
 */
package interfaceTest;

import java.util.Stack;

public class FixedStack<E> extends Stack<E> {
	private int maxSize;

    /**
     * Constructor
     * @param size The maximum size for the FixedStack object
     */
    public FixedStack(int size) {
        super();
        this.maxSize = size;
    }

    /* (non-Javadoc)
     * @see java.util.Stack#push(java.lang.Object)
     */
    @Override
    public Object push(Object object) {
        //If the stack is too big, remove elements until it's the right size.
        while (this.size() >= maxSize) {
            this.remove(0);
        }
        return super.push((E) object);
    }
    
    public boolean isFull(){
    	return (this.size() == maxSize);
    }
}
