package com.zetra.econsig.service.folha;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.ImpRetornoDAO;
import com.zetra.econsig.persistence.dao.ParcelaDescontoDAO;

/**
 * <p>Title: ImpRetornoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImpRetornoController {

    public void criarTabelasImportacaoRetorno(AcessoSistema responsavel) throws ImpRetornoControllerException;

    public void iniciarCargaArquivoRetorno(String nomeArquivoEntrada, boolean mantemArqRetorno, List<String> orgCodigos, List<String> estCodigos, ImpRetornoDAO retDAO, AcessoSistema responsavel) throws ImpRetornoControllerException;

    public void importarMargemRetorno(String nomeArquivoMargem, String nomeArquivoRetorno, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException;

    public void importarRetornoIntegracao(String nomeArquivo, String orgCodigo, String estCodigo,
            String tipo, java.sql.Date periodoRetAtrasado, AcessoSistema responsavel) throws ImpRetornoControllerException;

    public int carregaArquivoRetorno(String nomeArquivo, AcessoSistema responsavel)
    		throws ImpRetornoControllerException;

    public void finalizarIntegracaoFolha(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public void desfazerUltimoRetorno(String orgCodigo, String estCodigo, boolean recalcularMargem, boolean desfazerMovimento, String[] parcelas, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public java.util.Date getUltimoPeriodoRetorno(String orgCodigo, String estCodigo, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public boolean existeOutroPeriodoExportado(String orgCodigo, String estCodigo, java.util.Date periodo, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public List<TransferObject> getLinhasSemProcessamento(AcessoSistema responsavel)
    		throws ImpRetornoControllerException;

    public List<TransferObject> lstHistoricoConclusaoRetorno(String orgCodigo, int qtdeMesesPesquisa, String periodo, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public Map<String, String> buscaArquivosConfiguracao(String tipo, String estCodigo, String orgCodigo, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public Map<String, String> buscaArquivosConfiguracao(String nomeArquivo, String tipo, String estCodigo, String orgCodigo, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public String recuperaPeriodoRetorno(int tipoImportacaoRetorno, java.sql.Date periodoRetAtrasado, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public List<String> recuperaPeriodosRetorno(int tipoImportacaoRetorno, Date periodoRetAtrasado, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel)
            throws ImpRetornoControllerException;

    public void geraRelatorioIntegracaoSemProcessamentoXLS(String nomeArqSaida, String entradaImpRetorno, String tradutorImpRetorno, String pathSaida, AcessoSistema responsavel);
    public void geraRelatorioIntegracaoSemMapeamentoXLS(String nomeArqSaida, String entradaImpRetorno, String tradutorImpRetorno, String pathSaida, AcessoSistema responsavel);

    public void importarRegraInconsistencia(String adeCodigo, String iiaObs, Short iiaItem, java.util.Date iiaData, Boolean iiaPermanente, AcessoSistema responsavel) throws ImpRetornoControllerException;

    public List<TransferObject> getHistoricoParcelasAgrupado(java.util.Date ultPeriodoRetorno, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException;

    public void atualizarCsaCodigoTbArqRetorno(AcessoSistema responsavel) throws ImpRetornoControllerException;

    public void pagaParcelasParciais(Map<String, Map<String, Object>> linhasSemProcessamento,  HashMap<String, String> adeTipoEnvio,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao,
            List<String> camposChaveIdent, String[] ordemExcCamposChave, boolean critica, boolean atrasado,
            boolean exportaMensal, boolean consolida,
            AutorizacaoDAO adeDAO, ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, AcessoSistema responsavel) throws ImpRetornoControllerException;

    public void concluiDespesasComum(String periodoRetorno, AcessoSistema responsavel) throws ImpRetornoControllerException;

    public void cancelaRelacionamentosInsereAltera(AcessoSistema responsavel) throws ImpRetornoControllerException;

    public String ajustaTipoRetornoPeloPeriodo(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, AcessoSistema responsavel) throws ImpRetornoControllerException;

    public void agendarAtualizacaoBaseCentralCpf(AcessoSistema responsavel);

    public void agendarArquivamentoServidor(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel);

    public List<TransferObject> lstHistoricoConclusaoRetorno(String orgCodigo, int qtdeMesesPesquisa, String periodo, boolean ordemDescrescente, AcessoSistema responsavel) throws ImpRetornoControllerException;

}