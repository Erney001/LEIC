package pt.tecnico.example.client.it;

import static io.grpc.Status.Code.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.grpc.StatusRuntimeException;

/** Test suite for setName remote operation. */
public class SetNameIT extends BaseIT {
	private static final String EMPTY_ID = "";
	private static final String INVALID_ID = "   ";
	private static final String OVERSIZE_ID = "Longbottom";
	private static final String VALID_ID = "friend";

	@BeforeEach
	public void setUp() {
	}

	@Test
	public void validIdTest() {
		assertEquals(VALID_ID, frontend.setName(buildRequest(VALID_ID)).getName());
	}

	@Test
	public void emptyIdTest() {
		assertEquals(INVALID_ARGUMENT,
				assertThrows(StatusRuntimeException.class, () -> frontend.setName(buildRequest(EMPTY_ID))).getStatus()
						.getCode());
	}

	@Test
	public void invalidIdTest() {
		assertEquals(INVALID_ARGUMENT,
				assertThrows(StatusRuntimeException.class, () -> frontend.setName(buildRequest(INVALID_ID))).getStatus()
						.getCode());
	}

	@Test
	public void oversizeIdTest() {
		assertEquals(INVALID_ARGUMENT,
				assertThrows(StatusRuntimeException.class, () -> frontend.setName(buildRequest(OVERSIZE_ID))).getStatus()
						.getCode());
	}

	@AfterEach
	public void clear() {
	}
}
