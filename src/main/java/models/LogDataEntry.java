package models;

import java.util.Calendar;
import java.util.Locale;

public class LogDataEntry {
    private int operationId;
    private int userId;
    private long operationDateTime;
    private int balanceChange;
    private String operationInfo;
    private int referenceId;
    private String referenceName;
    private String referenceFamilyName;

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getOperationDateTime() {
        return operationDateTime;
    }

    public void setOperationDateTime(long operationDateTime) {
        this.operationDateTime = operationDateTime;
    }

    public int getBalanceChange() {
        return balanceChange;
    }

    public void setBalanceChange(int balanceChange) {
        this.balanceChange = balanceChange;
    }

    public String getOperationInfo() {
        return operationInfo;
    }

    public void setOperationInfo(String operationInfo) {
        this.operationInfo = operationInfo;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getReferenceFamilyName() {
        return referenceFamilyName;
    }

    public void setReferenceFamilyName(String referenceFamilyName) {
        this.referenceFamilyName = referenceFamilyName;
    }


    public String getDateText() {
        Calendar logEntryDate = Calendar.getInstance();
        logEntryDate.setTimeInMillis(operationDateTime);
        String dayOfWeek = logEntryDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String dateYear = Integer.toString(logEntryDate.get(Calendar.YEAR));
        String dateMonth = logEntryDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String dateday = Integer.toString(logEntryDate.get(Calendar.DAY_OF_MONTH));

        String dateText = dayOfWeek + " / " + dateYear + " " + dateMonth + " " + dateday;
        return dateText;

    }

    public String getMonthYearText() {
        Calendar logEntryDate = Calendar.getInstance();
        logEntryDate.setTimeInMillis(operationDateTime);
        String dateYear = Integer.toString(logEntryDate.get(Calendar.YEAR));
        String dateMonth = logEntryDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String dateText =   dateMonth + " " + dateYear;
        return dateText;
    }

    public String getShortDateText() {
        Calendar logEntryDate = Calendar.getInstance();
        logEntryDate.setTimeInMillis(operationDateTime);
        String dateYear = Integer.toString(logEntryDate.get(Calendar.YEAR));
        String dateMonth = logEntryDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String dateWeekday = logEntryDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        String dateDay = Integer.toString(logEntryDate.get(Calendar.DAY_OF_MONTH));
        String dateText =   dateWeekday + " " + dateDay;
        return dateText;
    }

    public String getTimeText() {
        Calendar logEntryDate = Calendar.getInstance();
        logEntryDate.setTimeInMillis(operationDateTime);
        String timeText = String.format(Locale.getDefault(),"%02d:%02d",logEntryDate.get(Calendar.HOUR_OF_DAY), logEntryDate.get(Calendar.MINUTE)) ;
        return timeText;
    }
}
