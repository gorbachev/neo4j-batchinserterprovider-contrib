package fi.iki.tpp.neo4j.graphdb.index.memcached;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

import fi.iki.tpp.neo4j.graphdb.index.ConstantIndexHitsIterator;
import fi.iki.tpp.neo4j.graphdb.index.IndexKeyValuePairStore;

/**
 * Indexes Neo4j entity properties in Redis.
 *
 * Queries not supported at this time.
 * 
 * @author tpp
 *
 */
public class MemcachedBatchInserterIndex implements BatchInserterIndex {
	private final String indexName;
    private final IndexKeyValuePairStore indexStore;

	public MemcachedBatchInserterIndex(String indexName, Map<String, String> config) {
		super();

		this.indexName = indexName;
		try {
			this.indexStore = new MemcachedIndexKeyValuePairStore(
					config.get("memcached.db.host"),
					Integer.valueOf(config.get("memcached.db.port")));
		} catch (Exception e) {
			throw new RuntimeException("MemcachedBatchInserterIndexProvider initialization failed", e);
		}
	}

	@Override
	public void add(long entityId, Map<String, Object> properties) {
		if (properties == null) return;

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			addSingleProperty(entityId, entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void updateOrAdd(long entityId, Map<String, Object> properties) {
		if (properties == null) return;

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			Collection<Long> entities = indexStore.get(indexName, entry.getKey(), entry.getValue());
			if (entities.contains(entityId)) {
				indexStore.remove(indexName, entry.getKey(), entry.getValue(), entityId);
			}
		}

    	add(entityId, properties);
	}

	private void addSingleProperty(long entityId, String propertyName, Object propertyValue) {
		indexStore.add(indexName, propertyName, propertyValue, entityId);
	}

	@Override
	public IndexHits<Long> get(String key, Object value) {
		if (key == null || value == null) return new ConstantIndexHitsIterator<Long>(new ArrayList<Long>(), 1f);

		return new ConstantIndexHitsIterator<Long>(indexStore.get(indexName, key, value), 1f);
	}

	@Override
	public IndexHits<Long> query(String key, Object queryOrQueryObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IndexHits<Long> query(Object queryOrQueryObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() {
	}

	@Override
	public void setCacheCapacity(String key, int size) {
		throw new UnsupportedOperationException();
	}
}
