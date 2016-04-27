package model;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.queryparser.classic.ParseException;

import preprocess.Question;
import util.Constants;

public class BM25Model extends CooccurrenceModel{

	public BM25Model() throws IOException {
		super();
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		
		BM25Model bm=new BM25Model();
		Question ques=new Question("","Chrome Extension: how to change origin in AJAX request header?","<p>I'm trying to manually set an origin in an ajax request header. In my background.js, I have thisAs you can see, the origin is changed. But when this Chrome extension get executed, the origin gets override to chrome-extension://iphajdjhoofhlpldiilkujgommcolacc and the console gives error 'Refused to set unsafe header \"origin\"'I've followed Chrome API (http://developer.chrome.com/extensions/xhr.html), and already set the permission as followsDoes anyone know how to properly set the origin in header? Thanks!.</p>","");
		bm.setQues(ques);
		
		bm.getTags();
		
		
	}
	
public List<Map.Entry<String,Double>> getTags() throws IOException, ParseException {
		
		List<Map.Entry<String,Double>> candTags=super.getTags();
		HashMap<String,Double> tagProbs=new HashMap<String,Double>();
		double avgdl=Constants.WORD_COUNT_TEST/Constants.TAG_COUNT;
		
		for(Map.Entry<String, Double> tag : candTags)
		{
			double score=0;
			for(Entry<String,Integer> word:quesWords.entrySet())
			{
				double tf=getCount(tag.getKey(),word.getKey());
				double docLen=getCount("TTeerrmm",tag.getKey());
				double qf=word.getValue();
				double df=getDocCount(word.getKey());
				//double weight=computeBM25Score(tf,Constants.TAG_COUNT,docLen,avgdl,qf,df);
				double weight=computeTfIdfScore(tf/docLen,Constants.TAG_COUNT,df);
				
				
				//if(score<pScore)
					score+=weight;
			}

			tagProbs.put(tag.getKey(), score);
		}
	
		List<Map.Entry<String,Double>> sorted = new LinkedList<Map.Entry<String,Double>>(tagProbs.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<String,Double>>() {
			public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

//		for(Map.Entry<String, Double> me:sorted){
//			System.out.println(me.getKey()+" "+me.getValue());
//		}
		candTags=candTags.subList(0, 2);
		candTags.add(sorted.get(0));
		candTags.add(sorted.get(1));
		return candTags;
	}


	public final double computeBM25Score(double tf, double numberOfDocuments, double docLength, double averageDocumentLength, double queryFrequency, double documentFrequency) {
		
	    double k_1 = 1.2d;
	    double k_3 = 8d;
	    double b=0.75d;
	    
        double K = k_1 * ((1 - b) + ((b * docLength) / averageDocumentLength));
        double weight = ( ((k_1 + 1d) * tf) / (K + tf) );	//first part
        weight = weight * (((k_3 + 1) * queryFrequency) / (k_3 + queryFrequency));	//second part
        
        // multiply the weight with idf 
        double idf = weight * Math.log((numberOfDocuments - documentFrequency + 0.5d) / (documentFrequency + 0.5d));	
        return idf;
	}
	
	public final double computeTfIdfScore(double tf, double numberOfDocuments,double documentFrequency) {
		
	    return tf*Math.log((numberOfDocuments - documentFrequency + 0.5d) / (documentFrequency + 0.5d));
	}
	
	
	

}
