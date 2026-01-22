package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: ParamOrgaoHome</p>
 * <p>Description: Classe Home para a entidade ParamOrgao</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamOrgaoHome extends AbstractEntityHome {

    public static ParamOrgao findByPrimaryKey(String orgCodigo, String taoCodigo) throws FindException {
        ParamOrgaoId id = new ParamOrgaoId(orgCodigo, taoCodigo);
        ParamOrgao paramOrgao = new ParamOrgao();
        paramOrgao.setId(id);
        return find(paramOrgao, id);
    }

    public static List<ParamOrgao> findByEstCodigoTaoCodigo(String estCodigo, String taoCodigo) throws FindException {
        String query = "FROM ParamOrgao pao WHERE pao.id.taoCodigo = :taoCodigo AND pao.orgao.estabelecimento.estCodigo = :estCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("taoCodigo", taoCodigo);
        parameters.put("estCodigo", estCodigo);

        return findByQuery(query, parameters);
    }

    public static ParamOrgao create(String orgCodigo, String taoCodigo, String paoVlr) throws CreateException {
        ParamOrgaoId id = new ParamOrgaoId(orgCodigo, taoCodigo);
        ParamOrgao bean = new ParamOrgao();

        bean.setId(id);
        bean.setPaoVlr(paoVlr);

        create(bean);
        return bean;
    }
}
