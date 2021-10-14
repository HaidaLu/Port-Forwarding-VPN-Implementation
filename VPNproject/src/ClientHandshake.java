/**
 * Client side of the handshake.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.*;
import java.util.Base64;

public class ClientHandshake {
    /*
     * The parameters below should be learned by the client
     * through the handshake protocol.
     */

    /* Session host/port  */
    public static String sessionHost = "localhost";
    public static int sessionPort = 12345;

    /*Client的证书和私钥*/
    public static String clientCertificate = "client.pem";
    public PrivateKey privateKey;
    public String clientPrivatekey = "client-private.der";

    /* Security parameters key/iv should also go here. 通过handshake学到的key/iv */
    public SessionKey sessionKey;
    public byte[] sessionIV;
    public byte[] sessionKeyBytes;

    /**
     * Run client handshake protocol on a handshake socket.
     * Here, we do nothing, for now.
     */
    public ClientHandshake(Socket handshakeSocket) throws Exception {
        System.out.println("ClientHello");
        InetAddress inetAddress = handshakeSocket.getInetAddress();
        String targethost = inetAddress.getHostName();
        int targetport = handshakeSocket.getPort();
        String Usercertificate = null;
        Usercertificate = readPEM(clientCertificate);


        System.out.println(Usercertificate);
        HandshakeMessage clientHello = new HandshakeMessage();
        clientHello.putParameter("MessageType", "ClientHello");
        clientHello.putParameter("Certificate", Usercertificate);
        clientHello.send(handshakeSocket);

        System.out.println("clienthello sent");

        HandshakeMessage fromserverhello = new HandshakeMessage();
        fromserverhello.recv(handshakeSocket);
        if (fromserverhello.getParameter("MessageType").equals("ServerHello")) {
            X509Certificate servercertificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(
                    new ByteArrayInputStream(fromserverhello.getParameter("Certificate").getBytes(StandardCharsets.UTF_8)));
            try {
                servercertificate.checkValidity();
            } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                e.printStackTrace();
            }
        }
        System.out.println("serverhello received");

        HandshakeMessage Forward = new HandshakeMessage();
        Forward.putParameter("MessageType", "Forward");
        Forward.putParameter("TargetHost", targethost);
        Forward.putParameter("TargetPort", Integer.toString(targetport));
        Forward.send(handshakeSocket);
        System.out.println("clientForward sent");

        HandshakeMessage session = new HandshakeMessage();
        session.recv(handshakeSocket);
        if (session.getParameter("MessageType").equals("Session")) {
            String Stringkey = session.getParameter("SessionKey");
            privateKey = HandshakeCrypto.getPrivateKeyFromKeyFile(clientPrivatekey);
            //sessionIV = HandshakeCrypto.decrypt(session.getParameter("SessionIV").getBytes(StandardCharsets.ISO_8859_1),privateKey);
            //sessionKey = new SessionKey(HandshakeCrypto.decrypt(Stringkey.getBytes(StandardCharsets.ISO_8859_1),privateKey));
            sessionIV = HandshakeCrypto.decrypt(Base64.getDecoder().decode(session.getParameter("SessionIV")), privateKey);
            sessionKey = new SessionKey(HandshakeCrypto.decrypt(Base64.getDecoder().decode(session.getParameter("SessionKey")), privateKey));
            sessionKeyBytes = sessionKey.bytearray;
            sessionHost = session.getParameter("SessionHost");
            sessionPort = Integer.parseInt(session.getParameter("SessionPort"));
        }
        System.out.println("session receceived");
    }



    private static String readPEM(String filename) {
        int len = 0;
        StringBuffer str = new StringBuffer("");
        File file = new File(filename);
        try {
            FileInputStream is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(isr);
            String line = null;
            while ((line = in.readLine()) != null) {
                if (len != 0) {
                    str.append("\r\n" + line);
                } else {
                    str.append(line);
                }
                len++;
            }
            in.close();
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str.toString();
    }

    //public byte[] getSessionIV() {
        //return sessionIV;
   // }
    //public byte[] getSessionKey(){
        //return sessionKeyBytes;
   // }
}