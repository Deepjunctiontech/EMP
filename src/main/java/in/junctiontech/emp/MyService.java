package in.junctiontech.emp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Junction on 7/30/2015.
 */
public class MyService extends Service implements LocationListener {

    LocationManager lm;
    public static String IMEI;
    private DataBaseHandler db;
    private MediaPlayer mp;
    private Timer timer;

    @Override
    public void onCreate() {

        super.onCreate();
        db=new DataBaseHandler(this,"junction",null,1);
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();
        lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        mp=MediaPlayer.create(this,R.raw.alarm);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        timer=new Timer();
        boolean gps_enable=false,network_enable=false;

        try
        {
            gps_enable=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception e)
        {
            Log.d("Exception", "Exception when GPS_Provider Enable");
        }

        try
        {
            network_enable=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception e)
        {
            Log.d("Exception", "Exception when Network_Provider Enable");
        }

        if(!gps_enable&&!network_enable)
        {
            Toast.makeText(this,"PLEASE ENABLE LOCATION SERVICE",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            mp.start();
        }
        else if(gps_enable) {
            Log.d("Message", "GPS Enable");
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            timer.schedule(new GetLastLocation(), 30000);
        }
        else if(network_enable) {
            Log.d("Message", "Network Enable");
            lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
        }
        ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE)).set(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis() + 30*60*1000,
                PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(getApplicationContext(), MyBroadCast.class),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        return START_REDELIVER_INTENT;
    }

    class GetLastLocation extends TimerTask
    {
        @Override
        public void run() {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     lm.removeUpdates(MyService.this);
                                     lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, MyService.this, null);


                                 }
                             }
                );


        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lm.removeUpdates(this);
            timer.cancel();


        Calendar calendar = Calendar.getInstance();
        db.addData(
                new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())),
                calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND),
                location.getLatitude()+ "",
                location.getLongitude()+ ""
        );

        db.searchAndSend();
    }

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
//        Toast.makeText(this, "onStatusChanged",
//                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
//        Toast.makeText(this, "onProviderEnabled",
//                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
//        Toast.makeText(this, "onProviderDisabled",
//                Toast.LENGTH_LONG).show();

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


}