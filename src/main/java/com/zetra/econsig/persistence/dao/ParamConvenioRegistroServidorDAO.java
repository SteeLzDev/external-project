package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ParamConvenioRegistroServidorDAO</p>
 * <p>Description: Interface do DAO para os Parametros de convênio por regsitro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ParamConvenioRegistroServidorDAO {

    public void corrigeBloqueioServidor() throws DAOException;

    public void copiaBloqueioCnv(String rseCodNovo, String rseCodAnt) throws DAOException;

    public void copiaBloqueioCnvPorConvenio(String cnvCodOrigem, String cnvCodDestino, AcessoSistema responsavel) throws DAOException;

    public void updateCnvVincCsaSvc(String csaCodigo, String svcCodigo, List<String> vrsCodigos, AcessoSistema responsavel) throws ServidorControllerException;
}
