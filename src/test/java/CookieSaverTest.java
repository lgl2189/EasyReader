import com.reader.util.json.CookieSaver;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;

/**
 * @author      ：李冠良
 * @description ：无描述

 * @date        ：2025 2月 17 23:25
 */


public class CookieSaverTest {
    public static void main(String[] args) throws Exception {
        BasicCookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(new BasicClientCookie("name1", "value1"));
        cookieStore.addCookie(new BasicClientCookie("name2", "value2"));
        cookieStore.addCookie(new BasicClientCookie("name3", "value3"));
        CookieSaver.save(cookieStore, "test1","C:\\Users\\12145\\Desktop\\新建文件夹");
        CookieStore cookieStore2 = CookieSaver.loadAsCookieStore("test1","C:\\Users\\12145\\Desktop\\新建文件夹");
        System.out.println(cookieStore2.getCookies());
    }
}