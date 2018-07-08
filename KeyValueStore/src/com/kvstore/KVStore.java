package com.kvstore;

public interface KVStore<K, V> {
	
	V get (K key) throws InterruptedException;
	
	void put(K key, V value);
	
	void delete (K key);
	
	void clear ();
	
	long size () throws InterruptedException;
	
	void print ();
	
}
