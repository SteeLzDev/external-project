package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TipoGrupoConsignatariaHome</p>
 * <p>Description: Classe Home para a entidade TipoGrupoConsignataria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoGrupoConsignatariaHome extends AbstractEntityHome {

    public static TipoGrupoConsignataria findByPrimaryKey(String tgcCodigo) throws FindException {
        TipoGrupoConsignataria tipoGrupoConsignataria = new TipoGrupoConsignataria();
        tipoGrupoConsignataria.setTgcCodigo(tgcCodigo);
        return find(tipoGrupoConsignataria, tgcCodigo);
    }

    public static TipoGrupoConsignataria findByIdn(String tgcIdentificador) throws FindException {
        String query = "FROM TipoGrupoConsignataria tgc WHERE tgc.tgcIdentificador = :tgcIdentificador";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tgcIdentificador", tgcIdentificador);

        List<TipoGrupoConsignataria> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static TipoGrupoConsignataria create(String tgcIdentificador, String tgcDescricao) throws CreateException {
        TipoGrupoConsignataria bean = new TipoGrupoConsignataria();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setTgcCodigo(objectId);
            bean.setTgcIdentificador(tgcIdentificador);
            bean.setTgcDescricao(tgcDescricao);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
        create(bean);
        return bean;
    }
}
