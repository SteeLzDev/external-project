package com.zetra.econsig.service.leilao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FiltroLeilaoSolicitacaoTO;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: LeilaoSolicitacaoController</p>
 * <p>Description: Session Façade para operações do módulo de leilão
 * de solicitação via simulação pelo servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface LeilaoSolicitacaoController {

    public boolean temSolicitacaoLeilao(String adeCodigo, boolean pendente, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public boolean temSolicitacaoLeilaoExpirada(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void iniciarProcessoLeilao(String adeCodigo, String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void cancelarProcessoLeilao(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void cancelarProcessoLeilaoPorErro(String adeCodigo, String motivoErro, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void encerrarLeilaoExpirado(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public List<TransferObject> acompanharLeilaoSolicitacao(TransferObject criteriosPesquisa, int offset, int count, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public int contarLeilaoSolicitacao(TransferObject criteriosPesquisa,  AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void informarPropostaLeilaoSolicitacao(String adeCodigo, String svcCodigo, String csaCodigoLeilao, BigDecimal taxaJuros, boolean validaTaxaJuros, String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void informarPropostaLeilaoSolicitacao(String adeCodigo, String svcCodigo, String csaCodigoLeilao, BigDecimal taxaJuros, BigDecimal taxaMinOfertaAut, BigDecimal decrementoOfertaAut, String emailOfertaAut, String txtContatoCsa, boolean validaTaxaJuros, String rseCodigo, Date dataProposta, boolean processarOfertasAutomaticas, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public String aprovarPropostaLeilaoSolicitacao(TransferObject portabilidade, String adeCodigo, String plsCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public List<TransferObject> lstPropostaLeilaoSolicitacao(String adeCodigo, String csaCodigo, String stpCodigo, boolean arquivado, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public List<TransferObject> lstPropostaLeilaoOfertaAutomatica(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public int countSolicitacaoLeilaoCanceladoParaBloqueio(String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public List<TransferObject> lstSolicitacaoLeilaoEncerrado(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public BigDecimal calcularValorPrestacao(String adeCodigo, String taxaJuros, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public BigDecimal obterMelhorTaxaLeilao(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public List<TransferObject> listarFiltros(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void criarFiltroLeilaoSolicitacao(FiltroLeilaoSolicitacaoTO filtro, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void excluirFiltroLeilaoSolicitacao(String fltCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public List<TransferObject> listarFiltrosByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public Integer qtdeContratos(String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public Integer qtdeSolicitacaoLeilao(String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public Integer qtdeLeilaoConcretizado(String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public TransferObject obterAnaliseDeRiscoRegistroServidor(String rseCodigo,  AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void informarAnaliseDeRisco(String rseCodigo, String csaCodigo, String arrRisco, AcessoSistema responsavel)  throws LeilaoSolicitacaoControllerException;

    public List<String> lstEmailConsignatariasNotificacaoLeilao(String adeCodigo, AcessoSistema responsavel)  throws LeilaoSolicitacaoControllerException;

    public int contarLeilaoFinalizadoSemContato(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public List<TransferObject> lstLeilaoFinalizadoSemContato(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void recusarInformacaoContatoLeilaoFinalizado(List<TransferObject> leiloes, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public void salvarInformacaoContatoLeilaoFinalizado(List<TransferObject> leiloes, String email, String ddd, String telefone, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public List<TransferObject> listarStatusPropostaLeilao(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public boolean podeReverPontuacaoLeilao(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException;

    public Date calcularDataValidadeLeilao(int qtdMinutosFechamentoLeilao, AcessoSistema responsavel) throws CalendarioControllerException;
}
