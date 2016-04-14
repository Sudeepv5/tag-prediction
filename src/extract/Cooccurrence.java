package extract;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import preprocess.Stopper;
import util.Constants;

public class Cooccurrence {
	
	public static class WordTagMapper extends Mapper<Object, Text, Text, IntWritable>{

		private final static IntWritable one = new IntWritable(1);
		
		private Tokenizer tokenizer;
		Stopper st;
		public void startup() throws IOException{
			st=new Stopper("models/stop");
			InputStream is = new FileInputStream("models/en-token.bin");
	        TokenizerModel model = new TokenizerModel(is);
	    	tokenizer = new TokenizerME(model);
		}

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			
			int len=value.toString().length();
			String line =value.toString().substring(Constants.START.length(),len-Constants.END.length());
			String[] ques=line.split(Constants.SPACE);
			
			String body=ques[1]+ques[2];
        	String[] tags=ques[3].substring(1,line.length()-1).split("><");
        	
        	ArrayList<String> words=new ArrayList<String>();
        	
        	String[] tokens=tokenizer.tokenize(body);
        	for(String word:tokens)
        	{
        		word=word.toLowerCase();
        		if(!Stopper.isStopWord(word) && word.length()>1){
        			words.add(word);
        			context.write(new Text(word), one);
        		}
        	}
        	
        	for(String tag:tags)
        	{
        		context.write(new Text(tag), one);
        		for(String word:words)
        		{
        			context.write(new Text(tag+"-"+word), one);
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
		FileInputFormat.addInputPath(job, new Path("data/Questions-Dev"));
		FileOutputFormat.setOutputPath(job, new Path("data/cooccurrence/"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
