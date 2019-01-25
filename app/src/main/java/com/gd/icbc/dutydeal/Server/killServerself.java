package com.gd.icbc.dutydeal.Server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class killServerself extends Service {

        /**关闭应用后多久重新启动*/
        private static  long stopDelayed=2000;
        private Handler handler;
        private String PackageName;
    public killServerself() {
            handler=new Handler();
        }

        @Override
        public int onStartCommand(final Intent intent, int flags, int startId) {
            stopDelayed=intent.getLongExtra("Delayed",2000);
            PackageName=intent.getStringExtra("PackageName");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PackageName);
                    startActivity(LaunchIntent);
                    killServerself.this.stopSelf();
                }
            } ,stopDelayed);
// ()->{
//            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PackageName);
//            startActivity(LaunchIntent);
//            killService.this.stopSelf();
//        }
            return super.onStartCommand(intent, flags, startId);
        }






    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
