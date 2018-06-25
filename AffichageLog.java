package com.reversyslog.servlets;

import java.io.IOException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.reversyslog.beans.VelocityConnection;
import com.reversyslog.run.GlobalConf;
import com.reversyslog.beans.FileAttributeResult;
import com.reversyslog.beans.FileAttributes;
import com.reversyslog.beans.FilesFinder;
import com.reversyslog.beans.LineAdditionUpResult;
import com.reversyslog.beans.LogFileReader;
import com.reversyslog.beans.LogPartDisplay;
import com.reversyslog.beans.SubstringSearch;

/**
 * Servlet implementation class AffichageLog
 */
//@WebServlet("/AffichageLog")
public class AffichageLog extends VelocityConnection {
	
	private static final long serialVersionUID = 1L;
     
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AffichageLog() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    
    /*This servlet enables the display of the main page of the application (log consulting)
    
     First, it finds the right log file and then what part of the log file we need to display 
     
    It redirects to the authentication page if not logged in
    doGet: when you go on the page and the form is not submitted yet
    doPost: when the form is submitted (gets the values of the fields and process them)
    
    
    It calls several methods from other classes to display what we are looking for (readlogfile, searchKeywords2, contentToDisplay...).
    I also used a java class to get the attributes of each log file and I compared the creation time and last modification time 
    of these files with the time I was looking for.
    
    However, it doesn't deal with the case when the date/time is higher than the current date/time or smaller than the oldest log file (I just need to add a condition)
    I used Velocity (a template engine) to better split the presentation tier and business tier
    
    */
    
	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
			HttpSession session = request.getSession();
			
			
			if ((session == null) || ( ! "1".equals(session.getAttribute("authentication")))) 
			{
				response.sendRedirect( request.getContextPath() + "/login?error=sessionMissing" );
				 return;			
			}
			
			String folder_instances_path = GlobalConf.searchPath;
			File[] instancelist= FilesFinder.findfiles(folder_instances_path);	
			   
			
			Map<String,Object> context = new HashMap<>();
			context.put("userEmail", session.getAttribute("email"));			
	        context.put("filecontentpart", "Veuillez selectionner une instance, un module et une date/heure."); 
	        context.put("instancelist", instancelist);	
		    applyTemplate( request,  response, "affichagelog.html", context);  
		
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
			String folder_instances_path = GlobalConf.searchPath;
			
			File[] instancelist= FilesFinder.findfiles(folder_instances_path); 
			
			String instancenameselected = request.getParameter("nominstance");
			String modulenameselected = request.getParameter("nom_module");		
			
			String recherchefieldvalue = request.getParameter("recherche");
			String datetimefieldvalue = request.getParameter("datetime1");
			
			
			String lognameselected = null;
			long current_fileCreation_date = 0;
			
			
			String folder_moduleSelected_path = folder_instances_path +	instancenameselected + "/" + modulenameselected + "/";	
		    File[] loglist= FilesFinder.findfiles(folder_moduleSelected_path); 
	  //    String[] loglist2 = new String[loglist.length];	 
			String[] loglist3 = new String[loglist.length];   
				    
			for (int i=0; i<loglist.length ; i++){
						
					//	loglist2[i] = loglist[i].getAbsolutePath();
						loglist3[i] = loglist[i].getName();
						
			}		
					
			
			ArrayList<Long> liste_file_creation_date = new ArrayList<>();	 
			
			for (String log : loglist3) {
				
				
				FileAttributeResult result = FileAttributes.getFileAttributes(folder_moduleSelected_path + log);	
				long fileCreation_date = result.fileCreation_date/1000;
				long fileLastModifiedTime = result.fileLastModifiedTime/1000;
				
				LocalDateTime requestedDate = LocalDateTime.parse(datetimefieldvalue, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")); //permet de faire correspondre la chaine de caractère qui correspond à la date entré à une date au format défini, en gros ça permet de dire que chaque caractère de la chaine correspont à un élément de la date selon le format défini.
				long requestedDateSinceEpoch = requestedDate.toEpochSecond(java.time.OffsetDateTime.now().getOffset()); //permet de convertir la date en milliseconds depuis 1970 (avec l'heure d'été c +2 apparemment)
				
				System.out.println("fileCreation_date:"+fileCreation_date);
				System.out.println("requestedDateSinceEpoch:"+requestedDateSinceEpoch);
				System.out.println("fileLastModifiedTime:"+fileLastModifiedTime);
				
				
				liste_file_creation_date.add(fileCreation_date);	
				
				
				
				if (fileCreation_date  <= requestedDateSinceEpoch && requestedDateSinceEpoch <= fileLastModifiedTime) {		
					
					System.out.println("right");
					System.out.println(log);
					lognameselected=log;									
					current_fileCreation_date = fileCreation_date;		
					
				}
				
				else {
					
					System.out.println("wrong");
					
				}
				
				
			}		
					
			
			String log_selected_path = folder_instances_path + instancenameselected + "/" + modulenameselected + "/" + lognameselected;
			System.out.println("Chemin complet log:"+log_selected_path);
			
			String wholefilecontent= LogFileReader.readlogfile(log_selected_path); 
			
			
			Map<String, Object> retour0 = SubstringSearch.searchDateTime(request, wholefilecontent);  
			
			Map<String, Object> retour = SubstringSearch.searchKeywords2(								 
					request,
					wholefilecontent, 
					(Integer) retour0.get("datetime_index_wholefilecontent"),
					(String) retour0.get("datetimefieldvalue2"));
			

			
			LineAdditionUpResult lineAdditionUpResult100= LogPartDisplay.contentToDisplay(			 
					(Integer) retour.get("substring_start_index_debut_ligne"),
					(String) retour.get("wholefilecontent_modified_or_not"), 
					request, 
					(Integer) retour0.get("datetime_index_wholefilecontent"));				
			
			 
			
			String filecontentpart = lineAdditionUpResult100.substring; 
			int initial_substringstartindex = lineAdditionUpResult100.substring_start_index;
			int initial_substringendindex = lineAdditionUpResult100.substring_end_index;
			
			System.out.println("initial_substringendindex:"+initial_substringendindex);
			
			
			
			Map <String,Object> context = new HashMap<>(); 
			context.put("filecontentpart", filecontentpart);    
			context.put("instancelist", instancelist); 
			context.put("initial_substringstartindex", initial_substringstartindex); 
			context.put("initial_substringendindex", initial_substringendindex);
			
			context.put("instancenameselected", instancenameselected);
			context.put("modulenameselected", modulenameselected);
			context.put("lognameselected", lognameselected); 					
			context.put("datetimefieldvalue", datetimefieldvalue);
			
			context.put("log_selected_path", log_selected_path);
			context.put("recherchefieldvalue", recherchefieldvalue);
			
			context.put("folder_moduleSelected_path", folder_moduleSelected_path);
			context.put("current_fileCreation_date", current_fileCreation_date);
			
			boolean enabled = true;
			context.put("enabled", enabled); 
			context.put("keyword_introuvable", (boolean) retour.get("keyword_introuvable"));
				
			applyTemplate( request,  response, "affichagelog.html", context);
			        
		
		
	}

}

