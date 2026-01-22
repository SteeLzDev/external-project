package com.zetra.econsig.folha.exportacao.validacao.regra;


/**
 * <p>Title: RegraQtdRegistros</p>
 * <p>Description: Classe abstrata com a implementação MYSQL da regra com a da quantidade de registros gerados por tipo.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraQtdRegistros extends RegraConciliacaoRegistros {

    public RegraQtdRegistros() {
        super();
        setListaContratos(false);
    }
}
