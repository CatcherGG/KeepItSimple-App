package com.productions.keep.keepitsimple.com.productions.keepitsimple.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Guy Gonen on 08/06/2016.
 */
public class DbHandler extends SQLiteOpenHelper implements Runnable {

    private static final String DATABASE_NAME = "capturedEvents";
    private static final int DATABASE_VERSION = 27;
    private static final String TAG = "DbHandler";

    /*public static Table[] tables = {new MotionEventTable(),
            new TouchEventTable(),
            new DoubleTouchTable(),
            new SwipeEventTable(),
            new ScaleEventTable(),
            new KeyEventTable()}; */

    private BlockingQueue<String> queue;
    private Thread thread;


    public DbHandler(Context context) {
        this(context, null, null, 0);
    }

    private DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        queue = new LinkedBlockingDeque<>();
        thread = new Thread(this);
        thread.start();
    }

    public static String getCreateTableQuery(Table t) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        //sb.append(DATABASE_NAME + ".");
        sb.append(t.getName() + "(\n");
        String[] columnNames = t.getColumnNames();
        SqliteColumnTypes[] columnTypes = t.getColumnTypes();
        String[] modifiers = t.getColumnModifiers();
        for (int i = 0; i < columnNames.length; i++) {
            sb.append(columnNames[i] + " ");
            sb.append(columnTypes[i] + " ");
            try {
                sb.append(modifiers[i]);
            } catch (IndexOutOfBoundsException e) {
                //keep calm and ignore the exception.
            }
            if (i < columnNames.length - 1) {
                sb.append(",\n");
            }
        }
        sb.append(");");
        return sb.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Table t : tables) {
            String query = getCreateTableQuery(t);
            db.execSQL(query);
            t.onCreate();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Table t : tables) {
            t.onUpgrade();
            db.execSQL("DROP TABLE IF EXISTS " + t.getName());
        }
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private long insert(String tableName, ContentValues values) {
        SQLiteDatabase db=null;
        try {
            db = getWritableDatabase();
            return db.insert(tableName, null, values);
        }
        catch (SQLiteException e) {
            Log.e(TAG,"insertion exception",e);
            return -1;
        }
    }

    private long insertScaleEvent(ScaleTouchEvent e) {
        ContentValues values = new ContentValues();
        values.put(ScaleEventTable.DOWN_TIME, e.getTimestamp());
        values.put(ScaleEventTable.IS_REMOVED, 0);
        values.put(ScaleEventTable.TAG, e.getTag());
        values.put(ScaleEventTable.ACTIVITY, e.getActivity());
        long id = insert(ScaleEventTable.TABLE_NAME, values);
        for (DoubleTouchEvent dte : e.getEvents()) {
            insertDoubleTouchEvent(dte, id);
        }
        return id;
    }

    public List<Jsonable> selectKeyboardEvents(String selection, String[] selectionArgs, String limit) {
        /*SQLiteDatabase db = getReadableDatabase();
        String[] projection = null;
        String sortOrder = KeyEventTable.DOWN_TIME + " ASC";
        Cursor cursor = db.query(
                KeyEventTable.TABLE_NAME,
                projection,
                selection,                                // where clause without the "where" word. use ?. these ?s will get value from the next arg.
                selectionArgs,
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,
                limit
        );
        List<Jsonable> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new KeyEventTable().cursorToEventObject(cursor,0));
            cursor.moveToNext();
        }
        cursor.close();*/
        return result;
    }


    private int delete(String tableName, String selection, String[] selectArgs) {
        return getReadableDatabase().delete(tableName, selection, selectArgs);
    }*/

    public void insert(Class clz, Jsonable event) {
        try {
            queue.put(new Pair<>(clz, event));
        } catch (InterruptedException e) {
            Log.e(TAG, "exception in inserting an event to the db queue ", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            break;
            /*

            try {

                Pair<Class, Jsonable> p = queue.take();
                if (p.first.equals(MotionSensorEventWrapper.class)) {
                    insertMotionSensorEvent((MotionSensorEventWrapper) p.second);
                } else if ((p.first.equals(SingleTouchEvent.class))) {
                    Log.d("INSERT","SingleTouch");
                    insertTouchEvent((SingleTouchEvent) p.second, -1);
                } else if ((p.first.equals(TouchEventChunk.class))) {
                    Log.d("INSERT","Swipe");
                    insertSwipeEvent((TouchEventChunk) p.second);
                } else if ((p.first.equals(KeyboardEvent.class))) {
                    Log.d("INSERT","KeyboardEvent");
                    insertKeyboardEvent((KeyboardEvent) p.second);
                } else if ((p.first.equals(ScaleTouchEvent.class))) {
                    Log.d("INSERT","Scale");
                    insertScaleEvent((ScaleTouchEvent) p.second);
                }

            } catch (InterruptedException e) {
                break;
            }*/
        }
    }

}
