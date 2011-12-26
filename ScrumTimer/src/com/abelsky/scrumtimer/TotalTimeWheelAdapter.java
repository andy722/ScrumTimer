package com.abelsky.scrumtimer;

import android.content.Context;
import com.abelsky.scrumtimer.view.CommentedWheelTextAdapter;

/**
 * Selection of scheduled meeting length. Provides both time values and comments.
 *
 * @author andy
 */
class TotalTimeWheelAdapter extends CommentedWheelTextAdapter {

    /** Increment, in minutes. */
    private static final int STEP_MINS = 5;

    private static final int MIN_VALUE = STEP_MINS;
    private static final int MAX_VALUE = Integer.MAX_VALUE;

    public TotalTimeWheelAdapter(Context context) {
        super(context, R.layout.wheel_item, R.id.team_elem_name, R.id.team_elem_desc);
    }

    @Override
    protected CharSequence getTitle(int index) {
        final int timeInMinutes = getValueForIndex(index);

        if (timeInMinutes < 60) {
            return timeInMinutes + " minutes";

        } else {
            final int hours = timeInMinutes / 60;
            final int minutes = timeInMinutes % 60;

            final String hrsStr =  hours + (hours == 1 ? " hour" : " hours");

            if (minutes == 0) {
                return hrsStr;
            } else {
                return hrsStr + " : " + minutes + " minutes";
            }
        }
    }

    @Override
    protected CharSequence getDesc(int index) {
        final int timeInMinutes = getValueForIndex(index);

        if (timeInMinutes == 5) {
            return "Just a quickie.";

        } else if (timeInMinutes > 20 && timeInMinutes <= 30) {
            return "That'll take long...";

        } else if (timeInMinutes > 30 && timeInMinutes <= 60) {
            return "Are we gonna work today or what?";

        } else if (timeInMinutes > 60 && timeInMinutes <= 240) {
            return "Shouldn't we get some beer?";

        } else if (timeInMinutes > 240) {
            return "F*ck the work, let's talk!";

        } else {
            return "It should be enough";
        }
    }

    public int getItemsCount() {
        return MAX_VALUE;
    }

    public static int getValueForIndex(int index) {
        return STEP_MINS * (index + 1);
    }

    public static int getIndexForValue(int minutes) {
        return minutes / STEP_MINS - 1;
    }
}
