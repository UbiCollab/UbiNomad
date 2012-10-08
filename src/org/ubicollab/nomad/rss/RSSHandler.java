/*
	Licensed to UbiCollab.org under one or more contributor
	license agreements.  See the NOTICE file distributed 
	with this work for additional information regarding
	copyright ownership. UbiCollab.org licenses this file
	to you under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance
	with the License. You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing,
	software distributed under the License is distributed on an
	"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	KIND, either express or implied.  See the License for the
	specific language governing permissions and limitations
	under the License.
*/

package org.ubicollab.nomad.rss;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


public class RSSHandler extends DefaultHandler {

	// Feed and Article objects to use for temporary storage
	private Feed currentArticle = new Feed();
	private List<Feed> articleList = new ArrayList<Feed>();

	// Number of articles added so far
	private int articlesAdded = 0;

	// Number of articles to download
	public static final int ARTICLES_LIMIT = 3;
	
	//Current characters being accumulated
	StringBuffer chars = new StringBuffer();

	
	/* 
	 * This method is called everytime a start element is found (an opening XML marker)
	 * here we always reset the characters StringBuffer as we are only currently interested
	 * in the the text values stored at leaf nodes
	 * 
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes atts) {
		chars = new StringBuffer();
	}




	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (localName.equalsIgnoreCase("title"))
		{
			Log.d("LOGGING RSS XML", "Setting article title: " + chars.toString());
			currentArticle.setTitle(chars.toString());

		}
		else if (localName.equalsIgnoreCase("description"))
		{
			Log.d("LOGGING RSS XML", "Setting article description: " + chars.toString());
			currentArticle.setDescription(chars.toString());
		}
		else if (localName.equalsIgnoreCase("pubDate"))
		{
			Log.d("LOGGING RSS XML", "Setting article published date: " + chars.toString());
//			currentArticle.setPubDate(chars.toString());
		}
		else if (localName.equalsIgnoreCase("encoded"))
		{
			Log.d("LOGGING RSS XML", "Setting article content: " + chars.toString());
//			currentArticle.setEncodedContent(chars.toString());
		}
		else if (localName.equalsIgnoreCase("item"))
		{

		}
		else if (localName.equalsIgnoreCase("link"))
		{
//			try {
//				Log.d("LOGGING RSS XML", "Setting article link url: " + chars.toString());
//				currentArticle.setUrl(new URL(chars.toString()));
//			} catch (MalformedURLException e) {
//				Log.e("RSA Error", e.getMessage());
//			}

		}




		// Check if looking for article, and if article is complete
		if (localName.equalsIgnoreCase("item")) {

			articleList.add(currentArticle);
			
			currentArticle = new Feed();

			// Lets check if we've hit our limit on number of articles
			articlesAdded++;
			if (articlesAdded >= ARTICLES_LIMIT)
			{
				throw new SAXException();
			}
		}
	}
	
	

	public void characters(char ch[], int start, int length) {
		chars.append(new String(ch, start, length));
	}





	//This is the entry point to the parser and creates the feed to be parsed

	public List<Feed> getLatestArticles(String feedUrl) {
		URL url = null;
		try {

			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			
			url = new URL(feedUrl);			
			
			xmlReader.setContentHandler(this);
			xmlReader.parse(new InputSource(url.openStream()));


		} catch (IOException e) {
			Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
		} catch (SAXException e) {
			Log.e("RSS Handler SAX", e.toString());
		} catch (ParserConfigurationException e) {
			Log.e("RSS Handler Parser Config", e.toString());
		}
		
		return articleList;
	}

}
