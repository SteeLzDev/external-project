package com.zetra.econsig.service.lote;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ControleProcessamentoLote;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.TipoLancamento;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;

/**
 * <p>Title: LoteController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface LoteController  {
    public Map<String, Object> validaEntrada(Map<String, Object> entrada) throws AutorizacaoControllerException;

    public List<TransferObject> buscaServidor(String operacao, String tipo, String tipoCodigo, String est, String org, String matricula, String cpf, boolean serAtivo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void verificaConvenioProcessaLote(String verba, List<String> orgaos, String csaCodigo, String svcIdentificador, boolean cnvAtivo, String nseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> buscaPrazoCoeficiente(String svcCodigo, String csaCodigo, String orgCodigo) throws AutorizacaoControllerException;

    public void validaInfBancariaObrigatoria(Map<String, Object> dadosServidorConvenio, Map<String, Object> paramCnv, Map<String, Object> entrada) throws AutorizacaoControllerException;

    public void validaInfObrigatoriaCnv(Map<String, Object> paramCnv, Map<String, Object> entrada) throws AutorizacaoControllerException;

    public String validaMotivoOperacao(Map<String, Object> entrada, String funCodigo, boolean permiteAlterarAdeSemMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<Map<String, Object>> lstServidorPorCnv(String codVerba, String csaCodigo, String tipo, String tipoCodigo, String estIdentificador, String orgIdentificador, String matricula, String cpf, boolean cnvAtivo, boolean serAtivo, String svcIdentificador, String nseCodigo, boolean inclusao, boolean renegociacao, List<String> ignorarRseCodigo, String numerContratoBeneficio, boolean buscaBenificiario, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<Map<String, Object>> buscaConsignacaoPorCnvSer(String operacao, String codVerba, String csaCodigo, String rseMatricula, String serCpf, String orgIdentificador, String svcIdentificador, String estIdentificador, boolean cnvAtivo, TransferObject criterio, String nseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<String> buscaConsignacaoAbertaParaRenegociacao(String rseCodigo, String csaCodigo, String svcCodigo, String adeIdentificador, boolean fixaServico, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<String> buscaConsignacaoLiquidadaParaRenegociacao(String rseCodigo, String csaCodigo, String svcCodigo, boolean fixaServico, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> buscarConsignacaoMensalidadeBeneficio(String csaCodigo, String cbeNumero, String tlaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> buscarConsignacaoMensalidadeBeneficioVerbaDestino(String csaCodigo, String cbeNumero, String cnvCodVerba, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public Servico buscarServicoDestinoRelacionamentoBeneficio(String svcCodigoOrigem, String tlaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> buscarRelacionamentoServicoVerbaDestino(String csaCodigo, String cnvCodVerba, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void relacionarRenegociacaoViaLote(String adeCodigoDestino, List<String> adeCodigosOrigem, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void relacionarMensalidadeViaLote(String adeCodigoDestino, String adeCodigoOrigem, String tlaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public TipoLancamento buscarTipoLancamentoPorTntCodigo(String tntCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public TipoLancamento buscarTipoLancamentoPorTlaCodigo(String tlaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    // Métodos utilizados no controle de processamento de lote via SOAP
    public ControleProcessamentoLote findProcessamentoByArquivoCentralizador(String arquivoCentralizador);

    public ControleProcessamentoLote findProcessamentoByArquivoeConsig(String arquivoeConsig);

    public ControleProcessamentoLote incluirProcessamento(String arquivoCentralizador, String arquivoeConsig, Short status, AcessoSistema responsavel) throws ZetraException;

    public void excluirProcessamento(ControleProcessamentoLote controleProcessamentoLote, AcessoSistema responsavel) throws ZetraException;

    public void alterarProcessamento(ControleProcessamentoLote controleProcessamentoLote, AcessoSistema responsavel) throws ZetraException;

    public void carregarBlocosLote(String nomeArqEntrada, String nomeArqConfEntrada, String nomeArqConfTradutor, String csaCodigo, Date bplPeriodo, ControleProcessamentoLote controleProcessamentoLote, AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> lstBlocoProcessamentoLote(String nomeArquivo, String csaCodigo, StatusBlocoProcessamentoEnum status, AcessoSistema responsavel) throws ZetraException;

    public void atualizarStatusBlocos(String nomeArquivo, String csaCodigo, StatusBlocoProcessamentoEnum statusOrigem, StatusBlocoProcessamentoEnum statusDestino, AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> lstLotesEmProcessamento(AcessoSistema responsavel) throws ZetraException;

    public void atualizarBlocoLote(String nomeArquivo, Integer numLinha, StatusBlocoProcessamentoEnum novoStatus, String critica, AcessoSistema responsavel) throws ZetraException;
}
