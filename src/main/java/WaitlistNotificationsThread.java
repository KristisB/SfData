import notification.MessageData;
import notification.NotificationMessage;
import notification.NotificationModel;

import java.util.ArrayList;

public class WaitlistNotificationsThread implements Runnable {
    public static final long TIME_BETWEEN_NOTIFICATIONS = 1000 * 60 * 15;    //15min
    private ArrayList<WaitlistItem> waitlist;
    Database db = new Database("sfdata");

    public WaitlistNotificationsThread(ArrayList<WaitlistItem> waitlist) {
        this.waitlist = waitlist;
    }

    @Override
    public void run() {
        for (WaitlistItem waitlistItem : waitlist) {
            User user = db.getUserData(waitlistItem.getUserId());
            Workout workout = db.getWorkout(waitlistItem.getWorkoutId());
            if ((workout.getFreePlaces() == 0)||(workout.getDateTime()<System.currentTimeMillis())) {
                break;
            }
            String notificationText = "Workout on " + workout.getDateText() + " at "
                    + workout.getTimeText() + " has free places. You are welcome to book";

            NotificationMessage msg = new NotificationMessage(user.getToken(), new NotificationModel(
                    "Workout reservation availabe", notificationText), new MessageData("vienas", "du"));
            NotificationHandler notificationHandler = new NotificationHandler();
            notificationHandler.sendNotification(msg);
            System.out.println("notification sent " + msg.toString());
            try {
                Thread.sleep(TIME_BETWEEN_NOTIFICATIONS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
