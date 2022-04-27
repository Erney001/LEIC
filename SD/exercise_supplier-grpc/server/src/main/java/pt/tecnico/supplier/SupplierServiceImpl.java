package pt.tecnico.supplier;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.google.common.primitives.Bytes;
import com.google.protobuf.ByteString;
import com.google.type.Money;

import io.grpc.stub.StreamObserver;
import pt.tecnico.supplier.domain.Supplier;
import pt.tecnico.supplier.grpc.Product;
import pt.tecnico.supplier.grpc.ProductsRequest;
import pt.tecnico.supplier.grpc.ProductsResponse;
import pt.tecnico.supplier.grpc.SupplierGrpc;
import pt.tecnico.supplier.grpc.SignedResponse;
import pt.tecnico.supplier.grpc.Signature;

import java.security.MessageDigest;
import javax.crypto.Cipher;

import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.util.Date;

public class SupplierServiceImpl extends SupplierGrpc.SupplierImplBase {
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

	/** Domain object. */
	final private Supplier supplier = Supplier.getInstance();

	/** Constructor */
	public SupplierServiceImpl() {
		debug("Loading demo data...");
		supplier.demoData();
	}

	/** Helper method to convert domain product to message product. */
	private Product buildProductFromProduct(pt.tecnico.supplier.domain.Product p) {
		Product.Builder productBuilder = Product.newBuilder();
		productBuilder.setIdentifier(p.getId());
		productBuilder.setDescription(p.getDescription());
		productBuilder.setQuantity(p.getQuantity());

		Money.Builder moneyBuilder = Money.newBuilder();
		moneyBuilder.setCurrencyCode("EUR").setUnits(p.getPrice());
		productBuilder.setPrice(moneyBuilder.build());

		return productBuilder.build();
	}

	@Override
	public void listProducts(ProductsRequest request, StreamObserver<SignedResponse> responseObserver) {
		debug("listProducts called");

		debug("Received request:");
		debug(request.toString());
		debug("in binary hexadecimals:");
		byte[] requestBinary = request.toByteArray();
		debug(String.format("%d bytes%n", requestBinary.length));

		// build response
		ProductsResponse.Builder responseBuilder = ProductsResponse.newBuilder();
		responseBuilder.setSupplierIdentifier(supplier.getId());
		for (String pid : supplier.getProductsIDs()) {
			pt.tecnico.supplier.domain.Product p = supplier.getProduct(pid);
			Product product = buildProductFromProduct(p);
			responseBuilder.addProduct(product);
		}

		ProductsResponse response = responseBuilder.build();

		SignedResponse.Builder responseBuilder2 = SignedResponse.newBuilder();

		responseBuilder2.setResponse(response);

		try {
			debug("Response to send:");
			debug(response.toString());
			debug("in binary hexadecimals:");

			int timestamp = (int) new Date().getTime();

			byte[] responseBytes = response.toByteArray();
			byte[] idBytes = supplier.getId().getBytes();
			byte[] timestampBytes = Integer.toBinaryString(timestamp).getBytes();
			byte[] c = Bytes.concat(responseBytes, idBytes, timestampBytes);

			debug(printHexBinary(responseBytes));
			debug(String.format("%d bytes%n", responseBytes.length));

			MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
			messageDigest.update(c);
			byte[] d = messageDigest.digest();

			Cipher cipher = Cipher.getInstance(SYM_CIPHER);
			cipher.init(Cipher.ENCRYPT_MODE, readKey("secret.key"));

			Signature signature = Signature.newBuilder()
					.setValue(ByteString.copyFrom(cipher.doFinal(d)))
					.setSignerId(supplier.getId())
					.setTimestamp(timestamp)
					.build();

			responseBuilder2.setSignature(signature);

		} catch (Exception e){
			System.err.println(e.toString());
		}

		// send single response back
		responseObserver.onNext(responseBuilder2.build());
		// complete call
		responseObserver.onCompleted();
	}

}
