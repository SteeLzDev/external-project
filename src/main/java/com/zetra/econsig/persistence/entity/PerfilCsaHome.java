package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PerfilCsaHome</p>
 * <p>Description: Classe Home para a entidade PerfilCsa</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PerfilCsaHome extends AbstractEntityHome {

    public static PerfilCsa findByPrimaryKey(PerfilCsaId pk) throws FindException {
        PerfilCsa perfilCsa = new PerfilCsa();
        perfilCsa.setId(pk);
        return find(perfilCsa, pk);
    }

    public static PerfilCsa create(String csaCodigo, String perCodigo, Short pcaAtivo) throws CreateException {
        PerfilCsa bean = new PerfilCsa();

        PerfilCsaId id = new PerfilCsaId();
        id.setCsaCodigo(csaCodigo);
        id.setPerCodigo(perCodigo);
        bean.setId(id);
        bean.setPcaAtivo(pcaAtivo);

        create(bean);
        return bean;
    }

    public static List<PerfilCsa> findByPerCodigo(String perCodigo) throws FindException {
        String query = "FROM PerfilCsa AS p WHERE p.perfil.perCodigo = :perCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("perCodigo", perCodigo);

        return findByQuery(query, parameters);
    }
}
