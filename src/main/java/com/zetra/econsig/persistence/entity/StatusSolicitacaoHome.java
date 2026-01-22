package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: StatusSolicitacaoHome</p>
 * <p>Description: Classe Home para a entidade StatusSolicitacao</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusSolicitacaoHome extends AbstractEntityHome {

    public static StatusSolicitacao findByPrimaryKey(String ssoCodigo) throws FindException {
        StatusSolicitacao statusSolicitacao = new StatusSolicitacao();
        statusSolicitacao.setSsoCodigo(ssoCodigo);
        return find(statusSolicitacao, ssoCodigo);
    }

    public static StatusSolicitacao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
