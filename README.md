#Contributed Neo4j BatchInserterIndexProvider Implementations

GitHub:
http://github.com/gorbachev/neo4j-batchinserterprovider-contrib

For help:
http://groups.google.com/group/neo4j

## Dependencies

See the Maven pom.xml file for details and Maven dependencies.

* neo4j-kernel
* Mongo Java Driver
* Jedis
* Xmemcached

## Build Steps

	git clone git@github.com:gorbachev/neo4j-batchinserterprovider-contrib.git
	mvn clean package

The target directory will contain `neo4j-batchinserterindexprovider-contrib-<version>.jar` file, which can be imported to your project.

## Usage

For more information about Batch Insertion in Neo4j see: http://docs.neo4j.org/chunked/stable/batchinsert.html

The BatchInserterIndexProvider API is documented here: http://components.neo4j.org/neo4j/1.7/apidocs/org/neo4j/unsafe/batchinsert/BatchInserterIndexProvider.html

This library contains the following contributed BatchInserterIndexProvider classes using MongoDB, Redis or Memcached as the data store.

### MongoBatchInserterIndexProvider

Example:

	BatchInserter inserter = BatchInserters.inserter( "target/neo4jdb-batchinsert" );
	BatchInserterIndexProvider indexProvider = new MongoBatchInserterIndexProvider( inserter );
	Map<String, String> config = MapUtil.stringMap( "mongo.db.host", "localhost", "mongo.db.port", "27017", "mongo.db.name", "graph_db", "mongo.collection.name", "neo4j_batch_index" );
	BatchInserterIndex actors = indexProvider.nodeIndex( "actors",  );
	actors.setCacheCapacity( "name", 100000 );

	Map<String, Object> properties = MapUtil.map( "name", "Keanu Reeves" );
	long node = inserter.createNode( properties );
	actors.add( node, properties );
 
	actors.flush();
 
	indexProvider.shutdown();
	inserter.shutdown();

The MongoBatchInserterIndexProvider implements caching the same way the LuceneBatchInserterIndexProvider does.

The query() methods in the BatchInserterIndex interface are not implemented.

Indexes are always written to MongoDB at the time they're added (or removed), and flush() effectively does nothing. Using flush() is still encouraged, should the implementation change in the future.

### RedisBatchInserterIndexProvider

Example:

	BatchInserter inserter = BatchInserters.inserter( "target/neo4jdb-batchinsert" );
	BatchInserterIndexProvider indexProvider = new RedisBatchInserterIndexProvider( inserter );
	Map<String, String> config = MapUtil.stringMap( "redis.db.host", "localhost", "redis.db.port", "6379" );
	BatchInserterIndex actors = indexProvider.nodeIndex( "actors",  );

	Map<String, Object> properties = MapUtil.map( "name", "Keanu Reeves" );
	long node = inserter.createNode( properties );
	actors.add( node, properties );
 
	actors.flush();
 
	indexProvider.shutdown();
	inserter.shutdown();

The RedisBatchInserterIndexProvider does not do caching internally. It's largely redundant given the way Redis works.

The query() methods in the BatchInserterIndex interface are not implemented.

Indexes are always written to Redis at the time they're added (or removed), and flush() effectively does nothing. Using flush() is still encouraged, should the implementation change in the future.

### MemcachedBatchInserterIndexProvider

Example:

	BatchInserter inserter = BatchInserters.inserter( "target/neo4jdb-batchinsert" );
	BatchInserterIndexProvider indexProvider = new MemcachedBatchInserterIndexProvider( inserter );
	Map<String, String> config = MapUtil.stringMap( "memcached.db.host", "localhost", "memcached.db.port", "11211" );
	BatchInserterIndex actors = indexProvider.nodeIndex( "actors",  );

	Map<String, Object> properties = MapUtil.map( "name", "Keanu Reeves" );
	long node = inserter.createNode( properties );
	actors.add( node, properties );
 
	actors.flush();
 
	indexProvider.shutdown();
	inserter.shutdown();

The MemcachedBatchInserterIndexProvider does not do caching internally. It's largely redundant given the way Memcached works.

The query() methods in the BatchInserterIndex interface are not implemented.

Indexes are always written to Memcached at the time they're added (or removed), and flush() effectively does nothing. Using flush() is still encouraged, should the implementation change in the future.
