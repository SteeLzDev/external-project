package com.zetra.econsig.webservice.rest.request;

import java.math.BigDecimal;

/**
 * <p>Title: ReservarMargemResponseRest</p>
 * <p>Description: Response Rest com dados específicos de uma reserva solicitada via API rest.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReservarMargemResponseRest extends ResponseRestRequest {

	public Integer adePrazo;

	public BigDecimal adeVlr;

	public String cnvCodigo;

	public String serCpf;

	public String corIdentificador;
}
