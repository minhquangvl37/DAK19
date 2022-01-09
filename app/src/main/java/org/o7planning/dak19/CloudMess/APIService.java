package org.o7planning.dak19.CloudMess;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAH8spddo:APA91bG_THTcHMIipHGCFMuZBkR1ALLRNDKusZTO3Ekimx_sxAaQNEu4RAv8XIkxGGq1_6dk4r_hYEA11UQXSY5NZsvpwA_cDtzK_rR8RB5tJhwG8qTjkRonpLip78YCqBlYub7xhFm9"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

    // m đặt hàng cái nó vằn// t bam vo thong bao no vang// bấm của customer hay admin/ admin

}
