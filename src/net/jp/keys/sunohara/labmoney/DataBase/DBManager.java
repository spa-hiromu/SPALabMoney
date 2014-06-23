
package net.jp.keys.sunohara.labmoney.DataBase;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class DBManager {

    static final String DB = "labMoney.db";
    public static final String USER_TABLE = "user";
    public static final String NUDLE_TABLE = "nudle";
    public static final String DRINK_TABLE = "drink";
    public static final String PRICE_TABLE = "price";

    public static final String DATE_COLUMN = "date";
    public static final String USER_COLUMN = "userName";
    public static final String MAIL_COlUMN = "mailAddress";
    public static final String UID_COLUMN = "uID";
    public static final String PRICE_COLUMN = "price";
    public static final String NUDLE_PRICE_COLUMN = "nudle_price";
    public static final String NUDLE_COUNT_COLUMN = "nudle_count";
    public static final String DRINK_PRICE_COLUMN = "drink_price";
    public static final String DRINK_COUNT_COLUMN = "drink_count";
    public static final int ITEM_DRINK = 0;
    public static final int ITEM_NUDLE = 1;
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE = "create table mytable ( _id integer primary key autoincrement, data integer not null );";
    static final String DROP_TABLE = "drop table mytable;";

    static SQLiteDatabase mydb;

    private SimpleCursorAdapter myAdapter;
    private final Context mContext;

    public DBManager(Context context) {
        mContext = context;
        DBOpenHelper helper = new DBOpenHelper(mContext);
        mydb = helper.getWritableDatabase();
        Log.d("dbmanager", "dbmanager");
    }

    /** 行の挿入 */
    public void insert(String uid, String name, String mail) {
        ContentValues values = new ContentValues();
        values.put(UID_COLUMN, uid);
        values.put(USER_COLUMN, name);
        values.put(MAIL_COlUMN, mail);
        mydb.insert(USER_TABLE, null, values);
    }
   
    public void insertPrice(int uID, int price, int count, int item) {
        ContentValues values = new ContentValues();
        values.put(UID_COLUMN, uID);
        values.put(DATE_COLUMN, new Date().getTime());
        switch (item) {
            case ITEM_DRINK:
                values.put(NUDLE_PRICE_COLUMN, 0);
                values.put(NUDLE_COUNT_COLUMN, 0);
                values.put(DRINK_PRICE_COLUMN, price);
                values.put(DRINK_COUNT_COLUMN, count);
                break;
            case ITEM_NUDLE:
                values.put(NUDLE_PRICE_COLUMN, 0);
                values.put(NUDLE_COUNT_COLUMN, 0);
                values.put(DRINK_PRICE_COLUMN, price);
                values.put(DRINK_COUNT_COLUMN, count);
                break;
        }
        mydb.insert(PRICE_TABLE, null, values);
    }

    /**
     * 購入時の更新
     * 
     * @return
     */
    public int updatePrice(String column, int value, String uID) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        return mydb.update(PRICE_TABLE, values, UID_COLUMN + "=?",
                new String[] {
                    uID
                });
    }
    /** 行の検索 */
    public Cursor mySearch(String tableName, String[] columns) {
        return mydb.query(tableName, columns, null, null, null, null, null);
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context) {
            super(context, DB, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("createDB", "createDB");
            createUserTable(db);
            createPriceTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }

        public void createUserTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + USER_TABLE + " (" + " id "
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " + UID_COLUMN + " TEXT NOT NULL, "
                    + USER_COLUMN
                    + " TEXT NOT NULL, " + MAIL_COlUMN + " TEXT NOT NULL);");
        }

        public void createPriceTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + PRICE_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + UID_COLUMN + "TEXT NOT NULL, "
                    + DATE_COLUMN + "INTEGER NOT NULL, "
                    + NUDLE_PRICE_COLUMN + "INTEGER NOT NULL, "
                    + NUDLE_COUNT_COLUMN + "INTEGER NOT NULL, "
                    + DRINK_PRICE_COLUMN + "INTEGER NOT NULL, "
                    + DRINK_COUNT_COLUMN + "INTEGER NOT NULL);");
        }
    }
}
