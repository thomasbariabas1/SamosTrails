package gr.aegean.com.samostrails.SQLDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import gr.aegean.com.samostrails.Models.DifficultyLevel;
import gr.aegean.com.samostrails.Models.DistanceLevel;
import gr.aegean.com.samostrails.Models.KindOfTrail;
import gr.aegean.com.samostrails.Models.Trail;

import static gr.aegean.com.samostrails.SQLDb.TrailDb.TrailEntry.TABLE_NAME;

/**
 * Created by phantomas on 3/10/2017.
 */

public class TrailDb {

    private TrailDb() {
    }

    public static class TrailEntry implements BaseColumns {
        public static final String TABLE_NAME = "trail";
        public static final String COLUMN_NAME_CHILDRENFRIENDLY = "children_friendly";
        public static final String COLUMN_NAME_TRAILID = "trailid";
        public static final String COLUMN_NAME_DIFFICULTYLEVEL = "difficulty_level";
        public static final String COLUMN_NAME_DISTANCELEVEL = "distance_level";
        public static final String COLUMN_NAME_KINDOFTRAIL = "kind_of_trail";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_GEOMETRYCOLLECTION = "geometrycollection";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DOWNLIMAGE = "downlimage";
        public static final String COLUMN_NAME_CONNECTIONTOOTHERTRAILS = "connection_to_other_trails";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_MAINSIGHTS = "main_sights";
        public static final String COLUMN_NAME_OTHERTRANSPORT = "other_transport";
        public static final String COLUMN_NAME_STARTINGPOINT = "starting_point";
        public static final String COLUMN_NAME_TIPS = "tips";
        public static final String COLUMN_NAME_VIDEO = "video";
        public static final String COLUMN_NAME_EDITABLETRAIL = "editabletrail";


    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TrailEntry._ID + " INTEGER PRIMARY KEY," +
                    TrailEntry.COLUMN_NAME_CHILDRENFRIENDLY + " INTEGER," +
                    TrailEntry.COLUMN_NAME_TRAILID + " INTEGER," +
                    TrailEntry.COLUMN_NAME_DIFFICULTYLEVEL + " TEXT," +
                    TrailEntry.COLUMN_NAME_DISTANCELEVEL + " TEXT," +
                    TrailEntry.COLUMN_NAME_KINDOFTRAIL + " TEXT," +
                    TrailEntry.COLUMN_NAME_IMAGE + " TEXT," +
                    TrailEntry.COLUMN_NAME_GEOMETRYCOLLECTION + " TEXT," +
                    TrailEntry.COLUMN_NAME_DISTANCE + " REAL," +
                    TrailEntry.COLUMN_NAME_TITLE + " TEXT," +
                    TrailEntry.COLUMN_NAME_DOWNLIMAGE + " BLOB," +
                    TrailEntry.COLUMN_NAME_CONNECTIONTOOTHERTRAILS + " TEXT," +
                    TrailEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    TrailEntry.COLUMN_NAME_MAINSIGHTS + " TEXT," +
                    TrailEntry.COLUMN_NAME_OTHERTRANSPORT + " TEXT," +
                    TrailEntry.COLUMN_NAME_STARTINGPOINT + " TEXT," +
                    TrailEntry.COLUMN_NAME_TIPS + " TEXT," +
                    TrailEntry.COLUMN_NAME_VIDEO + " TEXT," +
                    TrailEntry.COLUMN_NAME_EDITABLETRAIL+ " INTEGER)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static class TrailDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "Trail.db";

        public TrailDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public static TrailDbHelper initiateDB(Context context) {
        TrailDbHelper mDbHelper = new TrailDbHelper(context);
        return mDbHelper;
    }

    public static long insertIntoDb(Trail trail, TrailDbHelper mDbHelper) {
        byte[] data = getBitmapAsByteArray(trail.getDownlImage());

// Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if(trail.isEditable()){
            trail.setTrailId(getMaxID(db)+1);
        }
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(TrailEntry.COLUMN_NAME_CHILDRENFRIENDLY, trail.getChildrenFriendly());
        values.put(TrailEntry.COLUMN_NAME_TRAILID, trail.getTrailId());
        values.put(TrailEntry.COLUMN_NAME_DIFFICULTYLEVEL, trail.getDifficultyLevel().toString());
        values.put(TrailEntry.COLUMN_NAME_DISTANCELEVEL, trail.getDistanceLevel().toString());
        values.put(TrailEntry.COLUMN_NAME_KINDOFTRAIL, trail.getKindOfTrail().toString());
        values.put(TrailEntry.COLUMN_NAME_IMAGE, trail.getImage());
        values.put(TrailEntry.COLUMN_NAME_GEOMETRYCOLLECTION, trail.getGeometryCollection());
        values.put(TrailEntry.COLUMN_NAME_DISTANCE, trail.getDistance());
        values.put(TrailEntry.COLUMN_NAME_TITLE, trail.getTitle());
        values.put(TrailEntry.COLUMN_NAME_DOWNLIMAGE, data);
        values.put(TrailEntry.COLUMN_NAME_CONNECTIONTOOTHERTRAILS, trail.getConnectionToOtherTrails());
        values.put(TrailEntry.COLUMN_NAME_DESCRIPTION, trail.getDescription());
        values.put(TrailEntry.COLUMN_NAME_MAINSIGHTS, trail.getMainSights());
        values.put(TrailEntry.COLUMN_NAME_OTHERTRANSPORT, trail.getOtherTransport());
        values.put(TrailEntry.COLUMN_NAME_STARTINGPOINT, trail.getStrartingPoin());
        values.put(TrailEntry.COLUMN_NAME_TIPS, trail.getTips());
        values.put(TrailEntry.COLUMN_NAME_VIDEO, trail.getVideo());
        values.put(TrailEntry.COLUMN_NAME_EDITABLETRAIL, trail.isEditable());

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);

        db.close();
        return newRowId;
    }
    public static boolean updateDb(Trail trail, TrailDbHelper mDbHelper){
        byte[] data = getBitmapAsByteArray(trail.getDownlImage());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Log.e("ttitleindb",""+trail.getTrailId());
        ContentValues values = new ContentValues();

        values.put(TrailEntry.COLUMN_NAME_CHILDRENFRIENDLY, trail.getChildrenFriendly());
        values.put(TrailEntry.COLUMN_NAME_DIFFICULTYLEVEL, trail.getDifficultyLevel().toString());
        values.put(TrailEntry.COLUMN_NAME_DISTANCELEVEL, trail.getDistanceLevel().toString());
        values.put(TrailEntry.COLUMN_NAME_KINDOFTRAIL, trail.getKindOfTrail().toString());
        values.put(TrailEntry.COLUMN_NAME_IMAGE, trail.getImage());
        values.put(TrailEntry.COLUMN_NAME_GEOMETRYCOLLECTION, trail.getGeometryCollection());
        values.put(TrailEntry.COLUMN_NAME_DISTANCE, trail.getDistance());
        values.put(TrailEntry.COLUMN_NAME_TITLE, trail.getTitle());
        values.put(TrailEntry.COLUMN_NAME_DOWNLIMAGE, data);
        values.put(TrailEntry.COLUMN_NAME_CONNECTIONTOOTHERTRAILS, trail.getConnectionToOtherTrails());
        values.put(TrailEntry.COLUMN_NAME_DESCRIPTION, trail.getDescription());
        values.put(TrailEntry.COLUMN_NAME_MAINSIGHTS, trail.getMainSights());
        values.put(TrailEntry.COLUMN_NAME_OTHERTRANSPORT, trail.getOtherTransport());
        values.put(TrailEntry.COLUMN_NAME_STARTINGPOINT, trail.getStrartingPoin());
        values.put(TrailEntry.COLUMN_NAME_TIPS, trail.getTips());
        values.put(TrailEntry.COLUMN_NAME_VIDEO, trail.getVideo());




        /**
         * This is the normal SQL query for UPDATE
         UPDATE table_name
         SET column1=value, column2=value2,...
         WHERE some_column=some_value
         */


        int what= db.update(TABLE_NAME, values,  "trailid='"+trail.getTrailId()+"' AND editabletrail = 1", null);
        Log.e("what",""+what);
        db.close();
        return true;
    }
    public static ArrayList<Trail> readFromDb(TrailDbHelper mDbHelper) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                TrailEntry._ID,
                TrailEntry.COLUMN_NAME_CHILDRENFRIENDLY,
                TrailEntry.COLUMN_NAME_TRAILID,
                TrailEntry.COLUMN_NAME_DIFFICULTYLEVEL,
                TrailEntry.COLUMN_NAME_DISTANCELEVEL,
                TrailEntry.COLUMN_NAME_KINDOFTRAIL,
                TrailEntry.COLUMN_NAME_IMAGE,
                TrailEntry.COLUMN_NAME_GEOMETRYCOLLECTION,
                TrailEntry.COLUMN_NAME_DISTANCE,
                TrailEntry.COLUMN_NAME_TITLE,
                TrailEntry.COLUMN_NAME_DOWNLIMAGE,
                TrailEntry.COLUMN_NAME_CONNECTIONTOOTHERTRAILS,
                TrailEntry.COLUMN_NAME_DESCRIPTION,
                TrailEntry.COLUMN_NAME_MAINSIGHTS,
                TrailEntry.COLUMN_NAME_OTHERTRANSPORT,
                TrailEntry.COLUMN_NAME_STARTINGPOINT,
                TrailEntry.COLUMN_NAME_TIPS,
                TrailEntry.COLUMN_NAME_VIDEO,
                TrailEntry.COLUMN_NAME_EDITABLETRAIL,
        };

// Filter results WHERE "title" = 'My Title'
       /* String selection = TrailEntry.COLUMN_NAME_TRAILID + " = ?";
        String[] selectionArgs = {String.valueOf(TrailID)};*/

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                TrailEntry.COLUMN_NAME_TRAILID + " DESC";

        Cursor cursor = db.query(
                TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        ArrayList<Trail> itemIds = new ArrayList<>();
        Trail trail;
        while (cursor.moveToNext()) {
            trail = new Trail(cursor.getInt(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_CHILDRENFRIENDLY))>0,
                    cursor.getInt(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_TRAILID)),
                    DifficultyLevel.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_DIFFICULTYLEVEL))),
                    DistanceLevel.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_DISTANCELEVEL))),
                    KindOfTrail.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_KINDOFTRAIL))),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_IMAGE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_GEOMETRYCOLLECTION)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_DISTANCE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_CONNECTIONTOOTHERTRAILS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_MAINSIGHTS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_OTHERTRANSPORT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_STARTINGPOINT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_TIPS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_VIDEO)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_EDITABLETRAIL))>0);
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(TrailEntry.COLUMN_NAME_DOWNLIMAGE));
            Bitmap bmp = null;
            if (imageBytes != null) {
                bmp = convertByteArrayToBitmap(imageBytes);

            }
            trail.setDownlImage(bmp);
            itemIds.add(trail);
        }
        cursor.close();
        db.close();
        return itemIds;
    }

    public static boolean ifExists(Trail trail, TrailDbHelper mDbHelper) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + TrailEntry.COLUMN_NAME_TRAILID + " FROM " + TABLE_NAME + " WHERE " + TrailEntry.COLUMN_NAME_TRAILID + "= '" + trail.getTrailId() + "'";
        cursor = db.rawQuery(checkQuery, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public static boolean delete(Trail trail, TrailDbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // db.delete(TABLE_NAME,null,null);
        String checkQuery = "DELETE  FROM " + TABLE_NAME + " WHERE " + TrailEntry.COLUMN_NAME_TRAILID + "= '" + trail.getTrailId() + "' AND "+TrailEntry.COLUMN_NAME_EDITABLETRAIL + "= 0";
        db.execSQL(checkQuery);
        //db.execSQL("TRUNCATE table " + TABLE_NAME);
        db.close();


        return true;

    }
    public static boolean deleteRecord(Trail trail, TrailDbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // db.delete(TABLE_NAME,null,null);
        String checkQuery = "DELETE  FROM " + TABLE_NAME + " WHERE " + TrailEntry.COLUMN_NAME_TRAILID + "= '" + trail.getTrailId() + "' AND "+TrailEntry.COLUMN_NAME_EDITABLETRAIL + "= 1";
        db.execSQL(checkQuery);
        //db.execSQL("TRUNCATE table " + TABLE_NAME);
        db.close();


        return true;

    }

    public static void deleteAll(TrailDbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.execSQL("delete from " + TABLE_NAME);
        //db.execSQL("TRUNCATE table " + TABLE_NAME);
        db.close();
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            return outputStream.toByteArray();
        }
        return new byte[0];
    }

    private static Bitmap convertByteArrayToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    public static int getMaxID(SQLiteDatabase db) {
        int id = 0;
        final String MY_QUERY = "SELECT MAX(_id) AS _id FROM " + TABLE_NAME + "";
        Cursor mCursor = db.rawQuery(MY_QUERY, null);

        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            id = mCursor.getInt(mCursor.getColumnIndex("_id"));
        }

        return id;
    }

}
