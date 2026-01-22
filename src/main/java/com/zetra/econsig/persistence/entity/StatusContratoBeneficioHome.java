package com.zetra.econsig.persistence.entity;

import java.util.List;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: CalculoBeneficioHome </p>
 * <p>Description: Classe Home da entidade StatusContratoBeneficio.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class StatusContratoBeneficioHome extends AbstractEntityHome {

    public static List<StatusContratoBeneficio> listAll() throws FindException {
        String query = "FROM StatusContratoBeneficio scb";
        List<StatusContratoBeneficio> statusContratoBeneficio = findByQuery(query, null);

        return statusContratoBeneficio;
    }

    public static StatusContratoBeneficio findByPrimaryKey(String scbCodigo) throws FindException {
        StatusContratoBeneficio statusContratoBeneficio = new StatusContratoBeneficio();
        statusContratoBeneficio.setScbCodigo(scbCodigo);

        return find(statusContratoBeneficio, scbCodigo);
    }
}
