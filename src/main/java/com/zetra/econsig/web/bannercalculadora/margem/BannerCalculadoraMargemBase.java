package com.zetra.econsig.web.bannercalculadora.margem;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: BannerCalculadoraMargem</p>
 * <p>Description: Interface para implementação de customizações para exibição da calculadora de margem na página inicial do Servidor
 * de importação de margem, retorno, transferidos e crítica.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public abstract class BannerCalculadoraMargemBase {

    public static final Character NAO_EXIBE = '0';

	public String montarBannerCalculadoraMargem (String rseCodigo, AcessoSistema responsavel) throws ZetraException {
		return "";
	}
}
