package com.khirye.rpc.client;

import com.itranswarp.compiler.JavaStringCompiler;
import com.khirye.rpc.transport.Transport;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class DynamicStubFactory implements StubFactory {

    private final static String STUB_SOURCE_TEMPLATE =
        "package com.khirye.rpc.client.stubs;\n" +
            "import come.khirye.rpc.serialize.SerializeSupport;\n" +
            "\n" +
            "public class %s extends AbstractStub implements %s {\n" +
            "   @Override\n" +
            "   public String %s(String arg) {\n" +
            "       return SerializeSupport.parse(\n" +
            "           invokeRemote(\n" +
            "               new RpcRequest(\n" +
            "                   \"%s\",\n" +
            "                   \"%s\",\n" +
            "                   SerializeSupport.serialize(arg)\n" +
            "               )\n" +
            "           )\n" +
            "       );\n" +
            "   }\n" +
            "}";
    @Override
    public <T> T createStud(Transport transport, Class<T> serviceClass) {
        String stubSimpleName = serviceClass.getSimpleName() + "Stub";
        String classFullName = serviceClass.getName();
        String stubFullName = "com.khirye.rpc.client.stubs." + stubSimpleName;
        String methodName = serviceClass.getMethods()[0].getName();
        String source = String.format(STUB_SOURCE_TEMPLATE, stubSimpleName, classFullName, methodName, classFullName, methodName);

        JavaStringCompiler compiler = new JavaStringCompiler();
        try {
            Map<String, byte[]> results = compiler.compile(stubSimpleName + ".java", source);
            Class<?> clazz = compiler.loadClass(stubFullName, results);

            ServiceStub stubInstance = (ServiceStub) clazz.getDeclaredConstructor().newInstance();
            stubInstance.setTransport(transport);

            return (T) stubInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
