/*
 * @author : Hiteshree Neve(hneve)
 */

package com.oracle.vikings.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:custom.properties")
public class RetroService {

	@Autowired
	FileService fileService;

	@Value("${saveDir}")
	String SAVE_DIR;
	@Value("${unzippedDir}")
	String UNZIPPED_DIR;
	@Value("${processDir}")
	String processDir;
	@Value("${utility.dir}")
	String utilityDir;

	List<String> redoingActionList = new ArrayList<>();

	/* Method to fetch all the basis retro details from logs */
	public LinkedHashMap<String, String> processRetroData(File file) {
		String methodName = "processRetroData";
		//System.out.println("In Retro Service - " + methodName);
		String fileName = UNZIPPED_DIR + File.separator + file.getName();
		List<String> filteredList;
		LinkedHashMap<String, String> responseMap = new LinkedHashMap<String, String>();

		// TODO : Check if this approach is efficient than Scanner Approach
		// Fetch Info using Stream API
		// Fetch below lines from logs and put it in map
		/*
		 * try (Stream<String> lines = Files.lines(Paths.get(fileName))) { filteredList
		 * = lines.filter(line -> line.startsWith("LOGGING =") ||
		 * line.startsWith("action_type =") ||
		 * line.startsWith("Action Parameter THREADS") ||
		 * line.startsWith("new payroll_action_id") ||
		 * line.startsWith("pay rel id          =") ||
		 * line.startsWith("Effective Date   :") ||
		 * line.startsWith("PERSON_ID =")).distinct().collect(Collectors.toList()); ;
		 * 
		 * filteredList.forEach(//System.out::println);
		 * 
		 * Iterator<String> it = filteredList.iterator();
		 * 
		 * while (it.hasNext()) { String currLine = it.next(); String[] arr =
		 * currLine.split("=|:"); if (arr[0].startsWith("LOGGING")) {
		 * responseMap.put("Logging", arr[1]); } else if
		 * (arr[0].startsWith("action_type")) { responseMap.put("ActionType", arr[1]); }
		 * else if (arr[0].startsWith("Action Parameter THREADS")) {
		 * responseMap.put("Threads", arr[1]); } else if
		 * (arr[0].startsWith("new payroll_action_id")) {
		 * responseMap.put("PayrollActionId", arr[1]); } else if
		 * (arr[0].startsWith("pay rel id")) { responseMap.put("PayrollRelationshipId",
		 * arr[1]); } else if (arr[0].startsWith("Effective Date")) {
		 * responseMap.put("EffectiveDate", arr[1].trim().split(" ")[0]); } else if
		 * (arr[0].startsWith("PERSON_ID") && (arr[1].trim() != " " &&
		 * !arr[1].trim().isEmpty())) { responseMap.put("PersonId", arr[1]); } }
		 * 
		 * } catch (IOException io) { io.printStackTrace(); }
		 */

		try {
			Scanner scanner = new Scanner(file);

			while (scanner.hasNext()) {
				String currentLine = (String) scanner.nextLine();
				if (currentLine.startsWith("LOGGING =")) {
					String[] logging = currentLine.split("=");
					//System.out.println(methodName + " - Logging : " + logging[1]);
					responseMap.put("Logging", logging[1]);
				} else if (currentLine.startsWith("action_type =")) {
					String[] actionType = currentLine.split("=");
					//System.out.println(methodName + " - ActionType : " + actionType[1]);
					responseMap.put("ActionType", actionType[1]);
				} else if (currentLine.startsWith("Action Parameter THREADS")) {
					String[] threads = currentLine.split("=");
					//System.out.println(methodName + " - Threads : " + threads[1]);
					responseMap.put("Threads", threads[1]);
				} else if (currentLine.startsWith("new payroll_action_id")) {
					String[] pactId = currentLine.split("=");
					//System.out.println(methodName + " - PayrollActionId : " + pactId[1]);
					responseMap.put("PayrollActionId", pactId[1]);
				} else if (currentLine.startsWith("pay rel id          =")) {
					String[] payRelId = currentLine.split("=");
					//System.out.println(methodName + " - PayrollRelationshipId : " + payRelId[1]);
					responseMap.put("PayrollRelationshipId", payRelId[1]);
				} else if (currentLine.startsWith("Effective Date   :")) {
					String[] effectiveDate = currentLine.split(":");
					//System.out.println(methodName + " - EffectiveDate : " + effectiveDate[1]);
					responseMap.put("EffectiveDate", effectiveDate[1].trim().split(" ")[0]);
				} else if (currentLine.startsWith("PERSON_ID =")) {
					String[] personId = currentLine.split("=");
					if (personId[1].trim() != " " && !personId[1].trim().isEmpty()) {
						//System.out.println(methodName + " - PersonId : " + personId[1]);
						responseMap.put("PersonId", personId[1]);
					}
					break;

				}
				// scanner.nextLine();
			}
			scanner.close();
		} catch (FileNotFoundException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("--->" + methodName + " " + responseMap);
		//System.out.println("Out Retro Service - " + methodName);

		return responseMap;
	}

	/* Method to Fetch all the retro entries generated in this Retro */
	public List<LinkedHashMap<String, String>> fetchGeneratedRetroEntries(File file) {

		String methodName = "fetchGeneratedRetroEntries";

		//System.out.println("In Retro Service - " + methodName);
		List<LinkedHashMap<String, String>> retroEntriesList = new ArrayList<>();
		LinkedHashMap<String, String> retroEntriesMap = null;
		String currentLog = null;
		int lineo =0;
		Scanner scanner = null;
		try {
			 scanner = new Scanner(file,StandardCharsets.UTF_8.name());
			// Add details of one retro entry in Map and add it in the list
			while (scanner.hasNext()) {
				String currentLine = scanner.nextLine();
				if(lineo == 475334) {
					//System.out.print(lineo);
				}
				lineo++;
				
				if (currentLine.equalsIgnoreCase("Parameters passed to insert_retro_entry")) {
					retroEntriesMap = new LinkedHashMap<String, String>();
					currentLog = "Start";
				} else if (currentLine.startsWith("created entry ")) {
					String[] arr = currentLine.split(" ");
					retroEntriesMap.put("RetroEntryID", arr[2]);
					retroEntriesList.add(retroEntriesMap);
					currentLog = "End";
				} else if (currentLine.startsWith("Ended processing at")) {
					break;
				} else if (currentLog != null && currentLog.equals("Start")) {
					////System.out.println(currentLine);
					String[] arr = currentLine.split("=");
					if (arr[0].startsWith("original")) {
						retroEntriesMap.put("SourceRRID", arr[2]);
					} else if (arr[0].startsWith("v_src_rrid")) {
						retroEntriesMap.put("SourceRRID", arr[1]);
					} else
						retroEntriesMap.put(arr[0], arr[1]);
				}

			}

			

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
		scanner.close();
		//System.out.println(lineo);
		// Remove unwanted details of Retro Entry from Map
		// TODO : Check if below code can be removed and handled in UI

		for (LinkedHashMap<String, String> map : retroEntriesList) {

			if (!map.containsKey("v_ret_rrid ")) {
				map.put("v_ret_rrid ", null);
			} else {
				String val = map.get("v_ret_rrid ");
				map.remove("v_ret_rrid ");
				map.put("v_ret_rrid ", val);
			}
			if (!map.containsKey("SourceRRID")) {
				map.put("SourceRRID", null);
			} else {
				String val = map.get("SourceRRID");
				map.remove("SourceRRID");
				map.put("SourceRRID", val);
			}

			Iterator<Entry<String, String>> it = map.entrySet().iterator();
			// TODO : CHeck if map contains

			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();

				// || entry.getKey().equals("v_pay_rel_id") ||
				// entry.getKey().equals("v_curr_aaid") || entry.getKey().equals("v_src_taxu")
				if (entry.getKey().startsWith("effective date") || entry.getKey().startsWith("v_pay_rel_id")
						|| entry.getKey().startsWith("v_curr_aaid") || entry.getKey().startsWith("v_src_taxu")
						|| entry.getKey().startsWith("v_src_epath")) {
					it.remove();
				}

			}

		}
		//System.out.println("--->" + methodName + " " + retroEntriesList);
		//System.out.println("Out Retro Service - " + methodName);
		return retroEntriesList;
	}

	/* Method to find the Original Run Results and New Run REsults */
	public LinkedHashMap<String, String> findOriginalNewDifference(File file) {
		String methodName = "findOriginalNewDifference";
		//System.out.println("In Retro Service - " + methodName);
		// Map to store both the files
		LinkedHashMap<String, String> responseMap = new LinkedHashMap<String, String>();
		try {
			String originalValFilePath = processDir + File.separator + "OriginalValues.txt";
			String newValFilePath = processDir + File.separator + "NewValues.txt";
			PrintWriter originalRun = new PrintWriter(new FileWriter(originalValFilePath));
			PrintWriter newRun = new PrintWriter(new FileWriter(newValFilePath));
			PrintWriter currentLog = null;

			//System.out.println("--->" + methodName + " : Original Value File Name = " + originalValFilePath);
			//System.out.println("--->" + methodName + " : New Value File Name = " + newValFilePath);

			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String currentLine = scanner.nextLine();
				if (currentLine.equalsIgnoreCase("Original Values")) {
					currentLog = originalRun;
				} else if (currentLine.equalsIgnoreCase("New Values")) {
					currentLog = newRun;
				} else if (currentLine.startsWith("Campare for Master")) {
					scanner.close();
					break;
				} else if (currentLog != null)
					currentLog.println(currentLine);
			}
			originalRun.close();
			newRun.close();
			scanner.close();
			responseMap.put("originalValFileName", originalValFilePath);
			responseMap.put("newValFileName", newValFilePath);
			responseMap.put("originalValFileWithOutPath", File.separator + "OriginalValues.txt");
			responseMap.put("newValFileWithOutPath", File.separator + "NewValues.txt");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("Out Retro Service - " + methodName);
		return responseMap;
	}

	/* Method to find all the Reprocessed actions */
	public List<LinkedHashMap<String, String>> fetchRedoingActions(File file) {
		String methodName = "fetchRedoingActions";
		//System.out.println("In Retro Service - " + methodName);

		List<LinkedHashMap<String, String>> redoActionList = new ArrayList<>();
		LinkedHashMap<String, String> redoActionMap = null;
		String currentLog = null;
		String onetimeLog = null;
		Scanner scanner = null;
		int count = 0;
		try {
			 scanner = new Scanner(file);
			// Add details of one redo action in Map and add it in the list
			while (scanner.hasNextLine()) {
				String currentLine = scanner.nextLine();
				if (currentLine.equalsIgnoreCase("opened retropay master assignment actions cursor")) {
					onetimeLog = "Init";
				} else if (currentLine.startsWith("Inserted new aa :") && onetimeLog.equals("Init")) {
					String[] arr = currentLine.split(":");
					redoActionMap = new LinkedHashMap<String, String>();
					redoActionMap.put("Action Id", arr[1]);
					currentLog = "Start";
				} else if (currentLine.contains("Process Path :") && currentLog != null) {
					redoActionList.add(redoActionMap);
					currentLog = "End";
				} else if (currentLine.startsWith("Get Subactions for")) {

					break;
				} else if (currentLog != null && currentLog.equals("Start")) {
					String[] arr = currentLine.split(":");
					/*
					 * if(arr[0].startsWith("rows to process")) { count =
					 * Integer.parseInt(arr[1].trim()); } else { count--; }
					 */
					if (!arr[0].trim().contains("Process Path") && !arr[0].trim().startsWith("rows to process")) {
						if (arr[0].trim().contains("Inserted new aa")) {
							redoActionMap.put("Action Id", arr[1]);
						} else {
							redoActionMap.put(arr[0].trim(), arr[1].trim().split(" ")[0]);
						}

					}
				}

			}

	

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
		scanner.close();
		//System.out.println("--->" + methodName + " " + redoActionList);
		//System.out.println("Out Retro Service - " + methodName);
		return redoActionList;
	}
	
	/* Method to fetch all the basis retro details from logs */
	public LinkedHashMap<String, String> generateRetroDiagnosticFile(LinkedHashMap<String, String> retroInfo) throws FileNotFoundException,IOException  {
		LinkedHashMap<String, String> fileMap = new LinkedHashMap<String, String>();
		
		
		String methodName = "generateRetroDiagnosticFile";
		//System.out.println("In Retro Service - " + methodName);
		// Map to store both the files
		FileInputStream mainRetroDiag = null;
		FileOutputStream autoGeneratedRetroDiag = null;
		String commonRetroDiagnosticFilePath = utilityDir + File.separator + "AHMD_Retro_Diagnostic.sql";
		String outRetroDiagFilePath = processDir + File.separator + "AHMD_Retro_Diag_Autogenerated.sql";
		String outRetroDiagNameWithoutPath = File.separator + "AHMD_Retro_Diag_Autogenerated.sql";
		
		try {
			mainRetroDiag = new FileInputStream(commonRetroDiagnosticFilePath);
			autoGeneratedRetroDiag = new FileOutputStream(outRetroDiagFilePath);
		
			
			int b; 
	        while  ((b=mainRetroDiag.read()) != -1) 
	        	autoGeneratedRetroDiag.write(b); 
	  
	        /* read() will readonly next int so we used while 
	           loop here in order to read upto end of file and 
	           keep writing the read int into dest file */
	        
			} finally {
				mainRetroDiag.close(); 
		        autoGeneratedRetroDiag.close(); 
			} 
		
		Charset charset = StandardCharsets.UTF_8;
		Path path = Paths.get(outRetroDiagFilePath);
		String content = new String(Files.readAllBytes(path), charset);
		if( null!= retroInfo.get("PayrollActionId")) {

			content = content.replaceAll("&&PACTID", retroInfo.get("PayrollActionId"));
		}
		if( null!= retroInfo.get("PersonId")) {

			content = content.replaceAll("&&Person_ID", retroInfo.get("PersonId"));
		}
		Files.write(path, content.getBytes(charset));
		
		fileMap.put("retroDiagFileName", outRetroDiagNameWithoutPath);
		
		//System.out.println("Out Retro Service - " + methodName);
		
		
		return fileMap;
	}

}
