/*
 * @author : Hiteshree Neve(hneve)
 */

package com.oracle.vikings.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:custom.properties")
public class ErrorHandlerService {
	
	@Autowired
	FileService fileService;
	
	/* Method to find all the flow has errored */
	public LinkedHashMap<String, String> checkIfFlowHasErrorred(File file) {
		String methodName = "checkIfFlowHasErrorred";
		//System.out.println("In ErrorHandler Service - " + methodName);
		
		
		LinkedHashMap<String, String> errorMap = null;
		String currentLog = null;
		int count =6;
		try {
			Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name());
			// Add details of one redo action in Map and add it in the list
			while (scanner.hasNextLine() ) {
				String currentLine = scanner.nextLine();
				if (currentLine.equalsIgnoreCase("Oracle error occurred")) {
					errorMap = new LinkedHashMap<String, String>();
					currentLog = "Start";
					count--;
				} else if(currentLine.equalsIgnoreCase("Exiting without success") || count==0){
					currentLog = "End";
					break;	
				} else if (currentLog != null && currentLog.equals("Start")) {
					String[] arr = currentLine.split(":|=");
					String key = null;
					if(!arr[0].equals("")) {	
						if(errorMap.containsKey(arr[0])){
							 key = arr[0]+" ";
							 errorMap.put(key, arr[1]);
						} else {
							errorMap.put(arr[0], arr[1]);
						}
					}
					count--;
				}

			}

			scanner.close();
			
			
			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("--->" + methodName + " " + errorMap);
		//System.out.println("Out ErrorHandler Service - " + methodName);
		return errorMap;
	}
	
	
	/* Method to validate if the file is GMFZT enabled, single threaded and for single person */
	public LinkedHashMap<String, String> validateGMFZTLogFile(File file) {
		String methodName = "validateGMFZTLogFile";
		
		fileService.increaseCounter();
		
		//System.out.println("In ErrorHandler Service - " + methodName);
		
		
		LinkedHashMap<String, String> validateMap = new LinkedHashMap<String, String>();
		validateMap.put("valid","TRUE");
		try {
			Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name());
			int lineno = 0;
			
			while (scanner.hasNextLine() ) {
				
				String currentLine = scanner.nextLine();
				//currentLine = currentLine.replaceAll("[^\\x00-\\x7f]", "");
				lineno++;
				
				////System.out.println(currentLine);
				String[] arr = currentLine.split("=|:");
				if (currentLine.startsWith("LOGGING")) {
					validateMap.put(arr[0], arr[1]);
					if((!(arr[1].trim().equalsIgnoreCase("GMFZT") || arr[1].trim().equalsIgnoreCase("GMFTZ") || arr[1].trim().equalsIgnoreCase("GMTFZ"))) && !validateMap.get("valid").equals("FALSE")) {
						validateMap.put("valid","FALSE");
					}
					
				} else if (currentLine.startsWith("action_type =")) {
					validateMap.put(arr[0], arr[1]);
					if(!arr[1].trim().equalsIgnoreCase("L") && !validateMap.get("valid").equals("FALSE")) {
						validateMap.put("valid","FALSE");
					}
					
				} else if (currentLine.startsWith("Action Parameter THREADS")) {
					validateMap.put(arr[0], arr[1]);
					if(!arr[1].trim().equalsIgnoreCase("1") && !validateMap.get("valid").equals("FALSE")) {
						validateMap.put("valid","FALSE");
					}
					
				} else if (currentLine.startsWith("Total Pay Relship count")) {
					validateMap.put(arr[0], arr[1]);
					if(!arr[1].trim().equalsIgnoreCase("1") && !validateMap.get("valid").equals("FALSE")) {
						validateMap.put("valid","FALSE");
					}
					break;
				}

			}
			//System.out.println("LIneno : " + lineno);
			scanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	/*	LinkedHashMap<String, String> validateMap = new LinkedHashMap<String, String>();
		BufferedReader objReader = null;
		validateMap.put("valid","TRUE");
		int lineno = 0;
			   String currentLine;
				try {
			   objReader = new BufferedReader(new FileReader(file));

			   while ((currentLine = objReader.readLine()) != null) {
				   lineno++;
				   //System.out.println("--->Lineno" + lineno);
					////System.out.println(currentLine);
					String[] arr = currentLine.split("=|:");
					if (currentLine.startsWith("LOGGING")) {
						validateMap.put(arr[0], arr[1]);
						if((!(arr[1].trim().equalsIgnoreCase("GMFZT") || arr[1].trim().equalsIgnoreCase("GMFTZ") || arr[1].trim().equalsIgnoreCase("GMTFZ"))) && !validateMap.get("valid").equals("FALSE")) {
							validateMap.put("valid","FALSE");
						}
						
					} else if (currentLine.startsWith("Action Parameter THREADS")) {
						validateMap.put(arr[0], arr[1]);
						if(!arr[1].trim().equalsIgnoreCase("1") && !validateMap.get("valid").equals("FALSE")) {
							validateMap.put("valid","FALSE");
						}
						
					} else if (currentLine.startsWith("Total Pay Relship count")) {
						validateMap.put(arr[0], arr[1]);
						if(!arr[1].trim().equalsIgnoreCase("1") && !validateMap.get("valid").equals("FALSE")) {
							validateMap.put("valid","FALSE");
						}
						break;
					}
			   }
			    ////System.out.println(strCurrentLine);
			   } catch (FileNotFoundException e) {
			         e.printStackTrace();
			      } catch (IOException e) {
			         e.printStackTrace();
			      } finally {
			         if (objReader != null) {
			            try {
			            	objReader.close();
			            } catch (IOException e) {
			               e.printStackTrace();
			            }
			         }
			      }
		
		*/
				
		//System.out.println("--->" + methodName + " " + validateMap);
		//System.out.println("Out ErrorHandler Service - " + methodName);
		return validateMap;
	}

}
