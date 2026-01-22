package com.zetra.econsig.service.consignacao;

import static com.zetra.econsig.values.MotivoAdeNaoRenegociavelEnum.CHAVE_MOTIVO_INDISPONIBILIDADE;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.geradoradenumero.AdeNumeroHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.NaturezaServicoHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignanteHome;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.query.compra.ListaAcompanhamentoCompraQuery;
import com.zetra.econsig.persistence.query.consignacao.ContaContratosNaoPossuemRelacionamentoVerbaRescisoriaByRseQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaAdeVlrPorPeriodoInclusaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoAguardandoLiquidacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoConciliacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoDeferManualDataMenorQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoOrdenadaPorAdeDataQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoParaAutorizacaoDoServidorQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPorRseCnvCodVerbaQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPorRseCnvQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPorRseSadNseQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPortabilidadeCartaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPossuiRejeitoPgtSaldoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoReativacaoAutomaticaQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRelacionamentoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRenegociavelDetalheNativeQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRenegociavelNativeQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRseSvcQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoSemDecisaoJudicialQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoSemOcaNoPeriodoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoSemParcelaQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoSerByAdeCodigoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaLancamentosCartaoPorReservaQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaStatusConsignacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaTotalConsignacaoAtivasPorOrgaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListarConsignacaoParcelaQuery;
import com.zetra.econsig.persistence.query.consignacao.ListarConsignacaoReimplanteManualQuery;
import com.zetra.econsig.persistence.query.consignacao.ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoOrigemQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoReimplanteQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemDadosUsuarioUltimaOperacaoAdeQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemQtdAdeReservaCartaoSemLancamentoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalAdeVlrPorPeriodoInclusaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalCapitalDevidoVincendoRelSvcQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoAtivaCsaQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoCsaQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoRseQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoPorCodigoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoPorRseCnvQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorContratosAtivosQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemValorTotalContratosAtivosRsePorMargemQuery;
import com.zetra.econsig.persistence.query.historico.HistoricoConsignacaoOcorrenciaQuery;
import com.zetra.econsig.persistence.query.historico.HistoricoConsignacaoRelacionamentoQuery;
import com.zetra.econsig.persistence.query.historico.HistoricoConsignacaoTerceirosQuery;
import com.zetra.econsig.persistence.query.margem.ListaContratosCompulsoriosQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoComParametroQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPortabilidadeCartaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemQtdAdeReservaCartaoSemLancamentoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.MotivoAdeNaoRenegociavelEnum;

/**
 * <p>Title: PesquisarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para operacao de pesquisa de consignacao.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class PesquisarConsignacaoControllerBean implements PesquisarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PesquisarConsignacaoControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ParametroController parametroController;

    @Override
    public List<TransferObject> pesquisaAutorizacao(String rseCodigo, String cnvCodigo, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.setConvenio(cnvCodigo);
            log.add(Columns.SAD_CODIGO, sadCodigos, StatusAutorizacaoDescontoHome.class);
            log.write();

            final ListaConsignacaoPorRseCnvQuery query = new ListaConsignacaoPorRseCnvQuery();
            query.rseCodigo = rseCodigo;
            query.cnvCodigo = cnvCodigo;
            query.sadCodigos = sadCodigos;
            return query.executarDTO();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaAutorizacaoPorRseSadNse(String rseCodigo, List<String> sadCodigos, List<String> nseCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.add(Columns.NSE_CODIGO, nseCodigos, NaturezaServicoHome.class);
            log.write();

            final ListaConsignacaoPorRseSadNseQuery query = new ListaConsignacaoPorRseSadNseQuery();
            query.rseCodigo = rseCodigo;
            query.sadCodigos = sadCodigos;
            query.nseCodigos = nseCodigos;
            return query.executarDTO();
        } catch (LogControllerException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaAutorizacaoPorVerba(String rseCodigo, List<String> cnvCodVerba, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.add(Columns.SAD_CODIGO, sadCodigos, StatusAutorizacaoDescontoHome.class);
            log.write();

            final ListaConsignacaoPorRseCnvCodVerbaQuery query = new ListaConsignacaoPorRseCnvCodVerbaQuery();
            query.rseCodigo = rseCodigo;
            query.cnvCodVerba = cnvCodVerba;
            query.sadCodigos = sadCodigos;
            return query.executarDTO();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public BigDecimal obtemTotalValorContratosAtivos(AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemTotalValorContratosAtivosQuery query = new ObtemTotalValorContratosAtivosQuery(responsavel);

            final List<TransferObject> values = query.executarDTO();
            if ((values != null) && (values.size() > 0)) {
                final CustomTransferObject to = (CustomTransferObject) values.get(0);
                if (!TextHelper.isNull(to.getAttribute("total"))) {
                    return new BigDecimal(to.getAttribute("total").toString());
                }
            }
            return new BigDecimal("0");

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public BigDecimal obtemTotalValorConsignacaoPorCodigo(List<String> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemTotalValorConsignacaoPorCodigoQuery query = new ObtemTotalValorConsignacaoPorCodigoQuery(adeCodigos);
            return query.executarSomatorio(BigDecimal.ZERO);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public int listaConsignacoesAtivasCsa(String csaCodigo, String adeIdentificador, AcessoSistema responsavel) throws AutorizacaoControllerException {

        try{
            final ObtemTotalConsignacaoAtivaCsaQuery query = new ObtemTotalConsignacaoAtivaCsaQuery();
            query.csaCodigo = csaCodigo;
            query.adeIdentificador = adeIdentificador;

            return query.executarContador();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return pesquisaAutorizacao(tipo, codigo, rseCodigo, adeNumero, adeIdentificador, sadCodigos, svcCodigos, -1, -1, criterio, responsavel);
    }

    @Override
    public List<TransferObject> pesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, int offset, int count, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            List<Long> adeNumeros = null;
            if ((adeNumero != null) && (adeNumero.size() > 0)) {
                adeNumeros = new ArrayList<>();
                for (final String numero : adeNumero) {
                    try {
                        adeNumeros.add(Long.valueOf(numero));
                    } catch (final NumberFormatException ex) {
                        throw new AutorizacaoControllerException("mensagem.erro.ade.numero.invalido.arg0", responsavel, ex, numero);
                    }
                }
            }

            final ListaConsignacaoQuery query = new ListaConsignacaoQuery(responsavel);
            query.tipo = tipo;
            query.codigo = codigo;
            query.rseCodigo = rseCodigo;
            query.adeNumero = adeNumeros;
            query.adeIdentificador = adeIdentificador;
            query.sadCodigos = sadCodigos;
            query.svcCodigos = svcCodigos;

            // Criterios de pesquisa avancada
            setaCriterioQuery(query, criterio, responsavel);

            if((criterio != null) && (!TextHelper.isNull(criterio.getAttribute("operacaoSOAPListarSolicitacaoSaldo")) || !TextHelper.isNull(criterio.getAttribute("operacaoSOAPEditarSaldoDevedor")) )) {
                query.listaTipoSolicitacaoSaldo = (List<String>) criterio.getAttribute("listaTipoSolicitacaoSaldo");
                query.operacaoSOAPEditarSaldoDevedor = TextHelper.isNull(criterio.getAttribute("operacaoSOAPEditarSaldoDevedor"));
            }

            final String csaCodigo = query.csaCodigo;
            final boolean renegociacao = "renegociar".equalsIgnoreCase(query.tipoOperacao) || "simular_renegociacao".equalsIgnoreCase(query.tipoOperacao);
            final boolean portabilidade = "comprar".equalsIgnoreCase(query.tipoOperacao) || "solicitar_portabilidade".equalsIgnoreCase(query.tipoOperacao);

            if ((!portabilidade && !renegociacao) && (count != -1)) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            List<TransferObject> result = query.executarDTO();

            // Filtra os contratos que podem ser negociados
            if (portabilidade || renegociacao) {
                final List<String> todosAdeCodigos = new ArrayList<>();
                final Map<String, TransferObject> ades = new HashMap<>();
                final Map<String, TransferObject> adesSuspensas = new HashMap<>();
                for (final TransferObject ade : result) {
                    final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                    todosAdeCodigos.add(adeCodigo);

                    if (!CodedValues.SAD_SUSPENSA.equals(ade.getAttribute(Columns.SAD_CODIGO).toString())) {
                        ades.put(adeCodigo, ade);
                    } else {
                        adesSuspensas.put(adeCodigo, ade);
                    }
                }

                result = new ArrayList<>();

                final ListaConsignacaoRenegociavelNativeQuery lcrQuery = new ListaConsignacaoRenegociavelNativeQuery();
                lcrQuery.adeCodigos = ades.keySet();
                lcrQuery.tipoOperacao = query.tipoOperacao;
                lcrQuery.csaCodigo = query.csaCodigo;
                lcrQuery.svcCodigo = query.svcCodigo;
                lcrQuery.responsavel = responsavel;

                if (count != -1) {
                    lcrQuery.maxResults = count;
                    lcrQuery.firstResult = offset;
                }

                final List<String> adeRenegociavel = lcrQuery.executarLista();

                if (ParamSist.paramEquals(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, CodedValues.TPC_SIM, responsavel) && renegociacao) {
                    final ListaConsignacaoRenegociavelNativeQuery contratosReneSuspensos = new ListaConsignacaoRenegociavelNativeQuery();
                    contratosReneSuspensos.adeCodigos = adesSuspensas.keySet();
                    contratosReneSuspensos.tipoOperacao = query.tipoOperacao;
                    contratosReneSuspensos.csaCodigo = query.csaCodigo;
                    contratosReneSuspensos.svcCodigo = query.svcCodigo;
                    contratosReneSuspensos.responsavel = responsavel;
                    contratosReneSuspensos.adeSuspensas = true;

                    if (count != -1) {
                        lcrQuery.maxResults = count;
                        lcrQuery.firstResult = offset;
                    }

                    final List<String> adeRenegociavelSuspensa = contratosReneSuspensos.executarLista();
                    for (final String adeRenSuspensa : adeRenegociavelSuspensa) {
                        final CustomTransferObject adePermitidoRenegociarSuspensa = (CustomTransferObject) adesSuspensas.get(adeRenSuspensa);
                        ades.put(adeRenSuspensa, adePermitidoRenegociarSuspensa);
                        adeRenegociavel.add(adeRenSuspensa);
                    }
                }

                // Se a quantidade de ADEs que passam nos critérios de renegociação/portabilidade é menor que a quantidade
                // de ADEs do servidor, então verifica o motivo de não serem renegociáveis para incluí-lo na listagem
                if (ParamSist.paramEquals(CodedValues.TPC_LISTAR_MOTIVO_ADES_NAO_RENEGOCIAVEIS, CodedValues.TPC_SIM, responsavel) && CanalEnum.WEB.equals(responsavel.getCanal()) && (adeRenegociavel.size() < todosAdeCodigos.size())) {
                    // Extrai do "todosAdeCodigos" os que não estão em "adeRenegociavel", sendo estas as não renegociáveis
                    final List<String> adesNaoRenegociaveis = new ArrayList<>(todosAdeCodigos);
                    adesNaoRenegociaveis.removeAll(adeRenegociavel);

                    final boolean bloqueiaCompraProprioContrato = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_PROPRIO_CONTRATO, CodedValues.TPC_SIM, responsavel);
                    final boolean bloqueiaCompraServidorNovo = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_SERVIDOR_NOVO, CodedValues.TPC_SIM, responsavel);
                    final String numParcelasBloqueiaCompraParcela = (String) ParamSist.getInstance().getParam(CodedValues.TPC_BLOQUEIA_COMPRA_ULTIMA_PARCELA, responsavel);
                    final boolean bloqueiaCompraSolictSaldoLiq = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_COM_SOLICI_SALDO_LIQUID, CodedValues.TPC_SIM, responsavel);
                    final boolean bloqueiaCompraDataFimPassada = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_DATA_FINAL_PASSADA, CodedValues.TPC_SIM, responsavel);

                    // Para cada ADE não renegociável, valida os parâmetros
                    for (final String adeCodigoNaoRenegociavel : adesNaoRenegociaveis) {
                        TransferObject adeNaoRenegociavel = ades.get(adeCodigoNaoRenegociavel);
                        if (adeNaoRenegociavel == null) {
                            adeNaoRenegociavel = adesSuspensas.get(adeCodigoNaoRenegociavel);
                        }

                        // Volta ela para a lista dos renegociáveis, com motivo genérico para ser determinado em sequência
                        adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.OUTRO);
                        ades.put(adeCodigoNaoRenegociavel, adeNaoRenegociavel);
                        adeRenegociavel.add(adeCodigoNaoRenegociavel);

                        final String svcCodigoAde = adeNaoRenegociavel.getAttribute(Columns.SVC_CODIGO).toString();
                        final String csaCodigoAde = adeNaoRenegociavel.getAttribute(Columns.CSA_CODIGO).toString();
                        final String orgCodigoAde = adeNaoRenegociavel.getAttribute(Columns.ORG_CODIGO).toString();

                        final int prazo = adeNaoRenegociavel.getAttribute(Columns.ADE_PRAZO) != null ? ((Integer) adeNaoRenegociavel.getAttribute(Columns.ADE_PRAZO)) : 1000;
                        final int pagas = adeNaoRenegociavel.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ((Integer) adeNaoRenegociavel.getAttribute(Columns.ADE_PRD_PAGAS)) : 0;
                        final int pagasTotal = adeNaoRenegociavel.getAttribute(Columns.ADE_PRD_PAGAS_TOTAL) != null ? ((Integer) adeNaoRenegociavel.getAttribute(Columns.ADE_PRD_PAGAS_TOTAL)) : pagas;

                        final Date anoMesIni = (Date) adeNaoRenegociavel.getAttribute(Columns.ADE_ANO_MES_INI);
                        final Date anoMesFim = (Date) adeNaoRenegociavel.getAttribute(Columns.ADE_ANO_MES_FIM);
                        final Date anoMesIniRef = adeNaoRenegociavel.getAttribute(Columns.ADE_ANO_MES_INI_REF) != null ? (Date) adeNaoRenegociavel.getAttribute(Columns.ADE_ANO_MES_INI_REF) : anoMesIni;
                        final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigoAde, responsavel);

                        final boolean quinzenal = CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(adeNaoRenegociavel.getAttribute(Columns.ADE_PERIODICIDADE));

                        final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigoAde, responsavel);

                        // Parâmetro de Sistema 229 : Bloqueia compra de contratos da própria consignatária
                        if ((portabilidade && bloqueiaCompraProprioContrato) && (!TextHelper.isNull(csaCodigo) && csaCodigo.equals(csaCodigoAde))) {
                            adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_PROPRIA_CSA);
                            continue;
                        }

                        // Parâmetro de Sistema 327 : Bloqueia compra de servidor que não possui consignação aberta com a consignatária compradora
                        if (portabilidade && bloqueiaCompraServidorNovo) {
                            // Pesquisar se o servidor tem ADE com a CSA
                            final ListaConsignacaoRenegociavelDetalheNativeQuery queryDetalhe = new ListaConsignacaoRenegociavelDetalheNativeQuery();
                            queryDetalhe.adeCodigo = adeCodigoNaoRenegociavel;
                            queryDetalhe.tipoOperacao = query.tipoOperacao;
                            queryDetalhe.csaCodigo = query.csaCodigo;
                            queryDetalhe.svcCodigo = query.svcCodigo;
                            queryDetalhe.responsavel = responsavel;
                            queryDetalhe.servidorPossuiAde = true;
                            if (queryDetalhe.executarContador() == 0) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.SERVIDOR_SEM_CONTRATO_CSA);
                                continue;
                            }
                        }

                        // Parâmetro de Sistema 333 : Bloqueia compra de contratos pelo quantidade no parametro antes de acabar a tarefa EX: TPC = 4, não será permitido comprar contrato que falte 4 parcelas para o fim.
                        if (portabilidade && !TextHelper.isNull(numParcelasBloqueiaCompraParcela) && !"0".equals(numParcelasBloqueiaCompraParcela)) {
                            final List<ParcelaDescontoPeriodo> parcelasPeriodo = ParcelaDescontoPeriodoHome.findByAutDescontoStatus(adeCodigoNaoRenegociavel, CodedValues.SPD_EMPROCESSAMENTO);
                            final int qtdParcelasEmProcessamento = (parcelasPeriodo != null ? parcelasPeriodo.size() : 0);
                            if ((prazo - (pagas + qtdParcelasEmProcessamento)) <= Integer.parseInt(numParcelasBloqueiaCompraParcela)) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_NO_LIMITE_CONFIGURADO);
                                continue;
                            }
                        }

                        // Parâmetro de Sistema 369 : Bloqueia compra de contratos que possuem ocorrência de solicitação de saldo para liquidação
                        if (portabilidade && bloqueiaCompraSolictSaldoLiq) {
                            // Pesquisar se o servidor tem ADE com a CSA
                            final ListaConsignacaoRenegociavelDetalheNativeQuery queryDetalhe = new ListaConsignacaoRenegociavelDetalheNativeQuery();
                            queryDetalhe.adeCodigo = adeCodigoNaoRenegociavel;
                            queryDetalhe.tipoOperacao = query.tipoOperacao;
                            queryDetalhe.csaCodigo = query.csaCodigo;
                            queryDetalhe.svcCodigo = query.svcCodigo;
                            queryDetalhe.responsavel = responsavel;
                            queryDetalhe.adePossuiSolicitacaoSaldoLiq = true;
                            if (queryDetalhe.executarContador() == 0) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_COM_SOLICITACAO_SALDO_LIQUIDACAO);
                                continue;
                            }
                        }

                        // Parâmetro de Sistema 731 : Bloqueia compra de contratos com data final menor que o período atual de lançamentos
                        if ((portabilidade && bloqueiaCompraDataFimPassada) && ((anoMesFim != null) && (anoMesFim.compareTo(periodoAtual) < 0))) {
                            adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_COM_DATA_FIM_PASSADA);
                            continue;
                        }

                        // Parâmetro de Serviço 155 : Quantidade mínima de parcelas pagas para disponibilizar contratos para renegociação
                        if (renegociacao && TextHelper.isNum(paramSvcCse.getTpsMinimoPrdPagasRenegociacao())) {
                            final int minPrdPagas = Integer.parseInt(paramSvcCse.getTpsMinimoPrdPagasRenegociacao());
                            if (pagasTotal < minPrdPagas) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_SEM_QTD_MIN_PRD_PAGAS);
                                continue;
                            }
                        }

                        // Parâmetro de Serviço 170 : Percentual mínimo de parcelas pagas para disponibilizar contratos para renegociação
                        if (renegociacao && TextHelper.isDecimalNum(paramSvcCse.getTpsPercentualMinimoPrdPagasReneg())) {
                            final double percentualMinPagas = Double.parseDouble(paramSvcCse.getTpsPercentualMinimoPrdPagasReneg());
                            if (pagasTotal < ((prazo * percentualMinPagas) / 100)) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_SEM_PERC_MIN_PRD_PAGAS);
                                continue;
                            }
                        }

                        // Parâmetro de Serviço 173 : Percentual mínimo de vigência para disponibilizar contratos para renegociação
                        if (renegociacao && TextHelper.isDecimalNum(paramSvcCse.getTpsPercentualMinimoVigenciaReneg())) {
                            final double percentualMinVigencia = Double.parseDouble(paramSvcCse.getTpsPercentualMinimoVigenciaReneg());
                            final int vigencia = DateHelper.monthDiff(periodoAtual, anoMesIniRef);
                            final int prazoTotal = quinzenal ? prazo / 2 : prazo;
                            if (vigencia < ((prazoTotal * percentualMinVigencia) / 100)) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_SEM_PERC_MIN_MESES_VIGENCIA);
                                continue;
                            }
                        }

                        // Parâmetro de Serviço 177 : Quantidade mínima de parcelas pagas para disponibilizar contratos para compra
                        if (portabilidade && TextHelper.isNum(paramSvcCse.getTpsMinimoPrdPagasCompra())) {
                            final int minPrdPagas = Integer.parseInt(paramSvcCse.getTpsMinimoPrdPagasCompra());
                            if (pagasTotal < minPrdPagas) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_SEM_QTD_MIN_PRD_PAGAS);
                                continue;
                            }
                        }

                        // Parâmetro de Serviço 178 : Percentual mínimo de parcelas pagas para disponibilizar contratos para compra
                        if (portabilidade && TextHelper.isDecimalNum(paramSvcCse.getTpsPercentualMinimoPrdPagasCompra())) {
                            final double percentualMinPagas = Double.parseDouble(paramSvcCse.getTpsPercentualMinimoPrdPagasCompra());
                            if (pagasTotal < ((prazo * percentualMinPagas) / 100)) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_SEM_PERC_MIN_PRD_PAGAS);
                                continue;
                            }
                        }

                        // Parâmetro de Serviço 179 : Quantidade de meses de vigência para disponibilizar contratos para compra
                        if (portabilidade && TextHelper.isNum(paramSvcCse.getTpsMinimoVigenciaCompra())) {
                            final int minVigencia = Integer.parseInt(paramSvcCse.getTpsMinimoVigenciaCompra());
                            final int vigencia = DateHelper.monthDiff(periodoAtual, anoMesIniRef);
                            if (vigencia < minVigencia) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_SEM_QTD_MIN_MESES_VIGENCIA);
                                continue;
                            }
                        }

                        // Parâmetro de Serviço 180 : Percentual mínimo de vigência para disponibilizar contratos para compra
                        if (portabilidade && TextHelper.isDecimalNum(paramSvcCse.getTpsPercentualMinimoVigenciaCompra())) {
                            final double percentualMinVigencia = Double.parseDouble(paramSvcCse.getTpsPercentualMinimoVigenciaCompra());
                            final int vigencia = DateHelper.monthDiff(periodoAtual, anoMesIniRef);
                            final int prazoTotal = quinzenal ? prazo / 2 : prazo;
                            if (vigencia < ((prazoTotal * percentualMinVigencia) / 100)) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_SEM_PERC_MIN_MESES_VIGENCIA);
                                continue;
                            }
                        }

                        // Parâmetro de Serviço 181 : Quantidade de meses de vigência para disponibilizar contratos para renegociação
                        if (renegociacao && TextHelper.isNum(paramSvcCse.getTpsMinimoVigenciaReneg())) {
                            final int minVigencia = Integer.parseInt(paramSvcCse.getTpsMinimoVigenciaReneg());
                            final int vigencia = DateHelper.monthDiff(periodoAtual, anoMesIniRef);
                            if (vigencia < minVigencia) {
                                adeNaoRenegociavel.setAttribute(CHAVE_MOTIVO_INDISPONIBILIDADE, MotivoAdeNaoRenegociavelEnum.CONTRATO_SEM_QTD_MIN_MESES_VIGENCIA);
                                continue;
                            }
                        }
                    }
                }

                for (final String string : adeRenegociavel) {
                    result.add(ades.get(string));
                }

                if (portabilidade && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_PORTABILIDADE_CARTAO, responsavel)) {
                    final ListaConsignacaoPortabilidadeCartaoQuery cartaoQuery = new ListaConsignacaoPortabilidadeCartaoQuery();
                    cartaoQuery.adeCodigos = ades.keySet();
                    cartaoQuery.responsavel = responsavel;

                    result = new ArrayList<>();

                    final List<String> adeCartaoPortabilidade = cartaoQuery.executarLista();
                    for (final String ade : adeCartaoPortabilidade) {
                        result.add(ades.get(ade));
                    }
                }
            }

            //Filtra contratos que não tem decisão judicial aberta
            if (!TextHelper.isNull(criterio) && (criterio.getAttribute("FILTRO_DECISAO_JUDICIAL") != null)) {
                final Map<String, TransferObject> ades = new HashMap<>();
                for (final TransferObject ade : result) {
                    ades.put(ade.getAttribute(Columns.ADE_CODIGO).toString(), ade);
                }

                result = new ArrayList<>();

                final ListaConsignacaoSemDecisaoJudicialQuery lstAdesSemDecisao = new ListaConsignacaoSemDecisaoJudicialQuery();
                lstAdesSemDecisao.adeCodigos = ades.keySet();
                final List<String> adeSemDecisaoJudicial = lstAdesSemDecisao.executarLista();

                for (final String adeCodigo : adeSemDecisaoJudicial) {
                    result.add(ades.get(adeCodigo));
                }
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            if ((adeNumero != null) && (adeNumero.size() > 0)) {
                log.add(Columns.ADE_NUMERO, TextHelper.join(adeNumero, ", "));
                // Se foi feita pesquisa pelo adeNumero, e o rseCodigo nao foi passado, obtem esta informacao
                // do resultado da pesquisa para gravar no log de auditoria.
                if (TextHelper.isNull(rseCodigo) && (result != null) && (result.size() > 0) && (result.get(0) != null)) {
                    rseCodigo = (String) result.get(0).getAttribute(Columns.RSE_CODIGO);
                }
            }
            log.setRegistroServidor(rseCodigo);
            log.write();

            return result;

        } catch (LogControllerException | ParametroControllerException | PeriodoException | HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisarAutorizacoes(List<String> adeCodigos, String tipo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final ListaConsignacaoQuery query = new ListaConsignacaoQuery(responsavel);
        query.tipo = tipo;
        query.adeCodigo = adeCodigos;
        query.codigo = (AcessoSistema.ENTIDADE_SER.equals(tipo)) ? responsavel.getUsuCodigo() : responsavel.getCodigoEntidade();

        try {
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaRejeitoPgtSaldo(String csaCodigo, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoPossuiRejeitoPgtSaldoQuery query = new ListaConsignacaoPossuiRejeitoPgtSaldoQuery();
            query.csaCodigo = csaCodigo;

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countRejeitoPgtSaldo(String codigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoPossuiRejeitoPgtSaldoQuery query = new ListaConsignacaoPossuiRejeitoPgtSaldoQuery();
            query.csaCodigo = codigo;
            query.count = true;

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countPesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final String tipoOperacao = (criterio != null) && (criterio.getAttribute("TIPO_OPERACAO") != null) ? criterio.getAttribute("TIPO_OPERACAO").toString() : "";
            if ("comprar".equalsIgnoreCase(tipoOperacao) || "renegociar".equalsIgnoreCase(tipoOperacao) || ((criterio != null) && (criterio.getAttribute("FILTRO_DECISAO_JUDICIAL") != null))) {
                final List<TransferObject> result = pesquisaAutorizacao(tipo, codigo, rseCodigo, adeNumero, adeIdentificador, sadCodigos, svcCodigos, -1, -1, criterio, responsavel);
                return result.size();
            }

            List<Long> adeNumeros = null;
            if ((adeNumero != null) && (adeNumero.size() > 0)) {
                adeNumeros = new ArrayList<>();
                for (final String numero : adeNumero) {
                    adeNumeros.add(Long.valueOf(numero));
                }
            }

            final ListaConsignacaoQuery query = new ListaConsignacaoQuery(responsavel);
            query.count = true;
            query.tipo = tipo;
            query.codigo = codigo;
            query.rseCodigo = rseCodigo;
            query.adeNumero = adeNumeros;
            query.adeIdentificador = adeIdentificador;
            query.sadCodigos = sadCodigos;
            query.svcCodigos = svcCodigos;

            // Criterios de pesquisa avancada
            setaCriterioQuery(query, criterio, responsavel);

            return query.executarContador();
        } catch (HQueryException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public long getNextAdeNumero (String vcoCodigo, Date anoMesIni, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return AdeNumeroHelper.getNext(vcoCodigo, anoMesIni);
    }

    private void setaCriterioQuery(ListaConsignacaoQuery query, CustomTransferObject criterio, AcessoSistema responsavel) throws ParametroControllerException {
        if (criterio != null) {
            String tpsCodigo       = "";
            String tmoCodigo       = "";
            String tgcCodigo       = "";
            String csaCodigo       = "";
            String corCodigo       = "";
            String estCodigo       = "";
            String orgCodigo       = "";
            String tgsCodigo       = "";
            String svcCodigo       = "";
            String cnvCodVerba     = "";
            String adeAnoMesIni    = "";
            String infSaldoDevedor = "";
            String periodoIni      = "";
            String periodoFim      = "";
            String adeIndice       = "";
            String ocaDataIni      = "";
            String ocaDataFim      = "";
            String serCpf          = "";
            String rseMatricula    = "";
            String diasSolicitacaoSaldo = "";
            String diasSolicitacaoSaldoPagaAnexo = "";
            String tipoOcorrenciaPeriodo = "";
            Short adeIntFolha     = null;
            Short adeIncMargem    = null;
            final Boolean adePropria = (Boolean) criterio.getAttribute("adePropria") != null ? (Boolean) criterio.getAttribute("adePropria") : false;
            final Boolean transferencia = (Boolean) criterio.getAttribute("transferencia");
            final Object dataConciliacao = criterio.getAttribute("dataConciliacao");
            List<String> rseSrsCodigo = new ArrayList<>();
            final List<String> csaCodigos = criterio.getAttribute("csaCodigos") != null ? (List<String>) criterio.getAttribute("csaCodigos") : null;
            final List<String> nseCodigos = criterio.getAttribute("nseCodigos") != null ? (List<String>) criterio.getAttribute("nseCodigos") : null;
            final List<Short> marCodigos  = criterio.getAttribute("marCodigos") != null ? (List<Short>)  criterio.getAttribute("marCodigos") : null;

            // TIPO_OPERACAO -> indica qual tipo de parametro deve ser usado
            final String tipoOperacao = criterio.getAttribute("TIPO_OPERACAO") != null ? criterio.getAttribute("TIPO_OPERACAO").toString() : "";

            if ("alterar".equalsIgnoreCase(tipoOperacao)) {
                tpsCodigo = CodedValues.TPS_PERMITE_ALTERACAO_CONTRATOS;
            } else if ("renegociar".equalsIgnoreCase(tipoOperacao) || "comprar".equalsIgnoreCase(tipoOperacao)) {
                tpsCodigo = CodedValues.TPS_PERMITE_RENEGOCIACAO;
                svcCodigo = criterio.getAttribute(Columns.SVC_CODIGO) != null ? criterio.getAttribute(Columns.SVC_CODIGO).toString() : "";
                csaCodigo = criterio.getAttribute(Columns.CSA_CODIGO) != null ? criterio.getAttribute(Columns.CSA_CODIGO).toString() : "";
                corCodigo = criterio.getAttribute(Columns.COR_CODIGO) != null ? criterio.getAttribute(Columns.COR_CODIGO).toString() : "";
            } else if ("cancelar".equalsIgnoreCase(tipoOperacao) || "cancelar-ade".equalsIgnoreCase(tipoOperacao)) {
                tpsCodigo = CodedValues.TPS_PERMITE_CANCELAR_CONTRATOS;
            } else if ("liquidar".equalsIgnoreCase(tipoOperacao)) {
                tpsCodigo = CodedValues.TPS_PERMITE_LIQUIDAR_CONTRATOS;
            } else if ("liquidarparcela".equalsIgnoreCase(tipoOperacao)) {
                tpsCodigo = CodedValues.TPS_PERMITE_LIQUIDAR_PARCELA;
            }

            // Obtem os criterios de pesquisa avancada
            if (!"renegociar".equalsIgnoreCase(tipoOperacao) && !"comprar".equalsIgnoreCase(tipoOperacao)) {
                tmoCodigo       = criterio.getAttribute(Columns.TMO_CODIGO) != null ? criterio.getAttribute(Columns.TMO_CODIGO).toString() : "";
                tgcCodigo       = criterio.getAttribute(Columns.TGC_CODIGO) != null ? criterio.getAttribute(Columns.TGC_CODIGO).toString() : "";
                csaCodigo       = criterio.getAttribute(Columns.CSA_CODIGO) != null ? criterio.getAttribute(Columns.CSA_CODIGO).toString() : "";
                corCodigo       = criterio.getAttribute(Columns.COR_CODIGO) != null ? criterio.getAttribute(Columns.COR_CODIGO).toString() : "";
                estCodigo       = criterio.getAttribute(Columns.EST_CODIGO) != null ? criterio.getAttribute(Columns.EST_CODIGO).toString() : "";
                orgCodigo       = criterio.getAttribute(Columns.ORG_CODIGO) != null ? criterio.getAttribute(Columns.ORG_CODIGO).toString() : "";
                tgsCodigo       = criterio.getAttribute(Columns.TGS_CODIGO) != null ? criterio.getAttribute(Columns.TGS_CODIGO).toString() : "";
                svcCodigo       = criterio.getAttribute(Columns.SVC_CODIGO) != null ? criterio.getAttribute(Columns.SVC_CODIGO).toString() : "";
                cnvCodVerba     = criterio.getAttribute(Columns.CNV_COD_VERBA) != null ? criterio.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
                adeAnoMesIni    = criterio.getAttribute(Columns.ADE_ANO_MES_INI) != null ? criterio.getAttribute(Columns.ADE_ANO_MES_INI).toString() : "";
                infSaldoDevedor = criterio.getAttribute("infSaldoDevedor") != null ? criterio.getAttribute("infSaldoDevedor").toString() : "";
                periodoIni      = criterio.getAttribute("periodoIni") != null ? criterio.getAttribute("periodoIni").toString() : "";
                periodoFim      = criterio.getAttribute("periodoFim") != null ? criterio.getAttribute("periodoFim").toString() : "";
                adeIntFolha     = criterio.getAttribute(Columns.ADE_INT_FOLHA) != null ? Short.valueOf(criterio.getAttribute(Columns.ADE_INT_FOLHA).toString()) : null;
                adeIncMargem    = criterio.getAttribute(Columns.ADE_INC_MARGEM) != null ? Short.valueOf(criterio.getAttribute(Columns.ADE_INC_MARGEM).toString()) : null;
                adeIndice       = criterio.getAttribute(Columns.ADE_INDICE) != null ? criterio.getAttribute(Columns.ADE_INDICE).toString() : "";
                rseSrsCodigo    = criterio.getAttribute(Columns.RSE_SRS_CODIGO) != null ? new ArrayList<>((List<String>) criterio.getAttribute(Columns.RSE_SRS_CODIGO)) : new ArrayList<>();
                ocaDataIni      = criterio.getAttribute("ocaDataIni") != null ? criterio.getAttribute("ocaDataIni").toString() : "";
                ocaDataFim      = criterio.getAttribute("ocaDataFim") != null ? criterio.getAttribute("ocaDataFim").toString() : "";
                serCpf          = criterio.getAttribute(Columns.SER_CPF) != null ? criterio.getAttribute(Columns.SER_CPF).toString() : "";
                rseMatricula    = criterio.getAttribute(Columns.RSE_MATRICULA) != null ? criterio.getAttribute(Columns.RSE_MATRICULA).toString() : "";
                diasSolicitacaoSaldo = criterio.getAttribute("diasSolicitacaoSaldo") != null ? criterio.getAttribute("diasSolicitacaoSaldo").toString() : "-1";
                diasSolicitacaoSaldoPagaAnexo = criterio.getAttribute("diasSolicitacaoSaldoPagaAnexo") != null ? criterio.getAttribute("diasSolicitacaoSaldoPagaAnexo").toString() : "-1";
                tipoOcorrenciaPeriodo = criterio.getAttribute("tipoOcorrenciaPeriodo") != null ? criterio.getAttribute("tipoOcorrenciaPeriodo").toString() : "";
            }

            query.tipoOperacao = tipoOperacao;
            query.tpsCodigo = tpsCodigo;
            query.tmoCodigo = tmoCodigo;
            query.tgcCodigo = tgcCodigo;
            query.csaCodigo = csaCodigo;
            query.corCodigo = corCodigo;
            query.estCodigo = estCodigo;
            query.orgCodigo = orgCodigo;
            query.tgsCodigo = tgsCodigo;
            query.svcCodigo = svcCodigo;
            query.cnvCodVerba = cnvCodVerba;
            query.transferencia = (!TextHelper.isNull(transferencia) && transferencia.booleanValue());
            query.dataConciliacao = dataConciliacao;
            query.adePropria = adePropria;
            query.csaCodigos = csaCodigos;
            query.nseCodigos = nseCodigos;
            query.marCodigos = marCodigos;
            try {
                query.adeAnoMesIni = DateHelper.parse(adeAnoMesIni, "yyyy-MM-dd");
            } catch (final ParseException ex) {
                query.adeAnoMesIni = null;
            }
            try {
                query.periodoIni = DateHelper.parse(periodoIni, "yyyy-MM-dd HH:mm:ss");
            } catch (final ParseException e) {
                query.periodoIni = null;
            }
            try {
                query.periodoFim = DateHelper.parse(periodoFim, "yyyy-MM-dd HH:mm:ss");
            } catch (final ParseException e) {
                query.periodoFim = null;
            }
            try {
                query.ocaDataIni = DateHelper.parse(ocaDataIni, "yyyy-MM-dd HH:mm:ss");
            } catch (final ParseException e) {
                query.ocaDataIni = null;
            }
            try {
                query.ocaDataFim = DateHelper.parse(ocaDataFim, "yyyy-MM-dd HH:mm:ss");
            } catch (final ParseException e) {
                query.ocaDataFim = null;
            }
            query.infSaldoDevedor = infSaldoDevedor;
            query.adeIntFolha = adeIntFolha;
            query.adeIncMargem = adeIncMargem;
            query.adeIndice = adeIndice;
            query.srsCodigo = rseSrsCodigo;
            query.serCpf = serCpf;
            query.rseMatricula = rseMatricula;
            try {
                query.diasSolicitacaoSaldo = Integer.parseInt(diasSolicitacaoSaldo);
            } catch (final NumberFormatException ex) {
                query.diasSolicitacaoSaldo = -1;
            }
            try {
                query.diasSolicitacaoSaldoPagaAnexo = Integer.parseInt(diasSolicitacaoSaldoPagaAnexo);
            } catch (final NumberFormatException ex) {
                query.diasSolicitacaoSaldoPagaAnexo = -1;
            }

            try {
                final boolean retornaSomenteAdeCodigo = criterio.getAttribute("SOMENTE_ADE_CODIGO") != null ? Boolean.parseBoolean(criterio.getAttribute("SOMENTE_ADE_CODIGO").toString()) : false;
                query.retornaSomenteAdeCodigo = retornaSomenteAdeCodigo;
            } catch (final Exception e) {
                query.retornaSomenteAdeCodigo = false;
            }
            query.tipoOcorrenciaPeriodo = tipoOcorrenciaPeriodo;

            query.arquivado = (responsavel.isCseSup() && (criterio.getAttribute("arquivado") != null) && "S".equals(criterio.getAttribute("arquivado")));

            query.tipoOrdenacao = (!TextHelper.isNull(criterio.getAttribute("TIPO_ORDENACAO")) ? criterio.getAttribute("TIPO_ORDENACAO").toString() : "");
            if (!TextHelper.isNull(criterio.getAttribute("ORDENACAO"))) {
                query.ordenacao = criterio.getAttribute("ORDENACAO").toString();
            }

            query.temAnexoPendenteValidacao = (criterio.getAttribute("temAnexoPendenteValidacao") != null) && ((Boolean) criterio.getAttribute("temAnexoPendenteValidacao"));

            if (!TextHelper.isNull(criterio.getAttribute("usaModuloBeneficio"))) {
                query.usaModuloBeneficio = (boolean) criterio.getAttribute("usaModuloBeneficio");
            }

            final List<String> tntCodigos = criterio.getAttribute("tntCodigos") != null ? new ArrayList<>((List<String>) criterio.getAttribute("tntCodigos")) : new ArrayList<>();
            if ((tntCodigos != null) && !tntCodigos.isEmpty()) {
                query.tntCodigos = tntCodigos;
            }

            final List<String> existeTipoOcorrencias = criterio.getAttribute("existeTipoOcorrencias") != null ? new ArrayList<>((List<String>) criterio.getAttribute("existeTipoOcorrencias")) : null;
            if ((existeTipoOcorrencias != null) && !existeTipoOcorrencias.isEmpty()) {
                query.existeTipoOcorrencias = existeTipoOcorrencias;
            }

            // TODO Aplicar a restrição também ao confirmar renegociação, porém sobre as consignações renegociadas e não aquelas listadas no resultado
            if (responsavel.isCsaCor() && ("confirmar_liquidacao".equalsIgnoreCase(query.tipoOperacao) || "confirmar_renegociacao".equalsIgnoreCase(query.tipoOperacao)) &&
                    autorizacaoController.efetuarLiquidacaoDuasEtapas(responsavel) &&
                    autorizacaoController.exigirDuplaConfirmacaoLiquidacao(responsavel)) {
                // Se é operação de confirmação de liquidação, e a liquidação é obrigatoriamente em duas etapas para a CSA
                // então filtra as consignações que o usuário ainda não fez sua confirmação
                if ("confirmar_liquidacao".equalsIgnoreCase(query.tipoOperacao)) {
                    query.validarInexistenciaConfLiquidacao = true;
                } else {
                    query.validarInexistenciaConfLiquidacaoRelacionamento = true;
                }
            }
        }
    }

    private TransferObject setAutDescontoValues(AutDesconto adeBean) throws FindException {
        final TransferObject adeTO = new CustomTransferObject();
        adeTO.setAttribute(Columns.ADE_CODIGO, adeBean.getAdeCodigo());
        adeTO.setAttribute(Columns.ADE_AGENCIA, adeBean.getAdeAgencia());
        adeTO.setAttribute(Columns.ADE_AGENCIA_DV, adeBean.getAdeAgenciaDv());
        adeTO.setAttribute(Columns.ADE_ANO_MES_FIM, adeBean.getAdeAnoMesFim());
        adeTO.setAttribute(Columns.ADE_ANO_MES_FIM_FOLHA, adeBean.getAdeAnoMesFimFolha());
        adeTO.setAttribute(Columns.ADE_ANO_MES_FIM_REF, adeBean.getAdeAnoMesFimRef());
        adeTO.setAttribute(Columns.ADE_ANO_MES_INI, adeBean.getAdeAnoMesIni());
        adeTO.setAttribute(Columns.ADE_ANO_MES_INI_FOLHA, adeBean.getAdeAnoMesIniFolha());
        adeTO.setAttribute(Columns.ADE_BANCO, adeBean.getAdeBanco());
        adeTO.setAttribute(Columns.ADE_CARENCIA, adeBean.getAdeCarencia());
        adeTO.setAttribute(Columns.ADE_COD_REG, adeBean.getAdeCodReg());
        adeTO.setAttribute(Columns.ADE_CONTA, adeBean.getAdeConta());
        adeTO.setAttribute(Columns.ADE_CONTA_DV, adeBean.getAdeContaDv());
        adeTO.setAttribute(Columns.ADE_DATA, adeBean.getAdeData());
        adeTO.setAttribute(Columns.ADE_IDENTIFICADOR, adeBean.getAdeIdentificador());
        adeTO.setAttribute(Columns.ADE_INDICE, adeBean.getAdeIndice());
        adeTO.setAttribute(Columns.ADE_NUMERO, adeBean.getAdeNumero());
        adeTO.setAttribute(Columns.ADE_PAGA, adeBean.getAdePaga());
        adeTO.setAttribute(Columns.ADE_PRAZO, adeBean.getAdePrazo());
        adeTO.setAttribute(Columns.ADE_PRD_PAGAS, adeBean.getAdePrdPagas());
        adeTO.setAttribute(Columns.ADE_TAXA_JUROS, adeBean.getAdeTaxaJuros());
        adeTO.setAttribute(Columns.ADE_TIPO_VLR, adeBean.getAdeTipoVlr());
        adeTO.setAttribute(Columns.ADE_VLR, adeBean.getAdeVlr());
        adeTO.setAttribute(Columns.ADE_VLR_FOLHA, adeBean.getAdeVlrFolha());
        adeTO.setAttribute(Columns.ADE_VLR_IOF, adeBean.getAdeVlrIof());
        adeTO.setAttribute(Columns.ADE_VLR_LIQUIDO, adeBean.getAdeVlrLiquido());
        adeTO.setAttribute(Columns.ADE_VLR_REF, adeBean.getAdeVlrRef());
        adeTO.setAttribute(Columns.ADE_VLR_TAC, adeBean.getAdeVlrTac());
        adeTO.setAttribute(Columns.ADE_INC_MARGEM, adeBean.getAdeIncMargem());
        adeTO.setAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA, adeBean.getAdeVlrSegPrestamista());
        adeTO.setAttribute(Columns.RSE_CODIGO, adeBean.getRegistroServidor().getRseCodigo());
        adeTO.setAttribute(Columns.ADE_PERIODICIDADE, adeBean.getAdePeriodicidade());
        adeTO.setAttribute(Columns.ADE_DATA_LIBERACAO_VALOR, adeBean.getAdeDataLiberacaoValor());

        final StatusAutorizacaoDesconto sad = StatusAutorizacaoDescontoHome.findByPrimaryKey(adeBean.getStatusAutorizacaoDesconto().getSadCodigo());
        adeTO.setAttribute(Columns.SAD_DESCRICAO, sad.getSadDescricao());
        adeTO.setAttribute(Columns.SAD_CODIGO, sad.getSadCodigo());
        return adeTO;
    }

    @Override
    public TransferObject findAutDesconto(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
            return setAutDescontoValues(adeBean);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.encontrado", responsavel, ex);
        }
    }

    @Override
    public TransferObject findAutDescontoByAdeNumero(Long adeNumero, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto adeBean = AutDescontoHome.findByAdeNumero(adeNumero);
            return setAutDescontoValues(adeBean);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.encontrado", responsavel, ex);
        }
    }

    @Override
    public CustomTransferObject buscaAutorizacao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return buscaAutorizacao(adeCodigo, false, responsavel);
    }

    @Override
    public CustomTransferObject buscaAutorizacao(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            CustomTransferObject ade = null;
            if (!TextHelper.isNull(adeCodigo)) {
                if (autorizacaoController.usuarioPodeConsultarAde(adeCodigo, responsavel)) {
                    final List<String> adeCodigos = new ArrayList<>();
                    adeCodigos.add(adeCodigo);

                    final ObtemConsignacaoQuery query = new ObtemConsignacaoQuery();
                    query.adeCodigos = adeCodigos;
                    query.arquivado = arquivado;

                    final List<TransferObject> ades = query.executarDTO();
                    if ((ades != null) && (ades.size() > 0)) {
                        ade = (CustomTransferObject) ades.get(0);
                    }
                }
                final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.FIND, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);
                log.setRegistroServidor((ade != null ? (String) ade.getAttribute(Columns.RSE_CODIGO) : null));
                log.write();
            }

            if (ade != null) {
                return ade;
            } else {
                throw new AutorizacaoControllerException("mensagem.erro.interno.sistema.nenhuma.consignacao.encontrada", responsavel);
            }
        } catch (LogControllerException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> buscaAutorizacao(List<String> adeCodigos, boolean validaPermissao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if ((adeCodigos != null) && !adeCodigos.isEmpty()) {
                final ObtemConsignacaoQuery query = new ObtemConsignacaoQuery();
                query.adeCodigos = adeCodigos;
                final List<TransferObject> ade = query.executarDTO();

                for (final String adeCodigo : adeCodigos) {
                    if (validaPermissao) {
                        // Verifica se pode consultar o contrato
                        autorizacaoController.usuarioPodeConsultarAde(adeCodigo, responsavel);
                    }

                    // Grava log da pesquisa
                    final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.FIND, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.write();
                }

                return ade;
            }
            return null;
        } catch (LogControllerException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Acompanhamento de Compra de Contrato
    @Override
    public int contarCompraContratos(TransferObject parametros, String csaCodigo, String corCodigo, List<String> orgCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaAcompanhamentoCompraQuery query = new ListaAcompanhamentoCompraQuery();
            query.parametrosTO = parametros;
            query.csaCodigo = csaCodigo;
            query.corCodigo = corCodigo;
            query.orgCodigos = orgCodigo;
            query.responsavel = responsavel;

            if (CanalEnum.SOAP.equals(responsavel.getCanal()) && responsavel.isCsa()){
                final String param = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, responsavel);
                query.matriculaExataSoap = CodedValues.TPA_SIM.equals(param);
            }

            // Nao esta sendo utilizado o metodo executarContador, pois a query
            // nao permite uma versao com COUNT(*), ja que ela possui clasula HAVING.
            final List<TransferObject> resultado = query.executarDTO();

            return resultado != null ? resultado.size() : -1;
        } catch (final HQueryException | ParametroControllerException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> pesquisarCompraContratos(TransferObject parametros, String csaCodigo, String corCodigo, String orgCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return pesquisarCompraContratos(parametros, csaCodigo, corCodigo, orgCodigo, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> pesquisarCompraContratos(TransferObject parametros, String csaCodigo, String corCodigo, String orgCodigo,  int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            List<String> orgList = null;
            if (orgCodigo != null){
                orgList = new ArrayList<>();
                orgList.add(orgCodigo);
            }

            final ListaAcompanhamentoCompraQuery query = new ListaAcompanhamentoCompraQuery();
            query.parametrosTO = parametros;
            query.csaCodigo = csaCodigo;
            query.corCodigo = corCodigo;
            query.orgCodigos = orgList;
            query.responsavel = responsavel;

            if (CanalEnum.SOAP.equals(responsavel.getCanal()) && responsavel.isCsa()){
                final String param = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, responsavel);
                query.matriculaExataSoap = CodedValues.TPA_SIM.equals(param);
            }

            // Log de pesquisa de acompanhamento de compra de contratos
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);
            log.setCorrespondente(corCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.pesquisa.acompanhamento.compra.contrato", responsavel));
            log.write();

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            return query.executarDTO();
        } catch (final LogControllerException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erro.executar.pesquisa.acompanhamento.compra.contrato", responsavel, ex);
        }
    }

    /**
     * Retorna o adeCodigoDestino do contratos relacionado ao contrato representado pelo
     * parametro "adeCodigoOrigem" atraves de um processo de compra. Pesquisa pelo relacionamento
     * para o codigo TNT_CONTROLE_COMPRA, e pelo codigo da origem, retornando o codigo
     * do contrato de destino, ou seja, o contrato novo.
     * @param adeCodigoOrigem String
     * @param responsavel String
     * @return List
     * @throws AutorizacaoControllerException
     */
    @Override
    public String getAdeRelacionamentoCompra(String adeCodigoOrigem, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final List<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
            sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
            sadCodigos.add(CodedValues.SAD_DEFERIDA);

            final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigoOrigem, CodedValues.TNT_CONTROLE_COMPRA, sadCodigos);
            if ((adesCompra != null) && !adesCompra.isEmpty()) {
                final RelacionamentoAutorizacao radBean = adesCompra.iterator().next();
                return radBean.getAdeCodigoDestino();
            }
            return null;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Pesquisa por consignacoes envolvidas em algum relacionamento de consignacao especificados
     * pelos parametros
     * @param adeCodigoOrigem
     * @param adeCodigoDestino
     * @param csaCodigoOrigem
     * @param csaCodigoDestino
     * @param tntCodigo - tipo da natureza do relacionamento
     * @param stcCodigo - se for relacionamento de compra, indica o status da compra
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> pesquisarConsignacaoRelacionamento(String adeCodigoOrigem, String adeCodigoDestino, String csaCodigoOrigem, String csaCodigoDestino, String tntCodigo, List<String> stcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoRelacionamentoQuery query = new ListaConsignacaoRelacionamentoQuery();
            query.adeCodigoOrigem = adeCodigoOrigem;
            query.adeCodigoDestino = adeCodigoDestino;
            query.csaCodigoOrigem = csaCodigoOrigem;
            query.csaCodigoDestino = csaCodigoDestino;
            query.stcCodigo = stcCodigo;
            query.tntCodigo = tntCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erro.interno.executar.pesquisa.relacionamento.contratos", responsavel, ex);
        }
    }

    /**
     * Retorna o historico de um contrato, que e a soma das ocorrencias, mais
     * os relacionamentos de origem e os de destino. A ordenacao e feita pela data.
     * @param codigo
     * @param mostraTodoHistorico
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> historicoAutorizacao(String codigo, boolean mostraTodoHistorico, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final List<TransferObject> resultado = new ArrayList<>();

            // Historico de ocorrencias
            final HistoricoConsignacaoOcorrenciaQuery query1 = new HistoricoConsignacaoOcorrenciaQuery();
            query1.adeCodigo = codigo;
            query1.mostraTodoHistorico = mostraTodoHistorico;
            query1.arquivado = arquivado;
            query1.responsavel = responsavel;
            resultado.addAll(query1.executarDTO());

            // Historico de relacionamentos pela origem
            final HistoricoConsignacaoRelacionamentoQuery query2 = new HistoricoConsignacaoRelacionamentoQuery();
            query2.responsavel = responsavel;
            query2.adeCodigoOrigem = codigo;
            query2.arquivado = arquivado;
            resultado.addAll(query2.executarDTO());

            // Historico de relacionamentos pelo destino
            final HistoricoConsignacaoRelacionamentoQuery query3 = new HistoricoConsignacaoRelacionamentoQuery();
            query3.responsavel = responsavel;
            query3.adeCodigoDestino = codigo;
            query3.arquivado = arquivado;
            resultado.addAll(query3.executarDTO());

            if (responsavel.isCseSup()) {
                // Historico de relacionamentos pela origem
                final HistoricoConsignacaoRelacionamentoQuery query4 = new HistoricoConsignacaoRelacionamentoQuery();
                query4.responsavel = responsavel;
                query4.adeCodigoOrigem = codigo;
                query4.arquivado = arquivado;
                query4.intermediario = true;
                resultado.addAll(query4.executarDTO());

                // Historico de relacionamentos pelo destino
                final HistoricoConsignacaoRelacionamentoQuery query5 = new HistoricoConsignacaoRelacionamentoQuery();
                query5.responsavel = responsavel;
                query5.adeCodigoDestino = codigo;
                query5.arquivado = arquivado;
                query5.intermediario = true;
                resultado.addAll(query5.executarDTO());
            }

            // Ordena o resultado das pesquisas
            Collections.sort(resultado, (o1, o2) -> {
                final java.util.Date d1 = (java.util.Date) o1.getAttribute(Columns.OCA_DATA);
                final java.util.Date d2 = (java.util.Date) o2.getAttribute(Columns.OCA_DATA);
                return d2.compareTo(d1);
            });

            return resultado;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista historico de ocorrencias de um contrato comprado de terceiro pertinentes
     * ao processo de compra
     *
     * @param autoCode - Codigo da autorizacao de terceiro originario do codigo de autorizacao corrente
     * @param autCodeDest - Codigo da autorizacao corrente (ou de destino)
     */
    @Override
    public List<TransferObject> hstOrigemTerceiro(String autCode, String autCodeDest) throws AutorizacaoControllerException {
        try {
            final HistoricoConsignacaoTerceirosQuery query = new HistoricoConsignacaoTerceirosQuery();
            query.adeCodigoOrigem = autCode;
            query.adeCodigoDestino = autCodeDest;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> lstStatusAutorizacao(AcessoSistema responsavel) throws AutorizacaoControllerException {
        return lstStatusAutorizacao(null, false, responsavel);
    }

    @Override
    public List<TransferObject> lstStatusAutorizacao(List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return lstStatusAutorizacao(null, false, responsavel);
    }

    @Override
    public List<TransferObject> lstStatusAutorizacao(List<String> sadCodigos, boolean filtraApenasSadExibeSim, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaStatusConsignacaoQuery query = new ListaStatusConsignacaoQuery();
            query.sadCodigos = sadCodigos;
            query.filtraApenasSadExibeSim = filtraApenasSadExibeSim;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Map<String, String> selectStatusAutorizacao(AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaStatusConsignacaoQuery query = new ListaStatusConsignacaoQuery();
            return query.executarMapa();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se o servco informado por parametro e do tipo
     * compulsorio.
     * @param svcCodigo
     * @return
     */
    private boolean servicoCompulsorio(String svcCodigo) {
        boolean compulsorio = false;
        try {
            final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_SERVICO_COMPULSORIO, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
            compulsorio = ((pse != null) && (pse.getPseVlr() != null) && "1".equals(pse.getPseVlr()));
        } catch (final FindException ex) {
            LOG.debug(ex.getMessage());
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return compulsorio;
    }

    @Override
    public List<TransferObject> pesquisarContratosIncComp(String rseCodigo, String svcCodigo, String svcPrioridade, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final boolean compulsorio = servicoCompulsorio(svcCodigo);
            if (compulsorio && ((svcPrioridade != null) && !"".equals(svcPrioridade))) {
                // Se e um compulsorio, e o servico tem prioridade definida, entao retorna
                // a lista de contratos que podem ser colocados em estoque para a inclusao do contrato compulsorio.
                final ListaContratosCompulsoriosQuery query = new ListaContratosCompulsoriosQuery();
                query.rseCodigo = rseCodigo;
                query.svcCodigo = svcCodigo;
                query.svcPrioridade = svcPrioridade;
                return query.executarDTO();
            }
            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * retorna os contratos do servidor para o servico especifico, ordenado pela data de inclusao
     * @param rseCodigo Codigo do servidor
     * @param svcCodigo Codigo do servico
     * @param responsavel
     * @return
     */
    @Override
    public List<TransferObject> pesquisarContratosPorRseSvc(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final ListaConsignacaoRseSvcQuery contratosRseSvc = new ListaConsignacaoRseSvcQuery();
        contratosRseSvc.rseCodigo = rseCodigo;
        contratosRseSvc.svcCodigo = svcCodigo;

        try {
            return contratosRseSvc.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TransferObject> pesquisarContratosComParcela(String rseCodigo, List<String> sadCodigos, List<String> svcCodigos, List<String> csaCodigos, List<Long> adeNumeros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.add(Columns.CSA_CODIGO, csaCodigos, ConsignatariaHome.class);
            log.add(Columns.SAD_CODIGO, sadCodigos, StatusAutorizacaoDescontoHome.class);
            log.add(Columns.SVC_CODIGO, svcCodigos, ServicoHome.class);
            log.write();

            final ListarConsignacaoParcelaQuery query = new ListarConsignacaoParcelaQuery();
            query.rseCodigo = rseCodigo;
            query.sadCodigos = sadCodigos;
            query.svcCodigos = svcCodigos;
            query.csaCodigos = csaCodigos;
            query.adeNumeros = adeNumeros;
            return query.executarDTO();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean isDestinoRenegociacao(String adeCodigo) throws AutorizacaoControllerException {
        List<RelacionamentoAutorizacao> adeRenegociadas = null;
        try {
            adeRenegociadas = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
        } catch (final FindException ex) {
            LOG.debug(ex.getMessage());
        }
        return ((adeRenegociadas != null) && (adeRenegociadas.size() > 0));
    }

    @Override
    public boolean isDestinoRenegociacaoPortabilidade(String adeCodigo) throws AutorizacaoControllerException {
        List<RelacionamentoAutorizacao> adeRenegociadas = null;
        List<RelacionamentoAutorizacao> adePortadas = null;
        try {
            adeRenegociadas = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
        } catch (final FindException ex) {
            LOG.debug(ex.getMessage());
        }

        try {
            adePortadas = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
        } catch (final FindException ex) {
            LOG.debug(ex.getMessage());
        }
        return ((adeRenegociadas != null) && (adeRenegociadas.size() > 0)) || ((adePortadas != null) && (adePortadas.size() > 0));
    }

    @Override
    public boolean isDestinoRelacionamento(String adeCodigo, String tntCodigo) throws AutorizacaoControllerException {
        List<RelacionamentoAutorizacao> adeRenegociadas = null;
        try {
            adeRenegociadas = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, tntCodigo);
        } catch (final FindException ex) {
            LOG.debug(ex.getMessage());
        }
        return ((adeRenegociadas != null) && (adeRenegociadas.size() > 0));
    }

    /**
     * Seleciona o capital devido (prazo restante vincendo x valor da parcela) dos contratos abertos
     * do servidor representado pelo "rseCodigo".
     * @param rseCodigo : codigo do registro servidor
     * @param orgCodigo : codigo do orgao do registro servidor (se nulo sera feita uma busca pelo RegistroServidor)
     * @param svcCodigo : codigo do servico (se nulo sera utilizado o primeiro servico de EMPRESTIMO que tenha TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO)
     * @param adeCodigosRenegociacao : codigo de contratos que serao renegociados, e portanto nao devem contar no calculo
     * @param responsavel : responsavel pela operacao
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public BigDecimal pesquisarVlrCapitalDevidoAberto(String rseCodigo, String orgCodigo, String svcCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (TextHelper.isNull(rseCodigo)) {
            // Se o registro servidor nao foi informado, retorna erro
            throw new AutorizacaoControllerException("mensagem.erro.parametros.ausentes", responsavel);
        }
        if (TextHelper.isNull(orgCodigo)) {
            // Se o orgao nao foi informado, obtem atraves do registro servidor
            try {
                final RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                orgCodigo = rseBean.getOrgao().getOrgCodigo();
            } catch (final FindException ex) {
                throw new AutorizacaoControllerException("mensagem.nenhumServidorEncontrado", responsavel, ex);
            }
        }
        if (TextHelper.isNull(svcCodigo)) {
            try {
                // Se o servico nao foi informado, pega o servico de EMPRESTIMO que tem o parametro
                // que habilita a validacao de capital devido igual a "1" (Sim)
                final ListaServicoComParametroQuery query = new ListaServicoComParametroQuery();
                query.tpsCodigo = CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO;
                query.nseCodigo = CodedValues.NSE_EMPRESTIMO;
                query.orgCodigo = orgCodigo;
                query.csaCodigo = responsavel.getCsaCodigo();

                final List<String> pseVlrs = new ArrayList<>();
                pseVlrs.add(CodedValues.PSE_VLR_LIMITA_BASE_CALCULO_VLR_LIBERADO);
                pseVlrs.add(CodedValues.PSE_VLR_LIMITA_BASE_CALCULO_CAPITAL_DEVIDO);
                query.pseVlrs = pseVlrs;

                final List<TransferObject> servicos = query.executarDTO();
                if ((servicos == null) || (servicos.size() == 0)) {
                    throw new AutorizacaoControllerException("mensagem.erro.nenhum.servico.encontrado", responsavel);
                }
                svcCodigo = servicos.get(0).getAttribute(Columns.SVC_CODIGO).toString();
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        // Define o periodo atual de lancamento, para contabilizar apenas parcelas vincendas
        String periodoAtual = null;
        try {
            // Busca a carencia do orgao para o calculo do periodo, porem a carencia
            // do contrato deve ser passada como Zero, pois queremos o periodo vigente
            periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel).toString();
        } catch (final PeriodoException ex) {
            throw new AutorizacaoControllerException(ex);
        }

        // Realiza o somatorio do capital devido vincendo dos contratos abertos do servidor
        BigDecimal totalCapitalDevidoAtual = null;
        try {
            final ObtemTotalCapitalDevidoVincendoRelSvcQuery query = new ObtemTotalCapitalDevidoVincendoRelSvcQuery();
            query.rseCodigo = rseCodigo;
            query.svcCodigo = svcCodigo;
            query.periodoAtual = periodoAtual;
            query.adeCodigos = adeCodigosRenegociacao;
            final List<BigDecimal> resultado = query.executarLista();
            if ((resultado != null) && (resultado.size() == 1) && (resultado.get(0) != null)) {
                totalCapitalDevidoAtual = resultado.get(0);
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return totalCapitalDevidoAtual;
    }

    /**
     * retorna os contratos do servidor com status de Aguard. Deferimento e Aguard. Liquidacao, com data menor a data informada
     * @param rseCodigo Codigo do servidor
     * @param adeData Data referencia da autorizacao
     * @param responsavel
     * @return
     */
    @Override
    public List<TransferObject> pesquisarContratosDeferManualDataMenor(String rseCodigo, java.util.Date adeData, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final ListaConsignacaoDeferManualDataMenorQuery query = new ListaConsignacaoDeferManualDataMenorQuery();
        query.rseCodigo = rseCodigo;
        query.adeDataContrato = adeData;
        query.responsavel = responsavel;
        try {
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * retorna os contratos do servidor com status de Aguard. Liquidacao a partir de uma lista de contratos que serao deferidos/indeferidos
     * @param adeCodigos Codigos de contratos do servidor que serao deferidos/indeferidos
     * @param responsavel
     * @return
     */
    @Override
    public List<TransferObject> pesquisarContratosAguardandoLiquidacao(List<TransferObject> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final List<String> rseCodigos = new ArrayList<>();
        if ((adeCodigos != null) && (adeCodigos.size() > 0)) {
            for (final TransferObject adeCodigo : adeCodigos) {
                try {
                    rseCodigos.add(adeCodigo.getAttribute(Columns.RSE_CODIGO).toString());
                } catch (final Exception ex) {
                    if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                        throw (AutorizacaoControllerException) ex;
                    } else {
                        throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }
            }
        }

        final ListaConsignacaoAguardandoLiquidacaoQuery query = new ListaConsignacaoAguardandoLiquidacaoQuery();
        query.rseCodigos = rseCodigos;
        query.responsavel = responsavel;
        try {
            return query.executarDTO();
            // return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<String> ordenarContratosPorDataCrescente(List<String> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return ordenarContratosPorData(adeCodigos, "ASC", responsavel);
    }

    /**
     * retorna a lista de contratos ordenados pela data crescente
     * @param adeCodigos Codigos dos contratos a serem ordenados
     * @param tipoOrdenacao DESC ou ASC
     * @param responsavel
     * @return
     */
    private  List<String> ordenarContratosPorData(List<String> adeCodigos, String tipoOrdenacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoOrdenadaPorAdeDataQuery query = new ListaConsignacaoOrdenadaPorAdeDataQuery();
            query.adeCodigos = adeCodigos;
            query.tipoOrdenacao = tipoOrdenacao;
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int obtemTotalConsignacaoPorRse(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemTotalConsignacaoRseQuery query = new ObtemTotalConsignacaoRseQuery();
            query.rseCodigo = rseCodigo;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> obtemTotalConsignacaoPorCsa(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemTotalConsignacaoCsaQuery query = new ObtemTotalConsignacaoCsaQuery();
            query.rseCodigo = rseCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<String> listarConsignacoesReativacaoAutomatica(AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoReativacaoAutomaticaQuery query = new ListaConsignacaoReativacaoAutomaticaQuery();
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject obtemDadosUsuarioUltimaOperacaoAde(String adeCodigo, String tocCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemDadosUsuarioUltimaOperacaoAdeQuery query = new ObtemDadosUsuarioUltimaOperacaoAdeQuery();
            query.adeCodigo = adeCodigo;
            query.tocCodigo = tocCodigo;
            final List<TransferObject> dados = query.executarDTO();
            if ((dados != null) && !dados.isEmpty()) {
                return dados.get(0);
            }
            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaAutorizacaoRsePorCsa(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {

            final ListaConsignacaoPorRseCnvCodVerbaQuery query = new ListaConsignacaoPorRseCnvCodVerbaQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisarConsignacoesSolicitacoesLiquidacaoNaoAntendida(String csaCodigo, boolean count, AcessoSistema responsavel) throws AutorizacaoControllerException {

    	final ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery query = new ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery();

    	query.csaCodigo = csaCodigo;
    	query.count = count;

    	try {
    		return query.executarDTO();
    	} catch (final HQueryException ex) {
    		LOG.error(ex.getMessage(), ex);
    		throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
    	}
    }

    @Override
    public int countConsignacoesSolicitacoesLiquidacaoNaoAntendida(String codigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
    	try {
    		final ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery query = new ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery();

    		query.csaCodigo = codigo;
    		query.count = true;
    		return query.executarContador();

    	} catch (final HQueryException ex) {
    		throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
    	}

    }

    @Override
    public BigDecimal obterTotalAdeVlrPorPeriodoInclusao(String rseCodigo, java.util.Date dataReservaInicial, java.util.Date dataReservaFinal, Short adeIncMargem, List<String> adeCodigosExclusao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return obterTotalAdeVlrPorPeriodoInclusao(rseCodigo, dataReservaInicial, dataReservaFinal, adeIncMargem, adeCodigosExclusao, false, responsavel);
    }

    @Override
    public BigDecimal obterTotalAdeVlrPorPeriodoInclusao(String rseCodigo, java.util.Date dataReservaInicial, java.util.Date dataReservaFinal, Short adeIncMargem, List<String> adeCodigosExclusao, boolean verificaAdeAnoMesIni, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final ObtemTotalAdeVlrPorPeriodoInclusaoQuery query = new ObtemTotalAdeVlrPorPeriodoInclusaoQuery();

        query.rseCodigo = rseCodigo;
        query.periodoIni = dataReservaInicial;
        query.periodoFim = dataReservaFinal;
        query.adeIncMargem = adeIncMargem;
        query.adeCodigosExclusao = adeCodigosExclusao;
        query.verificaAdeAnoMesIni = verificaAdeAnoMesIni;

        try {
            return query.executarSomatorio(new BigDecimal(0));
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstAdeVlrPorPeriodoInclusao(String rseCodigo, java.util.Date dataReservaInicial, java.util.Date dataReservaFinal, Short adeIncMargem, List<String> adeCodigosExclusao, AcessoSistema responsavel) throws AutorizacaoControllerException {
    	 final ListaAdeVlrPorPeriodoInclusaoQuery query = new ListaAdeVlrPorPeriodoInclusaoQuery();

         query.rseCodigo = rseCodigo;
         query.periodoIni = dataReservaInicial;
         query.periodoFim = dataReservaFinal;
         query.adeIncMargem = adeIncMargem;
         query.adeCodigosExclusao = adeCodigosExclusao;

         try {
             return query.executarDTO();
         } catch (final HQueryException ex) {
             LOG.error(ex.getMessage(), ex);
             throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
         }
     }

    @Override
    public List<TransferObject> listaContratosParcelasReimplanteManual (String rseCodigo, String csaCodigo, List<Long> adeNumeros, AcessoSistema responsavel) throws AutorizacaoControllerException{
        try {

            Date periodoAtual = null;
            if(!TextHelper.isNull(rseCodigo)) {
                final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(registroServidor.getOrgao().getOrgCodigo(), responsavel);
            }

            final ListarConsignacaoReimplanteManualQuery query = new ListarConsignacaoReimplanteManualQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            query.adeNumeros = adeNumeros;
            query.periodoAtual = periodoAtual;

            return query.executarDTO();
        } catch (HQueryException | FindException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countContratosParcelasReimplanteManual (String rseCodigo, String csaCodigo, List<Long> adeNumeros, AcessoSistema responsavel) throws AutorizacaoControllerException{
        try {
            final ListarConsignacaoReimplanteManualQuery query = new ListarConsignacaoReimplanteManualQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            query.adeNumeros = adeNumeros;
            query.count = true;

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaAutorizacaoSemParcela(List<String> adeNumero, String rseCodigo, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.write();

            List<Long> adeNumeros = null;
            if ((adeNumero != null) && !adeNumero.isEmpty()) {
                adeNumeros = new ArrayList<>();
                for (final String numero : adeNumero) {
                    try {
                        adeNumeros.add(Long.valueOf(numero));
                    } catch (final NumberFormatException ex) {
                        throw new AutorizacaoControllerException("mensagem.erro.ade.numero.invalido.arg0", responsavel, ex, numero);
                    }
                }
            }

            final ListaConsignacaoSemParcelaQuery query = new ListaConsignacaoSemParcelaQuery();

            if (criterio != null) {
                if (criterio.getAttribute(Columns.COR_CODIGO) != null) {
                    query.orgCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
                }

                if (criterio.getAttribute(Columns.CSA_CODIGO) != null) {
                    query.csaCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
                }

                if (criterio.getAttribute(Columns.ORG_CODIGO) != null) {
                    query.corCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
                }
            }

            query.adeNumero = adeNumeros;
            query.rseCodigo = rseCodigo;
            return query.executarDTO();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countPesquisaAutorizacaoSemParcela(List<String> adeNumero, String rseCodigo, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.write();

            List<Long> adeNumeros = null;
            if ((adeNumero != null) && !adeNumero.isEmpty()) {
                adeNumeros = new ArrayList<>();
                for (final String numero : adeNumero) {
                    try {
                        adeNumeros.add(Long.valueOf(numero));
                    } catch (final NumberFormatException ex) {
                        throw new AutorizacaoControllerException("mensagem.erro.ade.numero.invalido.arg0", responsavel, ex, numero);
                    }
                }
            }

            final ListaConsignacaoSemParcelaQuery query = new ListaConsignacaoSemParcelaQuery();

            if (criterio != null) {
                if (criterio.getAttribute(Columns.COR_CODIGO) != null) {
                    query.orgCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
                }

                if (criterio.getAttribute(Columns.CSA_CODIGO) != null) {
                    query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
                }

                if (criterio.getAttribute(Columns.ORG_CODIGO) != null) {
                    query.corCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
                }
            }

            query.adeNumero = adeNumeros;
            query.rseCodigo = rseCodigo;
            query.count = true;

            return query.executarContador();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaAdeOrigem(String adeNumero, AcessoSistema responsavel) throws AutorizacaoControllerException{
        try {
            final ObtemConsignacaoOrigemQuery query = new ObtemConsignacaoOrigemQuery();

            query.adeCodigoDestino = adeNumero;

            return query.executarDTO();
        } catch (final HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }


    @Override
    public List<TransferObject> pesquisarConsignacaoConciliacao(String orgIdentificador, java.util.Date periodo, List<String> cpf, List<Long> adeNumero, List<String> adeIdentificador, String statusPagamento, AcessoSistema responsavel) throws AutorizacaoControllerException{
        try {
            final ListaConsignacaoConciliacaoQuery query = new ListaConsignacaoConciliacaoQuery();

            query.orgIdentificador = orgIdentificador;
            query.periodo = periodo;
            query.cpf = cpf;
            query.adeNumero = adeNumero;
            query.adeIdentificador = adeIdentificador;
            query.statusPagamento = statusPagamento;

            return query.executarDTO();
        } catch (final HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> verificaAdeReservaTrazLancamentos(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try{

            final TransferObject isReserva = new CustomTransferObject();
            isReserva.setAttribute("isReserva", true);
            List<TransferObject> lancamentos = new ArrayList<>();

            final ListaLancamentosCartaoPorReservaQuery qrLancamentos = new ListaLancamentosCartaoPorReservaQuery();
            qrLancamentos.adeCodigo = adeCodigo;

            lancamentos = qrLancamentos.executarDTO();

            final ListaLancamentosCartaoPorReservaQuery qrLancamentosHistorico = new ListaLancamentosCartaoPorReservaQuery();
            qrLancamentosHistorico.adeCodigo = adeCodigo;
            qrLancamentosHistorico.verificaHistorico = true;
            lancamentos.addAll(qrLancamentosHistorico.executarDTO());

            if(!lancamentos.isEmpty()) {

                Collections.sort(lancamentos, (o1, o2) -> {
                    final java.util.Date d1 = (java.util.Date) o1.getAttribute(Columns.PRD_DATA_DESCONTO);
                    final java.util.Date d2 = (java.util.Date) o2.getAttribute(Columns.PRD_DATA_DESCONTO);
                    return d1.compareTo(d2);
                });

                lancamentos.add(0, isReserva);
            }

            return lancamentos;
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public int findAdeReimplanteLoteCount(List<String> adeNumero, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemConsignacaoReimplanteQuery dados = new ObtemConsignacaoReimplanteQuery();

            List<Long> adeNumeros = null;
            if ((adeNumero != null) && (adeNumero.size() > 0)) {
                adeNumeros = new ArrayList<>();
                for (final String numero : adeNumero) {
                    adeNumeros.add(Long.valueOf(numero));
                }
            }

            dados.count = true;
            dados.adeNum = adeNumeros;


            return dados.executarContador();
        } catch (final HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }    }

    @Override
    public List<TransferObject> findAdeReimplanteLote(List<String> adeNumero, int offset, int size, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemConsignacaoReimplanteQuery dados = new ObtemConsignacaoReimplanteQuery();

            List<Long> adeNumeros = null;
            if ((adeNumero != null) && (adeNumero.size() > 0)) {
                adeNumeros = new ArrayList<>();
                for (final String numero : adeNumero) {
                    adeNumeros.add(Long.valueOf(numero));
                }
            }

            dados.adeNum = adeNumeros;

            if (size != -1) {
                dados.maxResults = size;
                dados.firstResult = offset;
            }

            return dados.executarDTO();
        } catch (final HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * lista contratos da CSA (ou de todas, se csaCodigo = null) que não possuem as ocorrências listadas no parâmetro tocCodigos.
     */
    @Override
    public List<TransferObject> lstContratosCsaSemOcorrencias(String csaCodigo, String corCodigo, List<String> tocCodigos, List<String> sadCodigos, Date ocaPeriodo, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final ListaConsignacaoSemOcaNoPeriodoQuery query = new ListaConsignacaoSemOcaNoPeriodoQuery();
        query.tocCodigos = tocCodigos;

        if (ocaPeriodo != null) {
            query.ocaPeriodo = ocaPeriodo;
        }


        if (csaCodigo != null) {
            query.csaCodigo = csaCodigo;
        }

        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            query.sadCodigos = sadCodigos;
        }



        if (offset != -1) {
            query.firstResult = offset;
        }

        if (count != -1) {
            query.maxResults = count;
        }

        try {
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findConsignacaoSerByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoSerByAdeCodigoQuery query = new ListaConsignacaoSerByAdeCodigoQuery();

            query.adeCodigo = adeCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

    }

    @Override
    public int obterTotalReservaCartaoSemLancamento(String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemQtdAdeReservaCartaoSemLancamentoQuery query = new ObtemQtdAdeReservaCartaoSemLancamentoQuery();
            query.csaCodigo = csaCodigo;
            return query.executarContador();
        } catch (final HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstContratosPortabilidadeCartao(String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoPortabilidadeCartaoQuery query = new ListaConsignacaoPortabilidadeCartaoQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int obterTotalContratosPortabilidadeCartaoCsa(String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery query = new ObtemTotalConsignacaoPortabilidadeCartaoCsaQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<Long> resultadoLista = query.executarLista();
            int numeroTotal = 0;
            for (final Long resultado : resultadoLista) {
                numeroTotal += resultado.intValue();
            }
            return numeroTotal;
        } catch (final HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int contaContratosNaoPossuemRelacionamentoVerbaRescisoria(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try{
            final ContaContratosNaoPossuemRelacionamentoVerbaRescisoriaByRseQuery query = new ContaContratosNaoPossuemRelacionamentoVerbaRescisoriaByRseQuery();
            query.rseCodigo = rseCodigo;

            return query.executarContador();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean findByOrigemOuDestino(boolean origem, String adeCodigo, String tntCodigo, AcessoSistema responsavel)  {
        List<RelacionamentoAutorizacao> rel = null;
        try {
            if (origem) {
                rel = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, tntCodigo);
            } else {
                rel = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, tntCodigo);
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return !TextHelper.isNull(rel) && !rel.isEmpty();
    }

    @Override
    public List<TransferObject> lstConsignacaoParaAutorizacaoDoServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaConsignacaoParaAutorizacaoDoServidorQuery query = new ListaConsignacaoParaAutorizacaoDoServidorQuery();
            query.rseCodigo = rseCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public BigDecimal ObtemTotalValorConsignacaoPorRseCnv(String rseCodigo, List<String> cnvCodigos, java.sql.Date periodoAtual, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemTotalValorConsignacaoPorRseCnvQuery query = new ObtemTotalValorConsignacaoPorRseCnvQuery();
            query.rseCodigo = rseCodigo;
            query.cnvCodigos = cnvCodigos;
            query.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
            query.periodoAtual = periodoAtual;

            final BigDecimal valorLancado = query.executarSomatorio();
            return valorLancado != null ? valorLancado : BigDecimal.ZERO;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal obtemTotalValorContratosRsePorMargem(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemValorTotalContratosAtivosRsePorMargemQuery query = new ObtemValorTotalContratosAtivosRsePorMargemQuery();
            query.rseCodigo = rseCodigo;
            query.marCodigo = marCodigo;
            final List<TransferObject> values = query.executarDTO();
            if ((values != null) && !values.isEmpty()) {
                final CustomTransferObject to = (CustomTransferObject) values.get(0);
                if (!TextHelper.isNull(to.getAttribute("total"))) {
                    return new BigDecimal(to.getAttribute("total").toString());
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return BigDecimal.ZERO;
    }

    public List<TransferObject> listaTotalConsignacaoAtivasPorOrgao(AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaTotalConsignacaoAtivasPorOrgaoQuery query = new ListaTotalConsignacaoAtivasPorOrgaoQuery();
            query.responsavel = responsavel;
            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    public List<TransferObject> listaSolicitacaoSaldoDevedorPorRegistroServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery query = new ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery();
            query.rseCodigo = rseCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}

