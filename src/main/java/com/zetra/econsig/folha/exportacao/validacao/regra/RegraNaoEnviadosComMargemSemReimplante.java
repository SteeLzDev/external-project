package com.zetra.econsig.folha.exportacao.validacao.regra;
/**
 * <p>Title: RegraNaoEnviadosComMargemSemReimplante</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação se
 *    existe algum contrato que não foi enviado mesmo a matrícula tendo margem,
 *    não considerando o reimplante.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RegraNaoEnviadosComMargemSemReimplante extends RegraNaoEnviadosComMargem {

    public RegraNaoEnviadosComMargemSemReimplante() {
        super();
        verificaReimplante = false;
    }
}
