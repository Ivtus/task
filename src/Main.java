
import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.GatewayException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;

public class Main {
    public static void main(String[] args) throws CommitException, GatewayException, CertificateException, IOException, InvalidKeyException, InterruptedException {
        App app = new App();
//        app.Invoke("SetRate", new String[]{"0.221"});
        app.Query("GetRate", new String[]{});
//        app.Invoke("CreateAsset", new String[]{"asset1","Maybe","100","0"});
//        app.Query("ReadAsset", new String[]{"asset1"});
//        app.Invoke("TransferPoint", new String[]{"asset1","200"});
//        app.Invoke("DeleteAsset", new String[]{"asset3"});
//        app.Invoke("UpdateAsset",new String[]{"asset4","Maybe","150","0"});
//        app.Invoke("DeleteAsset",new String[]{"asset3"});
    }
}