package test.ivacompany.com.testmetronom;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    public MyService() {
    }

    int isType, isTime;
    Thread vibroThread, flashThread, soundThread;
    static Camera cam = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        allThreadOff();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type = intent.getIntExtra("type", isType);
        int time = intent.getIntExtra("time", isTime);

        isTime = time;
        isType = type;

            switch (type) {
                case Code.Simple:
                    allThreadOff();
                    break;
                case Code.VibroOn:
                    try {
                        if (vibroThread.isAlive()) {
                            vibroThread.interrupt();
                        }
                        if (flashThread.isAlive()) {
                            flashThread.interrupt();
                            flash(time);
                        }
                        if (soundThread.isAlive()) {
                            soundThread.interrupt();
                            sound(time);
                        }
                    }catch(Exception e){}
                    vibro(time);

                    break;
                case Code.FlashOn:
                    try {
                        if (flashThread.isAlive()) {
                            flashThread.interrupt();
                        }
                        if (vibroThread.isAlive()) {
                            vibroThread.interrupt();
                            vibro(time);
                        }
                        if (soundThread.isAlive()) {
                            soundThread.interrupt();
                            sound(time);
                        }
                    }catch (Exception e){}
                    flash(time);
                    break;
                case Code.SoundOn:
                    try {
                        if (flashThread.isAlive()) {
                            flashThread.interrupt();
                            flash(time);
                        }
                        if (vibroThread.isAlive()) {
                            vibroThread.interrupt();
                            vibro(time);
                        }
                        if (soundThread.isAlive()) {
                            soundThread.interrupt();
                        }
                    }catch (Exception e){}
                    sound(time);
                    break;
                case Code.VibroOff:
                    if (vibroThread.isAlive()) {
                        vibroThread.interrupt();
                    }
                    break;
                case Code.FlashOff:
                    if (flashThread.isAlive()) {
                        flashThread.interrupt();
                    }
                    break;
                case Code.SoundOff:
                    if (soundThread.isAlive()) {
                        soundThread.interrupt();
                    }
                    break;
            }

        return super.onStartCommand(intent, flags, startId);
    }


    public void sound(final int time) {
        Ringtone r = null;
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
        final Ringtone finalR = r;
        soundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                    finalR.play();
                }
            }
        });

        soundThread.start();
    }

    public void vibro(final int time) {

        vibroThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                    Vibrator v = (Vibrator) getApplication().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(100);
                }
            }
        });

        vibroThread.start();


    }

    public void allThreadOff() {
        if (vibroThread != null) {
            vibroThread.interrupt();
        }
        if (flashThread != null) {
            flashThread.interrupt();
        }
        if (soundThread != null) {
            soundThread.interrupt();cam.release();
        }
    }

    public boolean isCameraUsebyApp() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            return true;
        } finally {
            if (camera != null) camera.release();
        }
        return false;
    }

    public void flash(final int time) {
        if(isCameraUsebyApp()) {
            flashThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }

                        Camera.Parameters p = cam.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        cam.setParameters(p);
                        cam.startPreview();
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        cam.setParameters(p);
                        cam.stopPreview();

                    }
                }
            });

            flashThread.start();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        isType = 0;
        isTime = 1000;
        try {
            cam = Camera.open();
        }catch (RuntimeException e){e.printStackTrace();}

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
