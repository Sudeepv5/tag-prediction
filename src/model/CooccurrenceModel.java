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

import org.apache.hadoop.io.Text;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
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
	
	public static void main(String[] args) throws ParseException {
		
		Question ques=new Question("","Finding the dimensionality of an array in Java","Given some object o, I need to find its dimensionality (eg: for int[x][y][z] the dimensionality is 3), I figured that any appropriate method would be in the class of the object.works, but its source refers to a native method, so I'm left getting the answer from a string, rather than directly.If anyone knows a better way of doing this it would be appreciated (although more for the sake of curiosity than necessity).","");
		String[] tags={};
		
		try {
			getTags(ques);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void getTags(Question ques) throws IOException, ParseException {
		
		ques.parseBody();
		ArrayList<String> qWords=loadWordsInQues(ques);
		
		HashMap<String,Integer> allTags=loadTopTags(2000);
		HashMap<String,Double> tagProbs=new HashMap<String,Double>();
		System.out.println("strted");
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(Constants.WORD_TAG_INDEX).toPath()));
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		
		for(Map.Entry<String, Integer> me : allTags.entrySet())
		{
			double pTagGivenWord=-1;
			for(String word:qWords)
			{
				double wordCount=getCount("WWoorrdd",word,indexSearcher);
				double tagwordCount=getCount(me.getKey(),word,indexSearcher);
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
		
		for(Map.Entry<String,Double> me:sorted.subList(0, 30))
		{
			System.out.println(me.getKey()+": "+ me.getValue());
		}
	}
	
	private static double getCount(String tag,String word, IndexSearcher indexSearcher) throws IOException
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

	private static ArrayList<String> loadWordsInQues(Question ques) throws IOException {
		
		ArrayList<String> words=new ArrayList<String>();
		Analyzer analyzer=new WDFAnalyzer();
		Stopper st=new Stopper("models/stop");
		Stemmer stmr=new Stemmer();
		
		TokenStream ts = analyzer.tokenStream("myfield", new StringReader(ques.getBody()+ques.getTitle()));
		CharTermAttribute ta = ts.addAttribute(CharTermAttribute.class);

		try{
			ts.reset(); 
			while (ts.incrementToken()) {
				String word=ta.toString().toLowerCase();
				
				if(!st.isStopWord(word) && word.length()>1){
					word=stmr.stem(word);
					words.add(word);
				}
			}
			ts.end();
		}
		finally
		{
			ts.close();
			analyzer.close();
		}

		return words;
	}

	private static HashMap<String,Integer> loadTopTags(int num) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(Constants.TAG_FILE));
		String line;
		HashMap<String,Integer> allTags=new HashMap<String,Integer>();
		int count=0;
		while ((line = br.readLine()) != null) {
			if(++count==num)
				break;
			String[] tline=line.split(",");
			allTags.put(tline[0], Integer.parseInt(tline[1]));
		}
		br.close();
		return allTags;
	}

}
