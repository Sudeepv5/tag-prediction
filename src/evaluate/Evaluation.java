package evaluate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.lucene.queryparser.classic.ParseException;

import model.CooccurrenceModel;
import preprocess.Question;
import util.Constants;

public class Evaluation {
	public static void main(String[] args) throws IOException, ParseException {
		
		//softEvaluate(Constants.QUES_DEV);
		singleTagEvaluate(Constants.QUES_DEV);
	}


	public static void softEvaluate(String filename) throws IOException, ParseException
	{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		int total=0,matched=0;
		CooccurrenceModel cm=new CooccurrenceModel();
		int numQues=0;
		while ((line = br.readLine()) != null) {
			
			int len=line.toString().length();
			String value =line.toString().substring(Constants.START.length(),len-Constants.END.length());

			String[] ques=value.split(Constants.SPACE);
			Question question=new Question("",ques[1],ques[2],"");
			
			cm.setQues(question);
			List<Entry<String, Double>> pred=cm.getTags();
			
			HashMap<String, Double> predicted=new HashMap<String, Double>();
			for(Entry<String, Double> me:pred){
				predicted.put(me.getKey(), me.getValue());
			}
			
			String[] tags=ques[3].substring(1,ques[3].length()-1).split("><");
			total+=tags.length;
			for(String tag:tags){
				if(predicted.containsKey(tag))
					matched++;
			}
			System.out.println((++numQues) +" m/t: "+matched/(double)total);
		}
		br.close();
		System.out.println("Completed: total "+total+"\nmatched "+matched);
	}
	
	private static void singleTagEvaluate(String filename) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		int total=0,matched=0;
		CooccurrenceModel cm=new CooccurrenceModel();
		while ((line = br.readLine()) != null) {
			
			int len=line.toString().length();
			String value =line.toString().substring(Constants.START.length(),len-Constants.END.length());

			String[] ques=value.split(Constants.SPACE);
			Question question=new Question("",ques[1],ques[2],"");
			
			cm.setQues(question);
			String predictedTag=cm.getTags().get(0).getKey();

			String[] tags=ques[3].substring(1,ques[3].length()-1).split("><");
			total+=1;
			for(String tag:tags){
				if(tag.equals(predictedTag)){
					matched++;
					break;
				}
			}
			System.out.println((total) +" m/t: "+matched/(double)total);
		}
		br.close();
		System.out.println("Completed: total "+total+"\nmatched "+matched);
	}
	
	private static void ThreeTagEvaluate(String filename) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		int total=0,matched=0;
		CooccurrenceModel cm=new CooccurrenceModel();
		while ((line = br.readLine()) != null) {
			
			int len=line.toString().length();
			String value =line.toString().substring(Constants.START.length(),len-Constants.END.length());

			String[] ques=value.split(Constants.SPACE);
			Question question=new Question("",ques[1],ques[2],"");
			
			cm.setQues(question);
			String predictedTag=cm.getTags().get(0).getKey();

			String[] tags=ques[3].substring(1,ques[3].length()-1).split("><");
			total+=1;
			for(String tag:tags){
				if(tag.equals(predictedTag)){
					matched++;
					break;
				}
			}
			System.out.println((total) +" m/t: "+matched/(double)total);
		}
		br.close();
		System.out.println("Completed: total "+total+"\nmatched "+matched);
	}
	

}
