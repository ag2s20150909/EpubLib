package me.ag2s.base.okhttp;


import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class KingCardAuthenticator implements okhttp3.Authenticator {

    @Override
    public Request authenticate(Route p1, Response response) throws IOException {
        String credential = okhttp3.Credentials.basic("admin", "19950913");
        return response.request().newBuilder().header("Authorization", credential).build();
    }


//	@Override
//	public Response intercept(Interceptor.Chain chain) throws IOException
//	{
//		Request request = chain.request()  
//
//			.newBuilder()  
//            .addHeader("Q-GUID","4da0783fcd0a626c942bdded10c588cb")
//			.addHeader("Q-TOKEN","2e07d6a00994047f39eb2dd29e0743c95053489f8c586fc2e9b9c0e016d2ea740c1cba6fab6dd8321a077ecbecb62d03")
//			.build();  
//
//		return chain.proceed(request); 
//	}

}
