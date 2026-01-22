package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: ConvenioVinculoRegistroHome</p>
 * <p>Description: Classe Home para a entidade ConvenioVinculoRegistro</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConvenioVinculoRegistroHome extends AbstractEntityHome {

    public static ConvenioVinculoRegistro findByPrimaryKey(ConvenioVinculoRegistroId id) throws FindException {
        ConvenioVinculoRegistro cvr = new ConvenioVinculoRegistro();
        cvr.setId(id);
        return find(cvr, id);
    }

    public static List<ConvenioVinculoRegistro> findByCsaSvcCodigo(String csaCodigo, String svcCodigo) throws FindException {
        String query = "SELECT cvr FROM ConvenioVinculoRegistro cvr JOIN FETCH cvr.vinculoRegistroServidor WHERE cvr.csaCodigo = :csaCodigo AND cvr.svcCodigo = :svcCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("svcCodigo", svcCodigo);

        return findByQuery(query, parameters);
    }

    public static ConvenioVinculoRegistro create(String vrsCodigo, String svcCodigo, String csaCodigo) throws CreateException {
        ConvenioVinculoRegistroId id = new ConvenioVinculoRegistroId(vrsCodigo, svcCodigo, csaCodigo);
        ConvenioVinculoRegistro cvr = new ConvenioVinculoRegistro();
        cvr.setId(id);
        create(cvr);
        return cvr;
    }
}
