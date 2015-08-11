package in.junctiontech.emp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Junction on 7/30/2015.
 */
public class DataBaseHandler extends SQLiteOpenHelper {
    Context context;
    public DataBaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                     int version) {
        super(context, name, factory, version);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE employee(date TEXT, time TEXT, latitude TEXT, longitude TEXT)");

        Log.d("onCreate()", "RUN");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("Drop Table employee");

        Log.d("onUpgrade()", "Run");
 //       onCreate(db);

    }

    public void addData(String date, String time, String latitude, String longitude) {

        SQLiteDatabase db = super.getWritableDatabase();
        ContentValues c1 = new ContentValues();
        c1.put("date", date);
        c1.put("time", time);
        c1.put("latitude", latitude);
        c1.put("longitude", longitude);

        if (db.insert("employee", null, c1) == -1) {
//            Toast.makeText(context, "FAILL!!!!", Toast.LENGTH_LONG)
//                    .show();
        } else
        {

        }
//            Toast.makeText(context, "New Entry Successfull", Toast.LENGTH_SHORT)
//                    .show();
        db.close();

    }

    public void deleteEmployeeRecords(String date, String time) {
        SQLiteDatabase db = super.getWritableDatabase();

//        time = time.replace("\"", "");
//        date=date.replace("\"", "");
//        Toast.makeText(context ,date+"\n"+time, Toast.LENGTH_SHORT).show();
        db.delete("employee", "date=? AND time=?", new String[]{date, time});
        //db.execSQL("DELETE FROM employee WHERE time="+t);
        db.close();

    }

    public void searchAndSend() {
        SQLiteDatabase db = super.getReadableDatabase();
        Cursor cs1 = db.rawQuery("SELECT * FROM employee ORDER BY time,date ASC",null);

        while(cs1.moveToNext())
        {
                updateServer(
                        cs1.getString(cs1.getColumnIndex("date")),
                        cs1.getString(cs1.getColumnIndex("time")),
                        cs1.getString(cs1.getColumnIndex("latitude")),
                        cs1.getString(cs1.getColumnIndex("longitude"))
                                );
        }


//        String []s=new String[cs1.getCount()];
//
//        if (cs1.moveToFirst()) {
//            String[] empl;
//            String date = cs1.getString(cs1.getColumnIndex("date"));
//            String time = cs1.getString(cs1.getColumnIndex("time"));
//            String lati = cs1.getString(cs1.getColumnIndex("latitude"));
//            String longi = cs1.getString(cs1.getColumnIndex("longitude"));
//        }
//        int i = cs1.getCount();
//		 Bundle b[]=new Bundle[cs1.getCount()];

//		 int i=0;
//		while (cs1.moveToNext()) {
//			b[i].putString("date",cs1.getString(cs1.getColumnIndex("date")));
//			b[i].putString("time",cs1.getString(cs1.getColumnIndex("time")));
//			b[i].putString("latitude",cs1.getString(cs1.getColumnIndex("latitude")));
//			b[i].putString("longitude",cs1.getString(cs1.getColumnIndex("longitude")));
//			i++;
//		}
//


	      cs1.close();
        db.close();


    }

    public void updateServer(final String date, final String time, final String lati, final String longi) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("IMEI",MyService.IMEI);
        params.put("DATE", date);
        params.put("TIME",time);
        params.put("LATI",lati);
        params.put("LONGI",longi);

        client.post("http://www.junctionerp.com/login/updateServer", params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        // prgDialog.hide();
                      //  Toast.makeText(context, "Successfully Inserted",
                       //         Toast.LENGTH_SHORT).show();


                //TODO check success from server
                        if(response.equalsIgnoreCase("true")) {
                            deleteEmployeeRecords(date, time);
                       //     Toast.makeText(context, "Entry Deleted",
                         //           Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
/*
						if (statusCode == 404) {
							Toast.makeText(context, "Requested resource not found",
									Toast.LENGTH_SHORT).show();
						} else if (statusCode == 500) {
							Toast.makeText(context,
									"Something went wrong at server end",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(
									context,
									"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
									Toast.LENGTH_SHORT).show();
						}
  */                  }
                });
    }


}
