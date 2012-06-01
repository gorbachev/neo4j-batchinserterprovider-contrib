package fi.iki.tpp.neo4j.graphdb.index.redis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.helpers.Pair;

import fi.iki.tpp.neo4j.graphdb.index.AbstractKeyValuePairStore;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisIndexKeyValuePairStore extends AbstractKeyValuePairStore {
	private final JedisPool redisPool;
	
	public RedisIndexKeyValuePairStore(
			String redisHost,
			Integer redisPort
			) {
		this.redisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);
	}

	@Override
	public void add(String indexName, String propertyName, Object propertyValue, Long entityId) {
		Jedis jedis = redisPool.getResource();
		try {
			jedis.sadd(getKey(indexName, propertyName, propertyValue), entityId.toString());
		} finally {
			redisPool.returnBrokenResource(jedis);
		}
	}

	@Override
	public void remove(String indexName, String propertyName, Object propertyValue, Long entityId) {
		Jedis jedis = redisPool.getResource();
		try {
			jedis.srem(getKey(indexName, propertyName, propertyValue), entityId.toString());
		} finally {
			redisPool.returnBrokenResource(jedis);
		}
	}

	@Override
	public Collection<Long> get(String indexName, String propertyName, Object propertyValue) {
		Jedis jedis = redisPool.getResource();
		try {
			Set<String> results = jedis.smembers(getKey(indexName, propertyName, propertyValue));
			return transform(results, new Transformer<String, Long>() {
				@Override
				public Long transform(String input) {
					return Long.valueOf(input);
				}
			});
		} finally {
			redisPool.returnResource(jedis);
		}
	}

	@Override
	public Collection<Pair<String, Object>> findByEntityId(String indexName, Long entityId) {
		throw new UnsupportedOperationException();
	}

	private <I,O> Set<O> transform(Set<I> set, Transformer<I,O> transformer) {
		Set<O> output = new HashSet<O>(set.size());
		for (I input : set) {
			output.add(transformer.transform(input));
		}
		return output;
	}

	private interface Transformer<I,O> {
		O transform(I input);
	}
}
