package com.optum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
/*This class is a simple implementation of the ClientHttpRequestInterceptor interface that adds an Authorization header with a value of "Token " + token to every HttpRequest that is executed by the RestTemplate.
* */
@Configuration
public class AuthHeaderInterceptor implements ClientHttpRequestInterceptor {


    private final String token="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";

    public AuthHeaderInterceptor authHeaderInterceptor() {
        return new AuthHeaderInterceptor();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("Authorization", "Token " + token);
        return execution.execute(request, body);
    }
}
