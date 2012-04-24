package mock;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.cubeia.firebase.api.util.UnmodifiableSet;

/**
 * Testable implementation of a Firebase unmodifiable set.
 * @author w
 */
public final class UnmongofiableSet <T> implements UnmodifiableSet<T> {
    private final Set<T> set;

    public UnmongofiableSet(Collection<T> collection) {
        this.set = new HashSet<T>(collection);
    }
    
    public UnmongofiableSet() {
    	this.set = new HashSet<T>();
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public boolean contains(T object) {
        return set.contains(object);
    }
}