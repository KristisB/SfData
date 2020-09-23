package Services;

import database.Database;
import models.WaitlistItem;
import notification.NotificationMessage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;

public class NotificationHandler {
    private MsgApiService msgService;
    public MsgApiService getMsgService() {
        return msgService;
    }

    public void sendNotification(NotificationMessage msg) {
        Retrofit retrofitMsgService = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        msgService = retrofitMsgService.create(MsgApiService.class);
        Call<ResponseBody> call = msgService.sendMessage(msg);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println("message sent " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("message not sent ");
            }
        });
    }

    //todo fix moving in waiting list line logic
    public void handleWaitlist(int workoutId, Database db) {
        ArrayList<WaitlistItem> waitlist = db.getWaitlistByWorkout(workoutId);
        Thread waitlistNotificationsThread = new Thread(new WaitlistNotificationsThread(waitlist));
        waitlistNotificationsThread.start();
    }
}
