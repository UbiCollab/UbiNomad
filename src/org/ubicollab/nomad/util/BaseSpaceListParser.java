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

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


public abstract class BaseSpaceListParser {

    // names of the XML tags
	static final String SPACE_LIST = "spacelist";
	static final String SPACE = "space";
    static final String CREATED = "created";
    static final String MODIFIED = "modified";
    static final String NAME = "name";
    static final String DESCRIPTION = "description";
    static final String PARENT = "parent";
    static final String SHAREABLE = "shareable";
    static final String INFORMATION = "information";
    
    // Entity specific
    static final String ENTITIES = "entities";
    static final String ENTITY = "entity";
    static final String TYPE = "type";
    static final String DATA = "data";
    	
    final String filePath;

    protected BaseSpaceListParser(String filePath){
    	this.filePath = filePath;
    }

    protected InputStream getInputStream() {
        try {
        	return new FileInputStream(filePath); 
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected Reader getInputReader() {
        try {
        	return new FileReader(filePath); 
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
