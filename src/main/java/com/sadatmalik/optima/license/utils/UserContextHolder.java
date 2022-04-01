package com.sadatmalik.optima.license.utils;

import org.springframework.util.Assert;

/**
 * The UserContextHolder class stores the UserContext in a ThreadLocal class. Once it’s stored
 * in ThreadLocal, any code that’s executed for a request will use the UserContext object
 * stored in the UserContextHolder.
 *
 * Be careful when working directly with ThreadLocal. An incorrect development inside
 * ThreadLocal can lead to memory leaks in the application.
 *
 * The ThreadLocal variable will be accessible by any method being invoked by the thread
 * processing the user’s request.
 *
 * @author sadatmalik
 */
public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    public static UserContext getContext(){
        UserContext context = userContext.get();

        if (context == null) {
            context = createEmptyContext();
            userContext.set(context);
        }
        return userContext.get();
    }

    public static void setContext(UserContext context) {
        Assert.notNull(context,
                "Only non-null UserContext instances are permitted");
        userContext.set(context);
    }

    public static UserContext createEmptyContext(){
        return new UserContext();
    }
}