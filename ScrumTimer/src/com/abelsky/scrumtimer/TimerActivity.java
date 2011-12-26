package com.abelsky.scrumtimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.abelsky.scrumtimer.model.MeetingOptions;
import com.abelsky.scrumtimer.model.MeetingState;
import com.abelsky.scrumtimer.view.Pretty;
import com.abelsky.scrumtimer.view.Thingies;
import com.markupartist.android.widget.ActionBar;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Shows timer and meeting status.
 *
 * @author andy
 */
public class TimerActivity extends Activity {

    private static final String TAG = "TimerActivity";
    
    private static final String TIMER_FONT_ASSET_PATH = "fonts/digital-7 (mono).ttf";

    private MeetingState state;
    
    private TextView totalTeamView;
    private TextView totalTimeView;
    private TextView estTime;
    private Button nextButton;

    private final Timer timer = new Timer();

    private TimerTask timerTask = new TimerTask() {
        
        private final Runnable uiUpdate = new Runnable() {
            public void run() {
                eachSecond();
            }
        };
        
        @Override
        public void run() {
            runOnUiThread(uiUpdate);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        // set up the panel at the top
        {
            final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);

            actionBar.setHomeAction(new GoHomeAction());

            final ActionBar.Action addTeamMemberAction =
                    new ActionBar.AbstractAction(R.drawable.ic_title_plus_default) {
                        public void performAction(View view) {
                            addTeamMember();
                        }
                    };
            actionBar.addAction(addTeamMemberAction);

            final ActionBar.Action aboutAction = new HelpAction(R.drawable.ic_title_help_default);
            actionBar.addAction(aboutAction);

        }

        totalTeamView = (TextView) findViewById(R.id.status);
        totalTimeView = (TextView) findViewById(R.id.total_time);

        estTime = (TextView) findViewById(R.id.estTime);

        final Typeface timerFont = Typeface.createFromAsset(getAssets(), TIMER_FONT_ASSET_PATH);
        estTime.setTypeface(timerFont);
        estTime.setOnClickListener(new TimerClickListener());

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new NextButtonClickListener());
    }

    private void addTeamMember() {
        state.addTeamMember();
        updateStatus();
    }

    private void eachSecond() {
        updateTimerText();

        state.tick();

        if (state.currentSpeakerTalksTooLong()) {
            estTime.setBackgroundResource(R.drawable.timer_back_red);
            Thingies.vibrate(this);
        }
    }

    private void updateTimerText() {
        totalTimeView.setText(Pretty.printTimeStatus(state));
        estTime.setText(Pretty.printTimerText(state));
    }

    public void next() {
        state.next();
        updateStatus();
        updateTimerText();

        estTime.setBackgroundResource(R.drawable.timer_back_green);
        
        if (state.isLastManSpeakingNow()) {
            nextButton.setText(R.string.finish);
        }
    }

    private void finishMeeting() {
        stopTimer();
        
        final String text =
                String.format(getString(R.string.finish_text), state.getTotalElapsed().getMinutes());

        new AlertDialog.Builder(this)
                .setTitle(R.string.finish_caption)
                .setMessage(text)
                .setNeutralButton(R.string.finish_okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        final MeetingOptions options = MeetingOptions.load(this);
        state = new MeetingState(options);

        updateStatus();
        startTimer();

        super.onResume();
    }

    private void updateStatus() {
        totalTeamView.setText(Pretty.printMembersCount(this, state));
    }

    public void startTimer() {
        timer.purge();
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void stopTimer() {
        timer.cancel();
    }

    private class GoHomeAction extends ActionBar.AbstractAction {

        public GoHomeAction() {
            super(R.drawable.ic_title_home_default);
        }

        public void performAction(View view) {
            new AlertDialog.Builder(TimerActivity.this)
                    .setTitle(R.string.interrupt_caption)
                    .setMessage(R.string.interrupt_text)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopTimer();

                            final Intent intent = new Intent(TimerActivity.this, MeetingSetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // nothing here
                        }
                    }).show();
        }
    }

    @Override
    protected void onDestroy() {
        stopTimer();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(TimerActivity.this)
                .setTitle(R.string.interrupt_caption)
                .setMessage(R.string.interrupt_text)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopTimer();
                        finish();                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing here
                    }
                }).show();
    }

    private class NextButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            if (state.isLastManSpeakingNow()) {
                finishMeeting();
            } else {
                next();
            }
        }
    }
    
    private class TimerClickListener implements View.OnClickListener {

        public void onClick(View view) {
            if (state.currentSpeakerTalksTooLong()) {
                // current speaker talks too much
                Thingies.beep(TimerActivity.this);
            }
        }

    }

    // XXX: duplicated in MeetingSetupActivity
    private class HelpAction extends ActionBar.AbstractAction {
        private HelpAction(int drawable) {
            super(drawable);
        }

        public void performAction(View view) {
            Toast.makeText(TimerActivity.this, "Well, I'm kinda working on this one...", 5000).show();
        }
    }

}
