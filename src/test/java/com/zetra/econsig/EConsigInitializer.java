package com.zetra.econsig;

import static io.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.zetra.econsig.config.CreateCustomFlywayImages;

import io.restassured.RestAssured;
import lombok.extern.log4j.Log4j2;

/**
 * <p>Title: EConsigInitializer</p>
 * <p>Description: Classe de inicialização do ambiente de testes.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Rubem Kalebe, Leonel Martins
 */
@Log4j2
@Testcontainers
@SpringJUnitConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = {
	"classpath:application-test.properties",
	"classpath:persistence-test.properties"
})
@SuppressWarnings("resource")
public class EConsigInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    // Local port where system will be responding, random between lower and upper bound
    private static int serverLocalPort = 0;

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
	    // Define uma nova porta randômica para o servidor, para no caso de estar rodando a suite completa
	    // não ocorrer conflito de porta com a instância do eNomina
	    setServerRandomPort();

		GenericContainer<?> mysql = new GenericContainer<>("registry.zetrasoft.com.br/zetra/mysql:local")
				.withNetworkAliases("database")
	            .withEnv("MYSQL_ROOT_PASSWORD", "root")
	            .withEnv("MYSQL_DATABASE", "consig")
	            .withEnv("MYSQL_USER", "user")
	            .withEnv("MYSQL_PASSWORD", "teste@123")
	            .withExposedPorts(3306)
	            .withStartupTimeout(Duration.ofSeconds(120));

	    GenericContainer<?> redis = new GenericContainer<>("redis:latest").withExposedPorts(6379);

	    Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LoggerFactory.getLogger(EConsigInitializer.class));

	    CreateCustomFlywayImages createCustomFlywayImages = new CreateCustomFlywayImages();
	    createCustomFlywayImages.create();

	    mysql.start(); // prevents JUnit of trying to create two instances

	    String dockerGatewayIP = null;
	    try {
	        dockerGatewayIP = getDocker0IpAddress();
	        log.info("IP da rede docker0: " + dockerGatewayIP);
	    } catch (UnknownHostException | SocketException e) {
	        log.error("Nao foi possivel identificar o IP da rede docker0");
	        throw new RuntimeException("Nao foi possivel identificar o IP da rede docker0");
	    }

	    GenericContainer<?> flyway = new GenericContainer<>("flyway-econsig:latest")
	            .withCommand("-url=jdbc:mysql://" + dockerGatewayIP + ":" + mysql.getMappedPort(3306) + "/consig -user=user -password=teste@123 -connectRetries=60 -placeholderReplacement=false -encoding=ISO-8859-1 migrate")
	            .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(3000)))
	            .withLogConsumer(logConsumer);
	    flyway.start();
	    redis.start();

		String databaseAddress = dockerGatewayIP + ":" + mysql.getMappedPort(3306);

		TestPropertyValues.of(
			"spring.datasource.url=jdbc:mysql://" + databaseAddress
				+ "/consig?zeroDateTimeBehavior=convertToNull&jdbcCompliantTruncation=false&serverTimezone=America/Sao_Paulo&connectionCollation=latin1_swedish_ci&characterSetResults=latin1&characterEncoding=latin1",
			"spring.datasource.username=user",
			"spring.datasource.password=teste@123",

			"jdbc.jdbcUrl=jdbc:mysql://" + databaseAddress
				+ "/consig?zeroDateTimeBehavior=convertToNull&jdbcCompliantTruncation=false&serverTimezone=America/Sao_Paulo&connectionCollation=latin1_swedish_ci&characterSetResults=latin1&characterEncoding=latin1",
			"jdbc.username=user",
			"jdbc.password=teste@123",
            "server.port=" + serverLocalPort,
			"spring.data.redis.host=" + dockerGatewayIP,
			"spring.data.redis.port=" + redis.getMappedPort(6379)
		).applyTo(applicationContext.getEnvironment());

		prepareFilesLocally();

		// clean previous screenshots
		try {
            FileUtils.cleanDirectory(new File("target/Screenshot"));
        } catch (final IOException e) {
            log.error("cleaning target/Screenshot is unsuccessful");
        } catch(final IllegalArgumentException e) {
            log.error("directory target/Screenshot does not exist or is not a directory");
        }
	}

	private void prepareFilesLocally() {
		final ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", "./src/test/resources/prepare_files_and_folders.sh");

		try {
			final Process process = processBuilder.start();

			final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				log.info(line);
			}

			final int exitVal = process.waitFor();

			if (exitVal == 0) {
				log.info("Pasta /tmp/eConsigTestes/econsig_arquivos devidamente configurada localmente");
			} else {
				log.error("Nao foi possivel configurar a pasta /tmp/eConsigTestes/econsig_arquivos");
				throw new RuntimeException("Nao foi possivel configurar a pasta /tmp/eConsigTestes/econsig_arquivos");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getDocker0IpAddress() throws SocketException, UnknownHostException {
	    final String dockerHostVar = System.getenv("DOCKER_HOST");
	    if (StringUtils.isNotBlank(dockerHostVar) && dockerHostVar.startsWith("tcp")) {
	        final String host = dockerHostVar.substring(6);
	        final String ip = host.substring(0, host.indexOf(":"));
	        return ip;
	    }
	    final String dockerHostProp = System.getProperty("docker.host");
        if (StringUtils.isNotBlank(dockerHostProp)) {
            return dockerHostProp;
        }
	    final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            final NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }

            if (iface.getDisplayName().equals("docker0") || iface.getDisplayName().equals("en0")) {
                final Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    final InetAddress addr = InetAddress.getByName(addresses.nextElement().getHostAddress());
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        }
        return DockerClientFactory.instance().dockerHostIpAddress();
	}

	public static void limparCache() {
		RestAssured.useRelaxedHTTPSValidation();
		given()
			.when()
				.get("https://localhost:" + serverLocalPort + "/consig/admin/limpar_cache.jsp")
			.then()
				.assertThat()
					.statusCode(HttpStatus.SC_OK);
	}

	public static void setServerRandomPort() {
	    // Random Port Range
	    final int upper = 50000;
	    final int lower = 10000;

	    // Local port where system will be responding, random between lower and upper bound
	    serverLocalPort = (int) (Math.random() * (upper - lower)) + lower;
	}

	public static int getServerRandomPort() {
	    return serverLocalPort;
	}

    public static String getBaseURL() {
        // Base URL for Web Testing
        return "https://localhost:" + serverLocalPort;
    }
}
