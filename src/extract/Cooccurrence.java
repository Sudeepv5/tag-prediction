package extract;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import preprocess.Stemmer;
import preprocess.Stopper;
import util.Constants;

public class Cooccurrence {

	public static class WordTagMapper extends Mapper<Object, Text, Text, IntWritable>{

		private final static IntWritable one = new IntWritable(1);

		Analyzer analyzer;
		Stopper st;
		Stemmer stmr;

		public void setup(Context ctx) throws IOException{
			st=new Stopper("models/stop");
			stmr=new Stemmer();
			analyzer = new WDFAnalyzer();
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


		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			int len=value.toString().length();
			String line =value.toString().substring(Constants.START.length(),len-Constants.END.length());
			//System.out.println(line);
			String[] ques=line.split(Constants.SPACE);

			String body=ques[1]+ques[2];
			String[] tags=ques[3].substring(1,ques[3].length()-1).split("><");

			ArrayList<String> words=new ArrayList<String>();

			TokenStream ts = analyzer.tokenStream("myfield", new StringReader(body));

			CharTermAttribute ta = ts.addAttribute(CharTermAttribute.class);

			try{
				ts.reset(); 
				while (ts.incrementToken()) {
					String word=ta.toString().toLowerCase();

					if(!st.isStopWord(word) && word.length()>1){
						word=stmr.stem(word);
						words.add(word);
						context.write(new Text("WWoorrdd"+Constants.SPACE+word), one);
					}
				}
				ts.end();
			}
			finally
			{
				ts.close();
			}

			for(String tag:tags)
			{
				context.write(new Text("TTaagg"+Constants.SPACE+tag), one);
				for(String word:words)
				{
					context.write(new Text(tag+Constants.SPACE+word), one);
				}
			}

		}


	}


	public static class WordTagReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Tag-Word");
		job.setJarByClass(Cooccurrence.class);
		job.setMapperClass(WordTagMapper.class);
		//job.setCombinerClass(FlightStatsReducer.class);
		job.setReducerClass(WordTagReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path("data/Questions-Test"));
		FileOutputFormat.setOutputPath(job, new Path("data/cooccurrence/"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
