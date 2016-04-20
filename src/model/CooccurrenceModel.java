package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import preprocess.Question;
import preprocess.Stemmer;
import preprocess.Stopper;
import util.Constants;

public class CooccurrenceModel {
	
	Question ques;
	public Question getQues() {
		return ques;
	}

	public void setQues(Question ques) {
		this.ques = ques;
	}

	HashMap<String,Integer> allTags=new HashMap<String,Integer>();
	IndexSearcher indexSearcher;
	Stopper st;
	Analyzer analyzer;
	Stemmer stmr;
	
	public CooccurrenceModel() throws IOException
	{
		allTags= loadTopTags(2000);
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(Constants.WORD_TAG_INDEX).toPath()));
		indexSearcher = new IndexSearcher(reader);
		st=new Stopper("models/stop");
		analyzer=new WDFAnalyzer();
		stmr=new Stemmer();
	}
	
	private static class WDFAnalyzer extends Analyzer { 
		@Override 
		protected TokenStreamComponents createComponents(String fieldName) { 
			Tokenizer src = new LetterTokenizer(); 
			int flags = 0; 
			flags |= WordDelimiterFilter.GENERATE_WORD_PARTS; 
			flags |= WordDelimiterFilter.SPLIT_ON_CASE_CHANGE; 
			flags |= WordDelimiterFilter.SPLIT_ON_NUMERICS; 
			TokenStream tok = new WordDelimiterFilter(src, flags, null); 
			return new TokenStreamComponents(src, tok); 
		} 
	} 
	
	public static void main(String[] args) throws ParseException, IOException {
		
		Question ques=new Question("","How do I sort Lucene results by field value using a HitCollector?","I'm using the following code to execute a query in Lucene.Net How do I sort these search results based on a field?","");
		CooccurrenceModel cm=new CooccurrenceModel();
		cm.setQues(ques);
		
		try {
			cm.getTags();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public List<Map.Entry<String,Double>> getTags() throws IOException, ParseException 
	{
		ques.parseBody();
		HashMap<String,Integer> qWords=loadWordsInQues(ques);
		HashMap<String,Double> tagProbs=new HashMap<String,Double>();
		System.out.println("strted");

		for(Map.Entry<String, Integer> me : allTags.entrySet())
		{
			double pTagGivenWord=-1;
			for(Entry<String,Integer> word:qWords.entrySet())
			{
				double wordCount=getCount("WWoorrdd",word.getKey());
				double tagwordCount=getCount(me.getKey(),word.getKey());
				if(wordCount>0 && pTagGivenWord<(tagwordCount/wordCount))
				{
					pTagGivenWord=tagwordCount/wordCount;
				}
			}
			tagProbs.put(me.getKey(), pTagGivenWord);
		}
	
		List<Map.Entry<String,Double>> sorted = new LinkedList<Map.Entry<String,Double>>(tagProbs.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<String,Double>>() {
			public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		
		return sorted.subList(0, 20);
	}
	
	public double getCount(String tag,String word) throws IOException
	{
		Query query = new TermQuery(new Term("wt",tag+Constants.SPACE+word));
		TopDocs hits=indexSearcher.search(query,1);
		Document doc=new Document();
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			//Will Iterate only once
            doc = indexSearcher.doc(scoreDoc.doc);
        }
		double count=(hits.totalHits>0)?Double.parseDouble(doc.get("name")):0.0;
		return count;
	}

	public HashMap<String,Integer> loadWordsInQues(Question ques) throws IOException {
		
		HashMap<String,Integer> words=new HashMap<String,Integer>();
		
		TokenStream ts = analyzer.tokenStream("myfield", new StringReader(ques.getBody()+ques.getTitle()));
		CharTermAttribute ta = ts.addAttribute(CharTermAttribute.class);

		try{
			ts.reset(); 
			while (ts.incrementToken()) {
				String word=ta.toString().toLowerCase();
				
				if(!st.isStopWord(word) && word.length()>1){
					word=stmr.stem(word);
					if(words.containsKey(word))
						words.put(word, words.get(word)+1);
					else
						words.put(word, 1);
				}
			}
			ts.end();
		}
		finally
		{
			ts.close();
		}

		return words;
	}

	public static HashMap<String,Integer> loadTopTags(int num) {
		BufferedReader br;
		HashMap<String,Integer> allTags=new HashMap<String,Integer>();
		try 
		{
			br = new BufferedReader(new FileReader(Constants.TAG_FILE));
			String line;
			int count=0;
			while ((line = br.readLine()) != null) {
				if(++count==num)
					break;
				String[] tline=line.split(",");
				allTags.put(tline[0], Integer.parseInt(tline[1]));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allTags;
	}

}

