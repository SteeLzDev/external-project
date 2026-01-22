package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: NivelSegurancaParamSistHome</p>
 * <p>Description: Classe Home para a entidade NivelSegurancaParamSist</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NivelSegurancaParamSistHome extends AbstractEntityHome {

    public static NivelSegurancaParamSist findByPrimaryKey(NivelSegurancaParamSistId id) throws FindException {
        NivelSegurancaParamSist nsp = new NivelSegurancaParamSist();
        nsp.setId(id);
        return find(nsp, id);
    }

    public static List<NivelSegurancaParamSist> findByTipoParamSist(String tpcCodigo) throws FindException {
        String query = "FROM NivelSegurancaParamSist n WHERE n.tpcCodigo = :tpcCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tpcCodigo", tpcCodigo);

        return findByQuery(query, parameters);
    }

    public static List<NivelSegurancaParamSist> findByNivelSeguranca(String nsgCodigo) throws FindException {
        String query = "FROM NivelSegurancaParamSist n WHERE n.nsgCodigo = :nsgCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nsgCodigo", nsgCodigo);

        return findByQuery(query, parameters);
    }

    public static NivelSegurancaParamSist create(String nsgCodigo, String tpcCodigo, String nspVlrEsperado) throws CreateException {
        NivelSegurancaParamSist bean = new NivelSegurancaParamSist();

        NivelSegurancaParamSistId id = new NivelSegurancaParamSistId();
        id.setNsgCodigo(nsgCodigo);
        id.setTpcCodigo(tpcCodigo);
        bean.setId(id);
        bean.setNspVlrEsperado(nspVlrEsperado);

        create(bean);
        return bean;
    }
}
