package com.abelsky.scrumtimer.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.abelsky.scrumtimer.util.SerializationUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Meeting parameters, specified before it starts.
 *
 * @author andy
 */
public class MeetingOptions implements Serializable {

    private static final String PREFERENCES_NAME = "meeting_options";

    public static final int DEFAULT_TEAM_SIZE = 5;

    public static final int DEFAULT_MEETING_LENGTH_MINUTES = 15; // minutes

    /**
     * Number of team members.
     */
    private int teamSize = DEFAULT_TEAM_SIZE;

    /**
     * Expected overall meeting length, minutes.
     */
    private int lengthLimit = DEFAULT_MEETING_LENGTH_MINUTES;

    public MeetingOptions() {
        // for de-serialization
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        assert teamSize > 0;
        this.teamSize = teamSize;
    }

    public int getLengthLimit() {
        return lengthLimit;
    }

    public void setLengthLimit(int timeInMinutes) {
        assert timeInMinutes > 0;
        this.lengthLimit = timeInMinutes;
    }

    /**
     * Persists the options.
     */
    public void save(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        // save serialized selection
        editor.putString(PREFERENCES_NAME, SerializationUtils.serialize(this));

        editor.commit();
    }

    public static MeetingOptions load(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        final Map<String, ?> entries = prefs.getAll();

        if (!entries.isEmpty()) {
            assert entries.size() == 1;

            final String data = (String) entries.values().toArray()[0];
            return SerializationUtils.deserialize(data);
        }

        return new MeetingOptions();
    }
}
