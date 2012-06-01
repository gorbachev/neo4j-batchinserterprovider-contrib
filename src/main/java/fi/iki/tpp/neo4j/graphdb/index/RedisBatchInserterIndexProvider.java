package fi.iki.tpp.neo4j.graphdb.index;

import java.util.Map;

import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

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
