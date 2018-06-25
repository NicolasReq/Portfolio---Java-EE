package com.reversyslog.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;



public class SubstringSearch2 {

	
	
	/*
	Purpose of this method: it enables to search for keywords in a string (wholefilecontent). I made an interface and in it there is a form that we have to fill out and there is a "keyword" field. 
	This method deals with all the cases: when the "keyword" field is not filled out, when it's filled out and no keywords are found and when it's filled out and keywords are found.
	If keywords are found, it gets the indexes of all the occurrences then it gets the indexes of the first character of each line where there is a occurrence.
	It also highlights the different occurences by adding tags and classes to the string (for example "<span class=\"keywords_found\">"+recherchefieldvalue+"</span>") and thanks to a CSS file (that I didn't import).
	Since some characters are added with tags and attributes, this method gets the new indexes.
	When the form is submitted, we have to select a occurence. So this method returns the index of the first character of the line where there is the selected occurrence. 
	*/
	
	
	public static Map<String, Object> searchkeywords(HttpServletRequest request, String wholefilecontent){
		
		Map <String,Object> retour = new HashMap<>();
		
		int substring_start_index=0;
		int occurence_selected2=0;
		String recherchefieldvalue = request.getParameter("recherche");
		String occurence_selected= request.getParameter("numOccurence");
		if (occurence_selected !=null){
		occurence_selected2=Integer.parseInt(occurence_selected); 
		}
		System.out.println("Num�ro occurence:"+occurence_selected);
		
		
		
		if (recherchefieldvalue.isEmpty())
			
			{
			    System.out.println("Aucune valeur entr�e dans le champs recherche");
				substring_start_index=0;		
				retour.put("index_keyword", 0); 
			} 
		
		else 
		
			{
				
			
			System.out.println("Mots cl�s recherch�s:"+recherchefieldvalue);
			ArrayList<Integer> liste_index_occurrences = new ArrayList<>();
			
			while (substring_start_index !=-1)
			{
				if (substring_start_index==0){     
					
					substring_start_index= wholefilecontent.indexOf(recherchefieldvalue, substring_start_index);
					if (substring_start_index !=-1) {
					liste_index_occurrences.add(substring_start_index);
					}
					
				}
				
				substring_start_index= wholefilecontent.indexOf(recherchefieldvalue, substring_start_index+1);
				
				if (substring_start_index !=-1) {
				liste_index_occurrences.add(substring_start_index);
				}
				
			}
			
			System.out.println("Nombre d'occurences trouv�es:"+ liste_index_occurrences.size());
			for(int i = 0; i < liste_index_occurrences.size(); i++)
		    {
		      System.out.println("Index du 1er caract�re de l'occurence " + i + " = " + liste_index_occurrences.get(i));
		      
		    }     
		              
		
			
							if (liste_index_occurrences.isEmpty())
								{	
									System.out.println("Mot recherch� introuvable");
									substring_start_index=0;
									retour.put("index_keyword", 0);
								}
							
							else 
								{
		
								retour.put("index_keyword", liste_index_occurrences.get(occurence_selected2));
								wholefilecontent= wholefilecontent.replace(recherchefieldvalue, "<span class=\"keywords_found\">"+recherchefieldvalue+"</span>");
								
								
								substring_start_index=0; 
								
								ArrayList<Integer> liste_index_occurrences_modifie = new ArrayList<>(); 
								
								while (substring_start_index !=-1)
								{
									if (substring_start_index==0){      
										
										substring_start_index= wholefilecontent.indexOf(recherchefieldvalue, substring_start_index);
										if (substring_start_index !=-1) {
											liste_index_occurrences_modifie.add(substring_start_index);
										}
										
									}
									
									substring_start_index= wholefilecontent.indexOf(recherchefieldvalue, substring_start_index+1);
									
									if (substring_start_index !=-1) {
										liste_index_occurrences_modifie.add(substring_start_index);
									}
									
								}
								
								
								System.out.println("Nombre d'occurences trouv�es2:"+ liste_index_occurrences_modifie.size());
								for(int i = 0; i < liste_index_occurrences_modifie.size(); i++)
							    {
							      System.out.println("Index_modifi� du 1er caract�re de l'occurence " + i + " = " + liste_index_occurrences_modifie.get(i));
							      
							    }     
							         
								
								
								retour.put("index_keyword", liste_index_occurrences_modifie.get(occurence_selected2));
								
								
								StringBuilder str = new StringBuilder(wholefilecontent);
								str.insert((Integer) retour.get("index_keyword")-1, " id=\"keywordselected\"");
								wholefilecontent = str.toString();
								
								
								
								for(int i=0; i<liste_index_occurrences_modifie.size() ; i++) {
									int index_occurrence = liste_index_occurrences_modifie.get(i);
								
									while((index_occurrence>0) && (wholefilecontent.charAt(index_occurrence) != '\n')) 
										index_occurrence--;										  
									if (wholefilecontent.charAt(index_occurrence) == '\n')
										index_occurrence++;
									 
									liste_index_occurrences_modifie.set(i, index_occurrence);
								}	
								
								substring_start_index = liste_index_occurrences_modifie.get(occurence_selected2);
								
							//	System.out.println("index_keyword2:"+liste_index_occurrences_modifie.get(occurence_selected2));
								
								}
			
			
			}
		
		
		retour.put("substring_start_index_debut_ligne", substring_start_index);
		retour.put("wholefilecontent_modified_or_not", wholefilecontent);
		return retour;
		
		
	}
	
	
	
}