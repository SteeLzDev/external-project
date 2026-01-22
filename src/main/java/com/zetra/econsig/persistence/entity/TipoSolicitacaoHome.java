package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoSolicitacaoHome</p>
 * <p>Description: Classe Home para a entidade TipoSolicitacao</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoSolicitacaoHome extends AbstractEntityHome {

    public static TipoSolicitacao findByPrimaryKey(String tisCodigo) throws FindException {
        TipoSolicitacao tipoSolicitacao = new TipoSolicitacao();
        tipoSolicitacao.setTisCodigo(tisCodigo);
        return find(tipoSolicitacao, tisCodigo);
    }

    public static TipoSolicitacao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
