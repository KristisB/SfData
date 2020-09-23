package Services;

import database.Database;
import models.User;
import models.Workout;
import notification.MessageData;
import notification.NotificationMessage;
import notification.NotificationModel;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderThread implements Runnable {
    Database db = new Database("sfdata");
    Calendar calendar = Calendar.getInstance();

    @Override
    public void run() {
        while (true) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            System.out.println("Services.ReminderThread is running "+ calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
            long startDateTime = System.currentTimeMillis() + 1 * 60 * 60 * 1000;   //fix reminder to 1h before
            long checkInterval = 1000 * 60 * 15 ;                                    //scan db every 15 min
            ArrayList<Workout> workouts = db.getWorkouts(startDateTime, startDateTime + checkInterval);
            for (Workout w : workouts) {
                ArrayList<User> workoutParticipants = db.getWorkoutParticipants(w);
                for (User user : workoutParticipants) {
                    String msgTitle="Upcoming workout reminder";
                    String msgBody="Waiting for you at "+ w.getTimeText()+"\n"+w.getDescription();
                    NotificationHandler notificationHandler = new NotificationHandler();
                    notificationHandler.sendNotification(new NotificationMessage(user.getToken(),
                            new NotificationModel(msgTitle, msgBody), new MessageData("", "")));
                }
                calendar.setTimeInMillis(System.currentTimeMillis());
                System.out.println("reminders sent to " + workoutParticipants.size() + " participants "+
                        calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));
            }
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}