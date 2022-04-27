package pt.tecnico.example.client.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import pt.tecnico.example.SetNameRequest;
import pt.tecnico.example.client.ExampleFrontend;

/**
 * Base class for integration tests. Extending classes can access test
 * properties defined in the configuration resource.
 */
public class BaseIT {
	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties properties;
	static ExampleFrontend frontend;

	@BeforeAll
	public static void oneTimeSetup() throws IOException {
		properties = new Properties();

		try {
			properties.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Test properties:");
			System.out.println(properties);

		} catch (IOException e) {
			final String msg = String.format("Could not load properties file %s", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String host = properties.getProperty("server.host");
		final int port = Integer.parseInt(properties.getProperty("server.port"));
		frontend = new ExampleFrontend(host, port);
	}

	@AfterAll
	public static void cleanup() {
	}

	protected SetNameRequest buildRequest(String name) {
		return SetNameRequest.newBuilder().setProposedName(name).build();
	}
}
