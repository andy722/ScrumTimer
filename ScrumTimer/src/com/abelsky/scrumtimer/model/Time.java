package com.abelsky.scrumtimer.model;

/**
 * Useful time representation, to the seconds precision.
 *
 * @author andy
 */
public class Time {
    int seconds;

    public Time() {
        this(0);
    }

    private Time(int seconds) {
        this.seconds = seconds;
    }

    public int getMinutes() {
        return seconds / 60;
    }

    /**
     * @return Seconds of the last minute.
     *
     * For instance, if the time is 5 minutes 30 seconds (5:30), 30 will be returned.
     */
    public int getSeconds() {
        return seconds % 60;
    }
    
    public static Time roundTo(Time time, Time step) {
        final int diff = time.seconds % step.seconds;
        final int margin = 15;

        int seconds = step.seconds * ((time.seconds / step.seconds) + ((diff < margin) ? 0 : 1));
        return new Time(seconds);
    }

    public void addSecond() {
        this.seconds++;
    }
    
    public static boolean lt(Time t1, Time t2) {
        return t1.seconds < t2.seconds;
    }

    public static boolean gt(Time t1, Time t2) {
        return t1.seconds > t2.seconds;
    }

    public static Time sub(Time t1, Time t2) {
        final int diff = t1.seconds - t2.seconds;
        return new Time(diff < 0 ? 0 : diff);
    }

    public static Time div(Time t, int div) {
        return new Time(t.seconds / div);
    }
    
    public static Time fromMinutes(int minutes) {
        return fromSeconds(minutes * 60);
    }

    public static Time fromSeconds(int seconds) {
        return new Time(seconds);
    }
}
