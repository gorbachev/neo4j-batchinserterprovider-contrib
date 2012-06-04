package fi.iki.tpp.neo4j.graphdb.index.memcached;

import java.util.Map;

import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

import fi.iki.tpp.neo4j.graphdb.index.AbstractKeyValueBatchInserterIndexProvider;

/**
 * The config must contain the following entries:
 *
 * memcached.db.host : The hostname of the Memcached server used
 *                     e.g. localhost, 127.0.0.1
 * memcached.db.port : The port number of the Redis server used
 *                     e.g. 11211
 * 
 * There are no defaults for these settings
 * 
 * @author tpp
 *
 */
public class MemcachedBatchInserterIndexProvider extends AbstractKeyValueBatchInserterIndexProvider {

	@Override
	public BatchInserterIndex nodeIndex(String indexName, Map<String, String> config) {
		return forIndex("node", indexName, config);
	}

	@Override
	public BatchInserterIndex relationshipIndex(String indexName, Map<String, String> config) {
		return forIndex("rel", indexName, config);
	}

	protected BatchInserterIndex createIndex(String indexType, String indexName, Map<String, String> config) {
		return new MemcachedBatchInserterIndex(String.format("%s-%s", indexType, indexName), config);
	}
}
