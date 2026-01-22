package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;

/**
 * <p>Title: DestinatarioEmailCsaSvcHome</p>
 * <p>Description: Classe Home para a entidade DestinatarioEmailCsaSvc</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DestinatarioEmailCsaSvcHome extends AbstractEntityHome {
	public static DestinatarioEmailCsaSvc findByPrimaryKey(String funCodigo, String papCodigo, String csaCodigo, String svcCodigo) throws FindException {
        return findByPrimaryKey(new DestinatarioEmailCsaSvcId(funCodigo, papCodigo, csaCodigo, svcCodigo));
    }

    public static DestinatarioEmailCsaSvc findByPrimaryKey(DestinatarioEmailCsaSvcId pk) throws FindException {
    	DestinatarioEmailCsaSvc bean = new DestinatarioEmailCsaSvc();
        bean.setId(pk);
        return find(bean, pk);
    }

    public static DestinatarioEmailCsaSvc create(String funCodigo, String papCodigo, String csaCodigo, String svcCodigo) throws CreateException {
    	DestinatarioEmailCsaSvc bean = new DestinatarioEmailCsaSvc();

    	DestinatarioEmailCsaSvcId id = new DestinatarioEmailCsaSvcId();
        id.setFunCodigo(funCodigo);
        id.setPapCodigo(papCodigo);
        id.setCsaCodigo(csaCodigo);
        id.setSvcCodigo(svcCodigo);
        bean.setId(id);

        create(bean);
        return bean;
    }
    
    public static void removeAllByFuncaoPapelCsa(String funCodigo, String papCodigo, String csaCodigo) throws FindException, RemoveException {
        final String query = "FROM DestinatarioEmailCsaSvc dcs WHERE dcs.funcao.funCodigo = :funCodigo and dcs.papel.papCodigo = :papCodigo and dcs.consignataria.csaCodigo = :csaCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("funCodigo", funCodigo);
        parameters.put("papCodigo", papCodigo);
        parameters.put("csaCodigo", csaCodigo);

        final List<DestinatarioEmailCsaSvc> resultado = findByQuery(query, parameters);

        if ((resultado != null) && !resultado.isEmpty()) {
            for (final DestinatarioEmailCsaSvc dcs : resultado) {
                remove(dcs);
            }
        }
    }
}
