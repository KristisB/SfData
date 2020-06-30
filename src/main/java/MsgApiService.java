import notification.NotificationMessage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MsgApiService {
    final String key="AAAA5KXl5YQ:APA91bG6YQiIQVeQ3XknpceIuNKkcgrcw_RmDS82ulk1j_lH9nKZzNyZDMb0Rvh9o_" +
            "Sr3nyucnigLrtSBnR0CqIG_z7GXc7dxU3KEIsj-Qiy-IC5j2fpxus9cgp2lKg-dPoJOgPA5Pox";

    @Headers({"Authorization: key="  + key,
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendMessage(@Body NotificationMessage message);

}