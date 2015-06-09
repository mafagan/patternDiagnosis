package kse.findj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ObjectIOUtil {
	
	public static boolean save(Object obj, String fileURL) {
		
		File file = new File(fileURL);
		if(file.exists()) {
			file.delete();
		}
		
		ObjectOutputStream oos = null;
		boolean flag = false;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(obj);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	public static Object load(String fileURL) {
		File file = new File(fileURL);
		
		if(!file.exists()) {
			return null;
		}
		
		Object obj = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			obj = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

}
