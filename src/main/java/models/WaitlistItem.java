package models;

public class WaitlistItem extends Workout{
    private int waitlistId;
    private long waitlistDate;
    private int userId;
    private int numberInLine;

    public int getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(int waitlistId) {
        this.waitlistId = waitlistId;
    }

    public long getWaitlistDate() {
        return waitlistDate;
    }

    public void setWaitlistDate(long waitlistDate) {
        this.waitlistDate = waitlistDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNumberInLine() {
        return numberInLine;
    }

    public void setNumberInLine(int numberInLine) {
        this.numberInLine = numberInLine;
    }
}
