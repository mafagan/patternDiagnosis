package kse.findj.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
	
	private File file = null;
	private FileWriter writer = null;
	
	public LogWriter(String fileURL){
		file = new File(fileURL);
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeWriter(){
		if(writer != null){
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void write(String msg){		
		try {
			writer.append(msg + "\r\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
