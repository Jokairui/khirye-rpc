import com.khirye.rpc.hello.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements HelloService {

    private static final Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(String name) {
        log.info("Received : {}", name);
        String response = "Hello, " + name;
        log.info("Response : {}", response);
        return response;
    }
}
