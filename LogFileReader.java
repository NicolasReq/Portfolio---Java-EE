package com.reversyslog.beans;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class LogFileReader {

	
	/*
	This method enables us to read a txt file and save all the lines in a string. We just need the path of a file.
	*/
	
	
	public static String readlogfile(String filepath) {
		System.out.println("d�but de lecture " + LocalDateTime.now());
		int i=0;
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(filepath))); 
			String ligne;
			
			while ((ligne = buff.readLine()) != null) {
				
				builder.append(ligne).append('\n');
				
				i++;
				
				}
			
			buff.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		System.out.println("fin de lecture " + LocalDateTime.now());
		return builder.toString();
	}

}
