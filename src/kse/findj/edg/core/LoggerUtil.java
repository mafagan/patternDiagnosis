package kse.findj.edg.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LoggerUtil {
	
	private static String filePath = "";
	private static File file = null;
	private static FileWriter writer = null;
	private static boolean opened = false;
	
	public static void setFile(String fileURL){
		filePath = fileURL;
		file = new File(filePath);
	}
	
	public static void open(){
		opened = true;
	}
	
	public static void close(){
		opened = false;
	}
	
	public static void closeWriter(){
		if(writer != null){
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void p(ExplanationRoutine expRoutine, String msg){
		if(opened){
			try {
				if(writer == null)
					writer = new FileWriter(file);
				
				String[] msgs = msg.split("\n");
				for(String str : msgs){
					//System.out.println(routine.getRoutineID() + ":" + routine.getInputAxiom().toString() + " > " + str);
					writer.append(expRoutine.toString() + " > " + str + "\r\n");
				}
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	
}
