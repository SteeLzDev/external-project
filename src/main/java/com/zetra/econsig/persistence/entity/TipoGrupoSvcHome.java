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
 * <p>Title: TipoGrupoSvcHome</p>
 * <p>Description: Classe Home para a entidade TipoGrupoSvc</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoGrupoSvcHome extends AbstractEntityHome {

    public static TipoGrupoSvc findByPrimaryKey(String tgsCodigo) throws FindException {
        TipoGrupoSvc tipoGrupoSvc = new TipoGrupoSvc();
        tipoGrupoSvc.setTgsCodigo(tgsCodigo);
        return find(tipoGrupoSvc, tgsCodigo);
    }

    public static TipoGrupoSvc findByIdn(String tgsIdentificador) throws FindException {
        String query = "FROM TipoGrupoSvc tgs WHERE tgs.tgsIdentificador = :tgsIdentificador";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tgsIdentificador", tgsIdentificador);

        List<TipoGrupoSvc> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static TipoGrupoSvc create(String tgsGrupo, Integer tgsQuantidade, Integer tgsQuantidadePorCsa, String tgsIdentificador) throws CreateException {
        TipoGrupoSvc bean = new TipoGrupoSvc();
        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setTgsCodigo(objectId);
            bean.setTgsGrupo(tgsGrupo);
            bean.setTgsQuantidade(tgsQuantidade);
            bean.setTgsQuantidadePorCsa(tgsQuantidadePorCsa);
            bean.setTgsIdentificador(tgsIdentificador);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
        create(bean);
        return bean;
    }
}
