package com.zetra.econsig.enomina;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.config.CreateCustomFlywayImages;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Testcontainers
@SpringJUnitConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = {
	"classpath:application-test.properties",
	"classpath:persistence-test.properties"
})
@SuppressWarnings("resource")
public class ENominaInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
        // Define uma nova porta randômica para o servidor, para no caso de estar rodando a suite completa
        // não ocorrer conflito de porta com a instância do eConsig
	    EConsigInitializer.setServerRandomPort();

		GenericContainer<?> mysql = new GenericContainer<>("registry.zetrasoft.com.br/zetra/mysql:local")
				.withNetworkAliases("database")
	            .withEnv("MYSQL_ROOT_PASSWORD", "root")
	            .withEnv("MYSQL_DATABASE", "consig")
	            .withEnv("MYSQL_USER", "user")
	            .withEnv("MYSQL_PASSWORD", "teste@123")
	            .withExposedPorts(3306);

	    GenericContainer<?> redis = new GenericContainer<>("redis:latest").withExposedPorts(6379);

	    Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LoggerFactory.getLogger(ENominaInitializer.class));

        CreateCustomFlywayImages createCustomFlywayImages = new CreateCustomFlywayImages();
        createCustomFlywayImages.create();

        mysql.start(); // prevents JUnit of trying to create two instances

        String dockerGatewayIP = null;
        try {
            dockerGatewayIP = EConsigInitializer.getDocker0IpAddress();
            log.info("IP da rede docker0: " + dockerGatewayIP);
        } catch (UnknownHostException | SocketException e) {
            log.error("Nao foi possivel identificar o IP da rede docker0");
            throw new RuntimeException("Nao foi possivel identificar o IP da rede docker0");
        }

        GenericContainer<?> flyway = new GenericContainer<>("flyway-enomina:latest")
                .withCommand("-url=jdbc:mysql://" + dockerGatewayIP + ":" + mysql.getMappedPort(3306) + "/consig -user=user -password=teste@123 -connectRetries=60 -placeholderReplacement=false -encoding=ISO-8859-1 migrate")
                .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(300)))
                .withLogConsumer(logConsumer);
        flyway.start();
        redis.start();

        // https://mkyong.com/webservices/jax-ws/java-security-cert-certificateexception-no-name-matching-localhost-found/
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
            (hostname, sslSession) -> {
                if(hostname.equals("localhost")) {
                    return true;
                }
                return false;
            }
        );

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
            "server.port=" + EConsigInitializer.getServerRandomPort(),
            "spring.data.redis.host=" + dockerGatewayIP,
            "spring.data.redis.port=" + redis.getMappedPort(6379)
		).applyTo(applicationContext.getEnvironment());
	}

	public static void limparCache() {
		EConsigInitializer.limparCache();
	}
}
