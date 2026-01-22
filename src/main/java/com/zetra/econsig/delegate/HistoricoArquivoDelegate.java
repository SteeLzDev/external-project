package com.zetra.econsig.delegate;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: HistoricoArquivoDelegate</p>
 * <p>Description: Delegate de Histórico de Arquivo</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoArquivoDelegate extends AbstractDelegate {

    private HistoricoArquivoController historicoArquivoController = null;

    public HistoricoArquivoDelegate() {
    }

    private HistoricoArquivoController getHistoricoArquivoController() throws HistoricoArquivoControllerException {
        try {
            if (historicoArquivoController == null) {
                historicoArquivoController = ApplicationContextProvider.getApplicationContext().getBean(HistoricoArquivoController.class);
            }
            return historicoArquivoController;
        } catch (Exception ex) {
            throw new HistoricoArquivoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public Long createHistoricoArquivo(String tipoEntidade, String codigoEntidade, TipoArquivoEnum tipoArquivo, String harNomeArquivo, String harObs, Date harDataProc, Date harPeriodo, String harResultadoProc, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        return getHistoricoArquivoController().createHistoricoArquivo(tipoEntidade, codigoEntidade, tipoArquivo, harNomeArquivo, harObs, harDataProc, harPeriodo, harResultadoProc, null, responsavel);
    }

    public Long createHistoricoArquivo(String tipoEntidade, String codigoEntidade, TipoArquivoEnum tipoArquivo, String harNomeArquivo, String harObs, Date harDataProc, Date harPeriodo, String harResultadoProc, String funCodigo, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        return getHistoricoArquivoController().createHistoricoArquivo(tipoEntidade, codigoEntidade, tipoArquivo, harNomeArquivo, harObs, harDataProc, harPeriodo, harResultadoProc, funCodigo, responsavel);
    }

    public void updateHistoricoArquivo(Long harCodigo, TipoArquivoEnum tipoArquivo, String harObs, Date harPeriodo, String harResultadoProc, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        getHistoricoArquivoController().updateHistoricoArquivo(harCodigo, tipoArquivo, harObs, harPeriodo, harResultadoProc, responsavel);
    }

    public List<TransferObject> lstTiposArquivo(AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        return getHistoricoArquivoController().lstTiposArquivo(responsavel);
    }

    public List<TransferObject> lstTiposArquivoByTarCodigos(List<String> tarCodigos, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        return getHistoricoArquivoController().lstTiposArquivoByTarCodigos(tarCodigos, responsavel);
    }
}
