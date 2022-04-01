package com.sadatmalik.optima.license.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Often in a REST-based environment, we want to pass contextual information to a service call
 * that will help us operationally manage the service. For example, we might pass a correlation
 * ID or authentication token in the HTTP header of the REST call that can then be propagated
 * to any downstream service calls. The correlation ID allows us to have a unique identifier
 * that can be traced across multiple service calls in a single transaction.
 *
 * To make this value available anywhere within our service call, we can use a Spring Filter
 * class to intercept every call in our REST service. It can then retrieve this information
 * from the incoming HTTP request and store this contextual information in a custom
 * UserContext object. Then, anytime our code needs to access this value in our REST service
 * call, our code can retrieve the UserContext from the ThreadLocal storage variable and read
 * the value.
 *
 * @author sm@creativefusion.net
 */
@Slf4j
@Component
public class UserContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {


        HttpServletRequest httpServletRequest
                = (HttpServletRequest) servletRequest;

        UserContextHolder.getContext().setCorrelationId(
                httpServletRequest.getHeader(UserContext.CORRELATION_ID) );
        UserContextHolder.getContext().setUserId(
                httpServletRequest.getHeader(UserContext.USER_ID));
        UserContextHolder.getContext().setAuthToken(
                httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
        UserContextHolder.getContext().setOrganisationId(
                httpServletRequest.getHeader(UserContext.ORGANISATION_ID));

        log.debug("UserContextFilter Correlation id: {}",
                UserContextHolder.getContext().getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}