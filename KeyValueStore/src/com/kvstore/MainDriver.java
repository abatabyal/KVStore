package com.kvstore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
		System.out.println("KV Store size : " + kv.size());
		
		kv.print();
		
		//kv.clear();
		
		//kv.print();
		
		//System.out.println("KV Store size : " + kv.size());
		
		//kv.persist(kv);
		
		try {
			FileOutputStream fos = new FileOutputStream("kvstore.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(kv);
			oos.close();
			fos.close();
			System.out.println("KV Store Serialized");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		/*KVStoreImpl<Integer, String> deser = null;
		
		try {
			FileInputStream fis = new FileInputStream("kvstore.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			deser = (KVStoreImpl) ois.readObject();
			ois.close();
			fis.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class Not Found");
			c.printStackTrace();
			return;
		}
		
		System.out.println("Deserialized KV Store :");
		
		deser.print();*/
	}

}
