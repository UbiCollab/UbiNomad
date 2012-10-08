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

/*
 * Class Database responsible for creating a database class using SQLite
 * to keep and manage data incoming from external sources - private application data.
 *    
 * Sooner or later this class will be changed to the MySQL configuration settings
 *  
 * Author: Boris Mocialov
 */

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.ubicollab.nomad.space.Entity;
import org.ubicollab.nomad.space.Rule;
import org.ubicollab.nomad.space.Space;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class MainDB extends Exception {

	/**
	 * 
	 */
	// private static final long serialVersionUID = 1L;
	// Database Information
	private static final String DATABASE_NAME = "example.db";
	private static final int DATABASE_VERSION = 11;

	// My Spaces table columns
	private static final String Space_Table = "Myspaces";
	private static final String Space_ID = "spaceid";
	private static final String Space_CreationDate = "creationdate";
	private static final String Space_ModifiedDate = "spacemodifieddate";
	private static final String Space_Name = "name";
	private static final String Space_Description = "description";
	private static final String Space_Information = "spaceinformation";
	private static final String Space_ParentID = "parentid";
	private static final String Space_isShareable = "isshareable";

	// Current Space table
	private static final String CurrentSpace_Table = "table2";
	private static final String CurrentSpace_ID = "currentspaceid";
	private static final String CurrentSpace_Name = "currentspacename";
	private static final String CurrentSpace_Description = "currentspacedescription";
	private static final String CurrentSpace_DateUsed = "dateused";

	// Sensor table
	private static final String Sensor_Table = "table3";
	private static final String Sensor_ID = "sensorid";
	private static final String Sensor_SpaceID = "spaceid";
	private static final String Sensor_Name = "name";
	private static final String Sensor_DateRead = "dateread";

	// Log table
	private static final String Log_Table = "table4";
	private static final String Log_ID = "logid";
	private static final String Log_Space_DateUsed = "logspacedateused";
	private static final String Log_Space_Name = "logspacename";
	private static final String Log_Space_ID = "logspaceid";
	private static final String Log_Space_Description = "logspacedescription";

	// Entities table
	private static final String Entities_Table = "Entities";
	private static final String Entity_Id = "entityid";
	private static final String Entity_Space_Id = "entityspaceid";
	private static final String Entity_Description = "entitydescription";
	private static final String Entity_Type = "entitytype";
	private static final String Entity_Data = "entitydata";

	// Rules table
	private static final String Rules_Table = "Rules";
	private static final String Rule_Id = "ruleid";
	private static final String Rule_Space_Id = "rulespaceid";
	private static final String Rule_Description = "ruledescription";

	// Statistics table
	private static final String Statistics_Table = "Statistics";
	private static final String Statistics_Space_ID = "statisticsspaceid";
	private static final String Statistics_Space_Times_Used = "statisticstimesused";

	private Context context;
	private SQLiteDatabase db;

	// Insert statement definition for all tables
	// Insert into Space table
	private SQLiteStatement insertStmt_Space_table;
	private static final String INSERT_Space_table = "insert into "
			+ Space_Table
			+ "(name, description, spaceinformation, parentid, isshareable) values(?, ?, ?, ?, ?)";

	// Insert into Current Space table
	private SQLiteStatement insertStmt_CurrentSpace_table;
	private static final String INSERT_CurrentSpace_table = "INSERT INTO "
			+ CurrentSpace_Table
			+ "(currentspaceid, currentspacename, currentspacedescription) values (?, ?, ?)";

	// Insert into Sensor table
	private SQLiteStatement insertStmt_Sensor_table;
	private static final String INSERT_Sensor_table = "INSERT INTO "
			+ Sensor_Table + "(spaceid, name, dateread) values (?, ?, ?)";

	// Insert into Entities table
	private SQLiteStatement insertStmt_Entities_table;
	private static final String INSERT_Entities_table = "insert into "
			+ Entities_Table
			+ "(entityid ,entityspaceid, entitydescription, entitytype, entitydata) values(?, ?, ?, ?, ?)";

	// Insert into Rules table
	private SQLiteStatement insertStmt_Rules_table;
	private static final String INSERT_Rules_table = "insert into "
			+ Rules_Table
			+ "(ruleid ,rulespaceid, ruledescription) values(?, ?, ?)";

	// Insert into Statistics table
	private SQLiteStatement insertStmt_Statistics_table;
	private static final String INSERT_Statistics_table = "insert into "
			+ Statistics_Table + "(" + Statistics_Space_ID + ","
			+ Statistics_Space_Times_Used + ") values(?, ?)";

	// Constructor
	public MainDB(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		this.insertStmt_Space_table = this.db
				.compileStatement(INSERT_Space_table);
		this.insertStmt_CurrentSpace_table = this.db
				.compileStatement(INSERT_CurrentSpace_table);
		this.insertStmt_Sensor_table = this.db
				.compileStatement(INSERT_Sensor_table);
		this.insertStmt_Entities_table = this.db
				.compileStatement(INSERT_Entities_table);
		this.insertStmt_Rules_table = this.db
				.compileStatement(INSERT_Rules_table);
		this.insertStmt_Statistics_table = this.db
				.compileStatement(INSERT_Statistics_table);
	}

	/*
	 * Creating new space record in the database
	 */
	public Space createNewSpace(Space space) throws DatabaseExceptions {
		this.insertStmt_Space_table.bindString(1, space.getName());
		this.insertStmt_Space_table.bindString(2, space.getDescription());
		this.insertStmt_Space_table.bindString(3, space.getInformation());
		this.insertStmt_Space_table.bindString(4,
				String.valueOf(space.getParent()));
		this.insertStmt_Space_table.bindString(5,
				String.valueOf(space.isShareable()));

		// Execute the insert before the entities. In this manner space will
		// have an id which will be used by Entities table to bind space and
		// entities.
		db.beginTransaction();
		try {
			insertStmt_Space_table.executeInsert();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		// Select last row id from the Space table
		int space_id = 0;
		db.beginTransaction();
		try {
			SQLiteStatement selectLastRowIdStmt = db
					.compileStatement("SELECT last_insert_rowid() FROM "
							+ Space_Table);
			space_id = (int) selectLastRowIdStmt.simpleQueryForLong();
		} finally {
			db.endTransaction();
		}
		// Create Entities for the space
		List<Entity> entities = space.getEntities();
		if (entities != null) {
			createEntities(space_id, entities);
		}
		Space newSpace = new Space(String.valueOf(space_id),
				space.getCreated(), space.getModified(), space.getName(),
				space.getDescription(), space.getInformation(),
				space.getParent(), space.isShareable(), space.getEntities(),
				space.getRules());

		return newSpace;
	}

	// Setting Entities for the space in the database
	public void createEntities(int pos, List<Entity> entities)
			throws DatabaseExceptions {
		for (int i = 0; i < entities.size(); i++) {
			this.insertStmt_Entities_table.bindString(1, String.valueOf(i));
			this.insertStmt_Entities_table.bindString(2, String.valueOf(pos));
			this.insertStmt_Entities_table.bindString(3, entities.get(i)
					.getDescription());
			this.insertStmt_Entities_table.bindString(4, entities.get(i)
					.getType());
			this.insertStmt_Entities_table.bindString(5, entities.get(i)
					.getData());
			// Inserting entities to the Entities table referenced with the
			// space
			db.beginTransaction();
			try {
				this.insertStmt_Entities_table.executeInsert();
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
	}

	public ArrayList<Rule> createRules(String id, ArrayList<Rule> rules) {
		ArrayList<Rule> newRules;
		
		System.out.println("Database UbiNomad " + id);

		// /check if rules for this Id already exist

		boolean exists = false;

		Cursor cursor = db.query(Rules_Table, null, Rule_Space_Id + " like "
				+ "'" + id + "'", null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				if ((cursor.getCount() > 0) && cursor != null) {
					exists = true;
				}
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		if (exists) {
			for (int u = 0; u < rules.size(); u++) {
				ContentValues cv = new ContentValues();
				
				cv.put(Rule_Description, rules.get(u).getDescription());

				this.db.update(Rules_Table, cv, Rule_Space_Id + "="
						+ id+" AND " +Rule_Id + "="
						+ u, null);
			}

		} else {
			// write rules ruleid ,rulespaceid, ruledescription
			for (int i = 0; i < rules.size(); i++) {
				this.insertStmt_Rules_table.bindString(1, String.valueOf(i));
				this.insertStmt_Rules_table.bindString(2, id);
				this.insertStmt_Rules_table.bindString(3, rules.get(i).getDescription());

				this.insertStmt_Rules_table.executeInsert();
			}
		}
		newRules = getRulesBySpace(id);
		return newRules;
	}

	// Handle later, when the home screen will be finished
	// TODO: after home
	public void setCurrentSpace(Space space) {

		this.insertStmt_CurrentSpace_table.bindString(1, space.getId());
		this.insertStmt_CurrentSpace_table.bindString(2, space.getName());
		this.insertStmt_CurrentSpace_table
				.bindString(3, space.getDescription());

		this.insertStmt_CurrentSpace_table.executeInsert();
	}

	// Don't know if needed
	// TODO: take care
	public long insertIntoSensors(String spaceID, String name) {
		this.insertStmt_Sensor_table.bindString(1, spaceID);
		this.insertStmt_Sensor_table.bindString(2, name);
		this.insertStmt_Sensor_table.bindString(3,
				Long.toString(System.currentTimeMillis()));

		return this.insertStmt_Sensor_table.executeInsert();
	}

	// Insert into statistics (spaceid, space times used)
	public void insertIntoStatistics(Space space) {
		String value = null;
		Cursor cursor = this.db.query(Statistics_Table,
				new String[] { Statistics_Space_Times_Used },
				Statistics_Space_ID + "=" + space.getId(), null, null, null,
				null);
		if (cursor.moveToFirst()) {
			value = cursor.getString(0);
			System.out.println("Cursor value: " + value);
		}

		int times = 0;
		if (value != null) {
			times = Integer.parseInt(value);
		}

		ContentValues cv = new ContentValues();
		if (value != null) {
			cv.put(Statistics_Space_ID, space.getId());
			cv.put(Statistics_Space_Times_Used, times + 1);

			this.db.update(Statistics_Table, cv, Statistics_Space_ID + "="
					+ space.getId(), null);
		} else {
			this.insertStmt_Statistics_table.bindString(1, space.getId());
			this.insertStmt_Statistics_table.bindString(2, "1");

			this.insertStmt_Statistics_table.executeInsert();
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	// Updating the space
	public void updateSpace(Space space) throws DatabaseExceptions {
		int pos = Integer.parseInt(space.getId());

		ContentValues values = new ContentValues();
		// Clear the values of Content for the use of new values
		values.clear();
		// Put new values
		values.put(Space_Name, space.getName());
		values.put(Space_Description, space.getDescription());
		values.put(Space_Information, space.getInformation());
		values.put(Space_ParentID, "parent");
		values.put(Space_isShareable, "true");

		// Update the space record
		db.beginTransaction();
		try {
			db.update(Space_Table, values, Space_ID + "=" + space.getId(), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		// Update the Modified time of the Space
		db.beginTransaction();
		try {
			this.db.execSQL("update '" + Space_Table + "' set '"
					+ Space_ModifiedDate + "'=datetime('now') where "
					+ Space_ID + "=" + space.getId());
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		// Add entities if there are new
		// TODO: Check ModifySpace for how new entities are handled on update
		List<Entity> entities = space.getEntities();
		if (entities != null) {
			deleteEntities(space);
			createEntities(pos, entities);
		}
	}

	// Delete Space with it's entities
	public void deleteSpace(Space space) throws DatabaseExceptions {
		db.beginTransaction();
		try {
			this.db.delete(Space_Table, Space_ID + "=" + space.getId(), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		// Delete entities of a deleted space
		deleteEntities(space);
	}

	private void deleteEntities(Space space) {
		db.beginTransaction();
		try {
			this.db.delete(Entities_Table,
					Entity_Space_Id + "=" + space.getId(), null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	// TODO: if needed
	public void deleteAllSpaces() {
		this.db.delete(Space_Table, null, null);
	}

	// TODO: if needed
	public void deleteCurrentSpace(String id) {
		this.db.delete(CurrentSpace_Table, CurrentSpace_ID + " LIKE '%" + id
				+ "%'", null);
	}

	public void deleteAllSensors() {
		this.db.delete(Sensor_Table, null, null);
	}

	public void dropAllTables() {
		db.execSQL("DROP TABLE " + Space_Table);
		db.execSQL("DROP TABLE " + CurrentSpace_Table);
		db.execSQL("DROP TABLE " + Sensor_Table);
		db.execSQL("DROP TABLE " + Log_Table);
	}

	public int getSpace(Space space) {
		return Integer.parseInt(space.getId());
	}

	// Get id of the currently set space - used by the getCurrentSpace() method
	public String getCurrentSpaceID() {
		Cursor cursor;
		String currentSpaceId = null;
		cursor = this.db.query(CurrentSpace_Table, null, null, null, null,
				null, null);
		if (cursor.moveToFirst()) {
			currentSpaceId = cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return currentSpaceId;
	}

	// Gets currently set space from the database, if the table is empty,
	// returns null
	public Space getCurrentSpace() {

		Space current = null;
		// Retrieve the current space id with the respect to the space id
		String id = getCurrentSpaceID();

		if (id != null) {
			Cursor cursor = this.db.query(Space_Table, null, "spaceid=" + id,
					null, null, null, null);
			if (cursor.moveToFirst()) {

				current = new Space(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5), null, true,
						null, null);
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return current;
	}

	public boolean RecordExists(String id) {
		boolean exists = false;
		Cursor cursor = db.query(Statistics_Table, null, Statistics_Space_ID
				+ " like " + "'" + id + "'", null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				if ((cursor.getCount() > 0) && cursor != null) {
					exists = true;
				}
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return exists;
	}

	// Used by adapter
	public List<Space> selectAll() {
		List<Space> list = new ArrayList<Space>();

		Cursor cursor = this.db.query(Space_Table, null, null, null, null,
				null, null);
		if (cursor.moveToFirst()) {
			do {
				Space current = new Space(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), null, false,
						getEntitiesBySpace(cursor.getString(0)),
						getRulesBySpace(cursor.getString(0)));

				list.add(current);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public List<Space> getStatistics() {
		List<Space> list = new ArrayList<Space>();

		Cursor cursor = this.db.query(Statistics_Table, null, null, null, null,
				null, null);
		if (cursor.moveToFirst()) {
			do {
				Space current = new Space(cursor.getString(0), null, null,
						cursor.getString(1), null, null, null, false, null,
						null);

				list.add(current);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}
	
	public List<Space> getMostUsedStatistics() {
		List<Space> list = new ArrayList<Space>();

		Cursor cursor = this.db.query(Statistics_Table, null, null, null, null,
				null, Statistics_Space_Times_Used +" DESC");
		if (cursor.moveToFirst()) {
			do {
				Space current = new Space(cursor.getString(0), null, null,
						cursor.getString(1), null, null, null, false, null,
						null);

				list.add(current);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public int retrieveSpaceStatistics(Space space) {
		String times_used = null;

		Cursor cursor = this.db.query(Statistics_Table,
				new String[] { Statistics_Space_Times_Used },
				Statistics_Space_ID + "=" + space.getId(), null, null, null,
				null);
		if (cursor.moveToFirst()) {
			times_used = cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		if (times_used == null) {
			return 0;
		}
		return Integer.parseInt(times_used);
	}
	
	public String getSpaceNameByID(String id) {
		String name = "none";

		Cursor cursor = this.db.query(Space_Table,
				new String[] { Space_Name },
				Space_ID + "=" + id, null, null, null,
				null);
		if (cursor.moveToFirst()) {
			name = cursor.getString(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return name;
	}

	public ArrayList<Entity> getEntitiesBySpace(String id) {
		ArrayList<Entity> list = new ArrayList<Entity>();

		Cursor cursor = this.db.query(Entities_Table, null, Entity_Space_Id
				+ " like " + "'" + id + "'", null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Entity current = new Entity(cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4));

				list.add(current);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public ArrayList<Rule> getRulesBySpace(String id) {
		ArrayList<Rule> list = new ArrayList<Rule>();

		Cursor cursor = this.db.query(Rules_Table, null, Rule_Space_Id
				+ " like " + "'" + id + "'", null, null, null, null);
		Rule current = null;
		if (cursor.moveToFirst()) {
			do {
				try {
					current = new Rule(cursor.getString(1), cursor.getString(2));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				list.add(current);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public List<Space> getLog() {
		List<Space> list = new ArrayList<Space>();

		Cursor cursor = this.db.query(Log_Table, null, null, null, null, null,
				 Log_ID+ " DESC");
		if (cursor.moveToFirst()) {
			do {
				Space current = new Space(cursor.getString(1),
						cursor.getString(5), cursor.getString(3),
						cursor.getString(2), cursor.getString(4), null, null, // date
						false, null, null);

				list.add(current);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			// Create table Space with it's columns
			db.execSQL("CREATE TABLE IF NOT EXISTS " + Space_Table + " ("
					+ Space_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Space_CreationDate + " REAL, " + Space_ModifiedDate
					+ " REAL, " + Space_Name + " TEXT, " + Space_Description
					+ " TEXT, " + Space_Information + " TEXT, "
					+ Space_ParentID + " INTEGER, " + Space_isShareable
					+ " INTEGER)");
			// Create table Current space
			db.execSQL("CREATE TABLE IF NOT EXISTS " + CurrentSpace_Table
					+ " (" + CurrentSpace_ID + " TEXT, " + CurrentSpace_Name
					+ " TEXT, " + CurrentSpace_Description + " TEXT, "
					+ CurrentSpace_DateUsed + " INTEGER)");
			// Create table Sensor
			db.execSQL("CREATE TABLE IF NOT EXISTS " + Sensor_Table + " ("
					+ Sensor_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Sensor_SpaceID + " INTEGER, " + Sensor_Name + " TEXT, "
					+ Sensor_DateRead + " INTEGER)");
			// Create Log table
			db.execSQL("CREATE TABLE " + Log_Table + " (" 
					+ Log_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Log_Space_ID + " INTEGER, " + Log_Space_Name + " INTEGER, "
					+ " INTEGER, " + Log_Space_Description + " INTEGER, "
					+ Log_Space_DateUsed + " INTEGER)");
			// Create Entities table
			db.execSQL("CREATE TABLE IF NOT EXISTS " + Entities_Table + " ("
					+ Entity_Space_Id + " INTEGER, " + Entity_Id + " INTEGER, "
					+ Entity_Description + " TEXT, " + Entity_Type + " TEXT, "
					+ Entity_Data + " TEXT)");
			// Create Rules table
			db.execSQL("CREATE TABLE IF NOT EXISTS " + Rules_Table + " ("
					+ Rule_Space_Id + " INTEGER, " + Rule_Id + " INTEGER, "
					+ Rule_Description + " TEXT)");
			// Create Statistics table
			db.execSQL("CREATE TABLE IF NOT EXISTS " + Statistics_Table + " ("
					+ Statistics_Space_ID + " INTEGER, "
					+ Statistics_Space_Times_Used + " INTEGER)");

			// Triggers zone
			db.execSQL("CREATE TRIGGER insert_current_time" + " AFTER INSERT "
					+ "ON " + Space_Table + " BEGIN " + "UPDATE " + Space_Table
					+ " SET " + Space_CreationDate + " = DATETIME('NOW'), "
					+ Space_ModifiedDate + " = DATETIME('NOW') "
					+ "WHERE rowid = new.rowid;" + "END;");

			db.execSQL("CREATE TRIGGER insert_current_space_time"
					+ " AFTER INSERT " + "ON " + CurrentSpace_Table + " BEGIN "
					+ "UPDATE " + CurrentSpace_Table + " SET "
					+ CurrentSpace_DateUsed + " = DATETIME('NOW') "
					+ "WHERE rowid = 1;" + "END;");

			db.execSQL("CREATE TRIGGER delete_previous_row" + " BEFORE INSERT "
					+ "ON " + CurrentSpace_Table + " BEGIN " + "DELETE FROM "
					+ CurrentSpace_Table + " WHERE rowid = 1;" + "END;");

			db.execSQL("CREATE TRIGGER insert_into_log" + " AFTER UPDATE "
					+ "OF "
					+ CurrentSpace_DateUsed
					+ " ON "
					+ CurrentSpace_Table
					+ " BEGIN "
					+ "INSERT INTO "
					+ Log_Table
					+ "('"
					+ Log_Space_ID
					+ "','"
					+ Log_Space_Name
					+ "','"
					+ Log_Space_Description
					+ "','"
					+ Log_Space_DateUsed
					+ "')"
					+ "SELECT "
					+ CurrentSpace_ID
					+ ","
					+ CurrentSpace_Name
					+ ","
					+ CurrentSpace_Description
					+ ","
					+ CurrentSpace_DateUsed
					+ " FROM "
					+ CurrentSpace_Table
					+ ";" + "END;");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS " + Space_Table);
			db.execSQL("DROP TABLE IF EXISTS " + CurrentSpace_Table);
			db.execSQL("DROP TABLE IF EXISTS " + Sensor_Table);
			db.execSQL("DROP TABLE IF EXISTS " + Log_Table);
			db.execSQL("DROP TABLE IF EXISTS " + Entities_Table);

			db.execSQL("DROP TRIGGER IF EXISTS insert_current_time");
			db.execSQL("DROP TRIGGER IF EXISTS insert_current_space_time");
			db.execSQL("DROP TRIGGER IF EXISTS delete_previous_row");
			db.execSQL("DROP TRIGGER IF EXISTS insert_into_log");
			onCreate(db);
		}
	}
}
