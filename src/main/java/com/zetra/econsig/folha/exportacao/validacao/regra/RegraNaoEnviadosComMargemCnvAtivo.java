package com.zetra.econsig.folha.exportacao.validacao.regra;
/**
 * <p>Title: RegraNaoEnviadosComMargemCnvAtivo</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação se
 *    existe algum contrato que não foi enviado mesmo a matrícula tendo margem,
 *    considerando apenas convênios ativos.</p>
 * <p>Copyright: Copyright (c) 2003-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RegraNaoEnviadosComMargemCnvAtivo extends RegraNaoEnviadosComMargem {

    public RegraNaoEnviadosComMargemCnvAtivo() {
        super();
        verificaCnvAtivo = true;
    }
}
