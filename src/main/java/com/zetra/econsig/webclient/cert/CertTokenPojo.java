package com.zetra.econsig.webclient.cert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: CertTokenPojo</p>
 * <p>Description: Pojo para tratamento dos dados retornados para provedor de serviço do CERT.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 26071 $
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CertTokenPojo {
	
	private String id;

    private String data;

}
