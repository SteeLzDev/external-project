package com.zetra.econsig.persistence.entity;

import java.util.List;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoEntidadeHome</p>
 * <p>Description: Classe Home para a entidade TipoEntidade</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoEntidadeHome extends AbstractEntityHome {

    public static TipoEntidade findByPrimaryKey(String tenCodigo) throws FindException {
        TipoEntidade tipoEntidade = new TipoEntidade();
        tipoEntidade.setTenCodigo(tenCodigo);
        return find(tipoEntidade, tenCodigo);
    }

    public static List<TipoEntidade> getTiposEntidade() throws FindException {
        String query = "FROM TipoEntidade";
        return findByQuery(query, null);
    }

    public static TipoEntidade create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
