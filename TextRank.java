




package multidocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.*;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author Kush & Kanika
 *
 */
public class TextRank{ 
	protected static Utility lang = null;
	protected static String text = null;
	protected static Graph graph = null;

	public TextRank () throws Exception {
		String path  = "C:/Automatic-Text-Summarizer-master/Automatic-Text-Summarizer-master/";
		lang = func(path);
	}

	private static double[] hits_normalize(double[] hubRank) {
		double temp = 0.0;
		for(int i = 0; i < hubRank.length; i++) {
			temp += Math.pow(hubRank[i], 2);
		}

		temp = Math.pow(temp, .5);

		for(int i = 0; i < hubRank.length; i++) {
			hubRank[i] = hubRank[i]/temp;
		}

		return hubRank;
	}
	public static Utility func (String path) throws Exception {

		Utility lang = null;
		lang = new Utility(path);
		//System.out.println("Lang" + lang);
		return lang;
	}

	public void mainStart (final String text) throws Exception {

		graph = new Graph();
		this.text = text;
	}

	public static String calls() {//Collection<MetricVector> call () throws Exception {

		// scan sentences 

		int len = 0;
		final ArrayList<Sentence> s_list = new ArrayList<Sentence>();

		text = text.replace('"', ' ');
		text = text.replace(';', ' ');
		text = text.replaceAll("\\[a-z]\\.", "\\[a-z]\\. ");
		text = text.replaceAll("\\s+", " ");
		text = text.replace(" .", ".");

		for (String sent_text : lang.splitParagraph(text)) {
			len++;
		}

		final Sentence sen[] = new Sentence[len];
		double Similarity[][] = new double[len][len]; 
		double JacSimilarity [][] = new double[len][len];
		double DiceSimilarity [][] = new double[len][len];
		double CosSimilarity [][] = new double[len][len];
		double UnsymetricSimilarity [][] = new double[len][len];
		for (int i = 0; i < len ; i++) {
			for (int j = 0; j < len ; j++) {
				Similarity[i][j] = 0;
				JacSimilarity [i][j] = 0;
				DiceSimilarity [i][j] = 0;
				CosSimilarity [i][j] = 0;
				UnsymetricSimilarity [i][j] = 0;
			}
		}
		int k = 0;
		double Length_of_hashes[] = new double[len];
		for ( String sent_text : lang.splitParagraph(text)) {
			sen[k] = new Sentence(sent_text.trim());
			try {
				sen[k].mapTokens(lang, graph);
			} catch (Exception e) {
				e.printStackTrace();
			}

			s_list.add(sen[k]);

			// Get a set of the entries 
			Set set = sen[k].tokens.entrySet(); 
			// Get an iterator 
			Iterator j = set.iterator(); 
			int h = 0;
			// Display elements 
			while(j.hasNext()) { 
				Map.Entry me = (Map.Entry)j.next(); 
				h++;
			}
			Length_of_hashes[k] = h;


			k++;
		}

		CosineSimilarityTFIDF sim = new CosineSimilarityTFIDF();
		Collection<Double> sentenceOccurrenceVector[] = new Collection[len];
		for(int l=0; l < len ;l++){
			sentenceOccurrenceVector[l] = sim.createFrequencyOfOccurrenceVector(sen[l].text, len , text);		
		}

		for(int l = 0 ; l < len ; l++){

			Set set = sen[l].tokens.entrySet(); 
			// Get an iterator 
			Iterator o = set.iterator(); 
			// Display elements 
			while(o.hasNext()) { 
				Map.Entry me = (Map.Entry)o.next(); 

				for (int m = 0; m < len; m++) {
					if (sen[l] != sen[m]) {
						Set set1 = sen[m].tokens.entrySet(); 
						// Get an iterator 
						Iterator o1 = set1.iterator(); 
						// Display elements 
						while(o1.hasNext()) { 
							Map.Entry me1 = (Map.Entry)o1.next(); 
							if(me1.getKey() == me.getKey()) {
								Similarity[l][m] = Similarity[l][m] + 1;
								DiceSimilarity[l][m] = DiceSimilarity[l][m] + 1;
								JacSimilarity[l][m] = JacSimilarity[l][m] + 1;
								CosSimilarity[l][m] = sim.calculate(sen[l].text, sen[m].text, sentenceOccurrenceVector[l], sentenceOccurrenceVector[m]);			    	
							}
						}


					}
				}
			}		
		}

		for (int i = 0; i < len ; i++) {
			for (int j = 0; j < len ; j++) {
				if(i!=j)
				{
					if((Math.abs((Math.log(Length_of_hashes[i])))!=0 )){
						UnsymetricSimilarity[i][j] = (double) (Similarity[i][j] /(Math.abs((Math.log(Length_of_hashes[i])))));
					}

					if((Math.abs((Math.log(Length_of_hashes[i])))+Math.abs((Math.log(Length_of_hashes[j])))) !=0 ){

						Similarity[i][j] = (double) (Similarity[i][j] /(Math.abs((Math.log(Length_of_hashes[i])))+Math.abs((Math.log(Length_of_hashes[j])))));
						//System.out.println("Similarity matrix values" + i + " " + j + " " + Similarity[i][j]);
					}
					if(((Length_of_hashes[i])+(Length_of_hashes[j])-(Similarity[i][j])) != 0){
						JacSimilarity[i][j] = (double) (JacSimilarity[i][j] /((Length_of_hashes[i])+(Length_of_hashes[j])-(JacSimilarity[i][j])));

					}
					if(((Length_of_hashes[i])+(Length_of_hashes[j])) != 0) {
						DiceSimilarity[i][j] = (double) (2.0) * (DiceSimilarity[i][j] /((Length_of_hashes[i])+(Length_of_hashes[j])));
					}
				}
			}
		}

		for (int p = 0; p<Similarity.length; p++){
			for (int q = 0; q<Similarity.length; q++){
				//System.out.println("WORDS SIMILARITY::" + Similarity[p][q]);
			}
		}

		//***********************************************************************************************

		for (int p = 0; p<UnsymetricSimilarity.length; p++){
			for (int q = p; q<UnsymetricSimilarity.length; q++){
				//System.out.println(" FIRST HALF UNSYMETRIC WORDS SIMILARITY::" + UnsymetricSimilarity[p][q]);
			}
		}

		for (int p = 0; p<UnsymetricSimilarity.length; p++){
			for (int q = p; q<UnsymetricSimilarity.length; q++){
				//System.out.println(" SECOND HALF UNSYMETRIC WORDS SIMILARITY::" + UnsymetricSimilarity[q][p]);
			}
		}

		//***********************************************************************************************

		// START THE HITS ALGORITHM FROM HERE
		double StandardError = .01;
		double HubUnsyncrank1[] = new double[len];
		double CatUnsyncrank1[] = new double[len];
		double HubUnsyncrank2[] = new double[len];
		double CatUnsyncrank2[] = new double[len];

		for (int i = 0; i < len ; i++) {
			HubUnsyncrank1[i] = 1;
			CatUnsyncrank1[i] = 1;
			HubUnsyncrank2[i] = 0;
			CatUnsyncrank2[i] = 0;

		}
		double [] HubRank =  new double[len];
		//HubRankerror = callHub(HubUnsyncrank1, HubUnsyncrank2, len,UnsymetricSimilarity);

		double [] CatRank =  new double[len];
		//CatRankerror = callCat(CatUnsyncrank1, CatUnsyncrank2, len,UnsymetricSimilarity);

		double hubErr = 0.0, catErr = 0.0;
		//HubRankerror[len] = 1 + StandardError;
		//CatRankerror[len] = 1 + StandardError;
		int temp_count = 0;

		do{
			temp_count++;
			HubRank=callHub(CatUnsyncrank1, CatUnsyncrank2, len,UnsymetricSimilarity);
			//HubRank = hits_normalize(HubRank);
			//hubErr = FindError(HubRank, HubUnsyncrank1);

			for (int i = 0; i < HubUnsyncrank1.length && i < CatUnsyncrank1.length; i++) {
				HubUnsyncrank1[i] = HubRank[i];
				HubUnsyncrank2[i] = 0;
			}
			CatRank=callCat(HubUnsyncrank1, HubUnsyncrank2, len,UnsymetricSimilarity);
			//CatRank = hits_normalize(CatRank);
			//catErr = FindError(CatRank, CatUnsyncrank1);
			for (int i = 0; i < HubUnsyncrank1.length && i < CatUnsyncrank1.length; i++) {
				CatUnsyncrank1[i] = CatRank[i];
				CatUnsyncrank2[i] = 0;
			}
			if(temp_count==3) {
				break;
			}
		}while(true/*hubErr > StandardError || catErr > StandardError*/); 
		double rank[] = new double[len];
		double rank_copy[] = new double[len];
		double rank_temp[] = new double[len];
		double row_sum[] = new double[len];

		double Jacrank[] = new double[len];
		double Jacrank_copy[] = new double[len];
		double Jacrank_temp[] = new double[len];
		double Jacrow_sum[] = new double[len];

		double Dicerank[] = new double[len];
		double Dicerank_copy[] = new double[len];
		double Dicerank_temp[] = new double[len];
		double Dicerow_sum[] = new double[len];

		double Cosrank[] = new double[len];
		double Cosrank_copy[] = new double[len];
		double Cosrank_temp[] = new double[len];
		double Cosrow_sum[] = new double[len];

		double Hubrank_copy[] = new double[len];
		double Catrank_copy[] = new double[len];

		for (int i = 0; i < len ; i++) {
			rank[i] = 1;
			row_sum[i] = 0;
			Jacrank[i] = 1;
			Jacrow_sum[i] = 0;
			Dicerank[i] = 1;
			Dicerow_sum[i] = 0;
			Cosrank[i] = 1;
			Cosrow_sum[i] = 0;

		}
		double dampingFactor = 0.85;
		double errorRate = 0.005;


		for (int i = 0; i < len ; i++) {

			for (int j = 0; j < len ; j++) {
				row_sum[i] = row_sum[i] + Similarity[i][j];
				Jacrow_sum[i] = Jacrow_sum[i] + JacSimilarity[i][j];
				Dicerow_sum[i] = Dicerow_sum[i] + DiceSimilarity[i][j];
				Cosrow_sum[i] = Cosrow_sum[i] + CosSimilarity[i][j];
				//rank[i] = 
			}
		}
		//calculation of summation array

		for (int i = 0; i < len ; i++) {
			double sum = 0.0;
			double Jacsum = 0.0;
			double Dicesum = 0.0;
			double Cossum = 0.0;
			for (int j = 0; j < len ;j++) {

				if(i==j) {
					continue;
				}
				if(row_sum[j] != 0){
					sum = sum + ((rank[j]) * (Similarity[j][i]/row_sum[j]));
				}
				if(Jacrow_sum[j] != 0){
					Jacsum = Jacsum + ((Jacrank[j]) * (JacSimilarity[j][i]/Jacrow_sum[j]));
				}
				if(Dicerow_sum[j] != 0){
					Dicesum = Dicesum + ((Dicerank[j]) * (DiceSimilarity[j][i]/Dicerow_sum[j]));
				}
				if(Cosrow_sum[j] != 0){
					Cossum = Cossum + ((Cosrank[j]) * (CosSimilarity[j][i]/Cosrow_sum[j]));
				}
			}
			rank[i] = (1-dampingFactor) + (dampingFactor * sum);
			Jacrank[i] = (1-dampingFactor) + (dampingFactor * Jacsum);
			Dicerank[i] = (1-dampingFactor) + (dampingFactor * Dicesum);
			Cosrank[i] = (1-dampingFactor) + (dampingFactor * Cossum);
			//System.out.println("SUMMARY is LINE " + i + " " + sen[i].text);
			//System.out.println("Value of Ranks::  " + rank[i]);
		}
		for( int i = 0; i < len; i++){
			rank_copy[i] = rank[i];
			Jacrank_copy[i] = Jacrank[i];
			Dicerank_copy[i] = Dicerank[i];
			Cosrank_copy[i] = Cosrank[i];
			Catrank_copy[i] = CatUnsyncrank1[i];
			Hubrank_copy[i] = HubUnsyncrank1[i];
		}

		int number  = (int)((1.0 /5) * len);
		//System.out.println("NUMBER::" + len);
		double t = 0.0;
		for( int i = 0; i < len; i++){
			for(int j = 1; j < (len-i); j++){
				if(rank_copy[j-1] < rank_copy[j]){
					t = rank_copy[j-1];
					rank_copy[j-1]=rank_copy[j];
					rank_copy[j]=t;
				}
			}
		}


		double Jact = 0.0;
		for( int i = 0; i < len; i++){
			for(int j = 1; j < (len-i); j++){
				if(Jacrank_copy[j-1] < Jacrank_copy[j]){
					Jact = Jacrank_copy[j-1];
					Jacrank_copy[j-1]=Jacrank_copy[j];
					Jacrank_copy[j]=Jact;
				}
			}
		}

		double Hubt = 0.0;
		for( int i = 0; i < len; i++){
			for(int j = 1; j < (len-i); j++){
				if(Hubrank_copy[j-1] < Hubrank_copy[j]){
					Hubt = Hubrank_copy[j-1];
					Hubrank_copy[j-1]=Hubrank_copy[j];
					Hubrank_copy[j]=Hubt;
				}
			}
		}

		double Catt = 0.0;
		for( int i = 0; i < len; i++){
			for(int j = 1; j < (len-i); j++){
				if(Catrank_copy[j-1] < Catrank_copy[j]){
					Catt = Catrank_copy[j-1];
					Catrank_copy[j-1]=Catrank_copy[j];
					Catrank_copy[j]=Catt;
				}
			}
		}

		double Dicet = 0.0;
		for( int i = 0; i < len; i++){
			for(int j = 1; j < (len-i); j++){
				if(Dicerank_copy[j-1] < Dicerank_copy[j]){
					Dicet = Dicerank_copy[j-1];
					Dicerank_copy[j-1]=Dicerank_copy[j];
					Dicerank_copy[j]=Dicet;
				}
			}
		}

		double Cost = 0.0;
		for( int i = 0; i < len; i++){
			for(int j = 1; j < (len-i); j++){
				if(Cosrank_copy[j-1] < Cosrank_copy[j]){
					Cost = Cosrank_copy[j-1];
					Cosrank_copy[j-1]=Cosrank_copy[j];
					Cosrank_copy[j]=Cost;
				}
			}
		}



		String Summary = "";
		String JacSummary = "";
		String DiceSummary = "";
		String CosSummary = "";
		String HubSummary = "";
		String CatSummary = "";
		int Radaarr[] = new int[number];

		//System.out.println("---------------------------------------FINAL SUMMARY By Rada------------------------------------------------");

		for (int i = 0 ; i < number; i++) {

			for (int j = 0 ; j< len ; j++){
				if(rank_copy[i] == rank[j]) {
					Radaarr[i] = j;

				}
			}
		}
		int arrt = 0;
		for( int i = 0; i < Radaarr.length; i++){
			for(int j = 1; j < (Radaarr.length-i); j++){
				if(Radaarr[j-1] > Radaarr[j]){
					arrt =  Radaarr[j-1];
					Radaarr[j-1]=Radaarr[j];
					Radaarr[j]=arrt;
				}
			}
		}



		for(int h= 0; h<number; h++) {
			//	System.out.println("SUMMARY is LINE :: " + sen[Radaarr[h]].text);
			Summary = Summary + sen[Radaarr[h]].text + " ";
		}


		int Jacarr[] = new int[number];

		//System.out.println("---------------------------------------FINAL SUMMARY By Jaccard------------------------------------------------");
		for (int i = 0 ; i < number; i++) {

			for (int j = 0 ; j< len ; j++){
				if(Jacrank_copy[i] == Jacrank[j]) {
					Jacarr[i] = j;

				}
			}


		}

		int Jacarrt = 0;
		for( int i = 0; i < Jacarr.length; i++){
			for(int j = 1; j < (Jacarr.length-i); j++){
				if(Jacarr[j-1] > Jacarr[j]){
					Jacarrt =  Jacarr[j-1];
					Jacarr[j-1]=Jacarr[j];
					Jacarr[j]=Jacarrt;
				}
			}
		}
		for(int h= 0; h<number; h++) {
			//System.out.println("SUMMARY is LINE Jac:: " + sen[Jacarr[h]].text);
			JacSummary = JacSummary + sen[Jacarr[h]].text + " ";
		}
		//System.out.println("---------------------------------------FINAL SUMMARY By Dice------------------------------------------------");
		int Dicearr[] = new int[number];

		for (int i = 0 ; i < number; i++) {

			for (int j = 0 ; j< len ; j++){
				if(Dicerank_copy[i] == Dicerank[j]) {
					Dicearr[i] = j;

				}
			}


		}


		int Dicearrt = 0;
		for( int i = 0; i < Dicearr.length; i++){
			for(int j = 1; j < (Dicearr.length-i); j++){
				if(Dicearr[j-1] > Dicearr[j]){
					Dicearrt =  Dicearr[j-1];
					Dicearr[j-1]=Dicearr[j];
					Dicearr[j]=Dicearrt;
				}
			}
		}

		for(int h= 0; h<number; h++) {
			//System.out.println("SUMMARY is LINE DICE:: " + sen[Dicearr[h]].text);
			DiceSummary = DiceSummary + sen[Dicearr[h]].text + " ";
		}

		//System.out.println("Summmary String::" + Summary);
		int Cosarr[] = new int[number];

		//System.out.println("---------------------------------------FINAL SUMMARY By Cos------------------------------------------------");

		for (int i = 0 ; i < number; i++) {

			for (int j = 0 ; j< len ; j++){
				if(Cosrank_copy[i] == Cosrank[j]) {
					Cosarr[i] = j;

				}
			}
		}
		int Cosarrt = 0;
		for( int i = 0; i < Cosarr.length; i++){
			for(int j = 1; j < (Cosarr.length-i); j++){
				if(Cosarr[j-1] > Cosarr[j]){
					Cosarrt =  Cosarr[j-1];
					Cosarr[j-1]=Cosarr[j];
					Cosarr[j]=Cosarrt;
				}
			}
		}



		for(int h= 0; h<number; h++) {
			//	System.out.println("SUMMARY is LINE :: " + sen[Cosarr[h]].text);
			CosSummary = CosSummary + sen[Cosarr[h]].text + " ";
		}
		//System.out.println("---------------------------------------FINAL SUMMARY By Cat------------------------------------------------");
		int Catarr[] = new int[number];
		for (int i = 0 ; i < number; i++) {
			for (int j = 0 ; j< len ; j++){
				if(Catrank_copy[i] == CatUnsyncrank1[j]) {
					Catarr[i] = j;
				}
			}
		}
		int Catarrt = 0;
		for( int i = 0; i < Catarr.length; i++){
			for(int j = 1; j < (Catarr.length-i); j++){
				if(Catarr[j-1] > Catarr[j]){
					Catarrt =  Catarr[j-1];
					Catarr[j-1]=Catarr[j];
					Catarr[j]=Catarrt;
				}
			}
		}
		for(int h= 0; h<number; h++) {
			CatSummary = CatSummary + sen[Catarr[h]].text + " ";
		}
		//System.out.println("---------------------------------------FINAL SUMMARY By Hub------------------------------------------------");
		int Hubarr[] = new int[number];
		for (int i = 0 ; i < number; i++) {

			for (int j = 0 ; j< len ; j++){
				if(Hubrank_copy[i] == HubUnsyncrank1[j]) {
					Hubarr[i] = j;

				}
			}
		}
		int Hubarrt = 0;
		for( int i = 0; i < Hubarr.length; i++){
			for(int j = 1; j < (Hubarr.length-i); j++){
				if(Hubarr[j-1] > Hubarr[j]){
					Hubarrt =  Hubarr[j-1];
					Hubarr[j-1]=Hubarr[j];
					Hubarr[j]=Hubarrt;
				}
			}
		}



		for(int h= 0; h<number; h++) {
			//	System.out.println("SUMMARY is LINE :: " + sen[Hubarr[h]].text);
			HubSummary = HubSummary + sen[Hubarr[h]].text + " ";
		}

		String PackSum[] = new String[6];
		PackSum[0] = Summary;
		PackSum[1] = JacSummary;
		PackSum[2] = DiceSummary;
		PackSum[3] = CosSummary;
		PackSum[4] = HubSummary;
		PackSum[5] = CatSummary;
		return CosSummary;

		//fine Tuning the ranks of vertices by doing a Text Rank on vertices
	}


	private static double FindError(double[] new_values,
			double[] prev_values) {
		//double DiffHubUnsync[] = new double[len];
		double err = 0.0;
		double temp = 0.0;
		for (int p = 0; p<new_values.length && p < prev_values.length; p++){
			temp = (new_values[p] - prev_values[p]);
			err +=  Math.pow(temp, 2); 
		}
		err = err / new_values.length;
		err = Math.sqrt(err);
		return err;
	}

	public static double[] callHub(double [] HubUnsyncrank1, double [] HubUnsyncrank2, int len, double [][] UnsymetricSimilarity){

		for (int p = 0; p<HubUnsyncrank1.length; p++){
			for (int q = 0; q<HubUnsyncrank2.length; q++){
				HubUnsyncrank2[p] =  HubUnsyncrank2[p] + (HubUnsyncrank1[q] * UnsymetricSimilarity[p][q]);
			}
		}
		return HubUnsyncrank2;
	}

	public static double[] callCat(double [] CatUnsyncrank1, double [] CatUnsyncrank2, int len, double [][] UnsymetricSimilarity) {
		for (int p = 0; p<CatUnsyncrank1.length; p++){
			for (int q = 0; q<CatUnsyncrank2.length; q++){
				CatUnsyncrank2[p] =  CatUnsyncrank2[p] + (CatUnsyncrank1[q] * UnsymetricSimilarity[q][p]);
			}
		}
		return CatUnsyncrank2;
	}

	public static double[] ranking (double[] prevrank) {
		return null;
	}

	private static String readFileAsString(String filePath)
			throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(
				new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	public static void main (final String[] args) throws Exception {

		String input_path = "C:/Automatic-Text-Summarizer-master/Automatic-Text-Summarizer-master/Data/Summaries - Multi-Doc Split/Documents/";
		String output_path = "C:/Automatic-Text-Summarizer-master/Automatic-Text-Summarizer-master/Data/Summaries - Multi-Doc Split/Gen_sum/";
		for(int i = 10; i <= 60; i++) {
			if(i!=31) {
				String dir_name = "doc";
				String num = Integer.toString(i);
				if(i < 10) {
					//System.out.println(num);
					num = "0"+num;
				}
				dir_name += num;
				System.out.println(dir_name);
				String inp_dir, op_dir;
				inp_dir = input_path+dir_name;
				//System.out.println(inp_dir);
				File folder = new File(inp_dir);
				File[] listOfFiles = folder.listFiles();
				ArrayList <String> docs = new ArrayList<String>();
				for (File file: listOfFiles) {
					String file_name = file.getAbsolutePath();
					if(!file.getName().startsWith(".")) {
						docs.add(readFileAsString(file_name));
						System.out.println(file.getAbsolutePath());
					}
				}
				String summ_combined = "";
				for(String doc_text: docs) {
					final TextRank tr = new TextRank();
					tr.mainStart(doc_text);
					String s = calls();
					summ_combined += s+"\n";
					//System.out.println(s);
				}
				final TextRank tr = new TextRank();
				tr.mainStart(summ_combined);
				String final_sum = calls();	
				System.out.println(final_sum);
				op_dir = output_path+dir_name;
				new File(op_dir).mkdir();
				String op_file = op_dir+"/"+dir_name+".txt";
				FileWriter fstream = new FileWriter(op_file);
				System.out.println(op_file);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(final_sum);
				out.close();
			}
		}
	}
}