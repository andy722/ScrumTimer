package com.abelsky.scrumtimer.view;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import com.abelsky.scrumtimer.R;

/**
 * @author andy
 */
public class Thingies {
    
    public static int TOAST_TIME_MS = 5000;

    private static final long VIBRATE_INTERVAL_NS = 10 * 1000 * 1000 * 1000L;   // 10 seconds

    private static long lastVibrated = 0;

    /**
     * Vibrate, but only if the last vibration was at least {@link #VIBRATE_INTERVAL_NS} nanoseconds ago.
     */
    public static void vibrate(Activity caller) {
        if ((lastVibrated + VIBRATE_INTERVAL_NS) <= System.nanoTime()) {
            lastVibrated = System.nanoTime();

            final Vibrator vibrator = (Vibrator) caller.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
        }
    }

    private static MediaPlayer beeper;

    private static boolean prepareBeep(Context caller) {
        if (beeper == null) {
            beeper = MediaPlayer.create(caller, R.raw.iphone_alarm_short);
            beeper.setLooping(false);
        }

        return true;
    }

    public static void beep(Context caller) {
        if (prepareBeep(caller)) {
            beeper.start();
        }
    }
}
