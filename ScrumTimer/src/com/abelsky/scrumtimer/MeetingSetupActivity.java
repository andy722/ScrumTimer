package com.abelsky.scrumtimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.abelsky.scrumtimer.model.MeetingOptions;
import com.abelsky.scrumtimer.view.Thingies;
import com.markupartist.android.widget.ActionBar;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;

import static com.abelsky.scrumtimer.view.Thingies.TOAST_TIME_MS;

/**
 * Set up and start the meeting.
 */
public class MeetingSetupActivity extends Activity {

    private static final String TAG = "MeetingSetupActivity";

    private MeetingOptions meetingOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_setup);

        meetingOptions = MeetingOptions.load(this);

        {
            final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);

            final ActionBar.Action shareAction =
                    new ActionBar.IntentAction(this, createShareIntent(), R.drawable.ic_title_share_default);
            actionBar.addAction(shareAction);

            final ActionBar.Action aboutAction = new HelpAction(R.drawable.ic_title_help_default);
            actionBar.addAction(aboutAction);
        }

        {
            final WheelView teamSize = (WheelView) findViewById(R.id.teamSize);
            teamSize.setViewAdapter(new TeamSizeWheelAdapter(this));
            teamSize.setVisibleItems(3);

            final int defaultTeamSize = meetingOptions.getTeamSize();
            teamSize.setCurrentItem(TeamSizeWheelAdapter.getIndexForValue(defaultTeamSize));
            teamSize.addChangingListener(new OnWheelChangedListener() {
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    meetingOptions.setTeamSize(TeamSizeWheelAdapter.getValueForIndex(newValue));
                }
            });
        }

        {
            final WheelView totalTime = (WheelView) findViewById(R.id.totalTime);
            totalTime.setViewAdapter(new TotalTimeWheelAdapter(this));
            totalTime.setVisibleItems(2);

            final int defaultMeetingLength = meetingOptions.getLengthLimit();
            totalTime.setCurrentItem(TotalTimeWheelAdapter.getIndexForValue(defaultMeetingLength));
            totalTime.addChangingListener(new OnWheelChangedListener() {
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    meetingOptions.setTeamSize(TotalTimeWheelAdapter.getValueForIndex(newValue));
                }
            });
        }

        final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new StartMeetingListener());
    }

    private class StartMeetingListener implements View.OnClickListener {
        public void onClick(View view) {
            // save the options to be retrieved by TimerActivity
            meetingOptions.save(MeetingSetupActivity.this);

            // start the meeting
            final Intent intent = new Intent(MeetingSetupActivity.this, TimerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private Intent createShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, R.string.share_text);
        return Intent.createChooser(intent, getString(R.string.share_caption));
    }

    // XXX: duplicated in TimerActivity
    private class HelpAction extends ActionBar.AbstractAction {
        private HelpAction(int drawable) {
            super(drawable);
        }

        public void performAction(View view) {
            Toast.makeText(MeetingSetupActivity.this, "Well, I'm kinda working on this one...", TOAST_TIME_MS).show();
        }
    }
}
