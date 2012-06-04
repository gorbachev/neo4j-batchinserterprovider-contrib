package fi.iki.tpp.neo4j.graphdb.index;

import static org.junit.Assert.assertFalse;
import static org.neo4j.helpers.collection.MapUtil.map;
import static fi.iki.tpp.test.TestUtils.assertContains;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.impl.AbstractNeo4jTestCase;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import fi.iki.tpp.neo4j.graphdb.index.mongodb.MongoBatchInserterIndexProvider;

public class MongoBatchInserterIndexProviderTest {
    private static final String PATH = "target/var/batch-new";

    @Before
    public void cleanDirectory()
    {
    	AbstractNeo4jTestCase.deleteFileOrDirectory( new File( PATH ) );
    }

	@Test
	public void testSingleProperty() {
		String path = new File( PATH, "1").getAbsolutePath();
		BatchInserter inserter = BatchInserters.inserter(path);
		BatchInserterIndexProvider provider = new MongoBatchInserterIndexProvider();

		String indexName = "users";
		Map<String, String> config = createConfig(1);
		
		BatchInserterIndex index = provider.nodeIndex(indexName, config);

		Map<Integer, Long> ids = new HashMap<Integer, Long>();
        int count = 5;
        for ( int i = 0; i < count; i++ )
        {
            long id = inserter.createNode( null );
            index.add( id, map( "name", "Joe" + i ) );
            ids.put( i, id );
        }

        for ( int i = 0; i < count; i++ )
        {
            assertContains( index.get( "name", "Joe" + i ), ids.get( i ) );
        }

        provider.shutdown();
        inserter.shutdown();
	}

	@Test()
	public void testCaching() {
		String path = new File( PATH, "2").getAbsolutePath();
		BatchInserter inserter = BatchInserters.inserter(path);
		BatchInserterIndexProvider provider = new MongoBatchInserterIndexProvider();

		String indexName = "users";
		Map<String, String> config = createConfig(2);
		
		BatchInserterIndex index = provider.nodeIndex(indexName, config);
		index.setCacheCapacity("name", 10000);

		Map<Integer, List<Long>> ids = new HashMap<Integer, List<Long>>();
        int count = 5;
        for ( int i = 0; i < count; i++ )
        {
        	if (!ids.containsKey(i)) {
        		ids.put(i, new ArrayList<Long>());
        	}
        	
        	long id = inserter.createNode( null );
            index.add( id, map( "name", "Joe" + i ) );
            ids.get(i).add(id);

        	id = inserter.createNode( null );
            index.add( id, map( "name", "Joe" + i ) );
            ids.get(i).add(id);
        }

        for ( int i = 0; i < count; i++ )
        {
            assertContains( index.get( "name", "Joe" + i ), ids.get( i ).toArray(new Long[] {}) );
        }

        provider.shutdown();
        inserter.shutdown();
	}

	@Test
	public void testMultipleProperties() {
		String path = new File( PATH, "3").getAbsolutePath();
		BatchInserter inserter = BatchInserters.inserter(path);
		BatchInserterIndexProvider provider = new MongoBatchInserterIndexProvider();

		String indexName = "users";
		Map<String, String> config = createConfig(3);
		
		BatchInserterIndex index = provider.nodeIndex(indexName, config);

		Map<Integer, Long> ids = new HashMap<Integer, Long>();
        int count = 5;
        for ( int i = 0; i < count; i++ )
        {
            long id = inserter.createNode( null );
            index.add( id, map( "name", "Joe" + i, "other", "Schmoe" ) );
            ids.put( i, id );
        }

        for ( int i = 0; i < count; i++ )
        {
            assertContains( index.get( "name", "Joe" + i ), ids.get( i ) );
            assertContains( index.get("other", "Schmoe"), new Long[] { 1l, 2l, 3l, 4l, 5l });
        }

        provider.shutdown();
        inserter.shutdown();
	}

	@Test
	public void testUpdateOrAddOnAdds() {
		String path = new File( PATH, "4").getAbsolutePath();
		BatchInserter inserter = BatchInserters.inserter(path);
		BatchInserterIndexProvider provider = new MongoBatchInserterIndexProvider();

		String indexName = "users";
		Map<String, String> config = createConfig(4);
		
		BatchInserterIndex index = provider.nodeIndex(indexName, config);

		Map<Integer, Long> ids = new HashMap<Integer, Long>();
        int count = 5;
        for ( int i = 0; i < count; i++ )
        {
            long id = inserter.createNode( null );
            index.updateOrAdd( id, map( "name", "Joe" + i ) );
            ids.put( i, id );
        }

        for ( int i = 0; i < count; i++ )
        {
            assertContains( index.get( "name", "Joe" + i ), ids.get( i ) );
        }

        provider.shutdown();
        inserter.shutdown();
	}

	@Test
	public void testUpdateOrAddForceUpdates() {
		String path = new File( PATH, "5").getAbsolutePath();
		BatchInserter inserter = BatchInserters.inserter(path);
		BatchInserterIndexProvider provider = new MongoBatchInserterIndexProvider();

		String indexName = "users";
		Map<String, String> config = createConfig(5);
		
		BatchInserterIndex index = provider.nodeIndex(indexName, config);

		Map<Integer, Long> ids = new HashMap<Integer, Long>();
        int count = 5;
        for ( int i = 0; i < count; i++ )
        {
            long id = inserter.createNode( null );
            index.add( id, map( "name", "Joe" + i ) );
            ids.put( i, id );
        }

        for ( int i = 0; i < count; i++ )
        {
        	Long id = ids.get(i);
            index.updateOrAdd( id, map( "name", "Joe" + i ) );
        }

        for ( int i = 0; i < count; i++ )
        {
            assertContains( index.get( "name", "Joe" + i ), ids.get( i ) );
        }

        provider.shutdown();
        inserter.shutdown();
	}

	@Test
	public void testNonExistingIndex() {
		String path = new File( PATH, "6").getAbsolutePath();
		BatchInserter inserter = BatchInserters.inserter(path);
		BatchInserterIndexProvider provider = new MongoBatchInserterIndexProvider();

		String indexName = "users";
		Map<String, String> config = createConfig(6);
		
		BatchInserterIndex index = provider.nodeIndex(indexName, config);

		assertFalse(index.get("name", "Joe").hasNext());

        provider.shutdown();
        inserter.shutdown();
	}

	private static Map<String, String> createConfig(int index) {
		return MapUtil.stringMap( "mongo.db.host", "localhost", "mongo.db.port", "27017", "mongo.db.name", "graph_db", "mongo.collection.name", String.format("neo4j_batch_index_%d", index) );
	}
}
