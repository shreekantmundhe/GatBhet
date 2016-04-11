package com.gatbhet.services;

import com.gatbhet.model.TokenRequest;
import com.gatbhet.model.TokenResponse;
import com.google.gson.Gson;
import com.gatbhet.config.Util;

/**
 * Created by tinuzz on 04/08/2016.
 */
public class TokenWebService {

    public static final String queryParam = "api/bookAppointment";
    private static final String TAG = "BookAppointmentWebService";

    public TokenResponse getTokenResponse(TokenRequest tokenRequest) {
        Gson gson = new Gson();
        String data = gson.toJson(tokenRequest);
        Util.log(TAG, "Request : " + data);
        WebServiceHelper webServiceHelper = new WebServiceHelper();
        String response = webServiceHelper.makeWebServiceCall(Util.getTimeStamp());
        Util.log(TAG, "Response : " + response);
        return gson.fromJson(response, TokenResponse.class);
    }

}
