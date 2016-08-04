package interfaceTest;

import java.util.Stack;

public class FixedStack<E> extends Stack<E> {
	private int maxSize;

    public FixedStack(int size) {
        super();
        this.maxSize = size;
    }

    @Override
    public Object push(Object object) {
        //If the stack is too big, remove elements until it's the right size.
        while (this.size() >= maxSize) {
            this.remove(0);
        }
        return super.push((E) object);
    }
}
