package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoOcorrenciaHome</p>
 * <p>Description: Classe Home para a entidade TipoOcorrencia</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoOcorrenciaHome extends AbstractEntityHome {

    public static TipoOcorrencia findByPrimaryKey(String tocCodigo) throws FindException {
        TipoOcorrencia tipoOcorrencia = new TipoOcorrencia();
        tipoOcorrencia.setTocCodigo(tocCodigo);
        return find(tipoOcorrencia, tocCodigo);
    }

    public static TipoOcorrencia create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
