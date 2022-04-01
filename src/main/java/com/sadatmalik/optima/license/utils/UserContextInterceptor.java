package com.sadatmalik.optima.license.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * This class injects the correlation ID into any outgoing HTTP-based service request
 * that’s executed from a RestTemplate instance. This is done to ensure that we can
 * establish a link between service calls.
 *
 * We do this using a Spring interceptor that’s injected into the RestTemplate class.
 *
 * Invokes intercept() before the actual HTTP service call occurs by the RestTemplate,
 * which takes the HTTP request header that’s being prepared for the outgoing service call
 * and adds the correlation ID stored in the UserContext.
 *
 * To use UserContextInterceptor, we need to define a RestTemplate bean and then add
 * UserContextInterceptor to it.
 *
 * @see com.sadatmalik.optima.license.OptimaLicensingServiceApplication
 * @author sadatmalik
 */
@Slf4j
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        HttpHeaders headers = request.getHeaders();

        headers.add(UserContext.CORRELATION_ID,
                UserContextHolder.getContext().getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN,
                UserContextHolder.getContext().getAuthToken());

        return execution.execute(request, body);
    }

}
