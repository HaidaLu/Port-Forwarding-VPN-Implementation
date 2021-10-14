/**
 * Server side of the handshake.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.*;
import java.util.Base64;

public class ServerHandshake {
    /*
     * The parameters below should be learned by the server
     * through the handshake protocol. 
     */
    
    /* Session host/port, and the corresponding ServerSocket  */
    public static ServerSocket sessionSocket;
    public static String sessionHost;
    public static int sessionPort;    

    /* The final destination -- simulate handshake with constants */
    public static String targetHost = "localhost";
    public static int targetPort = 6789;

    public static String serverCertificate = "server.pem";
    public static String clientCertificate = "client.pem";
    /* Security parameters key/iv should also go here. Fill in! */

    /**
     * Run server handshake protocol on a handshake socket. 
     * Here, we simulate the handshake by just creating a new socket
     * with a preassigned port number for the session.
     */ 
    public ServerHandshake(Socket handshakeSocket) throws Exception{
        sessionSocket = new ServerSocket(12345);
        sessionHost = sessionSocket.getInetAddress().getHostName();
        sessionPort = sessionSocket.getLocalPort();
        String Servercertificate = readPEM(serverCertificate);
        //receive ClientHello
        HandshakeMessage fromclienthello = new HandshakeMessage();
        fromclienthello.recv(handshakeSocket);
        if (fromclienthello.getParameter("MessageType").equals("ClientHello")) {
            X509Certificate servercertificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(
                    new ByteArrayInputStream(fromclienthello.getParameter("Certificate").getBytes(StandardCharsets.UTF_8)));
            try {
                servercertificate.checkValidity();
            } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                e.printStackTrace();
            }
        }
        System.out.println("clienthello received");

        //Send ServerHello
        System.out.println(Servercertificate);
        HandshakeMessage clientHello = new HandshakeMessage();
        clientHello.putParameter("MessageType","ServerHello");
        clientHello.putParameter("Certificate", Servercertificate);
        clientHello.send(handshakeSocket);
        System.out.println("serverhello sent");

        //Receive Forward
        HandshakeMessage fromclientforward = new HandshakeMessage();
        fromclientforward.recv(handshakeSocket);
        if(fromclientforward.getParameter("MessageType").equals("Forward"));{
            targetHost=fromclientforward.getParameter("TargetHost");
            targetPort=Integer.parseInt(fromclientforward.getParameter("TargetPort"));
        }
        System.out.println("forward received");

        //Send Session
        PublicKey clientpublickey = HandshakeCrypto.getPublicKeyFromCertFile(clientCertificate);
        SessionEncrypter getsessionparameter = new SessionEncrypter(128);
        byte[] keyBytes = getsessionparameter.getKeyBytes();
        byte[] cipherKey = HandshakeCrypto.encrypt(keyBytes, clientpublickey);
        String keyString = Base64.getEncoder().encodeToString(cipherKey);

        byte[] ivBytes = getsessionparameter.getIVBytes();
        byte[] cipherIv = HandshakeCrypto.encrypt(ivBytes,clientpublickey);
        String ivString = Base64.getEncoder().encodeToString(cipherIv);

        HandshakeMessage Session = new HandshakeMessage();
        Session.putParameter("MessageType","Session");
        Session.putParameter("SessionKey",keyString);
        Session.putParameter("SessionIV",ivString);
        Session.putParameter("SessionHost",sessionHost);
        Session.putParameter("SessionPort",Integer.toString(sessionPort));
        Session.send(handshakeSocket);
        System.out.println("Session send");
    }
    private static String readPEM(String filename)
    {
        int len=0;
        StringBuffer str=new StringBuffer("");
        File file=new File(filename);
        try {
            FileInputStream is=new FileInputStream(file);
            InputStreamReader isr= new InputStreamReader(is);
            BufferedReader in= new BufferedReader(isr);
            String line=null;
            while( (line=in.readLine())!=null ) {
                if(len != 0) {
                    str.append("\r\n"+line);
                }
                else {
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
}
