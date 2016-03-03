package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import entities.Group;

public class MiscUtil {

	public static void delete(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();

			if (files.length == 0) {
				System.out.println("Delete folder " + f.getAbsolutePath());
				f.delete();
			} else {
				for (File c : files)
					delete(c);
			}
		} else {
			System.out.println("Delete file " + f.getAbsolutePath());
			f.delete();
		}
	}

	public static void createFile(String filePath) {
		File file = new File(filePath);

		// ficheiro nao existe
		if (!file.exists()) {
			file.getParentFile().mkdirs();

			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeObject(Object obj, String filePath) {
		File file = new File(filePath);

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

			objectOutputStream.close();
			fileOutputStream.close();
			objectOutputStream.writeObject(obj);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Object readObject(String filePath) {
		File file = new File(filePath);
		Object obj = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			obj = objectInputStream.readObject();

			objectInputStream.close();
			fileInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		
		return obj;
	}
}
