package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ParamConsignatariaHome</p>
 * <p>Description: Classe Home para a entidade ParamConsignataria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamConsignatariaHome extends AbstractEntityHome {

    public static ParamConsignataria findParamCsa(String tpaCodigo, String csaCodigo) throws FindException {
        String query = "FROM ParamConsignataria AS p WHERE p.id.tpaCodigo = :tpaCodigo AND p.id.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tpaCodigo", tpaCodigo);
        parameters.put("csaCodigo", csaCodigo);

        List<ParamConsignataria> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<ParamConsignataria> findByCsa(String csaCodigo) throws FindException {
        String query = "FROM ParamConsignataria AS p WHERE p.id.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("csaCodigo", csaCodigo);

        List<ParamConsignataria> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result;
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static ParamConsignataria findByPrimaryKey(ParamConsignatariaId pk) throws FindException {
        ParamConsignataria paramCsa = new ParamConsignataria();
        paramCsa.setId(pk);
        return find(paramCsa, pk);
    }

    public static ParamConsignataria create(String tpaCodigo, String csaCodigo, String pcsVlr) throws CreateException {
        ParamConsignataria bean = new ParamConsignataria();

        ParamConsignatariaId id = new ParamConsignatariaId();
        id.setCsaCodigo(csaCodigo);
        id.setTpaCodigo(tpaCodigo);
        bean.setId(id);
        bean.setPcsVlr(pcsVlr);

        create(bean);
        return bean;
    }

}
