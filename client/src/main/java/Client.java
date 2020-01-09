import com.khirye.rpc.api.NameService;
import com.khirye.rpc.api.RpcAccessPoint;
import com.khirye.rpc.api.spi.ServiceSupport;
import com.khirye.rpc.hello.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        String serviceName = HelloService.class.getCanonicalName();
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));

        File file = new File(tmpDirFile, "simple_rpc.name_service.data");
        String name = "a";

        try (RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class)){
             NameService nameService = rpcAccessPoint.getNameService(file.toURI());
             assert nameService != null;
             URI uri = nameService.lookupService(serviceName);
             log.info("the uri is {}", uri);
//             assert uri != null;
             HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
             String response = helloService.hello(name);
             log.info("{}", response);
        }

    }
}
