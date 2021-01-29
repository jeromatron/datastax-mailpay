package com.datastax.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileUtils {
	private static final String RESOURCES_DIR = "src/main/resources";
	public static List<String> readFileIntoList(String file) {
		try {
			return Files.readAllLines(Paths.get(RESOURCES_DIR + '/' + file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String readFileIntoString(String file) {
		try {
			return Files.readString(Paths.get(RESOURCES_DIR + '/' + file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
