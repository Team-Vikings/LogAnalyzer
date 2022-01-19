package com.oracle.vikings.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:custom.properties")
public class OdlMinifyService {
	@Autowired
	FileService fileService;
	@Value("${saveDir}")
	String SAVE_DIR;
	@Value("${unzippedDir}")
	String UNZIPPED_DIR;
	@Value("${processDir}")
	String processDir;
	@Value("${not.req.list.minify}")
	List<String> lstNotRec;
	List<String> notReqLines = new ArrayList<String>();

	@PostConstruct
	public void createDirIfNotExists() {
		System.out.println(SAVE_DIR + "..." + UNZIPPED_DIR + "..." + processDir);
	}

	// testing1
	public Map<String, Object> processFile(File f) {
		fileService.increaseCounter();
		// Map Storing response displayed on UI.
		Map<String, Object> resContent = new HashMap<String, Object>();
		fileService.createDirIfNotExists();
		List<String> lstErrors = new ArrayList<String>();
		List<String> exceptionStack= new ArrayList<String>();
		Map<String, Object> incError = new HashMap<String, Object>();
		File file = null;
		try (BufferedReader b = new BufferedReader(new FileReader(f))) {
			File dir = new File(processDir + File.separator);
			if (!(dir.exists() && dir.isDirectory())) {
				new File(processDir).mkdir();
			}
//			System.out.println("Absolute Path: " + f.getAbsolutePath());
			BufferedWriter write = new BufferedWriter(new FileWriter(processDir + File.separator + f.getName()));
			Date startTime = new Date();
			String str;
			int counter = 0;
			boolean skipLine = false, patternFound = false, firstIndex = true;
			String value = "", // variable contain value of given row (last sentence)
					partialString = "", // used to hold first 50 character, to reduce scanning over string
					logFile = "";
			int bracketIndex = 0, endBracketIndex = 0; // variable used to trace ']' end bracket index
			while ((str = b.readLine()) != null) {
				int len = str.length();
				if (len > 0) {
					// Don't add blank line if previous line was skipped
					if (!skipLine && (str.charAt(0) != '[' || (!str.contains("]")))) {
						
						//check for exception stack traces 
						if(null!=str && str.contains("Exception:")) {
							//System.out.println("string"+str);
							String stack="";
							StringBuffer sb=new StringBuffer(stack);
							while(null!=str && !str.contains("]]")) {
								 sb.append("\n"+str);
								 write.append(str);
								str= b.readLine();
							 
							}
							if(sb.toString().contains("oracle.apps.hcm.payrolls"))
							exceptionStack.add(sb.toString());
							else if(sb.toString().contains("oracle.apps.hcm"))
								lstErrors.add(sb.toString());
						}
						write.append(str);
					
					} else {
						// Check if line contains any unnecessary data
						skipLine = skipLine(str);
						if (!skipLine) {
							// Add Time Stamp
							bracketIndex = str.indexOf(']');
							write.append(str.substring(0, bracketIndex + 1));

							int index = 0;
							// finding last index of ] for identifying log statement
							for (index = len - 1; index > 0; index--) {
								if (str.charAt(index) == ']')
									break;
							}
							int lastIndex = index;// last index of ']'
							index = lastIndex;
							if (len - index < 4)
								lastIndex = str.lastIndexOf("log]") + 3;
							value = str.substring(lastIndex + 1, len);
							// Generate List of error message
							if (value.toLowerCase().contains(" error")) {
								lstErrors.add(value);
							}
							//capture all exception stack					
							partialString = str.substring(bracketIndex, bracketIndex + 40);
							if (partialString.contains("INCIDENT_ERROR")) {
								logFile = str.substring(str.lastIndexOf("LOG_FILE"), str.lastIndexOf(".log") + 4);
								// incError
								incError.put(value, logFile);
								write.append(value);
								continue;
							}
//                            value = str.substring(index+1, len);
							String regex = "Process Region|Region Refresh|MDS|MetaDataObject|MOResolver";
							Pattern pattern = Pattern.compile(regex);
							// Matching the compiled pattern in the String
							Matcher matcher = pattern.matcher(value);
							if (matcher.find()) {
								patternFound = true;
								continue;
							}
							if (value.contains("Evaluate EL") || value.contains("Evaluate Expression")) {
								for (; index > 0; index--) {
									if (str.charAt(index) == '[') {
										if (firstIndex) {
											firstIndex = false;
											endBracketIndex = index - 1;
										} else {
											firstIndex = true;
											break;
										}
									}
								}
								// Appending bracket value first then log statement
								value = str.substring(index, endBracketIndex) + value;
								// System.out.println("Evaluate EL: " + value);
								// this needs to be tested
								write.append(value);
								continue;
							}
							// If Execute Query
							else if (value.contains("Execute query") && str.contains("ADF_MESSAGE_CONTEXT_DATA")) {
								
								int startIndex = str.lastIndexOf("ADF_MESSAGE_CONTEXT_DATA");
								int endIndex = str.lastIndexOf("Component");

								if(str.contains("FND_MESSAGES"))
									lstErrors.add(str.substring(startIndex, endIndex));
								write.append(str.substring(startIndex, endIndex));

							}

							write.append(value);
						}
					}
				}
				counter++;
//				System.out.println(counter);
				if (!skipLine)
					write.append("\n");
			}
			Date endTime = new Date();
			System.out.println("\n\nLine processed: " + counter + "\n=========\nTotal time(Type 2): "
					+ (endTime.getTime() - startTime.getTime()) / 1000);
			/*
			 * =========Total time(Type 1): 79
			 */
			file = new File(processDir + File.separator + f.getName());
			File tempFile = fileService.zipFile(file);
			lstErrors.add(tempFile.getName());
			resContent.put("lstError", lstErrors);
			resContent.put("fileName", tempFile.getName());
			resContent.put("incError", incError);
			resContent.put("exceptionStack", exceptionStack);
			return resContent;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	private boolean skipLine(String line) {
		return lstNotRec.parallelStream().anyMatch(line::contains);
	}
	
	
	
}