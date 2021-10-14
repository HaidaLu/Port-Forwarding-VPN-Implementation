/**
 * ForwardServerClientThread handles the clients of Nakov Forward Server. It
 * connects two sockets and starts the TCP forwarding between given client
 * and its assigned server. After the forwarding is failed and the two threads
 * are stopped, closes the sockets.
 *
 */

/**
 * Modifications for IK2206:
 * - Server pool removed
 * - Two variants - client connects to listening socket or client is already connected
 *
 * Peter Sjodin, KTH
 */

import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import javax.crypto.CipherOutputStream;
import javax.crypto.CipherInputStream;

public class ForwardServerClientThread extends Thread
{
    private ForwardClient mForwardClient = null;
    private Socket mClientSocket = null;
    private Socket mServerSocket = null;
    private ServerSocket mListenSocket = null;
    private boolean mBothConnectionsAreAlive = false;
    private String mClientHostPort;
    private String mServerHostPort;
    private int mServerPort;
    private String mServerHost;

    /**
     * Creates a client thread for handling clients of NakovForwardServer. 创建一个处理Server的客户的客户线程
     * Wait for client to connect on client listening socket.
     * A server socket is created later by run() method.
     */
    public ForwardServerClientThread(ServerSocket listensocket, String serverhost, int serverport) throws IOException
    {
        mListenSocket = listensocket;
        mServerPort = serverport;
        mServerHost = serverhost;
    }

    public ServerSocket getListenSocket() {
        return mListenSocket;
    }

    /**
     * Obtains a socket for destination server.
     * First waits for incoming connection on the listen socket.
     * Starts two threads for forwarding : "client in <--> dest server out" and
     * "dest server in <--> client out", waits until one of these threads stop
     * due to read/write failure or connection closure. Closes opened connections.
     * 
     */
    public void run()
    {
        try {
 
            // Wait for incoming connection on listen socket 监听来自User的数据流，返回一个socket，成为客户端
            mClientSocket = mListenSocket.accept();
            mClientHostPort = mClientSocket.getInetAddress().getHostName() + ":" + mClientSocket.getPort();
            Logger.log("Accepted from " + mClientHostPort + " on " + mListenSocket.getLocalPort());

            //和mClientSocket对应的服务端建立，形成连接
            try {
                mServerSocket = new Socket(mServerHost, mServerPort);
            } catch (Exception e) {
                System.out.println("Connection failed to " + mServerHost + ":" + mServerPort);
                e.printStackTrace(); 
                // Prints what exception has been thrown 
                System.out.println(e); 
            }

            // Obtain input and output streams of server and client
            InputStream clientIn = mClientSocket.getInputStream();
            OutputStream clientOut = mClientSocket.getOutputStream();
            InputStream serverIn = mServerSocket.getInputStream();
            OutputStream serverOut = mServerSocket.getOutputStream();

            mServerHostPort = mServerHost + ":" + mServerPort;
            Logger.log("TCP Forwarding  " + mClientHostPort + " <--> " + mServerHostPort + "  started.");
 
            // Start forwarding of socket data between server and client
            ForwardThread clientForward = new ForwardThread(this, clientIn, serverOut);
            ForwardThread serverForward = new ForwardThread(this, serverIn, clientOut);
            mBothConnectionsAreAlive = true;
            clientForward.start();
            serverForward.start();
 
        } catch (IOException ioe) {
           ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /**
     * connectionBroken() method is called by forwarding child threads to notify
     * this thread (their parent thread) that one of the connections (server or client)
     * is broken (a read/write failure occured). This method disconnects both server
     * and client sockets causing both threads to stop forwarding.
     */
    public synchronized void connectionBroken()
    {
        if (mBothConnectionsAreAlive) {
           // One of the connections is broken. Close the other connection and stop forwarding
           // Closing these socket connections will close their input/output streams
           // and that way will stop the threads that read from these streams
           try { mServerSocket.close(); } catch (IOException e) {}
           try { mClientSocket.close(); } catch (IOException e) {}
 
           mBothConnectionsAreAlive = false;
 
           Logger.log("TCP Forwarding  " + mClientHostPort + " <--> " + mServerHostPort + "  stopped.");
        }
    }
 
}
/*     SessionKey,Session IV;
       SessionEncrypter sessionEncrypter = SessionEncrypter(SessionKey,SessionIV);
       OutputStream clientOut = mClientSocket.getOutputStream();
       CipherOutputStream cipherclientout = sessionEncrypter.openCipherOutputStream(clientOut);

       SessionDecrypter  sessionDecrypter = SessionDecrypter(SessionKey,Session IV);
       InputStream inputStream = new ByteArrayInputStream(ciphertext);
       CipherInputStream cipherin = sessionDecrypter.openCipherInputStream(inputStream);

 */