package com.zetra.econsig.folha.exportacao.validacao.regra;

/**
 * <p>Title: RegraVrcMediaRegistrosAlteracao</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a comparação da quantidade de registros de alteração gerados
 *                 ao longo dos últimos períodos.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraVrcMediaRegistrosAlteracao extends AbstractRegraVrcMediaRegistros {
    public RegraVrcMediaRegistrosAlteracao() {
        super("A");
    }
}
