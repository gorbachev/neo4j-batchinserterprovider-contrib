package fi.iki.tpp.neo4j.graphdb.index;

public abstract class AbstractKeyValuePairStore implements IndexKeyValuePairStore {
	private static final String KEY_FORMAT = "index-%s-prop-%s-value-%s";

	protected static String getKey(String indexName, String propertyName, Object propertyValue) {
		return String.format(KEY_FORMAT, indexName, propertyName, propertyValue.toString());
	}
}
