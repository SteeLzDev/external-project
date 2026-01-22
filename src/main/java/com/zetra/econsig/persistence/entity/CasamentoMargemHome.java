package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CasamentoMargemHome</p>
 * <p>Description: Classe Home para a entidade CasamentoMargem</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CasamentoMargemHome extends AbstractEntityHome {

    public static CasamentoMargem findByPrimaryKey(CasamentoMargemId casamentoMargemId) throws FindException {
        CasamentoMargem casamentoMargem = new CasamentoMargem();
        casamentoMargem.setId(casamentoMargemId);
        return find(casamentoMargem, casamentoMargemId);
    }

    public static CasamentoMargem create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
