package multidocument;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;


import opennlp.tools.lang.english.SentenceDetector;
import spiaotools.SentParDetector;

public class CosineSimilarityTFIDF {

	public static double dotp(Collection<Double> vectorOne, Collection<Double> vectorTwo) throws Exception {
		return dotp(vectorOne.toArray(new Double[0]), vectorTwo.toArray(new Double[0]));
	}

	public static double dotp(Double[] vectorOne, Double[] vectorTwo) throws Exception {
		double dotProduct = 0;
		//if(vectorOne.length != vectorTwo.length){
			//throw new Exception(
			//		"Input Vectors do not have the same number of dimensions.");
		//}
		int length = vectorTwo.length;
		if(vectorOne.length < vectorTwo.length)
			length = vectorOne.length;
		
		for(int i = 0; i < length; i++){
			dotProduct += (vectorOne[i] * vectorTwo[i]);
		}
		//System.out.println("DotProduct = " + dotProduct);
		return dotProduct;	
	}
	
	public static double magnitude(Collection<Double> vector){
		return magnitude(vector.toArray(new Double[0]));
	}
	
	public static double magnitude(Double[] vector){
		double magnitude = 0;
		for(int i = 0; i < vector.length; i++){
			magnitude += Math.pow(vector[i], 2);
		}
		return Math.sqrt(magnitude);
	}
	
	public static Collection<String> sentenceToWordSet(String sentence){
		Collection<String> charSet = new HashSet<String>();
		String myDelimiter = " ";
		String sentArray[] = sentence.split("(\\Q"+myDelimiter+"\\E)+");
		for(String word : sentArray){
			//System.out.println(word);
			charSet.add(word);
		}
		return charSet;
	}
	
	public static Collection<String> uniqueWords(Collection<String> vector){
		Collection<String> uniqueSet = new HashSet<String>();
		for(String c : vector){
			if(!uniqueSet.contains(c)){
				uniqueSet.add(c);
			}
		}
		return uniqueSet;
	}
	
	public static Collection<String> union(String sentence1, String sentence2){
		Collection<String> mergedVector = new TreeSet<String>();
		mergedVector.addAll(sentenceToWordSet(sentence1));
		mergedVector.addAll(sentenceToWordSet(sentence2));
		return uniqueWords(mergedVector);
	}
	
	public Collection<Double> createFrequencyOfOccurrenceVector(String sentence, int numofSenteces, String doc){
		Collection<Double> occurrenceVector = new Vector<Double>();
		String myDelimiter = " ";
		String sentArray[] = sentence.split("(\\Q"+myDelimiter+"\\E)+");
		for(String word : sentArray){
			occurrenceVector.add((Double)CalculateTFIDF(sentence, word,numofSenteces, doc));
		}
		return occurrenceVector;
	}

	private static double CalculateTFIDF(String sentence, String word, int numofSenteces, String doc)
	{
		double TF = countWords(sentence, word) / countTotalWords(sentence);  // Count of Word in a sentence / Number of words in a sentence
		double IDF = Math.log(numofSenteces / countSentenceswithWord(word, doc)) ; // Total number of sentences / Number of sentences in which the word appear
		//System.out.println("TF- IDF for word -> " + word + " is " + TF*IDF);
		return TF*IDF;
	}
	
	private static double countSentenceswithWord(String word, String text)
	{
		double count = 0;
		String sentArray[] = splitParagraph(text);
		
		for ( String sent_text : sentArray) {
		//	System.out.println("Sentences :: " + sent_text);
			if ( sent_text.indexOf(word) > -1 ) {
				count++;
		}}	
			//System.out.println(lang);
		//System.out.println("Count of Sentences with word -> " + word + " is " + count);
		return count;
	}
	
	private static String[] splitParagraph (final String text)
	{
		SentParDetector splitter_en = new SentParDetector();
		return splitter_en.markupRawText(2, text).split("\\n");
	}

	private static int countWords(String sentence, String word){
		int count = 0;
		String myDelimiter = " ";
		String sentArray[] = sentence.split("(\\Q"+myDelimiter+"\\E)+");
		for(String s : sentArray){
			if(s.equals(word)){
				count++;
			}
		}
		//System.out.println("\nCount of word-> " +  word + " in a sentence is " + count );
		return count;
	}
	
	private static double countTotalWords(String sentence){
		String myDelimiter = " ";
		String sentArray[] = sentence.split("(\\Q"+myDelimiter+"\\E)+");
		//System.out.println("Total number of words in a sentence is " +sentArray.length );
		return sentArray.length;
	}
	
	public double calculate(String sentenceOne, String sentenceTwo,Collection<Double> sentenceOneOccurrenceVector, Collection<Double> sentenceTwoOccurrenceVector) {
		//Collection<String> unionOfSentencesOneAndTwo = union(sentenceOne, sentenceTwo);
			//String arr[] = splitParagraph (doc);
			//int numofSentences = arr.length; 
				
		//Collection<Double> sentenceOneOccurrenceVector = createFrequencyOfOccurrenceVector(sentenceOne, numofSentences, doc);
		//Collection<Double> sentenceTwoOccurrenceVector = createFrequencyOfOccurrenceVector(sentenceTwo, numofSentences, doc);

		// ----------- Below is the formula for Cosine Similarity --------------------- //
		double dotProduct = 0;
		try {
			//System.out.println("Vector1 = " + sentenceOneOccurrenceVector);
			//System.out.println("Vector2 = " + sentenceTwoOccurrenceVector);
			
			dotProduct = dotp(sentenceOneOccurrenceVector, sentenceTwoOccurrenceVector);
		} catch (Exception e){
			e.printStackTrace();
			//System.out.println(sentenceOneOccurrenceVector);
			//System.out.println(sentenceTwoOccurrenceVector);
			return -2;
		}
		
		
		double vectorOneMagnitude = magnitude(sentenceOneOccurrenceVector);
		double vectorTwoMagnitude = magnitude(sentenceTwoOccurrenceVector);
		
		//System.out.println("\n docProduct = " + dotProduct);
		//System.out.println("\n vectorOneMagnitude = " + vectorOneMagnitude);
		//System.out.println("\n vectorTwoMagnitude = " + vectorTwoMagnitude);
		
		return dotProduct / (vectorOneMagnitude * vectorTwoMagnitude);
	}

}