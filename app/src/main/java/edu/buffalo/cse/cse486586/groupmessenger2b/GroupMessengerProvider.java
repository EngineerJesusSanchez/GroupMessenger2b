package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */


public class GroupMessengerProvider extends ContentProvider {


    
	private DatabaseHelper dbHelper;
	private static final String DBNAME = "groupMgrDB.db";
	private static final String TABLENAME = "msgtable";
	private static final String KEY = "key";
	private SQLiteDatabase sqlDB;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that I used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
    	
    	//sqlDB = dbHelper.getWritableDatabase();
    	long id = sqlDB.insert(TABLENAME, null, values);
    	getContext().getContentResolver().notifyChange(uri, null);
        Log.v("insert", values.toString());
        return Uri.parse(TABLENAME + "/" + id);
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
    	dbHelper = new DatabaseHelper(getContext(), DBNAME, 1);
    	sqlDB = dbHelper.getWritableDatabase();
    	//sqlDB.execSQL(SQL_DROP_TABLE);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         * 
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         * 
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
    	//reference : Andriod Developers
    	SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
    	qBuilder.setTables(TABLENAME);
    	//sqlDB = dbHelper.getWritableDatabase();
    	qBuilder.appendWhere(KEY + "='" + selection + "'");
    	
    	Cursor c = qBuilder.query(sqlDB,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        Log.v("query", selection);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
    
    //Reference: http://developer.android.com/guide/topics/providers/content-provider-creating.html
    
    //Create Table String
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLENAME +" (key TEXT, value TEXT)";
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
    public static final class DatabaseHelper extends SQLiteOpenHelper{
    	
    	DatabaseHelper(Context context, String DBNAME, int version){
    		super(context, DBNAME, null, version);
    	}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			
		}
    }
}
