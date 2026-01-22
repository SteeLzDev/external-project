package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ParametrosDAO</p>
 * <p>Description: Interface do DAO para os Parametros (Tarifação, Serviço, ...)</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ParametrosDAO {

    // Parametros de serviços
    public void updateParamSvcCsa(List<TransferObject> parametros) throws DAOException;

    public void deleteParamIgualCse(List<TransferObject> parametros) throws DAOException;

    public void deleteParamIgualCseRse(List<TransferObject> parametros) throws DAOException;

    public void ativaParamSvcCsa(String svcCodigo, String csaCodigo, List<String> tpsCodigos) throws DAOException;

    public void updateParamSvcSobrepoe(List<TransferObject> parametros) throws DAOException;
    
    public void updateParamSvcCor(List<TransferObject> parametros) throws DAOException;

    public void deleteParamIgualCsa(List<TransferObject> parametros) throws DAOException;
}
