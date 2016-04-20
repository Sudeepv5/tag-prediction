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
		Question ques=new Question("","How do I sort Lucene results by field value using a HitCollector?","I'm using the following code to execute a query in Lucene.Net How do I sort these search results based on a field? I had tried using TopFieldDocCollector but I got an error saying, value is too small or too large when i passed 5000 as numHits argument value. Please suggest a valid value to pass.","");
		knn.setQues(ques);
		
		knn.getTags();
		
		
	}
	
public List<Map.Entry<String,Double>> getTags() throws IOException, ParseException {
		
		ques.parseBody();
		
		HashMap<String,Integer> qWords=super.loadWordsInQues(ques);
		List<Map.Entry<String,Double>>candTags=super.getTags();
		HashMap<String,Double> tagProbs=new HashMap<String,Double>();
		
		for(Map.Entry<String, Double> me : candTags)
		{
			System.out.println(me.getKey()+" "+me.getValue());
			double numer=0,denom1=0,denom2=0;
			for(Entry<String,Integer> word:qWords.entrySet())
			{
				double tagwordCount=getCount(me.getKey(),word.getKey());
				numer+=tagwordCount*word.getValue();
				denom1+=tagwordCount*tagwordCount;
				denom2+=word.getValue()*word.getValue();
				System.out.print(word.getKey()+" "+word.getValue());
			}
			double simmi=numer/(Math.sqrt(denom1)*Math.sqrt(denom2));
			tagProbs.put(me.getKey(), simmi);
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



}
