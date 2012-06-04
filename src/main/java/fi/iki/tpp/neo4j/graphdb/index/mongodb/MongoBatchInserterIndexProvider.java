package fi.iki.tpp.neo4j.graphdb.index.mongodb;

import java.util.Map;

import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

import fi.iki.tpp.neo4j.graphdb.index.AbstractKeyValueBatchInserterIndexProvider;

/**
 * The config must contain the following entries:
 *
 * mongo.db.host : The hostname of the MongoDB server used
 *                 e.g. localhost, 127.0.0.1
 * mongo.db.port : The port number of the MongoDB server used
 *                 e.g. 27017
 * mongo.db.name : The name of the MongoDB used for the indexes
 *                 e.g. batch_db
 * mongo.collection.name : The MongoDB collection name used for the indexes
 *                         e.g. neo4j_batch_index
 * 
 * There are no defaults for these settings
 * 
 * @author tpp
 *
 */
public class MongoBatchInserterIndexProvider extends AbstractKeyValueBatchInserterIndexProvider {

	@Override
	public BatchInserterIndex nodeIndex(String indexName, Map<String, String> config) {
		return forIndex("node", indexName, config);
	}

	@Override
	public BatchInserterIndex relationshipIndex(String indexName, Map<String, String> config) {
		return forIndex("rel", indexName, config);
	}

	protected BatchInserterIndex createIndex(String indexType, String indexName, Map<String, String> config) {
		return new MongoBatchInserterIndex(String.format("%s-%s", indexType, indexName), config);
	}
}
