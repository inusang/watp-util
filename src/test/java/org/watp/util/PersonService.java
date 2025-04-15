package org.watp.util;

import org.springframework.stereotype.Component;
import org.watp.util.cache.annotaions.CacheDisable;
import org.watp.util.cache.annotaions.CacheDisableGroup;
import org.watp.util.cache.annotaions.CacheEnable;
import org.watp.util.cache.annotaions.KeyAttributes;
import org.watp.util.cache.enums.CacheScopeType;
import org.watp.util.cache.enums.CacheType;
import org.watp.util.entity.Person;
import org.watp.util.entity.PersonStay;

@Component
public class PersonService {
    @CacheEnable(desc = "Person", cacheType = CacheType.DATA, cacheScope = CacheScopeType.SCOPED,
            keyAttributes = {
                    @KeyAttributes(name = "id", value = "${person.id}"),
            })
    public Person getPerson(Person person) {
        return person;
    }

    @CacheEnable(desc = "PersonStay", cacheType = CacheType.DATA, cacheScope = CacheScopeType.SCOPED,
            keyAttributes = {
                    @KeyAttributes(name = "id", value = "${personStay.id}")
            })
    public PersonStay getPersonStay(PersonStay personStay) {
        return personStay;
    }

    @CacheDisableGroup(
            items = {@CacheDisable(desc = "Person", cacheType = CacheType.DATA, cacheScope = CacheScopeType.SCOPED,
                            keyAttributes = {
                                    @KeyAttributes(name = "id", value = "${person.id}"),
                    }),
                    @CacheDisable(desc = "PersonStay", cacheType = CacheType.DATA, cacheScope = CacheScopeType.SCOPED,
                            keyAttributes = {
                                    @KeyAttributes(name = "id", value = "${person.id}"),
                    })
            }
    )
    public void delPerson(Person person) {

    }
}