package fi.iki.tpp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestUtils {
	public static <T> void assertContains(Collection<T> collection, T... expectedItems) {
		String collectionString = join(", ", collection.toArray());
		assertEquals(collectionString, expectedItems.length, collection.size());
		for (T item : expectedItems) {
			assertTrue(collection.contains(item));
		}
	}

	public static <T> void assertContains(Iterable<T> items, T... expectedItems) {
		assertContains(asCollection(items), expectedItems);
	}

	public static <T> Collection<T> asCollection(Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();
		for (T item : iterable) {
			list.add(item);
		}
		return list;
	}

	public static <T> String join(String delimiter, T... items) {
		StringBuffer buffer = new StringBuffer();
		for (T item : items) {
			if (buffer.length() > 0) {
				buffer.append(delimiter);
			}
			buffer.append(item.toString());
		}
		return buffer.toString();
	}
}