package it.gov.pagopa.fdrtechsupport.util;

import com.github.terma.javaniotcpproxy.StaticTcpProxyConfig;
import com.github.terma.javaniotcpproxy.TcpProxy;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import lombok.SneakyThrows;
import org.testcontainers.containers.CosmosDBEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmosResource implements QuarkusTestResourceLifecycleManager {

  private static final Integer[] exposedPorts = {8081, 10251, 10252, 10253, 10254};

  private static CosmosDBEmulatorContainer cosmos = null;

  private static final List<TcpProxy> startedProxies = new ArrayList<>();

  @SneakyThrows
  @Override
  public Map<String, String> start() {

    cosmos =
        new CosmosDBEmulatorContainer(
                DockerImageName.parse("mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator"))
            .withEnv(
                "AZURE_COSMOS_EMULATOR_IP_ADDRESS_OVERRIDE",
                InetAddress.getLocalHost().getHostAddress())
            .withEnv("AZURE_COSMOS_EMULATOR_PARTITION_COUNT", "3")
            .withEnv("AZURE_COSMOS_EMULATOR_ENABLE_DATA_PERSISTENCE", "false")
            .withExposedPorts(exposedPorts);

    cosmos.start();

    CosmosResource.startTcpProxy(exposedPorts);

    Path keyStoreFile = File.createTempFile("azure-cosmos-emulator", ".keystore").toPath();
    KeyStore keyStore = cosmos.buildNewKeyStore();
    keyStore.store(
        new FileOutputStream(keyStoreFile.toFile()), cosmos.getEmulatorKey().toCharArray());

    System.setProperty("javax.net.ssl.trustStore", keyStoreFile.toString());
    System.setProperty("javax.net.ssl.trustStorePassword", cosmos.getEmulatorKey());
    System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

    Map<String, String> conf = new HashMap<>();
    conf.put("mockserver.biz.endpoint", cosmos.getEmulatorEndpoint());
    conf.put("mockserver.biz.key", cosmos.getEmulatorKey());
    return conf;
  }

  @Override
  public void stop() {
    cosmos.stop();
    startedProxies.forEach(p -> p.shutdown());
  }

  private static void startTcpProxy(Integer... ports) {
    for (Integer port : ports) {
      StaticTcpProxyConfig tcpProxyConfig =
          new StaticTcpProxyConfig(port, cosmos.getHost(), cosmos.getMappedPort(port));
      tcpProxyConfig.setWorkerCount(1);
      TcpProxy tcpProxy = new TcpProxy(tcpProxyConfig);
      tcpProxy.start();
      startedProxies.add(tcpProxy);
    }
  }
}
