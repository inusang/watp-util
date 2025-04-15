package org.watp.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.watp.util.entity.Person;
import org.watp.util.entity.PersonStay;

@SpringBootTest
@ActiveProfiles("test")
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public class WatpUtilTest {
    @Autowired
    private PersonService testService;

    @Test
    public void testCacheEnable() {
        Person person = new Person("01", "ren", 18);
        PersonStay personStay = new PersonStay("01", "tokyo", "plat");
        testService.getPerson(person);
        testService.getPersonStay(personStay);
    }

    @Test
    public void testCacheDisable() {
        Person person = new Person("01", "ren", 18);
        testService.delPerson(person);
    }

}
