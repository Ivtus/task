import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import org.hyperledger.fabric.client.*;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public  class App {
    private static final String MSP_ID = System.getenv().getOrDefault("MSP_ID", "Org1MSP");
    private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "basic");
    // Path to crypto materials.
    private static final Path CRYPTO_PATH = Paths.get("crypto-config/peerOrganizations/org1.example.com");
    // Path to user certificate.
    private static final Path CERT_PATH = CRYPTO_PATH.resolve(Paths.get("users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem"));
    // Path to user private key directory.
    private static final Path KEY_DIR_PATH = CRYPTO_PATH.resolve(Paths.get("users/User1@org1.example.com/msp/keystore"));
    // Path to peer tls certificate.
    private static final Path TLS_CERT_PATH = CRYPTO_PATH.resolve(Paths.get("peers/peer0.org1.example.com/tls/ca.crt"));
    // Gateway peer end point.
    private static final String PEER_ENDPOINT = "47.245.62.24:7051";
    private static final String OVERRIDE_AUTH = "peer0.org1.example.com";

    private static ManagedChannel newGrpcConnection() throws IOException {
        io.grpc.ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                .trustManager(TLS_CERT_PATH.toFile())
                .build();
        return Grpc.newChannelBuilder(PEER_ENDPOINT, credentials)
                .overrideAuthority(OVERRIDE_AUTH)
                .build();
    }

    private static Identity newIdentity() throws IOException, CertificateException {
        Reader certReader = Files.newBufferedReader(CERT_PATH);
        X509Certificate certificate = Identities.readX509Certificate(certReader);

        return new X509Identity(MSP_ID, certificate);
    }

    private static Signer newSigner() throws IOException, InvalidKeyException {
        Reader keyReader = Files.newBufferedReader(getPrivateKeyPath());
        PrivateKey privateKey = Identities.readPrivateKey(keyReader);

        return Signers.newPrivateKeySigner(privateKey);
    }

    private static Path getPrivateKeyPath() throws IOException {
        try (java.util.stream.Stream<Path> keyFiles = Files.list(KEY_DIR_PATH)) {
            return keyFiles.findFirst().get();
        }
    }

    public void Query(String funcName, String[] args) throws GatewayException, IOException, CertificateException, InvalidKeyException, InterruptedException {
        ManagedChannel channel = newGrpcConnection();
        Gateway.Builder builder = Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(channel)
                // Default timeouts for different gRPC calls
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);
            byte[] getResult = contract.evaluateTransaction(funcName,args);
            System.out.println(new String(getResult, StandardCharsets.UTF_8));

        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public void Invoke(String funcName, String[] args) throws CommitException,GatewayException, IOException, CertificateException, InvalidKeyException, InterruptedException {
        ManagedChannel channel = newGrpcConnection();
        Gateway.Builder builder = Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(channel)
                // Default timeouts for different gRPC calls
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);
            byte[] putResult = contract.submitTransaction(funcName,args);
            System.out.println(new String(putResult, StandardCharsets.UTF_8));

        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

}