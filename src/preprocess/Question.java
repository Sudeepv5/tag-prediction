package preprocess;

import java.io.FileInputStream;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

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
    	Document doc=Jsoup.parse(body);
    	Elements pres = doc.select("pre");
    	Elements ps=doc.select("p");
    	StringBuilder sb=new StringBuilder();
	    for (Element pre : pres) {
		    sb.append(pre.text().replaceAll("[\\t\\n\\r]","")) ;
		}
	    this.code=sb.toString();
		this.body = tokenize(ps);
	}

	private String tokenize(Elements ps) {
		
	    StringBuilder sb1=new StringBuilder();
	    Stemmer s=new Stemmer();
        InputStream is;
		try {
			is = new FileInputStream("models/en-token.bin");
	        TokenizerModel model = new TokenizerModel(is);
	    	Tokenizer tokenizer = new TokenizerME(model);
			for (Element p : ps) {
				String[] tokens=tokenizer.tokenize(p.text());
				for(String token:tokens)
				{
					token=token.toLowerCase();
					if(!Stopper.isStopWord(token) && token.length()>1){
						sb1.append(s.stem(token));
						sb1.append(" ");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sb1.toString();
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
	

	private String date;
	private String body;
	private String title;
	private String code;
	private String tags;
	
	Question(String date,String body,String title,String tags)
	{
		this.date=date;
		this.body=body;
		this.title=title;
		this.tags=tags;
	}
	Question()
	{
		this.date="";
		this.body="";
		this.title="";
		this.tags="";
		this.code="";
	}
	
//	public static void main(String[] args) throws IOException  {
//	
//		Stopper st=new Stopper("models/stop");
//    	String str="<p>I have been trying numerous ways to get the email addresses entered into the form here: <a href=\"http://contest.realfamilytrips.com\" rel=\"nofollow\">http://contest.realfamilytrips.com</a> saved to a file of some sort. I have had no luck. Now I am trying to email myself with each entry and not having much luck with that either.</p>  <p>I put together a bit of PHP code (after hours of research. I'm pretty new to this end of things) that was supposed to email me each time a user inputs their email address into the form, but it isn't working.</p>  <p>This is a single-page landing page so there's not a ton of code. I'd like to share the index.html and my php script, as well as the stylesheet and functions.js files. If anyone can tell me how to get these email addresses either saved or emailed or something I would be very grateful.</p>  <p>INDEX.html:</p>  <pre><code>&lt;!DOCTYPE html&gt; &lt;html lang=\"en-US\" xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\"&gt; &lt;head&gt;     &lt;title&gt;iPad Air Giveaway&lt;/title&gt;     &lt;meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\" /&gt;     &lt;link rel=\"shortcut icon\" href=\"css/images/favicon.ico\" /&gt;     &lt;link rel=\"stylesheet\" href=\"css/style.css\" type=\"text/css\" media=\"all\" /&gt;     &lt;link rel=\"stylesheet\" href=\"css/video-js.css\" type=\"text/css\" media=\"all\" /&gt;     &lt;script src=\"js/jquery-1.7.2.min.js\" type=\"text/javascript\"&gt;&lt;/script&gt;     &lt;script src=\"js/html5.js\" type=\"text/javascript\"&gt;&lt;/script&gt;     &lt;script src=\"js/functions.js\" type=\"text/javascript\"&gt;&lt;/script&gt;     &lt;link rel=\"shortcut icon\" href=\"favicon.ico\" &gt;    &lt;link rel=\"icon\" type=\"image/gif\" href=\"animated_favicon1.gif\" &gt;  &lt;/head&gt;         &lt;body class=\"type-3\"&gt;         &lt;p&gt;&lt;/p&gt; &lt;!-- Shell --&gt;     &lt;div class=\"shell\"&gt;&lt;!-- Sidebar --&gt; &lt;aside id=\"sidebar\"&gt;         &lt;div class=\"item\"&gt;&lt;img src=\"css/images/ipadgraphic.png\" alt=\"win an ipad air\" /&gt;&lt;/div&gt;         &lt;p style=\"text-align: left;\"&gt;Enter sweepstakes to win a brand new iPad Air. Unsubscribe&lt;br /&gt; anytime. &lt;a href=\"http://realfamilytrips.com/real-family-trips-great-family-itinerary-contest-terms-conditions/\"&gt;Click here for official terms and conditions&lt;/a&gt;&lt;/p&gt;         &lt;/aside&gt; &lt;!-- END Sidebar --&gt; &lt;!-- Content --&gt;&lt;section id=\"content\"&gt;&lt;!-- Logo --&gt;         &lt;h1 id=\"logo\"&gt;&lt;a href=\"http://realfamilytrips.com\" title=\"home\"&gt;Real Family Trips&lt;/a&gt;&lt;/h1&gt;     &lt;div&gt;         &lt;h2&gt;Win an iPad Air&lt;/h2&gt;         &lt;h2 class=\"pull-up\"&gt;For The Best Family Itinerary&lt;/h2&gt;         &lt;p style=\"text-align: left;\" class=\"intro-text\"&gt;&lt;em&gt;Hey there World Traveler!&lt;/em&gt;&lt;/p&gt;         &lt;p style=\"text-align: left;\" class=\"intro-text\"&gt;&lt;em&gt;Want to win an &lt;span class=\"largetext\"&gt;iPad Air&lt;/span&gt; in a contest for the best family itinerary? Start by answering the question below. You can also share this with others, so more families can learn how to travel with their kids for the best experience!&lt;span size=\"-1\"&gt;&lt;a href=\"http://realfamilytrips.com/real-family-trips-great-family-itinerary-contest-helpful-hints/\" style=\"text-decoration: none; border-bottom: 1px solid #095DF7;\"&gt; more details&lt;/a&gt;&lt;/span&gt;&lt;/em&gt;&lt;/p&gt; &lt;!-- Step 1 --&gt;     &lt;div class=\"steps step-1\"&gt;         &lt;h2 style=\"font-size: 26px;\"&gt;STEP 1: Answer this question&lt;/h2&gt;             &lt;form action=\"#\" method=\"post\"&gt;             &lt;div class=\"select-holder\"&gt;&lt;label&gt;Who is giving away a new 7.5\" retina display iPad Air?&lt;/label&gt;             &lt;div class=\"select-wrap\"&gt;&lt;select name=\"\"&gt; &lt;option selected=\"selected\" value=\"Select Your Answer\"&gt;Select Your Answer&lt;/option&gt; &lt;option value=\"LeadBrite, Duh!\"&gt;RealFamilyTrips, hello...&lt;/option&gt; &lt;option value=\"Lady Gaga\"&gt;My next door neighbor&lt;/option&gt; &lt;option value=\"Santa Claus\"&gt;Santa Claus&lt;/option&gt; &lt;/select&gt;&lt;/div&gt;             &lt;/div&gt;         &lt;input value=\"Submit Answer&amp;nbsp;&amp;nbsp;&amp;nbsp;\" class=\"submit-button\" type=\"submit\" /&gt;&lt;/form&gt;                 &lt;div class=\"contest-ends\"&gt;                 &lt;p&gt;&lt;span&gt;Contest Ends&lt;/span&gt;&lt;/p&gt;                 &lt;p&gt;January 31st, 2014 2:00 pm E.S.T.&lt;/p&gt;                 &lt;/div&gt;     &lt;/div&gt; &lt;!-- END Step 1 --&gt;  &lt;!-- Step 2 --&gt;     &lt;div class=\"steps step-2\"&gt;         &lt;h2 style=\"font-size: 26px;\"&gt;STEP 2: Your details&lt;/h2&gt; &amp;nbsp; &lt;form action=\"signup.php\" method=\"post\" name=\"emailaddy\"&gt;                     &lt;input type=\"text\" class=\"field\" value=\"Enter your email address\" title=\"Enter your email address\" /&gt;                     &lt;input type=\"submit\" value=\"Send\" class=\"send\" /&gt;                 &lt;/form&gt;             &lt;div class=\"contest-ends\"&gt;             &lt;p&gt;&lt;span&gt;Contest Ends&lt;/span&gt;&lt;/p&gt;             &lt;p&gt;January 31st, 2014 2:00 pm E.S.T.&lt;/p&gt;             &lt;/div&gt;     &lt;/div&gt; &lt;!-- END Step 2 --&gt;  &lt;!-- Step 3 --&gt;     &lt;div class=\"steps step-3\"&gt;         &lt;h2 style=\"font-size: 26px;\"&gt;STEP 3: Submit Itinerary&lt;/h2&gt;         &lt;a href=\"http://realfamilytrips.com/share-itineraries\"&gt; &lt;input value=\"Go!\" class=\"send\" type=\"submit\" /&gt; &lt;/a&gt;             &lt;div class=\"contest-ends\"&gt;             &lt;p&gt;&lt;span&gt;Contest Ends&lt;/span&gt;&lt;/p&gt;             &lt;p&gt;January 31st, 2014 2:00 pm E.S.T.&lt;/p&gt;             &lt;/div&gt;     &lt;/div&gt; &lt;!-- END Step 3 --&gt;         &lt;p class=\"inspired\"&gt;brought to you by &lt;a href=\"http://realfamilytrips.com\"&gt;Real Family Trips&lt;/a&gt;&lt;/p&gt;         &lt;/div&gt;     &lt;/section&gt;&lt;!-- END Content --&gt;     &lt;div class=\"cl\"&gt;&amp;nbsp;&lt;/div&gt; &lt;/div&gt; &lt;!-- END Shell --&gt;     &lt;p&gt;&lt;/p&gt;     &lt;/body&gt; &lt;/html&gt; </code></pre>  <p>SIGNUP.php:</p>  <pre><code>&lt;?php  /* Subject and Email variables */      $emailSubject = 'Real Family Trips Contest';     $webmaster = lifeleavesscars@gmail.com  /* Gathering Data Variables */      $emailField = $_POST['emailaddy'];      $body = &lt;&lt;&lt;EOD &lt;br&gt;&lt;hr&gt;&lt;br&gt; Email address: $email &lt;br&gt; EOD;      $headers = \"From: $email\r\n\";     $headers .= \"Content-type: text/html\r\n\";     $success = mail ($webmaster, $emailSubject, $body, $headers);  ?&gt; </code></pre>  <p>STYLE.css:</p>  <pre><code>@import url('fonts.css');  * { margin: 0; padding: 0; outline: 0; }  body, html { height: 100%; }  body {     font-size: 18px;     line-height: 24px;     font-family: 'SegoeUI', arial, sans-serif;     color: #314b7b;     background: #fff url(images/body1.png) repeat-y center 0;     min-width: 980px; }  a { color: #0252aa; text-decoration: none; cursor: pointer; } a:hover { text-decoration: underline; } a img { border: 0; }  em { font-style: normal; font-family: 'SegoeUI-Italic'; } .largetext { font-size:150%; font-weight:bold; }  input, textarea, select { font-size: 18px; font-family: 'SegoeUI', arial, sans-serif; } textarea { overflow: auto; } select { background:none repeat scroll 0 0 transparent; border: 0;   font-family: 'SegoeUI'; font-size: 14px; line-height: 24px; width: 367px; -webkit-appearance: menulist; margin: 0 5px 0 4px; }  .cl { display: block; height: 0; font-size: 0; line-height: 0; text-indent: -4000px; clear: both; } .notext { font-size: 0; line-height: 0; text-indent: -4000px; } .left, .alignleft { float: left; display: inline; } .right, .alignright { float: right; display: inline; }  h2 { font-family: 'SegoeUI-Bold'; font-size: 30px; line-height: 36px; color: #314b7b; text-align:center; font-weight: normal; } h2 span { font-size: 36px; display: block; padding-bottom: 2px; }  .shell { width: 960px; margin: 0 auto; }  #logo { width: 289px; margin: 0 auto; font-size: 0; line-height: 0; padding-bottom: 14px; } #logo a { display: block; width: 289px; height: 62px; background: url(images/logo.png) no-repeat 0 0; text-indent: -4000px;  }  #sidebar { display: inline; float: left; width: 503px;  padding-top: 32px;  } #sidebar h2 { width: 380px; margin: 0 auto; margin-top: 30px; }  #content { display: inline; float: right; padding: 27px 13px 0 11px; width: 415px; } #content h2 { color: #010101; text-shadow: 0 1px #fff; padding-right: 6px; letter-spacing: -1px; } #content h2.pull-up { position: relative; margin-top: -6px; padding-bottom: 17px; } #content p.intro-text { width: 372px; margin: 0 auto; text-align:center; padding: 0 0 9px 24px; } #content p.inspired { font-size: 15px; text-align:center; color: #8f8f8f; text-shadow: 1px 2px 1px #f6f6f6 } #content p.inspired a { color: #8f8f8f; text-decoration: underline; } #content p.inspired a:hover { text-decoration: none; }  .steps { padding-bottom: 29px; } .steps p { font-size: 20px; line-height: 30px; color: #010101; width: auto; text-align:center; } .steps form label { display: block; padding-bottom: 23px; }  .steps form p a { color: #015978; text-decoration: underline; } .steps form p a:hover { text-decoration: none; } .steps form .select-holder { margin-top: 7px; padding: 13px 19px 20px 13px; background: #fff; } .steps .select-wrap { width: 375px; border: solid 1px #707070; padding: 2px 0 4px 0; margin-left: 1px; } .steps .submit-button { display: block;  width: 346px; height: 73px; margin: 16px auto 0; background: url(images/submit-button.png) no-repeat 0 0; border: 0; } .steps .submit-button { font-family: 'SegoeUI-Bold'; font-size: 30px; color: #212121; cursor: pointer; text-shadow: 0 1px #faf562; letter-spacing: -1px; } .steps .submit-button:hover { background-position: 0 -73px; } .steps .send { display: block;  width: 346px; height: 73px; margin: 16px auto 0; background: url(images/send.png) no-repeat 0 0; border: 0;} .steps .send { font-family: 'SegoeUI-Bold'; font-size: 30px; color: #212121; cursor: pointer; text-shadow: 0 1px #faf562; letter-spacing: -1px; } .steps .send:hover { background-position: 0 -73px; } .steps .field { margin: 25px 0 17px 6px; padding: 10px 20px 11px; line-height: 28px; width: 366px; box-shadow: inset 3px 5px 3px 3px #f5f5f5; background: #fff; border: solid 1px #d7d7d7; }  .step-2 { display: none; } .step-3 { display: none; }   .contest-ends { padding-top: 16px; } .contest-ends p { font-size: 20px; line-height: 24px; color: #8f8f8f; text-shadow: 0 1px #fff; text-align:center; }  .contest-ends p span { display: block; line-height: 18px; font-size: 15px; }  body.type-2 { background: #e1e1e1 url(images/body2.png) repeat-x 0 0; } body.type-2 #wrapper { background: url(images/wrapper.jpg) no-repeat center 0; } body.type-2 #logo { width: 365px; } body.type-2 #logo a { width: 365px; height: 62px; background: url(images/logo2.png) no-repeat 0 0; } body.type-2 .steps p { color: #fff; } body.type-2 #sidebar h2 { margin-top: 10px; width: auto; color: #2e0035; } body.type-2 #content p.intro-text { font-family: 'MuseoSlab-500'; font-size: 24px; line-height: 30px; width: auto; padding: 2px 0 16px 0; color: #2e0035; } body.type-2 .select-holder { margin-left: 2px; } body.type-2 .select-holder label { padding-bottom: 13px; } body.type-2 .submit-button { margin-top: 23px; }  body.type-3 { background: #fff url(images/body3.png) repeat-y center 0; } body.type-3 .shell { width: 874px; } body.type-3 #logo { padding-bottom: 19px; } body.type-3 #sidebar { padding-top: 29px; width: 354px; } body.type-3 #sidebar h2 { padding: 0 0 42px 0; margin-top: 9px; width: 280px; } body.type-3 #content { width: 495px; } body.type-3 #content h2.pull-up { padding-bottom: 22px; } body.type-3 #content p.intro-text { padding: 0 0 27px 10px; width: 433px; } body.type-3 .steps p { letter-spacing: 1px; } body.type-3 .select-holder { margin: 16px 30px 0 35px; padding:  13px 19px 20px 18px; } body.type-3 .submit-button { margin-top: 29px; } body.type-3 .contest-ends { padding-top: 12px; } body.type-3 .field { margin: 20px 0 17px 47px; } body.type-3 #sidebar p { font-size: 12px; line-height: 18px; color: #666666; text-align:center; padding-right: 14px; padding-left: 14px; } body.type-3 #sidebar p a { color: #666; text-decoration: underline; } body.type-3 #sidebar p a:hover { text-decoration: none; }  #mc_embed_signup{ font:14px Helvetica,Arial,sans-serif; text-align: center; } </code></pre>  <p>FUNCTIONS.js</p>  <pre><code>$(function () {     $(document).on('focusin', '.field, textarea', function () {         if (this.title == this.value) {             this.value = '';         }     }).on('focusout', '.field, textarea', function () {         if (this.value === '') {             this.value = this.title;         }     });      $(document).on('click', '.submit-button', function () {         if ($('select').find('option:selected').index() === 0) {             alert('Select Your Answer.');             return false;         }          if ($('select').find('option:selected').index() !== 1) {             alert('Your guess is wrong! Please try again.');             return false;         } else {             $('.step-1').hide();             $('.step-2').fadeIn(800);             return false;         }     });      $(document).on('submit', 'form', function (e) {         // Check email address         // if email address ok             $('.step-2').hide();             $('.step-3').fadeIn(800);             e.preventDefault();         // else              // alert             // e.preventDefault();         // end if     }); }); </code></pre>  <p>Thanks and sorry for all the code. I don't know if it was needed but after so many days of trying I thought perhaps the jquery was messing with the php? Not sure. At this point I have no idea.</p>  <p>Thanks in advance.</p>";
//    	Question q=new Question();
//    	q.setBody(str);
//    	
//		System.out.println("body: "+q.getBody());
//		System.out.println("code: "+q.getCode());
//	}
}