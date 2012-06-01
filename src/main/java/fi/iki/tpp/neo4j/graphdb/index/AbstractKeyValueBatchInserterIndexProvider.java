package fi.iki.tpp.neo4j.graphdb.index;

import java.util.Map;

import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;

public abstract class AbstractKeyValueBatchInserterIndexProvider implements BatchInserterIndexProvider {
	protected static void assertProperties(String indexType, String indexName, Map<String, String> config) {
		if (indexType == null || indexType.isEmpty()) throw new IllegalArgumentException("indexType not set");
		if (indexName == null || indexName.isEmpty()) throw new IllegalArgumentException("indexName not set");
		if (config == null) throw new IllegalArgumentException("config is not set");
	}

	protected BatchInserterIndex forIndex(String indexType, String indexName, Map<String, String> config) {
		assertProperties(indexType, indexName, config);

		return createIndex(indexType, indexName, config);
	}

	@Override
	public void shutdown() {
	}

	protected abstract BatchInserterIndex createIndex(String indexType, String indexName, Map<String, String> config);
}
