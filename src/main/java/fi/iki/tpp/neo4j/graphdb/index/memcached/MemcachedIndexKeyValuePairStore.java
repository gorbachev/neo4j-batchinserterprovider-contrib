package fi.iki.tpp.neo4j.graphdb.index.memcached;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.neo4j.helpers.Pair;

import fi.iki.tpp.neo4j.graphdb.index.AbstractKeyValuePairStore;

public class MemcachedIndexKeyValuePairStore extends AbstractKeyValuePairStore {
	private final MemcachedClient client;

	public MemcachedIndexKeyValuePairStore(
			String memcachedHost,
			Integer memcachedPort
			) {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(String.format("%s:%d", memcachedHost, memcachedPort)));
		builder.setCommandFactory(new BinaryCommandFactory());
		builder.setConnectionPoolSize(1); // used in a single-threaded application, no need to use more than one connection
		try {
			this.client = builder.build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void add(String indexName, String propertyName, Object propertyValue, Long entityId) {
		String key = getKey(indexName, propertyName, propertyValue);
		if (key == null) return;

		try {
			ArrayList<Long> entities = client.get(key);
			if (entities == null) {
				entities = new ArrayList<Long>();
			}
			entities.add(entityId);
			client.set(key, 0, entities);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(String indexName, String propertyName, Object propertyValue, Long entityId) {
		String key = getKey(indexName, propertyName, propertyValue);
		if (key == null) return;

		try {
			ArrayList<Long> entities = client.get(key);
			if (entities != null) {
				entities.remove(entityId);
				client.set(key, 0, entities);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<Long> get(String indexName, String propertyName, Object propertyValue) {
		String key = getKey(indexName, propertyName, propertyValue);
		if (key == null) return new ArrayList<Long>();

		try {
			ArrayList<Long> entities = client.get(key);
			return entities == null ? new ArrayList<Long>() : entities;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static String getKey(String indexName, String propertyName, Object propertyValue) {
		String key = AbstractKeyValuePairStore.getKey(indexName, propertyName, propertyValue);
		return key.length() > 250 ? null : key;
	}

	@Override
	public Collection<Pair<String, Object>> findByEntityId(String indexName, Long entityId) {
		throw new UnsupportedOperationException();
	}
}
