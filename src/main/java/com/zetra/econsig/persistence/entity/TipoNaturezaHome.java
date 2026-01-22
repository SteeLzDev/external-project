package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoNaturezaHome</p>
 * <p>Description: Classe Home para a entidade TipoNatureza</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoNaturezaHome extends AbstractEntityHome {

    public static TipoNatureza findByPrimaryKey(String tntCodigo) throws FindException {
        TipoNatureza tipoNatureza = new TipoNatureza();
        tipoNatureza.setTntCodigo(tntCodigo);
        return find(tipoNatureza, tntCodigo);
    }

    public static TipoNatureza create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
