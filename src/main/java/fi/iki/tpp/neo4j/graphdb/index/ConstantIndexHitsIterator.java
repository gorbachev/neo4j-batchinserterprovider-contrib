package fi.iki.tpp.neo4j.graphdb.index;

import java.util.Collection;
import java.util.Iterator;

import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.IteratorUtil;

/**
 * Adapted from org.neo4j.index.impl.lucene.ConstantScoreIterator.
 * Because it's in the neo4j-lucene-index jar, it is not available
 * for use here.
 * 
 * @author tpp
 */
public class ConstantIndexHitsIterator<T> implements IndexHits<T> {
	private final Iterator<T> items;
	private final float score;
	private final int size;

	public ConstantIndexHitsIterator(Collection<T> items, float score) {
		this.items = items != null ? items.iterator() : null;
		this.score = score;
		this.size = items != null ? items.size() : 0;
	}

	@Override
	public boolean hasNext() {
		return items != null ? items.hasNext() : false;
	}

	@Override
	public T next() {
		return items != null ? items.next() : null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		return items;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void close() {
	}

	@Override
	public T getSingle() {
        try
        {
            return IteratorUtil.singleOrNull( (Iterator<T>) this );
        }
        finally
        {
            close();
        }
	}

	@Override
	public float currentScore() {
		return score;
	}
}
