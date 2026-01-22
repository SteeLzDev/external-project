package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ParamSistConsignanteHome</p>
 * <p>Description: Classe Home para a entidade ParamSistConsignante</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSistConsignanteHome extends AbstractEntityHome {

    public static List<ParamSistConsignante> findByCse(String cseCodigo) throws FindException {
        String query = "FROM ParamSistConsignante p WHERE p.cseCodigo = :cseCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cseCodigo", cseCodigo);

        List<ParamSistConsignante> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result;
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static ParamSistConsignante findByPrimaryKey(String tpcCodigo) throws FindException {
        ParamSistConsignanteId id = new ParamSistConsignanteId();
        id.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
        id.setTpcCodigo(tpcCodigo);
        ParamSistConsignante paramSistCse = new ParamSistConsignante();
        paramSistCse.setId(id);
        return find(paramSistCse, id);
    }

    public static ParamSistConsignante create(String tpcCodigo, String cseCodigo, String psiVlr) throws CreateException {
        ParamSistConsignante bean = new ParamSistConsignante();

        ParamSistConsignanteId id = new ParamSistConsignanteId();
        id.setCseCodigo(cseCodigo);
        id.setTpcCodigo(tpcCodigo);
        bean.setId(id);
        bean.setPsiVlr(psiVlr);

        create(bean);
        return bean;
    }


}
