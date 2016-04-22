package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.CachingCollector;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import preprocess.Question;
import util.Constants;

public class KNNModel extends CooccurrenceModel{

	public KNNModel() throws IOException {
		super();
		
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		
		KNNModel knn=new KNNModel();
		Question ques=new Question("","Type mismatch for Class Generics","<p>I have the following code that won't compile and although there is a way to make it compile I want to understand why it isn't compiling. Can someone enlighten me as to specifically why I get the error message I will post at the end please? By casting Test.class to Class<T> this compiles with an Unchecked cast warning and runs perfectly. </p>","");
		knn.setQues(ques);
		
		knn.getTags();
		
		
	}
	
public List<Map.Entry<String,Double>> getTags() throws IOException, ParseException {
		
		List<Map.Entry<String,Double>>candTags=super.getTags();
		HashMap<String,Double> tagProbs=new HashMap<String,Double>();
		
		for(Map.Entry<String, Double> me : candTags)
		{
			System.out.println(me.getKey()+" "+me.getValue());
			double distance=coSineDistance(me.getKey(),quesWords);
			//double distance=euclidDistance(me.getKey(),qWords);
			tagProbs.put(me.getKey(), distance);
		}
	
		List<Map.Entry<String,Double>> sorted = new LinkedList<Map.Entry<String,Double>>(tagProbs.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<String,Double>>() {
			public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		System.out.println("---");
		for(Entry<String, Double> me:sorted){
			System.out.println(me.getKey()+" "+me.getValue());
		}
		return sorted;
	}

	public double coSineDistance(String key,HashMap<String,Integer> qWords) throws IOException
	{
		double numer=0.0,denom1=0.0,denom2=0.0;
		for(Entry<String,Integer> word:qWords.entrySet())
		{
			double tagwordCount=getCount(key,word.getKey());
			numer+=tagwordCount*word.getValue();
			denom1+=tagwordCount*tagwordCount;
			denom2+=word.getValue()*word.getValue();
			//System.out.print(word.getKey()+" "+word.getValue()+" ");
		}
		double simmi=numer/(Math.sqrt(denom1)*Math.sqrt(denom2));
		return simmi;
	}
	
	public double euclidDistance(String key,HashMap<String,Integer> qWords) throws IOException
	{
		double dist=0.0;
		double tagCount=getCount("TTaagg",key);
		//double wordCount=qWords.size();
		for(Entry<String,Integer> word:qWords.entrySet())
		{
			//Normalize and find euclid dist
			double tagwordCount=getCount(key,word.getKey());
			double wordCount=getCount("WWoorrdd",word.getKey());
			double diff=(tagwordCount/wordCount)-(word.getValue()/wordCount);
			dist+=Math.pow(diff,2);

		}
		dist=Math.sqrt(dist);
		return dist;
	}



}
