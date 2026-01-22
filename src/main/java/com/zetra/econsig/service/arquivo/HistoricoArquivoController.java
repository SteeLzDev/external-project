package com.zetra.econsig.service.arquivo;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: HistoricoArquivoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface HistoricoArquivoController {

    public Long createHistoricoArquivo(String tipoEntidade, String codigoEntidade, TipoArquivoEnum tipoArquivo, String harNomeArquivo, String harObs, Date harDataProc, Date harPeriodo, String harResultadoProc, AcessoSistema responsavel) throws HistoricoArquivoControllerException;

    public Long createHistoricoArquivo(String tipoEntidade, String codigoEntidade, TipoArquivoEnum tipoArquivo, String harNomeArquivo, String harObs, Date harDataProc, Date harPeriodo, String harResultadoProc, String funCodigo, AcessoSistema responsavel) throws HistoricoArquivoControllerException;

    public void updateHistoricoArquivo(Long harCodigo, TipoArquivoEnum tipoArquivo, String harObs, Date harPeriodo, String harResultadoProc, AcessoSistema responsavel) throws HistoricoArquivoControllerException;

    public List<TransferObject> lstTiposArquivo(AcessoSistema responsavel) throws HistoricoArquivoControllerException;

    public List<TransferObject> lstHistoricoArquivo(List<String> tarCodigos, Date harPeriodo, String tipoEntidade, AcessoSistema responsavel) throws HistoricoArquivoControllerException;

    public List<TransferObject> lstHistoricoArquivoUpload(List<String> tarCodigos, Date harPeriodo, String tipoEntidade, String funCodigo, AcessoSistema responsavel) throws HistoricoArquivoControllerException;

    public List<TransferObject> lstTiposArquivoByTarCodigos(List<String> tarCodigos, AcessoSistema responsavel) throws HistoricoArquivoControllerException;

    public List<TransferObject> lstHistoricoArquivosDashboard(String csaCodigo, Date filterPeriodo, AcessoSistema responsavel) throws HistoricoArquivoControllerException;
}
