package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

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
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();			

			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createDir(String path) {
		File file = new File(path);

		// ficheiro nao existe
		if (!file.exists())
			file.mkdirs();
	}
	
	public static void writeObject(Object obj, String filePath) {
		File file = new File(filePath);

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(obj);

			objectOutputStream.close();
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Object readObject(String filePath) {
		File file = new File(filePath);

		if (!file.exists() || file.length() == 0) {
			System.out.println("[readObject]: saí antes de ler o object "+ filePath );
			return null;
		}

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
	
	public static void writeStringToFile (String content, String path) {
		
		try {
			BufferedWriter outF = new BufferedWriter( new FileWriter(path) );
			outF.write(content);
			outF.flush();
			outF.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static List<String> readFromFile (String path) {
		ArrayList<String> txt = new ArrayList<String>();
		try {
			BufferedReader inF = new BufferedReader( new FileReader(path) );
			String reader;
			while( (reader = inF.readLine())!= null) {
				System.out.println("[readFromFile]: " + reader);
				txt.add(reader);
				
			}
			inF.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return txt;
		
		
	}
	
	
}
