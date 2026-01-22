package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade TipoRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoRegistroServidorHome extends AbstractEntityHome {

    public static TipoRegistroServidor findByPrimaryKey(String trsCodigo) throws FindException {
        TipoRegistroServidor tipoRegistroServidor = new TipoRegistroServidor();
        tipoRegistroServidor.setTrsCodigo(trsCodigo);
        return find(tipoRegistroServidor, trsCodigo);
    }

    public static List<TipoRegistroServidor> list() throws FindException {
        String query = "FROM TipoRegistroServidor ";

        Map<String, Object> parameters = new HashMap<String, Object>();

        return findByQuery(query, parameters);
    }

    public static TipoRegistroServidor create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
