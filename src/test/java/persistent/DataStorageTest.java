package persistent;

import com.reader.storage.common.impl.ObjectDepository;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 06 15:03
 */


public class DataStorageTest {

    public static void main(String[] args) {

        ObjectDepository repo = new ObjectDepository(System.getProperty("user.dir") + "/data", "my_data");

        HashMap<String, String> map = new HashMap<>();
        map.put("18", "18");
        // 存储普通对象
        repo.add("user1", new Person("Alice",map),true);
        // 存储集合
        List<Person> users = Arrays.asList(new Person("Bob", map), new Person("Charlie", map));
        repo.add("all_users", users,true);
        // 获取数据
        Person alice = (Person) repo.get("user1");
        List<Person> userList = (List<Person>) repo.get("all_users");

        System.out.println(alice);
        System.out.println(userList);
    }

    public static class Person implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private String name;
        private Map<String, String> map;
        public Person(String name, Map<String, String> map) {
            this.name = name;
            this.map = map;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", map=" + map +
                    '}';
        }
    }

}