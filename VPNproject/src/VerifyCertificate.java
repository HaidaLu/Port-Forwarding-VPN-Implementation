import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class VerifyCertificate {
    //static String CAFile = "CA.pem";
    //static String UserFile = "User.pem";
    public static void main(String[] args) throws Exception {

        //File CAFile = new File(CAFilePath);
        //File UserFile = new File(UserFilePath);

        FileInputStream cain = new FileInputStream(args[0]);
        FileInputStream userin = new FileInputStream(args[1]);

        CertificateFactory cacf = CertificateFactory.getInstance("X.509");
        CertificateFactory usercf = CertificateFactory.getInstance("X.509");

        X509Certificate outCA = (X509Certificate) cacf.generateCertificate(cain);
        X509Certificate outUser = (X509Certificate) usercf.generateCertificate(userin);

        System.out.println(outCA.getSubjectDN().getName());
        System.out.println(outUser.getSubjectDN().getName());

        boolean Caflag = true;

        try {
            outCA.checkValidity();
        } catch (Exception e) {
            e.printStackTrace();
            Caflag = false;
            System.out.println("Fail The CA certificate is invalid");
        }

        boolean Userflag = true;

        try {
            outUser.checkValidity();
        } catch (Exception e) {
            e.printStackTrace();
            Userflag = false;
            System.out.println("Fail The User certificate is invalid");
        }
        if(Caflag  & Userflag ){
            System.out.println("Pass");
            //}else if(CAflag == true & Userflag==false){
            //  System.out.println("Fail The CA certificate is invalid");
            //}else if (CAflag == false & Userflag == true) {
            //  System.out.println("Fail Both the user certificate and CA certificate are invalid");
            //} else {
            //  System.out.println("Fail The CA certificate is invalid");
        }
    }
}
