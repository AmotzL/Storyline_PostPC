package com.postpc.nisha.storyline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbForStoriesHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    private static final String DB_NAME = "stories.db";
    private static final String STORIES_TABLE = "StoriesTable";
    private static final String COL0_ID = "StoryNumber";
    private static final String COL1_NAME = "Name";
    private static final String COL2_DESCRIPTION = "Description";
    private static final String COL3_LOCATION = "Location";
    private static final String COL4_USERNAME = "OwnerUserName";
    private static final String COL5_START_DATE = "StartDate";
    private static final String COL6_START_TIME = "StartTime";
    private static final String COL7_END_DATE = "EndDate";
    private static final String COL8_END_TIME = "EndTime";
    private static final String COL9_FILES = "Files";
    private static final String COL10_PASSWORD = "Password";
    private static final String COL11_OWNER_NAME = "OwnerName";
    private static final String COL12_EMAIL = "Email";

    private static final String LOCATIONS_TABLE = "LocationsTable";
    private static final String COL0_LOCATION_ID = "LocationNumber";
    private static final String COL1_STORY_NAME = "StoryName";
    private static final String COL2_LOCATION_DATE = "LocationDate";
    private static final String COL3_LAT_LOCATION = "LatLocation";
    private static final String COL4_LONG_LOCATION = "LongLocation";

    private static final String CAMERA_IMAGES_PATH_TABLE = "CameraImagesPathTable";
    private static final String COL0_CAMERA_IMAGES_PATH = "Path";

    private static final String GIF_STORY_DIRC = "DirToGif";
    private static final String COL0_STORY_NAME = "StoryName";
    private static final String COL1_GIF_DIR = "GifDir";
    private static final String COL2_IS_EMPTY = "IsEmpty";


    /**
     * constructor for the SQLite class, initiates the variable db.
     */
    DbForStoriesHelper(Context context) {
        super(context, DB_NAME, null, 1);
        db = this.getWritableDatabase();
    }


    /**
     * Called when the database is created for the first time (in Login activity).
     * create the table called STORIES_TABLE.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + STORIES_TABLE +
                " (" +
                COL0_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1_NAME + " TEXT UNIQUE, " +
                COL2_DESCRIPTION + " TEXT, " +
                COL3_LOCATION + " TEXT, " +
                COL4_USERNAME + " TEXT, " +
                COL5_START_DATE + " TEXT, " +
                COL6_START_TIME + " TEXT," +
                COL7_END_DATE + " TEXT, " +
                COL8_END_TIME + " TEXT, " +
                COL9_FILES + " TEXT, " +
                COL10_PASSWORD + " TEXT, " +
                COL11_OWNER_NAME + " TEXT, " +
                COL12_EMAIL + " TEXT" +
                ")"
        );

        db.execSQL("CREATE TABLE " + LOCATIONS_TABLE +
                " (" +
                COL0_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1_STORY_NAME + "  TEXT, " +
                COL2_LOCATION_DATE + " TEXT, " +
                COL3_LAT_LOCATION + " TEXT, " +
                COL4_LONG_LOCATION + " TEXT" +
                ")"
        );

        db.execSQL("CREATE TABLE " + CAMERA_IMAGES_PATH_TABLE +
                " (" +
                COL0_CAMERA_IMAGES_PATH + " TEXT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + GIF_STORY_DIRC +
                " (" +
                COL0_STORY_NAME + " TEXT, " +
                COL1_GIF_DIR + " TEXT, " +
                COL2_IS_EMPTY + " TEXT" +
                ")"
        );
    }


    /**
     * Called when the database needs to be upgraded. It drops the table,
     * and create it again, to change it to the new schema version of the DB.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STORIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CAMERA_IMAGES_PATH_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GIF_STORY_DIRC);
        onCreate(db);
    }


    /**
     * Called when new story is creating. Populate all the columns, even if some
     * of them gets some default values (e.g. endDate and files)
     */
    public boolean insertNewStory(String name, String description, String location, String userName,
                                  String startDate, String startTime, String file, String password, String ownerName, String email, String endDate, String endTime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1_NAME, name);
        contentValues.put(COL2_DESCRIPTION, description);
        contentValues.put(COL3_LOCATION, location);
        contentValues.put(COL4_USERNAME, userName);
        contentValues.put(COL5_START_DATE, startDate);
        contentValues.put(COL6_START_TIME, startTime);
        contentValues.put(COL7_END_DATE, endDate);
        contentValues.put(COL8_END_TIME, endTime);
        contentValues.put(COL9_FILES, file);
        contentValues.put(COL10_PASSWORD, password);
        contentValues.put(COL11_OWNER_NAME, ownerName);
        contentValues.put(COL12_EMAIL, email);
        long res = db.insert(STORIES_TABLE, null, contentValues);
        return res != -1;   //return true when the insertion was done successfully.
    }

    /**
     * insert Gif Dir to DirToGif DB
     * @return if ok
     */
    public boolean insertGifDir(String storyName, String GifDir, String isEmpty){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0_STORY_NAME, storyName);
        contentValues.put(COL1_GIF_DIR, GifDir);
        contentValues.put(COL2_IS_EMPTY, isEmpty);
        long res = db.insert(GIF_STORY_DIRC, null, contentValues);
        return res != -1;
    }


    public Cursor getStoryGifDirDetailsFromStoryName(String storyName) {
        return db.rawQuery("SELECT * FROM " + GIF_STORY_DIRC +
                        " WHERE " + COL0_STORY_NAME + " = '" + storyName + "'"
                , null);
    }

    /**
     * insert new camera images path to CAMERA_IMAGES_PATH_TABLE
     * @param path new path
     * @return if ok
     */
    public boolean insertNewPath(String path){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0_CAMERA_IMAGES_PATH, path);
        long res = db.insert(CAMERA_IMAGES_PATH_TABLE, null, contentValues);
        return res != -1;
    }

    /**
     * return a cursor to camera images path in CAMERA_IMAGES_PATH_TABLE.
     */
    public Cursor getPath(){
        return db.rawQuery("SELECT " + COL0_CAMERA_IMAGES_PATH + " FROM " +
                CAMERA_IMAGES_PATH_TABLE, null);
    }

    /**
     * return camera images path as string.
     */
    public String getPathAsString(){
        Cursor cursor = getPath();
        cursor.moveToNext();
        return cursor.getString(0);
    }

    /**
     * update the value of the camera images path
     * @param newPath new path to update to
     */
    public void updatePath (String newPath) {
        db.execSQL("UPDATE " + CAMERA_IMAGES_PATH_TABLE + " SET " + COL0_CAMERA_IMAGES_PATH + " " +
                "= '" + newPath + "';"
        );
    }

    /**
     * Copied from Danko's method.
     * Insert a new location to a story.
     */
    public boolean insertNewLocation (String name, String locationDate, String latLocation, String longLocation){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1_STORY_NAME, name);
        contentValues.put(COL2_LOCATION_DATE, locationDate);
        contentValues.put(COL3_LAT_LOCATION, latLocation);
        contentValues.put(COL4_LONG_LOCATION, longLocation);
        long res = db.insert(LOCATIONS_TABLE, null, contentValues);
        return res != -1;   //return true when the insertion was done successfully.
    }


    /**
     * Called when running story is finished. Updates the endDate according to the unique storyName.
     */
    public void updateEndDateAndTime(String dateToAdd, String storyName, String timeToAdd) {
        db.execSQL("UPDATE " + STORIES_TABLE + " SET " + COL7_END_DATE + "= '" + dateToAdd + "', "
                + COL8_END_TIME + "= '" + timeToAdd + "' WHERE " + COL1_NAME + " ='" + storyName + "';");
    }


    /**
     * Called when MyStories try to build the list of stories. It returns cursor to a table
     * with one column of all the stories that the given userName created.
     * The storyNames will be sorted from the newest to the oldest.
     */
    public Cursor getAllUserStoriesNames(String userName){
        return db.rawQuery("SELECT " + COL1_NAME + " FROM " + STORIES_TABLE + " WHERE " +
                COL4_USERNAME + " = '" + userName + "' ORDER BY " + COL0_ID + " DESC", null);
    }

    public Cursor getAllStoriesNamesAndEndDates(){
        return db.rawQuery("SELECT " + COL0_ID + ", " + COL1_NAME + ", " + COL7_END_DATE + " FROM " + STORIES_TABLE +
                " ORDER BY " + COL0_ID + " DESC", null);
    }

    public Cursor getAllStories(){
        return db.rawQuery("SELECT " + COL1_NAME + " FROM " + STORIES_TABLE, null);
    }

    /**
     * Copied from Danko's method.
     * Get all the locations of the story.
     */
    public Cursor getAllUserLocations (String storyName){
        return db.rawQuery("SELECT " + COL0_LOCATION_ID +", "+ COL3_LAT_LOCATION + ", " + COL4_LONG_LOCATION +
                                " FROM " + LOCATIONS_TABLE +
                                " WHERE " + COL1_STORY_NAME + " = '" + storyName +
                                "' ORDER BY " + COL2_LOCATION_DATE + " DESC", null);
    }

    /**
     * Copied from Danko's method.
     * Get all the locations of the story.
     */
    public Cursor getTimeOfLocation (String location_id){
        return db.rawQuery("SELECT " + COL0_LOCATION_ID +", "+ COL2_LOCATION_DATE  +
                " FROM " + LOCATIONS_TABLE +
                " WHERE " + COL0_LOCATION_ID + " = '" + location_id + "';", null);
    }


    /**
     * Called when MyStories try to build the list of stories. It returns cursor to a table
     * with one column of all the stories that the given userName created.
     * The storyNames will be sorted from the newest to the oldest.
     */
    public Cursor getUnfinishedStory(String endDate){
        return db.rawQuery("SELECT " + COL1_NAME +", "+ COL7_END_DATE + " FROM " + STORIES_TABLE
                + " WHERE " +
                        COL7_END_DATE + " = '" + endDate + "';"
                , null);
    }

    /**
     * Called from FinishActivity. It returns cursor to a table with one row and
     * all the details about the given storyName.
     */
    public Cursor getStoryDetailsFromName(String storyName){
        return db.rawQuery("SELECT * FROM " + STORIES_TABLE +
                " WHERE " + COL1_NAME + " = '" + storyName + "'"
//                + " AND " + COL7_END_DATE + " <> " + R.string.default_end_date
                , null);
    }
    public Cursor getStoryStartDateFromName(String storyName){
        return db.rawQuery("SELECT " + COL1_NAME + ", " + COL5_START_DATE + " FROM " + STORIES_TABLE +
                        " WHERE " + COL1_NAME + " = '" + storyName + "'"
                , null);
    }


    /**
     * Returns true only if the given user name and password exists in the Table.
     * false otherwise.
     */
    public boolean isLoginValid(String userName, String password) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + STORIES_TABLE + " WHERE " +
                COL4_USERNAME + " = '" + userName + "' AND " + COL10_PASSWORD +
                        " = '" + password + "';", null);
        cursor.moveToNext();
        boolean ret = cursor.getInt(0) > 0;
        cursor.close();
        return ret;
    }


    /**
     * Returns the name of the user with the given userName
     */
    public String getOwnerNameOfUser(String userName) {
        Cursor cursor = db.rawQuery("SELECT " + COL11_OWNER_NAME + " FROM " + STORIES_TABLE +
                " WHERE " + COL4_USERNAME + " = '" + userName + "';", null);
        cursor.moveToNext();   // Its enough to look at the first row.
        String ret = cursor.getString(0);
        cursor.close();
        return ret;
    }


    /**
     * Returns the password of the user with the given userName
     */
    public String getPasswordOfUser(String userName) {
        Cursor cursor = db.rawQuery("SELECT " + COL10_PASSWORD + " FROM " + STORIES_TABLE +
                " WHERE " + COL4_USERNAME + " = '" + userName + "';", null);
        cursor.moveToNext(); // Its enough to look at the first row.
        String ret = cursor.getString(0);
        cursor.close();
        return ret;
    }


    /**
     * Returns the email of the user with the given userName
     */
    public String getEmailOfUser(String userName) {
        Cursor cursor = db.rawQuery("SELECT " + COL12_EMAIL + " FROM " + STORIES_TABLE +
                " WHERE " + COL4_USERNAME + " = '" + userName + "';", null);
        cursor.moveToNext(); // Its enough to look at the first row
        String ret = cursor.getString(0);
        cursor.close();
        return ret;

    }


    /**
     * Returns true if the given user name already exists in the Table.
     * false otherwise.
     */
    public boolean isUserNameExists(String userName) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + STORIES_TABLE + " WHERE " +
                COL4_USERNAME + " = '" + userName + "';", null);
        cursor.moveToNext();
        boolean ret = cursor.getInt(0) > 0;
        cursor.close();
        return ret;
    }


    /**
     * Helper method that prints all the table values
     */
    public void prinAll(){
        Cursor all = db.rawQuery("SELECT * FROM " + STORIES_TABLE, null);
        while (all.moveToNext()){
            System.out.print(all.getInt(0) + " | ");
            System.out.print(all.getString(1) + " | ");
            System.out.print(all.getString(2) + " | ");
            System.out.print(all.getString(3) + " | ");
            System.out.print(all.getString(4) + " | ");
            System.out.print(all.getString(5) + " | ");
            System.out.print(all.getString(6) + " | ");
            System.out.print(all.getString(7) + " | ");
            System.out.print(all.getString(8) + " | ");
            System.out.print(all.getString(9) + " | ");
            System.out.println(all.getString(10));
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
        all.close();
    }

    /**
     * Helper method that prints locations
     */
    public void printLocations(){
        Cursor all = db.rawQuery("SELECT * FROM " + LOCATIONS_TABLE, null);
        while (all.moveToNext()){
            System.out.println(all.getInt(0) + " | ");
            System.out.print(all.getString(1) + " | ");
            System.out.print(all.getString(2) + " | ");
            System.out.print(all.getString(3) + " | ");
            System.out.println(all.getString(4));
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
        all.close();
    }
}
