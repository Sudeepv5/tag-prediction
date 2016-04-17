package preprocess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Stopper {
	
	public HashSet<String> words=new HashSet<String>();
	
	public Stopper(String filename) throws IOException
	{
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
        	
        	words.add(line.trim());
        }
        br.close();
	}
	
	public boolean isStopWord(String str)
	{
		return words.contains(str);
	}

}
