package com.kvstore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainDriver {

	public static void main(String[] args) {
		KVStoreImpl<Integer, String> kv = new KVStoreImpl<>(4);

		kv.put(1, "Jack");
		kv.put(2, "Coke");
		kv.put(3, "Tiger");

		System.out.println("KV Store size : " + kv.size());

		String a = kv.get(1);
		String b = kv.get(2);
		System.out.println(a + " " + b);

		kv.put(5, "Redmi");
		System.out.println("KV Store size : " + kv.size());

		kv.delete(3);
		kv.put(7, "gun");
		kv.print();
		System.out.println("KV Store size : " + kv.size());

		kv.put(7, "gun22");

		// kv.clear();

		// kv.print();

		// System.out.println("KV Store size : " + kv.size());

		// kv.serialize(kv);
		// kv.clear();
		kv.print();

		// to deserialize from a given file.
		/*
		 * KVStoreImpl<Integer, String> deser = null;
		 * 
		 * try { FileInputStream fis = new FileInputStream("kvstore.ser");
		 * ObjectInputStream ois = new ObjectInputStream(fis); deser = (KVStoreImpl)
		 * ois.readObject(); ois.close(); fis.close();
		 * System.out.println("Deserialized KV Store :"); } catch (IOException ioe) {
		 * ioe.printStackTrace(); return; } catch (ClassNotFoundException c) {
		 * System.out.println("Class Not Found"); c.printStackTrace(); return; }
		 * 
		 * deser.print();
		 */

		final int threadCount = 4;
		final ExecutorService service = Executors.newFixedThreadPool(threadCount);
		KVStoreImpl object = new KVStoreImpl();

		Runnable writeTask = () -> {

			object.put(1, "value1");
		};
		Runnable readTask = () -> {

			object.get(1);
		};
		Runnable readOptimisticTask = () -> {

			object.get(1);
		};
		service.submit(writeTask);
		service.submit(writeTask);
		service.submit(readTask);
		service.submit(readOptimisticTask);

		service.shutdown();

	}

}
