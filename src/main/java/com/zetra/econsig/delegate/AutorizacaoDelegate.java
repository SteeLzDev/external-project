package com.zetra.econsig.delegate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: AutorizacaoDelegate</p>
 * <p>Description: Delegate para operações de pesquisa sobre as consignações.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AutorizacaoDelegate extends AbstractDelegate {

    private AutorizacaoController adeController;
    private CancelarConsignacaoController cancelarController;
    private PesquisarConsignacaoController pesquisarController;

    public AutorizacaoDelegate() {
    }

    private AutorizacaoController getAutorizacaoController() throws AutorizacaoControllerException {
        try {
            if (adeController == null) {
                adeController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
            }
            return adeController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private CancelarConsignacaoController getCancelarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (cancelarController == null) {
                cancelarController = ApplicationContextProvider.getApplicationContext().getBean(CancelarConsignacaoController.class);
            }
            return cancelarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private PesquisarConsignacaoController getPesquisarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (pesquisarController == null) {
                pesquisarController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            }
            return pesquisarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**************************** MÉTODOS DE NEGÓCIO *******************************/

    public TransferObject findAutDesconto(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().findAutDesconto(adeCodigo, responsavel);
    }

    public TransferObject findAutDescontoByAdeNumero(Long adeNumero, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().findAutDescontoByAdeNumero(adeNumero, responsavel);
    }

    public String getAdeRelacionamentoCompra(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().getAdeRelacionamentoCompra(adeCodigo, responsavel);
    }

    public List<TransferObject> pesquisarCompraContratos(TransferObject parametros, String csaCodigo, String corCodigo, String orgCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().pesquisarCompraContratos(parametros, csaCodigo, corCodigo, orgCodigo, responsavel);
    }

    public List<TransferObject> pesquisarCompraContratos(TransferObject parametros, String csaCodigo, String corCodigo, String orgCodigo, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().pesquisarCompraContratos(parametros, csaCodigo, corCodigo, orgCodigo, offset, count, responsavel);
    }

    public List<TransferObject> pesquisarConsignacaoRelacionamento(String adeCodigoOrigem, String adeCodigoDestino, String csaCodigoOrigem, String csaCodigoDestino, String tntCodigo, List<String> stcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().pesquisarConsignacaoRelacionamento(adeCodigoOrigem, adeCodigoDestino, csaCodigoOrigem, csaCodigoDestino, tntCodigo, stcCodigo, responsavel);
    }

    public void setDadoAutDesconto(String adeCodigo, String tdaCodigo, String dadValor, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getAutorizacaoController().setDadoAutDesconto(adeCodigo, tdaCodigo, dadValor, responsavel);
    }

    public String getValorDadoAutDesconto(String adeCodigo, String tdaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getValorDadoAutDesconto(adeCodigo, tdaCodigo, false, responsavel);
    }

    public String getValorDadoAutDesconto(String adeCodigo, String tdaCodigo, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().getValorDadoAutDesconto(adeCodigo, tdaCodigo, arquivado, responsavel);
    }

    public String getValorDadoServidor(String serCodigo, String tdaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().getValorDadoServidor(serCodigo, tdaCodigo, responsavel);
    }

    public List<TransferObject> lstDadoAutDesconto(String adeCodigo, String tdaCodigo, VisibilidadeTipoDadoAdicionalEnum visibilidade, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().lstDadoAutDesconto(adeCodigo, tdaCodigo, visibilidade, responsavel);
    }

    public List<TransferObject> lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum acao, VisibilidadeTipoDadoAdicionalEnum visibilidade, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().lstTipoDadoAdicional(acao, visibilidade, svcCodigo, csaCodigo, responsavel);
    }

    public boolean validarTetoDescontoPeloCargo(String rseCodigo, String svcCodigo, BigDecimal adeVlr, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().validarTetoDescontoPeloCargo(rseCodigo, svcCodigo, adeVlr, responsavel);
    }

    public CustomTransferObject buscaAutorizacao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().buscaAutorizacao(adeCodigo, false, responsavel);
    }

    public CustomTransferObject buscaAutorizacao(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().buscaAutorizacao(adeCodigo, arquivado, responsavel);
    }

    public List<TransferObject> buscaAutorizacao(List<String> adeCodigo, boolean validaPermissao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().buscaAutorizacao(adeCodigo, validaPermissao, responsavel);
    }

    public int listaConsignacoesAtivasCsa(String csaCodigo, String adeIdentificador, AcessoSistema responsavel) throws DespesaIndividualControllerException, AutorizacaoControllerException {
        return getPesquisarConsignacaoController().listaConsignacoesAtivasCsa(csaCodigo, adeIdentificador, responsavel);
    }

    public List<TransferObject> pesquisaAutorizacao(String rseCodigo, String cnvCodigo, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().pesquisaAutorizacao(rseCodigo, cnvCodigo, sadCodigos, responsavel);
    }

    public List<TransferObject> pesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().pesquisaAutorizacao(tipo, codigo, rseCodigo, adeNumero, adeIdentificador, sadCodigos, svcCodigos, -1, -1, criterio, responsavel);
    }

    public BigDecimal obtemTotalValorContratosAtivos(AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().obtemTotalValorContratosAtivos(responsavel);
    }

    public List<TransferObject> pesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, int offset, int count, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().pesquisaAutorizacao(tipo, codigo, rseCodigo, adeNumero, adeIdentificador, sadCodigos, svcCodigos, offset, count, criterio, responsavel);
    }

    public int countPesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().countPesquisaAutorizacao(tipo, codigo, rseCodigo, adeNumero, adeIdentificador, sadCodigos, svcCodigos, criterio, responsavel);
    }

    public Map<String, String> selectStatusAutorizacao(AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().selectStatusAutorizacao(responsavel);
    }

    public void cancelaAdeExpiradas(List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().cancelarExpiradas(null, null, sadCodigos, responsavel);
    }

    public void cancelaAdeExpiradas(String rseCodigo, String adeNumero, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().cancelarExpiradas(rseCodigo, adeNumero, sadCodigos, responsavel);
    }

    public Map<String, String> getParamSvcADE(String adeCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().getParamSvcADE(adeCodigo, tpsCodigos, responsavel);
    }

    public List<TransferObject> lstDuplicaParcela(String csaCodigo, String cnvCodVerba, String adeIndice, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().lstDuplicaParcela(csaCodigo, cnvCodVerba, adeIndice, responsavel);
    }

    public List<TransferObject> lstReajustaAde(String csaCodigo, CustomTransferObject regras, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().lstReajustaAde(csaCodigo, regras, responsavel);
    }

    public boolean isDestinoRenegociacao(String adeCodigo) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().isDestinoRenegociacao(adeCodigo);
    }

    public boolean isDestinoRenegociacaoPortabilidade(String adeCodigo) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().isDestinoRenegociacaoPortabilidade(adeCodigo);
    }

    public boolean isDestinoRelacionamento(String adeCodigo, String tntCodigo) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().isDestinoRelacionamento(adeCodigo, tntCodigo);
    }

    public BigDecimal pesquisarVlrCapitalDevidoAberto(String rseCodigo, String orgCodigo, String svcCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().pesquisarVlrCapitalDevidoAberto(rseCodigo, orgCodigo, svcCodigo, adeCodigosRenegociacao, responsavel);
    }

    public long getNextAdeNumero(String vcoCodigo, java.sql.Date anoMesIni, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().getNextAdeNumero(vcoCodigo, anoMesIni, responsavel);
    }

    public List<String> listarConsignacoesReativacaoAutomatica(AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().listarConsignacoesReativacaoAutomatica(responsavel);
    }

    public TransferObject obtemDadosUsuarioUltimaOperacaoAde(String adeCodigo, String tocCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().obtemDadosUsuarioUltimaOperacaoAde(adeCodigo, tocCodigo, responsavel);
    }

    public List<TransferObject> buscarStatusConsignacaoPorServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAutorizacaoController().buscarStatusConsignacaoPorServidor(rseCodigo, responsavel);
    }

    public List<TransferObject> buscarNaturezaConsignacaoPorServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException{
        return getAutorizacaoController().buscarNaturezaConsignacaoPorServidor(rseCodigo, responsavel);
    }

    public void validarCancelamentoConsignacaoDentroPrazo(String adeCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException{
        getAutorizacaoController().validarCancelamentoConsignacaoDentroPrazo(adeCodigo, svcCodigo, responsavel);
    }

    public List<TransferObject> pesquisarConsignacaoConciliacao(String orgIdentificador, Date periodo,List<String> cpf, List<Long> adeNumero, List<String> adeIdentificador, String statusPagamento, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().pesquisarConsignacaoConciliacao(orgIdentificador, periodo, cpf, adeNumero, adeIdentificador, statusPagamento, responsavel);
    }

    public List<TransferObject> listaTotalConsignacaoAtivasPorOrgao(AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().listaTotalConsignacaoAtivasPorOrgao(responsavel);
    }

    public List<TransferObject> listaSolicitacaoSaldoDevedorPorRegistroServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getPesquisarConsignacaoController().listaSolicitacaoSaldoDevedorPorRegistroServidor(rseCodigo, responsavel);
    }
}
