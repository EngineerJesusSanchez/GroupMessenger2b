package edu.buffalo.cse.cse486586.groupmessenger2;

package edu.buffalo.cse.cse486586.groupmessenger;

        import android.content.ContentResolver;
        import android.content.ContentValues;
        import android.database.Cursor;
        import android.net.Uri;
        import android.util.Log;
        import android.view.View;
        import android.widget.TextView;
/**
 * Created by jesus on 4/28/15.
 */

public class MyContentResolver {
    private static final String TAG = OnPTestClickListener.class.getName();
    private static final int TEST_CNT = 50;
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";

    private final ContentResolver mContentResolver;
    private final Uri mUri;
    private ContentValues mContentValues;

    public MyContentResolver(ContentResolver _cr) {
        mContentResolver = _cr;
        mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
        //mContentValues = initTestValues();
    }

    /**
     * buildUri() demonstrates how to build a URI for a ContentProvider.
     *
     * @param scheme
     * @param authority
     * @return the URI
     */
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

   /* private ContentValues initValues(String key, String value){
        ContentValues cv;
            cv = new ContentValues();
            cv.put(KEY_FIELD, key);
            cv.put(VALUE_FIELD, value);
        return cv;
    }*/


    public boolean callInsert(String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_FIELD, key);
        cv.put(VALUE_FIELD, value);
        try {
            mContentResolver.insert(mUri, cv);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }

        return true;
    }

    /**
     * testQuery() uses ContentResolver.query() to retrieves values from your ContentProvider.
     * It simply queries one key at a time and verifies whether it matches any (key, value) pair
     * previously inserted by testInsert().
     *
     * Please pay extra attention to the Cursor object you return from your ContentProvider.
     * It should have two columns; the first column (KEY_FIELD) is for keys
     * and the second column (VALUE_FIELD) is values. In addition, it should include exactly
     * one row that contains a key and a value.
     *
     * @return
     */
    /*private boolean testQuery() {
        try {
            for (int i = 0; i < TEST_CNT; i++) {
                String key = (String) mContentValues[i].get(KEY_FIELD);
                String val = (String) mContentValues[i].get(VALUE_FIELD);
                Cursor resultCursor = mContentResolver.query(mUri, null, key, null, null);
                if (resultCursor == null) {
                    Log.e(TAG, "Result null");
                    throw new Exception();
                }
                int keyIndex = resultCursor.getColumnIndex(KEY_FIELD);
                int valueIndex = resultCursor.getColumnIndex(VALUE_FIELD);
                if (keyIndex == -1 || valueIndex == -1) {
                    Log.e(TAG, "Wrong columns");
                    resultCursor.close();
                    throw new Exception();
                }
                resultCursor.moveToFirst();
                if (!(resultCursor.isFirst() && resultCursor.isLast())) {
                    Log.e(TAG, "Wrong number of rows");
                    resultCursor.close();
                    throw new Exception();
                }
                String returnKey = resultCursor.getString(keyIndex);
                String returnValue = resultCursor.getString(valueIndex);
                if (!(returnKey.equals(key) && returnValue.equals(val))) {
                    Log.e(TAG, "(key, value) pairs don't match\n");
                    resultCursor.close();
                    throw new Exception();
                }
                resultCursor.close();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }*/
}
