package com.test.gateway.service.security;

import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class CustomX509TrustManager implements X509TrustManager {

	private static final String PUBLIC_KEY="30820122300d06092a864886f70d01010105000382010f003082010a0282010100ba41242fd1b370705bb6fe63fd6b404d33051bb17ef4c5d2ab969ac2dc36a787025c41e40029dacc8e24c311122a88bb2de0c440447e5e202402ef7e3412262ae8bd385df461f96ee9fbd94c365b2821f44c559b865e40267f893c71b7c2eaa9966135b44c745a6ea40969477ec5ab82c0bc74b5a6eb39fddd51d1e851609790c976970475f62b90cc7a594c5f0898d970753cb6e74530fa013240d68dbd061e9c5b9d8fc317668e19845e2893e88df3248c7f5587fba054c8a4c254fc78fcb099cb2c4e459f5e3ce92b506578bb8ce25853ac8d565b0e28411167e3f76841f317c4c01dc2dd34b6c2cb79a4977ece95a33b6bc7e77db80b229c89575695d2f50203010001";
	private X509TrustManager trustManager;

	public CustomX509TrustManager(TrustManager trustManager) {
		this.trustManager = (X509TrustManager) trustManager;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		if (chain == null) {
			throw new IllegalArgumentException("checkClientTrusted: X509Certificate array is null");
		}

		assert (chain.length > 0);
		if (!(chain.length > 0)) {
			throw new IllegalArgumentException("checkClientTrusted: X509Certificate is empty");
		}

		assert (null != authType && authType.equalsIgnoreCase("RSA"));
		if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {
			throw new CertificateException("checkClientTrusted: AuthType is not RSA");
		}
		
		// Hack ahead: BigInteger and toString(). We know a DER encoded Public
		// Key starts with 0x30 (ASN.1 SEQUENCE and CONSTRUCTED), so there is
		// no leading 0x00 to drop.
		RSAPublicKey pubkey = (RSAPublicKey) chain[0].getPublicKey();
		String encoded = new BigInteger(1 /* positive */, pubkey.getEncoded()).toString(16);

		// Pin it!
		//boolean isValid = trustStoreService.validatePublicKeyByDN(chain[0].getIssuerDN().toString(), encoded);
		final boolean isValid = PUBLIC_KEY.equalsIgnoreCase(encoded);
		assert (isValid);
		if (!isValid) {
			throw new CertificateException("checkClientTrusted: invalid public key");
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		if (chain == null) {
			throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
		}

		if (!(chain.length > 0)) {
			throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
		}

		if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {
			throw new CertificateException("checkServerTrusted: AuthType is not RSA");
		}

		// Perform customary SSL/TLS checks
		TrustManagerFactory tmf;
		try {
			tmf = TrustManagerFactory.getInstance("X509");
			tmf.init((KeyStore) null);

			for (TrustManager trustManager : tmf.getTrustManagers()) {
				((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
			}

		} catch (Exception e) {
			throw new CertificateException(e);
		}
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return trustManager.getAcceptedIssuers();
	}

}
