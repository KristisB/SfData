package models;

import java.util.Calendar;
import java.util.Locale;

public class Workout {
    private int workoutId;
    private long dateTime;
    private long duration;
    private int maxGroupSize;
    private String description;
    private int freePlaces;
    private int extraInfo1;
    private int extraInfo2;

    public static final long TIME_TO_CANCEL = 43200000;

    public int getExtraInfo2() {
        return extraInfo2;
    }

    public void setExtraInfo2(int extraInfo2) {
        this.extraInfo2 = extraInfo2;
    }

    public int getFreePlaces() {
        return freePlaces;
    }

    public void setFreePlaces(int freePlaces) {
        this.freePlaces = freePlaces;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getMaxGroupSize() {
        return maxGroupSize;
    }

    public void setMaxGroupSize(int maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }

    public String getDescription() {
        return description;
    }

    public int getExtraInfo1() {
        return extraInfo1;
    }

    public void setExtraInfo1(int extraInfo1) {
        this.extraInfo1 = extraInfo1;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDateText() {
        Calendar workoutDate = Calendar.getInstance();
        workoutDate.setTimeInMillis(dateTime);
        String dayOfWeek = workoutDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String dateYear = Integer.toString(workoutDate.get(Calendar.YEAR));
        String dateMonth = workoutDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String dateday = Integer.toString(workoutDate.get(Calendar.DAY_OF_MONTH));

        String dateText = dayOfWeek + " / " + dateYear + " " + dateMonth + " " + dateday;
        return dateText;

    }

    public String getTimeText() {
        Calendar workoutDate = Calendar.getInstance();
        workoutDate.setTimeInMillis(dateTime);
        String timeText = workoutDate.get(Calendar.HOUR_OF_DAY) + ":" + workoutDate.get(Calendar.MINUTE);
        return timeText;
    }

}
