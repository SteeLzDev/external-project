package com.zetra.econsig.webclient.crm;

/**
 * <p>Title: CRMClient</p>
 * <p>Description: Pojo para tratamento dos dados retornados para provedor de serviço do CRM.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 26071 $
 */
public class ServiceProviderPojo {
	
	public String id;
	public String name;
	public String integrationCode;
	public String registrationNumber;

	public ServiceProviderPojo() {
		super();
	}

	public ServiceProviderPojo(String id, String name, String integrationCode, String registrationNumber) {
		super();
		this.id = id;
		this.name = name;
		this.integrationCode = integrationCode;
		this.registrationNumber = registrationNumber;
	}
}
