package util;

import java.io.File;

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
}
