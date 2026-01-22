package com.zetra.econsig.persistence.dao.generic;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.dao.ConsigBIDAO;

/**
 * <p>Title: GenericConsigBIDAO</p>
 * <p>Description: Implementacao Genérica do DAO do módulo BI. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericConsigBIDAO implements ConsigBIDAO {

    @Override
    public void atualizarBI(boolean populaDados) throws DAOException {
        atualizarDimensoes();
        atualizarTabelasAuxiliares();
        atualizarFatoContrato(populaDados);
        atualizarFatoParcela(populaDados);
        atualizarFatoMargem(populaDados);
    }
}
