package analyze;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import util.Constants;

public class TagAnalysis {
	
	public static void main(String[] args) throws IOException {
		countTags();
	}
	
	private static void countTags() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(Constants.RW_TAG_FILE));
		String line;
		HashMap<Integer,Integer> allTags=new HashMap<Integer,Integer>();
		allTags.put(1, 0);
		allTags.put(2, 0);
		allTags.put(3, 0);
		allTags.put(4, 0);
		allTags.put(5, 0);
		
		while ((line = br.readLine()) != null) {
			String tline=line.substring(1,line.length()-1);
			String[] tags=tline.split("><");
			int len=tags.length;
			allTags.put(len, allTags.get(len)+1);
		}
		br.close();
		
		System.out.println(allTags);
	}

}
