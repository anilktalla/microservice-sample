package com.test.gateway.service.security;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;

import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMReader;

public class PemReader {

	public static void main(String[] args) throws Exception{
		/*PEMParser pemParser = new PEMParser(new FileReader(new File("/Users/anilkumartalla/Documents/certs/netty2wayssl/client/public-client.pem")));
	    Object parsedObj = pemParser.readObject();
	    //System.out.println("PemParser returned: " + parsedObj);
	    if (parsedObj instanceof X509CertificateHolder)
	    {
	    	System.out.println(((X509CertificateHolder)parsedObj).getSubjectPublicKeyInfo().getPublicKeyData().);
	        X509CertificateHolder x509CertificateHolder = (X509CertificateHolder) parsedObj;
	        System.out.println(x509CertificateHolder.getSubjectPublicKeyInfo().getPublicKey().);
	    }
	    else
	    {
	        throw new RuntimeException("The parsed object was not an X509CertificateHolder.");
	    }*/
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		PEMReader reader = new PEMReader(new FileReader(new File("/Users/anilkumartalla/Documents/tmp/ssl-camel/client/clientcert.pem")));
		X509CertificateObject parsedObj = (X509CertificateObject)reader.readObject();
	    System.out.println("PemParser returned: " + parsedObj.getIssuerDN());
	    RSAPublicKey pubkey = (RSAPublicKey) parsedObj.getPublicKey();
		String encoded = new BigInteger(1 /* positive */, pubkey.getEncoded()).toString(16);
	    System.out.println(encoded);
	}
}
