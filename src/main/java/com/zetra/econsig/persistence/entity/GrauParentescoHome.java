package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: GrauParentescoHome </p>
 * <p>Description: Classe Home da entidade Grau Parentesco.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GrauParentescoHome extends AbstractEntityHome {

    public static GrauParentesco findByPrimaryKey(String grpCodigo) throws FindException {
        GrauParentesco grauParentesco = new GrauParentesco();
        grauParentesco.setGrpCodigo(grpCodigo);

        return find(grauParentesco, grpCodigo);
    }
}
