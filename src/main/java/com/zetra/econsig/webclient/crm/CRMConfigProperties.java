package com.zetra.econsig.webclient.crm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * <p>Title: CRMConfigProperties</p>
 * <p>Description: Propriedades para configuração de acesso ao microserviço CRM.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Configuration
@PropertySource("classpath:CRMProperties.properties")
@ConfigurationProperties(prefix = "crm")
public class CRMConfigProperties {

	private String sslKeyStore;
	private String sslKeyStorePassword;
	private String sslKeyStoreProtocol;
	private String sslKeyStoreType;
	private String sslClientAuth;
	
	public String getSslKeyStore() {
		return sslKeyStore;
	}
	
	public void setSslKeyStore(String sslKeyStore) {
		this.sslKeyStore = sslKeyStore;
	}
	
	public String getSslKeyStorePassword() {
		return sslKeyStorePassword;
	}
	
	public void setSslKeyStorePassword(String sslKeyStorePassword) {
		this.sslKeyStorePassword = sslKeyStorePassword;
	}
	
	public String getSslKeyStoreProtocol() {
		return sslKeyStoreProtocol;
	}
	
	public void setSslKeyStoreProtocol(String sslKeyStoreProtocol) {
		this.sslKeyStoreProtocol = sslKeyStoreProtocol;
	}
	
	public String getSslKeyStoreType() {
		return sslKeyStoreType;
	}
	
	public void setSslKeyStoreType(String sslKeyStoreType) {
		this.sslKeyStoreType = sslKeyStoreType;
	}
	
	public String getSslClientAuth() {
		return sslClientAuth;
	}
	
	public void setSslClientAuth(String sslClientAuth) {
		this.sslClientAuth = sslClientAuth;
	}
	
}
