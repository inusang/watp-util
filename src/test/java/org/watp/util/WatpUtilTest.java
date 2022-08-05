package org.watp.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.watp.util.cache.CacheEnable;
import org.watp.util.cache.CacheType;
import org.watp.util.cache.DataType;

@SpringBootTest(classes = {WatpUtilTest.class})
public class WatpUtilTest {

    @Test
    public void testCacheEnable() {
        Person person = new Person();
        person.setId("10000");
        getPerson(person);
    }

    @CacheEnable(type = CacheType.PRIVATE, desc = "test", dataType = DataType.DATA, privateId = "${metaPerson.id}")
    Person getPerson(Person metaPerson) {
        return new Person(metaPerson.id, "man", 100);
    }

    static class Person {
        private String id;
        private String name;
        private int age;

        public Person() {
        }

        public Person(String id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
