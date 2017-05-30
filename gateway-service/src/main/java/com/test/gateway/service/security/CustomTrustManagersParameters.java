package com.test.gateway.service.security;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.TrustManager;

import org.apache.camel.util.jsse.TrustManagersParameters;

public class CustomTrustManagersParameters extends TrustManagersParameters{

	@Override
	public TrustManager[] createTrustManagers() throws GeneralSecurityException, IOException {
		TrustManager[] managers = super.createTrustManagers();
		return new TrustManager[]{new CustomX509TrustManager(managers[0])};
	}

	
}