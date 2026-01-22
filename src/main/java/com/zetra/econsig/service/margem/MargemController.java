package com.zetra.econsig.service.margem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.dao.ServidorDAO;

/**
 * <p>Title: MargemController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface MargemController {
    public MargemTO findMargem(MargemTO margem, AcessoSistema responsavel) throws MargemControllerException;

    public void updateMargem(MargemTO margem, AcessoSistema responsavel) throws MargemControllerException;

    public List<TransferObject> lstMargem(AcessoSistema responsavel) throws MargemControllerException;

    public List<MargemTO> lstMargemRaiz(AcessoSistema responsavel) throws MargemControllerException;

    public List<MargemTO> lstMargemRaiz(boolean alteracaoMultiplaAde, AcessoSistema responsavel) throws MargemControllerException;

    public List<TransferObject> lstMargemReservaGap(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws MargemControllerException;

    public List<TransferObject> lstMargensIncidentesTransferencia(String csaCodigo, String orgCodigo, String rseCodigo, String estCodigo, String papCodigo, AcessoSistema responsavel) throws MargemControllerException;

    public List<TransferObject> lstCasamentoMargem(AcessoSistema responsavel) throws MargemControllerException;

    public Long createHistoricoMargem(TransferObject historicoMargem, Map<Short, Map<String, BigDecimal>> mediaMargem, List<Short> lstMarCodigosExtra, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws MargemControllerException;

    public List<TransferObject> lstHistoricoProcMargem(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws MargemControllerException;

    public List<TransferObject> lstHistoricoMediaMargem(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws MargemControllerException;

    public void recalculaMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws MargemControllerException;

    public void recalculaMargem(String tipoEntidade, List<String> entCodigos, ServidorDAO servidor, boolean atualizarAdeValor, boolean calcularPeriodo, AcessoSistema responsavel) throws MargemControllerException;

    public void recalculaMargemComHistorico(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws MargemControllerException;

    public Date recuperaPenultimoPeriodoHistoricoMargem(AcessoSistema responsavel) throws MargemControllerException;

    public void criaArquivoMargemOrigemServicoExterno(String urlSistemaExterno, AcessoSistema responsavel) throws MargemControllerException;

    public List<TransferObject> lstMargemComServicoAtivo(AcessoSistema responsavel) throws MargemControllerException;

    public TransferObject lstMargemIncideEmprestimo(String rseCodigo, AcessoSistema responsavel) throws MargemControllerException;

    public BigDecimal obtemVlrTotalConsignacoesCalculoSalario(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws MargemControllerException;

    public void createControleDocumentoMargem(String rseCodigo, String localArquivo, String chave, AcessoSistema responsavel) throws MargemControllerException;

    public TransferObject validaDocumentoMargem(String matricula, String cpf, String chave, AcessoSistema responsavel) throws MargemControllerException;
}
