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

package org.ubicollab.nomad.util;

import java.io.StringWriter;
import java.util.List;

import org.ubicollab.nomad.space.Entity;
import org.ubicollab.nomad.space.Space;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;
import static org.ubicollab.nomad.util.BaseSpaceListParser.*;

public class XmlSpaceListWriter {

	public String writeXml(List<Space> spaces){

		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();

		try {
			serializer.setOutput(writer);

			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startDocument("UTF-8", true);
			
//			serializer.startTag("", "provider");
//			serializer.text("UbiNomad");
//			serializer.endTag("", "provider");
			
			/* not recognized by android (but should be..) */
			//			// indentation as 3 spaces
			//			serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "   ");
			//			// also set the line separator
			//			serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");

			serializer.startTag("", SPACE_LIST);

			for (Space current: spaces){
				serializer.startTag("", SPACE);
				serializer.attribute("", "id", current.getId());

				serializer.startTag("", CREATED);
				serializer.text(current.getCreated());
				serializer.endTag("", CREATED);

				serializer.startTag("", MODIFIED);
				serializer.text(current.getModified());
				serializer.endTag("", MODIFIED);
				
				serializer.startTag("", NAME);
				serializer.text(current.getName());
				serializer.endTag("", NAME);

				serializer.startTag("", DESCRIPTION);
				if (current.getDescription() != null) {
					serializer.text(current.getDescription());
				}
				serializer.endTag("", DESCRIPTION);

				// Parent, could be null.
				serializer.startTag("", PARENT);
				if (current.getParent() != null) {
					serializer.text(current.getParent());
				}
				serializer.endTag("", PARENT);
				
				serializer.startTag("", SHAREABLE);
				serializer.text(""+current.isShareable());
				serializer.endTag("", SHAREABLE);

				serializer.startTag("", INFORMATION);
				if (current.getInformation() != null) {
					serializer.text(current.getInformation());
				}
				serializer.endTag("", INFORMATION);
				
				serializer.startTag("", ENTITIES);
				for (Entity currentEntity: current.getEntities()){
					serializer.startTag("", ENTITY);
					
					serializer.startTag("", DESCRIPTION);
					serializer.text(currentEntity.getDescription());
					serializer.endTag("", DESCRIPTION);
					
					serializer.startTag("", TYPE);
					serializer.text(currentEntity.getType());
					serializer.endTag("", TYPE);
					
					serializer.startTag("", DATA);
					serializer.text(currentEntity.getData());
					serializer.endTag("", DATA);
					
					serializer.endTag("", ENTITY);
				}
				serializer.endTag("", ENTITIES);
				
				serializer.endTag("", SPACE);
			}
			serializer.endTag("", SPACE_LIST);
			serializer.endDocument();
			return writer.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}
