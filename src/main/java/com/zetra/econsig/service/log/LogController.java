package com.zetra.econsig.service.log;


import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.TipoEntidade;
import com.zetra.econsig.values.CanalEnum;

/**
 * <p>Title: LogController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface LogController  {

    public void gravarLog(String line, String usuCodigo, String ipUsuario, Integer portaLogica, String tipo, String entidade, String funCodigo,
                          String codigoEntidade00, String codigoEntidade01, String codigoEntidade02, String codigoEntidade03,
                          String codigoEntidade04, String codigoEntidade05, String codigoEntidade06, String codigoEntidade07,
                          String codigoEntidade08, String codigoEntidade09, String codigoEntidade10, CanalEnum logCanal) throws LogControllerException;

    public List<TransferObject> lstTiposLog() throws LogControllerException;

    public List<TransferObject> getLogDataAtual() throws LogControllerException;

    public List<TransferObject> lstTiposEntidadesAuditoria() throws LogControllerException;

    public List<TransferObject> lstTipoEntidade(List<String> tenCodigos) throws LogControllerException;

    public List<TipoEntidade> getTiposEntidade(AcessoSistema responsavel) throws LogControllerException;

    public List<TransferObject> lstHistoricoArqLog(Date dataInicio, Date dataFim) throws LogControllerException;

    public void geraHistoricoLog(AcessoSistema responsavel) throws LogControllerException;

    public List<String> recuperaDescricoes(Class<? extends AbstractEntityHome> clazz, List<String> codigos) throws LogControllerException;
}