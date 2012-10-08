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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.util.Log;

import org.ubicollab.nomad.rss.Feed;
import org.ubicollab.nomad.rss.RSSHandler;

;

public class RssReader {

	public static String feed;

	//Retrieve rss - change to - add a parameter with the address from application settings
	public static List<JSONObject> getLatestRssFeed() {
		feed = "http://199.59.148.10/statuses/user_timeline/266785863.rss";

		RSSHandler rh = new RSSHandler();
		List<Feed> articles = rh.getLatestArticles(feed);

		if (articles.size() == 0) {
			Feed empty_feed = new Feed();
			empty_feed.setTitle("No feeds or Internet available");

			articles.add(empty_feed);
		}
		return fillData(articles);
	}

	private static List<JSONObject> fillData(List<Feed> articles) {

		List<JSONObject> items = new ArrayList<JSONObject>();
		for (Feed article : articles) {
			JSONObject current = new JSONObject();
			try {
				buildJsonObject(article, current);
			} catch (JSONException e) {
			}
			items.add(current);
		}

		return items;
	}

	private static void buildJsonObject(Feed article, JSONObject current)
			throws JSONException {
		StringBuffer sb = new StringBuffer();
		String title = article.getTitle();

		if (article.getTitle() == "No feeds or Internet available") {
			sb.append("<BR>");
			sb.append("<B>").append("<I>").append(title).append("</I>")
					.append("</B>");
			current.put("text", Html.fromHtml(sb.toString()));
		} else {

			sb.append(title);

			current.put("text", Html.fromHtml(sb.toString()));
		}
	}
}
