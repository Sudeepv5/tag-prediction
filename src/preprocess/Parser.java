package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import util.Constants;


public class Parser {

	public static int q,t=0;

	public static void main(String[] args) 
	{

		try
		{
			System.out.println(System.currentTimeMillis());
			parseQuestions();
			System.out.println(System.currentTimeMillis());
			parseTags();
			System.out.println(System.currentTimeMillis());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}


	public static void parseQuestions() throws XMLStreamException, IOException
	{
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		//inputFactory.setProperty("javax.xml.stream.isCoalescing", True);
		InputStream in = new FileInputStream(Constants.POSTS_FILE);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

		FileWriter fwTrain = new FileWriter(Constants.QUES_TRAIN); 
		BufferedWriter bwTrain = new BufferedWriter(fwTrain);

		FileWriter fwTest = new FileWriter(Constants.QUES_TEST); 
		BufferedWriter bwTest = new BufferedWriter(fwTest);

		FileWriter fwDev = new FileWriter(Constants.QUES_DEV); 
		BufferedWriter bwDev = new BufferedWriter(fwDev);

		FileWriter fwTags = new FileWriter(Constants.RW_TAG_FILE); 
		BufferedWriter bwTags = new BufferedWriter(fwTags);
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			//reach the start of an question
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals("row")) {
					//System.out.println("ques");
					Question ques=new Question();
					Iterator<Attribute> attributes = startElement.getAttributes();

					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						if (attribute.getName().toString().equals("PostTypeId")) {
							if(!attribute.getValue().equals("1"))
								break;
						}
						if (attribute.getName().toString().equals("CreationDate")) {
							String year=attribute.getValue().substring(0,4);
							if(!year.equals("2014") && !year.equals("2015") && !year.equals("2016"))
								break;
							ques.setDate(attribute.getValue().trim());
						}

						if (attribute.getName().toString().equals("Body")) {
							ques.setBody(attribute.getValue().trim());
						}
						if (attribute.getName().toString().equals("Title")) {
							ques.setTitle(attribute.getValue().trim());
						}
						if (attribute.getName().toString().equals("Tags")) {
							ques.setTags(attribute.getValue().trim());
						}
					}
					//System.out.println(ques.getDate()+"\t"+ques.getTitle()+"\t"+ques.getBody()+"\t"+ques.getTags());
					if(q<1000)
						write(ques,bwDev,bwTags);
					else if(q<1131000)
						write(ques,bwTest,bwTags);
					else
						write(ques,bwTrain,bwTags);
				}
			}
		}
		bwDev.close();
		bwTest.close();
		bwTrain.close();
		bwTags.close();
		System.out.println("Questions done: "+q);
	}

	private static void write(Question ques, BufferedWriter bw,BufferedWriter bw1) throws IOException {
		if(ques.isValid()){
			q++;
			if(q%5000==0)
				System.out.println("wrote "+q);
			ques.parseBody();
			bw.write(Constants.START);
			bw.write(ques.getDate()+Constants.SPACE+ques.getTitle()+Constants.SPACE+ques.getBody()+Constants.SPACE+ques.getTags()+Constants.SPACE+ques.getCode());
			//bw.write(ques.getDate()+","+ques.getTitle()+","+ques.getBody()+","+ques.getTags()+","+ques.getCode());
			bw.write(Constants.END);
			bw.newLine();

			bw1.write(ques.getTags());
			bw1.newLine();
		}
	}


	private static void parseTags() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(Constants.RW_TAG_FILE));
		String line;
		HashMap<String,Integer> allTags=new HashMap<String,Integer>();
		while ((line = br.readLine()) != null) {
			String tline=line.substring(1,line.length()-1);
			String[] tags=tline.split("><");
			for(String tag:tags){
				if(allTags.containsKey(tag))
					allTags.put(tag, allTags.get(tag)+1);
				else
					allTags.put(tag, 1);
			}
		}
		br.close();

		List<Map.Entry<String,Integer>> sorted = new LinkedList<Map.Entry<String,Integer>>(allTags.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<String,Integer>>() {
			public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});


		FileWriter fw = new FileWriter(Constants.TAG_FILE); 
		BufferedWriter bw = new BufferedWriter(fw);

		for(Map.Entry<String,Integer> tag: sorted)
		{
			t++;
			bw.write(tag.getKey()+","+tag.getValue());
			bw.newLine();
		}
		bw.close();
		System.out.println("Tags done: "+t);
	}
}

