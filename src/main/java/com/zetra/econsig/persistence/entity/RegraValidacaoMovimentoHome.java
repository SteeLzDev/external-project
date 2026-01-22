package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RegraValidacaoMovimentoHome</p>
 * <p>Description: Classe Home para a entidade RegraValidacaoMovimento</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoMovimentoHome extends AbstractEntityHome {


    public static RegraValidacaoMovimento findByPrimaryKey(String rvmCodigo) throws FindException {
        throw new FindException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static RegraValidacaoMovimento create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }


}
