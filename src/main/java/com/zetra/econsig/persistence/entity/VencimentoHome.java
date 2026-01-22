package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: VencimentoHome</p>
 * <p>Description: Classe Home para a entidade Vencimento</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VencimentoHome extends AbstractEntityHome {

    public static Vencimento findByPrimaryKey(String vctCodigo) throws FindException {
        Vencimento vencimento = new Vencimento();
        vencimento.setVctCodigo(vctCodigo);
        return find(vencimento, vctCodigo);
    }

    public static Vencimento create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
