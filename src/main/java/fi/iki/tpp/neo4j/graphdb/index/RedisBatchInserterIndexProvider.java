package fi.iki.tpp.neo4j.graphdb.index;

import java.util.Map;

import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

/**
 * The config must contain the following entries:
 *
 * redis.db.host : The hostname of the Redis server used
 *                 e.g. localhost, 127.0.0.1
 * redis.db.port : The port number of the Redis server used
 *                 e.g. 6379
 * 
 * There are no default for these settings
 * 
 * @author tpp
 *
 */
public class RedisBatchInserterIndexProvider extends AbstractKeyValueBatchInserterIndexProvider {

	@Override
	public BatchInserterIndex nodeIndex(String indexName, Map<String, String> config) {
		return forIndex("node", indexName, config);
	}

	@Override
	public BatchInserterIndex relationshipIndex(String indexName, Map<String, String> config) {
		return forIndex("rel", indexName, config);
	}

	protected BatchInserterIndex createIndex(String indexType, String indexName, Map<String, String> config) {
		return new RedisBatchInserterIndex(String.format("%s-%s", indexType, indexName), config);
	}
}
