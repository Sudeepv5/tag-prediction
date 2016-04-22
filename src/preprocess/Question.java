package preprocess;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Question{

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBody() {

		return body;
	}

	public void setBody(String body) 
	{
		this.body=body.replaceAll("[\\t\\n\\r]","");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isValid()
	{
		return !date.equals("") && !body.equals("") && 
				!title.equals("") && !tags.equals("");
	}

	public void parseBody()
	{
		Document doc=Jsoup.parse(this.body);
		Elements ps=doc.select("p");
		StringBuilder sb1=new StringBuilder();
		for (Element pre : ps) {
			sb1.append(pre.text());
		}
		this.body=sb1.toString();

		Elements pres=doc.select("pre");
		StringBuilder sb=new StringBuilder();
		for (Element pre : pres) {
			sb.append(pre.text());
		}
		this.code=sb.toString();
	}

	private String date;
	private String body;
	private String title;
	private String code;
	private String tags;

	public Question(String date,String title,String body,String tags)
	{
		this.date=date;
		this.body=body;
		this.title=title;
		this.tags=tags;
		this.code="";
	}
	public Question()
	{
		this.date="";
		this.body="";
		this.title="";
		this.tags="";
		this.code="";
	}
}