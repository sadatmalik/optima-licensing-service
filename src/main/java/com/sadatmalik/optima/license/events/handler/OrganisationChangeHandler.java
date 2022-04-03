package com.sadatmalik.optima.license.events.handler;

import com.sadatmalik.optima.license.events.CustomChannels;
import com.sadatmalik.optima.license.events.model.OrganisationChangeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 * We inject the CustomChannels interface and use it to process messages.
 *
 * @EnableBinding(CustomChannels.class) - Moves the @EnableBindings out of Application.java
 * and into OrganisationChangeHandler. Instead of using the Sink class, we use CustomChannels
 * as the parameter to pass.
 *
 * @author sadatmalik
 */
@Slf4j
@EnableBinding(CustomChannels.class)
public class OrganisationChangeHandler {

    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganisationChangeModel organisation) {

        log.debug("Received a message of type " + organisation.getType());

        switch(organisation.getAction()){
            case "GET":
                log.debug("Received a GET event from the organisation service for " +
                        "organisation id {}", organisation.getOrganisationId());
                break;
            case "SAVE":
                log.debug("Received a SAVE event from the organisation service for " +
                        "organisation id {}", organisation.getOrganisationId());
                break;
            case "UPDATE":
                log.debug("Received a UPDATE event from the organisation service for " +
                        "organisation id {}", organisation.getOrganisationId());
                break;
            case "DELETE":
                log.debug("Received a DELETE event from the organisation service for " +
                        "organisation id {}", organisation.getOrganisationId());
                break;
            default:
                log.error("Received an UNKNOWN event from the organisation service of " +
                        "type {}", organisation.getType());
                break;
        }
    }
}
