package example;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *  Test suite
 */
public class ExampleTest {

	// static members


	// one-time initialization and clean-up

	@BeforeAll
	public static void oneTimeSetUp() { }

	@AfterAll
	public static void oneTimeTearDown() { }

	
	// members

	private Example example;


	// initialization and clean-up for each test

	@BeforeEach
	public void setUp() {
		example = new Example();
	}

	@AfterEach
	public void tearDown() {
		example = null;
	}


	// tests

	@Test
	public void testGetDefaultGreeting() {
		assertEquals(/*expected*/ "Hello", /*actual*/ example.getGreeting());
	}

	@Test
	public void testSetGetGreeting() {
		final String greeting = "Olá";
		example.setGreeting(greeting);
		assertEquals(greeting, example.getGreeting());
	}

	@Test
	public void testSetNullGreeting() {
		// this test should throw an exception of the IllegalArgumentException class
		assertThrows(IllegalArgumentException.class, () -> example.setGreeting(null));
	}

	@Test
	public void testSetEmptyGreeting() {
		assertThrows(IllegalArgumentException.class, () -> example.setGreeting("")); 
	}

	@Test
	public void testDefaultGreet() {
		assertEquals("Hello friend!", example.greet("friend")); 
	}

	@Test
	public void testGreetWithNullName() {
		assertThrows(IllegalArgumentException.class, () -> example.greet(null)); 
	}

	@Test
	public void testGreetWithEmptyName() {
		// empty name should be allowed
		assertEquals("Hello!", example.greet(""));
	}

}
