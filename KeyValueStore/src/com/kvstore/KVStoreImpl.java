package com.kvstore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.locks.StampedLock;

public class KVStoreImpl<K, V> implements KVStore<K, V>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4951296243294880326L;

	private static class Entry<K, V> implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3085875937721754964L;
		private K k;
		private V v;
		private Entry<K, V> next;

		public Entry(K k, V v, Entry<K, V> next) {
			this.k = k;
			this.v = v;
			this.next = next;
		}

	}

	private Entry<K, V>[] entryTable;

	private int initialCapacity = 16;

	private StampedLock lock = new StampedLock();

	// constructor defining initial capacity of the key value store data structure
	public KVStoreImpl() {
		entryTable = new Entry[initialCapacity];
	}

	public KVStoreImpl(int capacity) {
		initialCapacity = capacity;
		entryTable = new Entry[initialCapacity];
	}

	// hashing function
	private int hashValue(K key) {
		return Math.abs(key.hashCode()) % initialCapacity;
	}

	@Override
	public V get(K key) {
		long stamp = lock.tryOptimisticRead();

		if (!lock.validate(stamp)) {
			stamp = lock.readLock();

			try {

				if (key == null) {
					return null;
				}

				// hash value of the key
				int hashValue = hashValue(key);
				if (entryTable[hashValue] == null) {
					return null;
				} else {
					Entry<K, V> temp = entryTable[hashValue];
					while (temp != null) {
						if (temp.k.equals(key)) {
							return temp.v;
						}
						temp = temp.next;
					}
				}

			} finally {
				lock.unlock(stamp);
			}
		}
		return null;
	}

	@Override
	public void put(K key, V value) {

		long stamp = lock.writeLock();

		try {
			if (key == null) {
				throw new RuntimeException("null key not allowed");
			}

			// hashing
			int hashValue = hashValue(key);

			// create entry
			Entry<K, V> entry = new Entry<K, V>(key, value, null);

			if (entryTable[hashValue] == null) {
				entryTable[hashValue] = entry;
			} else {
				Entry<K, V> previous = null;
				Entry<K, V> current = entryTable[hashValue];
				while (current != null) {
					if (current.k.equals(key)) {
						if (previous == null) {
							entry.next = current.next;
							entryTable[hashValue] = entry;
						} else {
							entry.next = current.next;
							previous.next = entry;
						}
					}
					previous = current;
					current = current.next;
				}
				previous.next = entry;
			}

		} finally {
			lock.unlockWrite(stamp);
		}
	}

	@Override
	public void delete(K key) {

		long stamp = lock.writeLock();

		try {

			if (key == null) {
				System.out.println("Key does not exist");
			}

			int hashValue = hashValue(key);
			if (entryTable[hashValue] == null) {
				System.out.println("Entry Table Hash Value Null");
			} else {
				Entry<K, V> previous = null;
				Entry<K, V> current = entryTable[hashValue];

				while (current != null) {
					if (current.k.equals(key)) {
						if (previous == null) {
							entryTable[hashValue] = entryTable[hashValue].next;
						} else {
							previous.next = current.next;
						}
					}
					previous = current;
					current = current.next;
				}
			}

		} finally {
			lock.unlockWrite(stamp);
		}

	}

	@Override
	public void clear() {

		long stamp = lock.writeLock();

		try {
			Entry<K, V>[] buckets;
			long size = size();

			if ((buckets = entryTable) != null && size > 0) {
				size = 0;
				for (int i = 0; i < buckets.length; ++i) {
					buckets[i] = null;
				}
			}

		} finally {
			lock.unlockWrite(stamp);
		}
	}

	@Override
	public long size() {

		// long stamp = lock.tryOptimisticRead();
		int count = 0;

		for (int i = 0; i < entryTable.length; i++) {
			if (entryTable[i] != null) {
				int nodeCount = 0;
				for (Entry<K, V> e = entryTable[i]; e.next != null; e = e.next) {
					nodeCount++;
				}

				// horizontal buckets
				count += nodeCount;

				// vertical count
				count++;
			}
		}

		return count;
	}

	@Override
	public void print() {
		long size = size();

		if (size == 0) {
			System.out.println("Empty KV Store, nothing to print");
		} else {
			for (int i = 0; i < initialCapacity; i++) {
				if (entryTable[i] != null) {
					Entry<K, V> entry = entryTable[i];
					while (entry != null) {
						System.out.println("{" + entry.k + " = " + entry.v + "}" + " ");
						entry = entry.next;
					}
				}
			}

		}

	}

	public void serialize(KVStoreImpl obj) {

		try {
			FileOutputStream fos = new FileOutputStream("kvstore.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(obj);
			oos.close();
			fos.close();
			System.out.println("KV Store Serialized");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
}
