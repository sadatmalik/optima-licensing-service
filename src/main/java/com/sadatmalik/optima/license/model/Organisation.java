package com.sadatmalik.optima.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.hateoas.RepresentationModel;

/**
 * POJO containing the data that we’ll store in our Redis cache.
 * @RedisHash("organisation") - sets the name of the hash in the Redis server where the
 * organisation data is stored.
 *
 * @author sm@creativefusion.net
 */
@Getter
@Setter
@ToString
@RedisHash("organisation")
public class Organisation extends RepresentationModel<Organisation> {
    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;
}
