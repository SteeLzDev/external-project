package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoBaseCalculoHome</p>
 * <p>Description: Classe Home para a entidade TipoBaseCalculo</p>
 * <p>Copyright: Copyright (c) 2002-20014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoBaseCalculoHome extends AbstractEntityHome {

    public static TipoBaseCalculo findByPrimaryKey(String tbcCodigo) throws FindException {
        TipoBaseCalculo tipoBaseCalculo = new TipoBaseCalculo();
        tipoBaseCalculo.setTbcCodigo(tbcCodigo);
        return find(tipoBaseCalculo, tbcCodigo);
    }

    public static TipoBaseCalculo create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
