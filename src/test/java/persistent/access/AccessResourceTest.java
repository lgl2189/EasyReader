package persistent.access;

import com.reader.entity.net.ResponseData;
import com.reader.webpage.access.AccessResource;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 2月 19 15:05
 */


public class AccessResourceTest {
    public static void main(String[] args) throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            AccessResource accessResource = new AccessResource("https://www.baidu.com",httpClient);
//            AccessResource accessResource = new AccessResource("https://img0.baidu.com/it/u=2040579468,2953150368&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=1083",httpClient);
            accessResource.execute();
            ResponseData responseData = accessResource.getData();
            System.out.println(responseData);
        }

    }
}