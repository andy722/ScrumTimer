package com.abelsky.scrumtimer;

import android.content.Context;
import com.abelsky.scrumtimer.view.CommentedWheelTextAdapter;

/**
 * Allows selecting team size. Provides both numeric values and comments.
 *
 * @author andy
 */
class TeamSizeWheelAdapter extends CommentedWheelTextAdapter {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = Integer.MAX_VALUE;

    public TeamSizeWheelAdapter(Context context) {
        super(context, R.layout.wheel_item, R.id.team_elem_name, R.id.team_elem_desc);
    }

    protected CharSequence getTitle(int index) {
        final int count = getValueForIndex(index);

        if (count == 1) {
            return "Just me";

        } else {
            return count + " people";
        }
    }

    protected CharSequence getDesc(int index) {
        final int count = getValueForIndex(index);

        // TODO: make fun with these!
        if (count == 1) {
            return "Sad and lonely...";

        } else if (count == 2) {
            return "Me and that other guy";

        } else if (count > 10 && count <= 20) {
            return "That's quite a crowd here!";

        } else if (count > 20) {
            return "Are we in China or what?";

        } else {
            return "";
        }
    }

    public int getItemsCount() {
        return MAX_VALUE;
    }

    public static int getValueForIndex(int index) {
        return index + 1;
    }

    public static int getIndexForValue(int count) {
        return count - 1;
    }
}
