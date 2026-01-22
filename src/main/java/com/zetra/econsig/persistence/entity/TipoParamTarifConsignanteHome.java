package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoParamTarifConsignanteHome</p>
 * <p>Description: Classe Home para a entidade TipoParamTarifCse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoParamTarifConsignanteHome extends AbstractEntityHome {

    public static TipoParamTarifCse findByPrimaryKey(String tptCodigo) throws FindException {
        TipoParamTarifCse tipoParamTarifConsignante = new TipoParamTarifCse();
        tipoParamTarifConsignante.setTptCodigo(tptCodigo);
        return find(tipoParamTarifConsignante, tptCodigo);
    }

    public static TipoParamTarifCse create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
