package fi.iki.tpp.neo4j.graphdb.index.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.Pair;
import org.neo4j.kernel.impl.cache.LruCache;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

import fi.iki.tpp.neo4j.graphdb.index.ConstantIndexHitsIterator;
import fi.iki.tpp.neo4j.graphdb.index.IndexKeyValuePairStore;

/**
 * Indexes Neo4j entities using MongoDB.
 *
 * The entityIds are stored in separate Mongo documents with the
 * following properties:
 * 
 * propertyName
 * propertyValue
 * indexName
 * entityId
 * key : a unique key for the property and its value, the format
 *       of the key is
 *       "index-<indexName>-prop-<propertyName>-value-<propertyValue>"
 * 
 * Queries not supported at this time
 * 
 * @author tpp
 *
 */
public class MongoBatchInserterIndex implements BatchInserterIndex {
	private final String indexName;
    private final IndexKeyValuePairStore indexStore;

    private Map<String, LruCache<String, Collection<Long>>> cache;

	public MongoBatchInserterIndex(String indexName, Map<String, String> config) {
		super();

		this.indexName = indexName;
		try {
			this.indexStore = new MongoIndexKeyValuePairStore(
					config.get("mongo.db.host"),
					Integer.valueOf(config.get("mongo.db.port")),
					config.get("mongo.db.name"),
					config.get("mongo.collection.name"));
		} catch (Exception e) {
			throw new RuntimeException("MongoBatchInserterIndexProvider initialization failed", e);
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

    	Collection<Pair<String, Object>> results = indexStore.findByEntityId(indexName, entityId);
    	for (Pair<String, Object> entity : results) {
    		indexStore.remove(indexName, entity.first(), entity.other(), entityId);
    		removeFromCache(entityId, entity.first(), entity.other());
    	}

    	add(entityId, properties);
	}

	private void addSingleProperty(long entityId, String propertyName, Object propertyValue) {
		indexStore.add(indexName, propertyName, propertyValue, entityId);
		addToCache(entityId, propertyName, propertyValue);
	}

	@Override
	public IndexHits<Long> get(String key, Object value) {
		if (key == null || value == null) return new ConstantIndexHitsIterator<Long>(new ArrayList<Long>(), 1f);

		IndexHits<Long> cached = getFromCache(key, value);
		return cached != null ? cached : new ConstantIndexHitsIterator<Long>(indexStore.get(indexName, key, value), 1f);
	}

	/**
	 * @see org.neo4j.index.impl.lucene.LuceneBatchInserterIndex#addToCache(long, String, Object) 
	 */
    private void addToCache( long entityId, String key, Object value )
    {
        if ( this.cache == null )
        {
            return;
        }
        
        String valueAsString = value.toString();
        LruCache<String, Collection<Long>> cache = this.cache.get( key );
        if ( cache != null )
        {
            Collection<Long> ids = cache.get( valueAsString );
            if ( ids == null )
            {
                ids = new HashSet<Long>();
                cache.put( valueAsString, ids );
            }
            ids.add( entityId );
        }
    }

	/**
	 * @see org.neo4j.index.impl.lucene.LuceneBatchInserterIndex#getFromCache(String, Object) 
	 */
	private IndexHits<Long> getFromCache(String key, Object value) {
        if ( this.cache == null )
        {
            return null;
        }
        
        String valueAsString = value.toString();
        LruCache<String, Collection<Long>> cache = this.cache.get( key );
        if ( cache != null )
        {
            Collection<Long> ids = cache.get( valueAsString );
            if ( ids != null )
            {
                return new ConstantIndexHitsIterator<Long>( ids, Float.NaN );
            }
        }
        return null;
	}

	/**
	 * @see org.neo4j.index.impl.lucene.LuceneBatchInserterIndex#removeFromCache(long, String, Object) 
	 */
    private void removeFromCache( long entityId, String key, Object value)
    {
        if ( this.cache == null )
        {
            return;
        }
        
        String valueAsString = value.toString();
        LruCache<String, Collection<Long>> cache = this.cache.get( key );
        if ( cache != null )
        {
            Collection<Long> ids = cache.get( valueAsString );
            if ( ids != null )
            {
                ids.remove( entityId );
            }
        }
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

	/**
	 * @see org.neo4j.index.impl.lucene.LuceneBatchInserterIndex#setCacheCapacity(String, int) 
	 */
	@Override
	public void setCacheCapacity(String key, int size) {
        if ( this.cache == null )
        {
            this.cache = new HashMap<String, LruCache<String,Collection<Long>>>();
        }
        LruCache<String, Collection<Long>> cache = this.cache.get( key );
        if ( cache != null )
        {
            cache.resize( size );
        }
        else
        {
            cache = new LruCache<String, Collection<Long>>( "Batch inserter cache for " + key, size );
            this.cache.put( key, cache );
        }
	}
}