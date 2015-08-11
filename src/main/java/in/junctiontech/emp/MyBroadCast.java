package in.junctiontech.emp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Junction on 7/30/2015.
 */
public class MyBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        arg0.startService(new Intent(arg0, MyService.class));
    }

}
