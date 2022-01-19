/*
 * @author 			 : Yash Shah(yvshah)
 * @updated 21/07/21 : Rohan Jain(rohajain)
 * @updated 29/07/21 : Rohan Jain(rohajain)
 */
package com.oracle.vikings.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:custom.properties")
public class PlSqlService {

	@Value("${saveDir}")
	String SAVE_DIR;
	@Value("${unzippedDir}")
	String UNZIPPED_DIR;
	@Value("${processDir}")
	String processDir;

	@Autowired
	FileService fileService;

	public static Map<String, String> formatData = new HashMap<>();
	public static String enter = "(?i).*Entering.*";
	public static String leave = "(?i).*leaving.*";
	public static Pattern method = Pattern.compile(":.\\w+.\\w+");
	public static Pattern step = Pattern.compile("\\d+");

	public LinkedHashMap<String, String> processPlSqlData(File file) {

		fileService.increaseCounter();

		List<Map<String, String>> myData = new ArrayList<>();

		// Save Required Data
		myData = createDataFromFile(file);

		myData = myData.stream().filter(t -> t.get("data").matches("(?i).*Entering.*|(?i).*leaving.*")).sorted(
				(t1, t2) -> Integer.compare(Integer.parseInt(t1.get("lineNo")), Integer.parseInt(t2.get("lineNo"))))
				.collect(Collectors.toList());

		for (int i = 0; i < myData.size(); i++) {

			String currData = myData.get(i).get("data");
			String lineNo = myData.get(i).get("lineNo");
			// System.out.println(myData.get(i));

			// Check If entering into method
			checkIfEntering(currData, lineNo);

			// CheckIfLeaving
			checkIfLeaving(currData, lineNo);

		}

		LinkedHashMap<String, String> sortedData = new LinkedHashMap<>();

		formatData.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.forEachOrdered(t -> sortedData.put(t.getKey(), t.getValue()));

		System.out.println(sortedData);

		return sortedData;

	}

	private static void checkIfLeaving(String currData, String lineNo) {
		String methodName = "";
		String stepCount = "";
		if (currData.matches(leave)) {
			Matcher methodMT = method.matcher(currData);
			if (methodMT.find())
				methodName = trim(methodMT.group());
			Matcher stepMT = step.matcher(currData);
			if (stepMT.find())
				stepCount = stepMT.group().trim();
			// System.err.println(methodName+stepCount);
			if (!"".equals(stepCount)&&Integer.parseInt(stepCount) > 800) 
				formatData.put(methodName, lineNo);
			else
				formatData.remove(methodName);
		}
	}

	private static void checkIfEntering(String currData, String lineNo) {
		String methodName = "";
		String stepCount = "";
		if (currData.matches(enter)) {
			Matcher methodMT = method.matcher(currData);
			if (methodMT.find())
				methodName = trim(methodMT.group());
			Matcher stepMT = step.matcher(currData);
			if (stepMT.find())
				stepCount = stepMT.group().trim();

			// System.out.println(methodName+stepCount);

			formatData.put(methodName, lineNo);
		}
	}

	private static String trim(String str) {
		String rStr = "";

		rStr = str.substring(str.indexOf(":") + 1).trim();

		return rStr;
	}

	private static ArrayList<Map<String, String>> createDataFromFile(File myFile) {

		ArrayList<Map<String, String>> tempData = new ArrayList<>();
		int lineCount = 1;

		LineIterator fileRead;
		try {
			fileRead = FileUtils.lineIterator(myFile, "UTF-8");
			try {

				while (fileRead.hasNext()) {
					Map<String, String> tempMap = new HashMap<>();
					String currLine = fileRead.nextLine();
					currLine = currLine.replaceAll("\\[.*]", "");
					tempMap.put("lineNo", String.valueOf(lineCount));
					tempMap.put("data", currLine);

					tempData.add(tempMap);

					lineCount++;

				}
			} finally {
				fileRead.close();
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return tempData;
	}

}
