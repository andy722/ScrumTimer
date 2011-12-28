package com.abelsky.scrumtimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import static com.abelsky.scrumtimer.view.Thingies.TOAST_TIME_MS;

/**
 * Shows timer and meeting status.
 *
 * @author andy
 */
public class TimerActivity extends Activity {

    private static final String TAG = "TimerActivity";
    
    private static final String TIMER_FONT_ASSET_PATH = "fonts/digital-7 (mono).ttf";

    private static final int MENU_ID_ADD_TEAM_MEMBER    = 101;
    private static final int MENU_ID_REMOVE_TEAM_MEMBER = 102;
    private static final int MENU_ID_PAUSE              = 103;
    private static final int MENU_ID_INTERRUPT          = 104;

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

        final MeetingOptions options = MeetingOptions.load(this);
        state = new MeetingState(options);
    }

    private void addTeamMember() {
        state.addTeamMember();
        updateStatus();

        Toast.makeText(this, R.string.speaker_added, TOAST_TIME_MS).show();
    }

    private void removeTeamMember() {
        if ((state.getTeamSize() - state.getCurrentSpeakerNumber()) <= 1) {
            // nobody to remove

        }
        state.addTeamMember();
        updateStatus();

        Toast.makeText(this, R.string.speaker_deleted, TOAST_TIME_MS).show();
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
        updateStatus();
        startTimer();

        super.onResume();
    }

    private void updateStatus() {
        totalTeamView.setText(Pretty.printMembersCount(this, state));
    }

    public void startTimer() {
        timer.purge();

        try {
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
            
        } catch (IllegalStateException e) {
            // In the case we're moved to background and then re-opened (which is probably what just happened),
            // the task hasn't got cancelled, so its re-scheduling will disgracefully fail.
            Log.wtf(TAG, e);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_ADD_TEAM_MEMBER, Menu.NONE, R.string.menu_add)
            .setIcon(R.drawable.ic_menu_add);

        menu.add(Menu.NONE, MENU_ID_REMOVE_TEAM_MEMBER, Menu.NONE, R.string.menu_remove)
            .setIcon(R.drawable.ic_menu_delete);

        menu.add(Menu.NONE, MENU_ID_PAUSE, Menu.NONE, R.string.menu_pause)
            .setIcon(R.drawable.ic_media_pause);

        menu.add(Menu.NONE, MENU_ID_INTERRUPT, Menu.NONE, R.string.menu_interrupt)
            .setIcon(R.drawable.ic_menu_close_clear_cancel);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_ADD_TEAM_MEMBER:
                addTeamMember();
                break;

            case MENU_ID_REMOVE_TEAM_MEMBER:
                addTeamMember();
                break;

            default:
                assert false;
        }
        return super.onOptionsItemSelected(item);
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
            Toast.makeText(TimerActivity.this, "Well, I'm kinda working on this one...", TOAST_TIME_MS).show();
        }
    }

}
