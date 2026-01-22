package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: OrigemSolicitacaoHome</p>
 * <p>Description: Classe Home para a entidade OrigemSolicitacao</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OrigemSolicitacaoHome extends AbstractEntityHome {

    public static OrigemSolicitacao findByPrimaryKey(String osoCodigo) throws FindException {
        OrigemSolicitacao origemSolicitacao = new OrigemSolicitacao();
        origemSolicitacao.setOsoCodigo(osoCodigo);
        return find(origemSolicitacao, osoCodigo);
    }

    public static TipoSolicitacao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
