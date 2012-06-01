package fi.iki.tpp.neo4j.graphdb.index;

import java.util.Collection;

import org.neo4j.helpers.Pair;

public interface IndexKeyValuePairStore {
	void add(String indexName, String propertyName, Object propertyValue, Long entityId);
	void remove(String indexName, String propertyName, Object propertyValue, Long entityId);

	Collection<Long> get(String indexName, String propertyName, Object propertyValue);
	Collection<Pair<String, Object>> findByEntityId(String indexName, Long entityId);
}
