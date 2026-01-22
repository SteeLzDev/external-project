package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ValidacaoFaturamentoBeneficioDAO</p>
 * <p>Description: Interface do DAO de validação de faturamento de benefícios</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ValidacaoFaturamentoBeneficioDAO {

    public void apagarPreviaFaturamentoOperadora(String fatCodigo) throws DAOException;

    public void copiarCamposLoteFaturamentoBeneficio(String fatCodigo) throws DAOException;

    public List<TransferObject> validarFaturamentoBeneficio(String fatCodigo, boolean validarPrevia) throws DAOException;

}
