package com.abelsky.scrumtimer.model;

/**
 * State of the meeting - tracks speakers and times.
 *
 * @author andy
 */
public class MeetingState {

    /**
     * Minimal time slice (no need to track each second).
     */
    private static final Time MIN_CHUNK = Time.fromSeconds(30);

    /**
     * Overall team size.
     */
    private int teamSize;

    /**
     * Planned time for the meeting, specified before the beginning.
     */
    private final Time totalEstimated;

    /**
     * Actually spent time at this moment. May be more than {@link #totalEstimated}.
     */
    private final Time totalElapsed = new Time();

    /**
     * Current speaker, from {@code 0} to <code>{@link #teamSize} - 1</code>.
     */
    private int currentSpeakerNumber = 0;

    /**
     * Time scheduled for the current speaker - total time left divided equally between all remaining team members.
     *
     * @see #calculateEstimatedTime
     */
    private Time currentSpeakerEstimated = new Time();
    
    /**
     * Time the current speaker (the one for {@link #currentSpeakerNumber}) actually talks.
     */
    private Time currentSpeakerElapsed = new Time();

    private MeetingState(int teamSize, int overallLengthLimitMinutes) {
        this.teamSize = teamSize;
        totalEstimated = Time.fromMinutes(overallLengthLimitMinutes);
        currentSpeakerEstimated = calculateEstimatedTime();
    }

    public MeetingState(MeetingOptions options) {
        this(options.getTeamSize(), options.getLengthLimit());
    }

    public int getCurrentSpeakerNumber() {
        return currentSpeakerNumber;
    }

    public Time getCurrentSpeakerEstimated() {
        return currentSpeakerEstimated;
    }

    /**
     * Re-calculates estimated time limit for the current speaker.
     */
    private Time calculateEstimatedTime() {
        final int speakersToGo = teamSize - currentSpeakerNumber;
        final Time timeLeft = Time.sub(totalEstimated, totalElapsed);

        final Time equallyDivided = Time.div(timeLeft, speakersToGo);

        if (Time.lt(equallyDivided, MIN_CHUNK)) {
            return MIN_CHUNK;

        } else {
            return Time.roundTo(equallyDivided, MIN_CHUNK);
        }
    }

    /**
     * @return Time planned for the current speaker.
     */
    public Time getCurrentSpeakerElapsed() {
        return currentSpeakerElapsed;
    }

    /**
     * @return Time since start of this meeting to this moment.
     */
    public Time getTotalElapsed() {
        return totalElapsed;
    }

    /**
     * @return Planned meeting length.
     */
    public Time getTotalEstimated() {
        return totalEstimated;
    }

    /**
     * @return Number of team members.
     */
    public int getTeamSize() {
        return teamSize;
    }

    /**
     * Checks if current speaker is the last.
     */
    public boolean isLastManSpeakingNow() {
        return getCurrentSpeakerNumber() == (getTeamSize() - 1);
    }

    public boolean currentSpeakerTalksTooLong() {
        return Time.gt(getCurrentSpeakerElapsed(), getCurrentSpeakerEstimated());
    }

    public void addTeamMember() {
        this.teamSize++;
    }

    /**
     * Switches to the next team member, i.e. the current one finished speaking.
     */
    public void next() {
        assert !isLastManSpeakingNow() : "no team members left";

        currentSpeakerElapsed = new Time();
        currentSpeakerNumber++;
        currentSpeakerEstimated = calculateEstimatedTime();
    }

    /**
     * Should be called each second.
     */
    public void tick() {
        currentSpeakerElapsed.addSecond();
        totalElapsed.addSecond();
    }
}
