package extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.store.FSDirectory;

import util.Constants;

public class WordTagIndexer {
	
	public static void main(String[] args) throws IOException {
		
		createIndex(Constants.COOC_MATRIX,Constants.WORD_TAG_INDEX);
		
	}

	private static void createIndex(String inputFile,String indexFile) throws IOException {
		
		Directory indexDir = FSDirectory.open(new File(indexFile).toPath());
		IndexWriterConfig config = new IndexWriterConfig( new StandardAnalyzer());
		IndexWriter indexWriter = new IndexWriter(indexDir, config);
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line;
		while ((line = br.readLine()) != null) {
			Document doc=new Document();
			//System.out.println(line);
			String[] wtline=line.split("\t");
			
		    doc.add(new StringField("wt", wtline[0], Field.Store.YES));
		    doc.add(new StringField("name", wtline[1], Field.Store.YES));
		    indexWriter.addDocument(doc);
		}
		br.close();
		
		System.out.println("index written"+ indexWriter.numDocs());
		indexWriter.close();
	}
	
	

}
