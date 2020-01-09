import com.khirye.rpc.api.NameService;
import com.khirye.rpc.api.RpcAccessPoint;
import com.khirye.rpc.api.spi.ServiceSupport;
import com.khirye.rpc.hello.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.net.URI;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);
    public static void main(String[] args) throws Exception {
        String serviceName = HelloService.class.getCanonicalName();

        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));

        File file = new File(tmpDirFile, "simple_rpc.name_service.data");
        HelloService helloService = new HelloServiceImpl();
        log.info("Initialize and Start RpcAccessPoint...");
        try(RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
            Closeable ignored = rpcAccessPoint.startServer()) {

            NameService nameService = rpcAccessPoint.getNameService(file.toURI());
            assert nameService != null;

            log.info("Register {} Service to RpcAccessPoint", serviceName);
            URI uri = rpcAccessPoint.addServiceProvider(helloService, HelloService.class);

            log.info("Register {} Service to NameService", serviceName);
            nameService.register(serviceName, uri);

            log.info("Start to offer service, Press any key to Quit!");
            System.in.read();

            log.info("Bye!");

        }
    }
}
