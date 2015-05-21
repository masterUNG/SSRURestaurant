package appewtc.masterung.ssrurestaurant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by masterUNG on 5/20/15 AD.
 */
public class UserTABLE {

    private MyOpenHelper objMyOpenHelper;
    private SQLiteDatabase writeDatabase, readDatabase;

    public static final String TABLE_USER = "userTABLE";
    public static final String COLUMN_ID_USER = "_id";
    public static final String COLUMN_USER = "User";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_NAME = "Name";

    public UserTABLE(Context context) {

        objMyOpenHelper = new MyOpenHelper(context);
        writeDatabase = objMyOpenHelper.getWritableDatabase();
        readDatabase = objMyOpenHelper.getReadableDatabase();

    }   // Constructor

    //Search User
    public String[] searchUser(String strUser) {

        try {

            String strMyResult[] = null;
            Cursor objCursor = readDatabase.query(TABLE_USER,
                    new String[] {COLUMN_ID_USER, COLUMN_USER, COLUMN_PASSWORD, COLUMN_NAME},
                    COLUMN_USER + "=?",
                    new String[]{String.valueOf(strUser)},
                    null, null, null, null);

            if (objCursor != null) {
                if (objCursor.moveToFirst()) {

                    strMyResult = new String[objCursor.getColumnCount()];
                    strMyResult[0] = objCursor.getString(0);
                    strMyResult[1] = objCursor.getString(1);
                    strMyResult[2] = objCursor.getString(2);
                    strMyResult[3] = objCursor.getString(3);

                }   //if2
            }   //if1

            objCursor.close();
            return strMyResult;

        } catch (Exception e) {
            return null;
        }

       // return new String[0];
    }



    //Add New Value to userTABLE
    public long addNewValue(String strUser, String strPassword, String strName) {

        ContentValues objContentValues = new ContentValues();
        objContentValues.put(COLUMN_USER, strUser);
        objContentValues.put(COLUMN_PASSWORD, strPassword);
        objContentValues.put(COLUMN_NAME, strName);

        return writeDatabase.insert(TABLE_USER, null, objContentValues);
    }


}   // Main Class
