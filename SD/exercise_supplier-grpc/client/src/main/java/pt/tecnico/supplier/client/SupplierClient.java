package pt.tecnico.supplier.client;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.google.common.primitives.Bytes;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.supplier.grpc.ProductsRequest;
import pt.tecnico.supplier.grpc.SupplierGrpc;
import pt.tecnico.supplier.grpc.SignedResponse;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.util.Date;

public class SupplierClient {
	private static final String DIGEST_ALGO = "SHA-256";
	private static final String SYM_CIPHER = "AES/ECB/PKCS5Padding";

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

	public static SecretKeySpec readKey(String resourcePathName) throws Exception {
		System.out.println("Reading key from resource " + resourcePathName + " ...");

		InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePathName);
		byte[] encoded = new byte[fis.available()];
		fis.read(encoded);
		fis.close();

		System.out.println("Key:");
		System.out.println(printHexBinary(encoded));
		SecretKeySpec keySpec = new SecretKeySpec(encoded, "AES");

		return keySpec;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(SupplierClient.class.getSimpleName() + " starting ...");

		// Receive and print arguments.
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// Check arguments.
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", SupplierClient.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final String target = host + ":" + port;

		// Channel is the abstraction to connect to a service end-point.
		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

		// Create a blocking stub for making synchronous remote calls.
		SupplierGrpc.SupplierBlockingStub stub = SupplierGrpc.newBlockingStub(channel);

		// Prepare request.
		ProductsRequest request = ProductsRequest.newBuilder().build();
		System.out.println("Request to send:");
		System.out.println(request.toString());
		debug("in binary hexadecimals:");
		byte[] requestBinary = request.toByteArray();
		debug(printHexBinary(requestBinary));
		debug(String.format("%d bytes%n", requestBinary.length));

		// Make the call using the stub.
		System.out.println("Remote call...");
		SignedResponse response = stub.listProducts(request);

		// Print response.
		System.out.println("Received response:");
		System.out.println(response.toString());
		debug("in binary hexadecimals:");

		int timestamp = (int) new Date().getTime();

		String signerId =  response.getSignature().getSignerId();
		byte[] signatureValue = response.getSignature().getValue().toByteArray();
		byte[] responseBinary = response.getResponse().toByteArray();

		int messageTS = response.getSignature().getTimestamp();
		byte[] messageTSBytes = Integer.toBinaryString(messageTS).getBytes();

		byte[] c = Bytes.concat(responseBinary, signerId.getBytes(), messageTSBytes);

		debug(printHexBinary(responseBinary));
		debug(String.format("%d bytes%n", responseBinary.length));

		Cipher cipher = Cipher.getInstance(SYM_CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, readKey("secret.key"));

		byte[] decipheredDigest = cipher.doFinal(signatureValue);

		MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
		messageDigest.update(c);
		byte[] digest = messageDigest.digest();

		if (Arrays.equals(digest, decipheredDigest)) {
			if (timestamp - messageTS < 100) {
				System.out.println("Signature is valid! Message accepted! :)");
			} else {
				System.out.println("Timestamps differ more than 100ms! :(");
			}
		} else {
			System.out.println("Signature is invalid! Message rejected! :(");
		}

		// A Channel should be shutdown before stopping the process.
		channel.shutdownNow();
	}

}
