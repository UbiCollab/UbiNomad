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

import java.util.ArrayList;

import org.ubicollab.nomad.space.Entity;
import org.ubicollab.nomad.space.Space;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class XmlPullSpaceListParser extends BaseSpaceListParser {
	
    public XmlPullSpaceListParser(String spaceListUrl) {
        super(spaceListUrl);
    }
    
    public ArrayList<Space> parse() {
    	
        ArrayList<Space> spaceList = null;
        ArrayList<Entity> tmpEntities = null;
        XmlPullParser parser = Xml.newPullParser();
        
        try {
            // auto-detect the encoding from the stream
        	parser.setInput(this.getInputReader());
            //parser.setInput(this.getInputStream(), null);
            int eventType = parser.getEventType();
            Space currentSpace = null;
            Entity currentEntity = null;
            
            boolean done = false;
            
            while (eventType != XmlPullParser.END_DOCUMENT && !done){
                String name = null;
                
                switch (eventType){
                
                    case XmlPullParser.START_DOCUMENT:
                        spaceList = new ArrayList<Space>();
                        break;
                        
                    case XmlPullParser.START_TAG:
                        
                    	name = parser.getName();
                    	
                    	if (name.equalsIgnoreCase(ENTITY)) {
                    		currentEntity = new Entity();
                    		
                    	} else if (currentEntity != null) {
                    		 if (name.equalsIgnoreCase(DESCRIPTION)){
                                 currentEntity.setDescription(parser.nextText());
                             } else if (name.equalsIgnoreCase(TYPE)){
                                 currentEntity.setType(parser.nextText());
                             } else if (name.equalsIgnoreCase(DATA)){
                                 currentEntity.setData(parser.nextText());
                             } 
                    		
                    	} else if (name.equalsIgnoreCase(SPACE)){
                            currentSpace = new Space();
                            tmpEntities = new ArrayList<Entity>();
                            
                        } else if (currentSpace != null){
                            if (name.equalsIgnoreCase(CREATED)){
                                currentSpace.setCreated(parser.nextText());
                            } else if (name.equalsIgnoreCase(MODIFIED)){
                                currentSpace.setModified(parser.nextText());
                            } else if (name.equalsIgnoreCase(DESCRIPTION)){
                                currentSpace.setDescription(parser.nextText());
                            } else if (name.equalsIgnoreCase(NAME)){
                                currentSpace.setName(parser.nextText());
                            } else if (name.equalsIgnoreCase(PARENT)){
                                currentSpace.setParent(parser.nextText());
                            } else if (name.equalsIgnoreCase(SHAREABLE)){
                                currentSpace.setShareable(Boolean.parseBoolean(parser.nextText()));
                            } else if (name.equalsIgnoreCase(INFORMATION)){
                                currentSpace.setInformation(parser.nextText());
                            }
                            
                            
                        }
                        break;
                        
                    case XmlPullParser.END_TAG:
                    	
                        name = parser.getName();
                        
                        if (name.equalsIgnoreCase(ENTITY) && currentEntity != null) {
                        	tmpEntities.add(currentEntity);
                        	currentEntity = null;
                        } else if (name.equalsIgnoreCase(SPACE) && currentSpace != null){
                            if (tmpEntities != null) {
                            	currentSpace.setEntities(tmpEntities);
                            }
                        	spaceList.add(currentSpace);
                            currentSpace = null;
                        } else if (name.equalsIgnoreCase(SPACE_LIST)){
                            done = true;
                        }
                        break;
                }
                
                eventType = parser.next();
                
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return spaceList;
    }
}
