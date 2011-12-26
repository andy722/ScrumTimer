package com.abelsky.scrumtimer.view;

import android.content.Context;
import com.abelsky.scrumtimer.R;
import com.abelsky.scrumtimer.model.MeetingState;
import com.abelsky.scrumtimer.model.Time;

/**
 * Text formatting utilities.
 *
 * @author andy
 */
public class Pretty {

    public static String printMembersCount(Context context, MeetingState state) {
        final String done;
        if (state.getCurrentSpeakerNumber() == 0) {
            done = context.getString(R.string.speakers_none);
        } else {
            done = String.format(context.getString(R.string.speakers_finished),
                    state.getCurrentSpeakerNumber());
        }

        final String left = String.format(context.getString(R.string.speakers_left),
                (state.getTeamSize() - state.getCurrentSpeakerNumber()));

        return done + " " + left;
    }

    public static String printTimeStatus(MeetingState state) {
        assert (state.getTotalEstimated().getSeconds() == 0);

        final String format = "%d:%s / %d:00";
        return String.format(format,
                state.getTotalElapsed().getMinutes(), make2digit(state.getTotalElapsed().getSeconds()),
                state.getTotalEstimated().getMinutes());
    }

    private static String make2digit(int x) {
        return ((x < 10) ? "0" : "") + x;
    }

    public static String printTimerText(MeetingState state) {
        final Time elapsed = state.getCurrentSpeakerElapsed();
        final Time estimated = state.getCurrentSpeakerEstimated();

        return String.format("%d:%s / %d:%s",
                elapsed.getMinutes(), make2digit(elapsed.getSeconds()),
                estimated.getMinutes(), make2digit(estimated.getSeconds()));

    }

}
