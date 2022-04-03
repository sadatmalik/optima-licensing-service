package com.sadatmalik.optima.license.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * For each custom input channel we want to expose, we define a method with @Input that returns
 * a SubscribableChannel class.
 *
 * @Input Names the channel
 * Returns a SubscribableChannel class for each channel exposed by @Input.
 *
 * We then use @Output before the method that will be called if we want to define output
 * channels for publishing messages. In the case of an output channel, the defined method
 * returns a MessageChannel class instead of the SubscribableChannel class used with the input
 * channel.
 *
 * We need to inject the CustomChannels interface into a class that’s going to use it to
 * process messages.
 *
 * @see com.sadatmalik.optima.license.events.handler.OrganisationChangeHandler
 *
 * @author sadatmalik
 */
public interface CustomChannels {

    @Input("inboundOrgChanges")
    SubscribableChannel orgs();

//    @Output("outboundOrg")
//    MessageChannel outboundOrg();
}