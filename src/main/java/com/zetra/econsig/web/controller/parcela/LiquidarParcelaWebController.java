package com.zetra.econsig.web.controller.parcela;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: LiquidarParcelaWebController</p>
 * <p>Description: Controlador Web para o caso de uso LiquidarParcela.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/liquidarParcela" })
public class LiquidarParcelaWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiquidarParcelaWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.liquidar.parcela.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/liquidarParcela");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/liquidarParcela?acao=editar";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.liquidar.parcela.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.liquidar.parcela", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.clique.aqui", responsavel);
        String msgConfirmacao = "";
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("LIQUIDAR_PARCELA", CodedValues.FUN_LIQUIDAR_PARCELA, descricao, descricaoCompleta, "liquidar_parcela.gif", "btnLiquidarParcela", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        // Adiciona o editar consignação
        link = "../v3/liquidarParcela?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";
        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "liquidarparcela");
        return criterio;
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "SER_CODIGO", required = true, defaultValue = "") String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        ParamSession paramSession = ParamSession.getParamSession(session);

        String adeCodigo = null;
        if (TextHelper.isNull(request.getParameter("ADE_CODIGO"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } else {
            adeCodigo = request.getParameter("ADE_CODIGO");
        }

        // Recupera a autorização desconto
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (AutorizacaoControllerException ex) {
            String msg = ex.getMessage();
            session.setAttribute(CodedValues.MSG_ERRO, msg);
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        boolean permiteLiquidarParcelaFutura = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_FUTURA, CodedValues.TPC_SIM, responsavel);
        boolean permiteLiquidarParcelaParcial = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_PGTO_PARCIAL, CodedValues.TPC_SIM, responsavel);
        boolean colocarEmCarenciaLiqUltParcela = ParamSist.paramEquals(CodedValues.TPC_COLOCAR_EM_CARENCIA_LIQUIDACAO_MANUAL_ULT_PARCELA, CodedValues.TPC_SIM, responsavel);

        // Status de parcelas que podem ser liquidadas manualmente
        List<String> spdCodigos = new ArrayList<>();
        spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
        spdCodigos.add(CodedValues.SPD_SEM_RETORNO);

        // Recupera as parcelas que poderão ser liquidadas
        List<ParcelaDescontoTO> parcelas = null;
        try {
            if (permiteLiquidarParcelaFutura) {
                // Se permite liquidar parcela futura, não passa a lista de status para recuperar
                // todas as parcelas e montar a listagem de forma correta
                parcelas = parcelaController.findParcelas(adeCodigo, null, responsavel);
            } else {
                parcelas = parcelaController.findParcelas(adeCodigo, spdCodigos, responsavel);
            }

            if (permiteLiquidarParcelaParcial) {
                List<ParcelaDescontoTO> parcelasLiquidarParciais = parcelaController.findParcelasLiquidarParcial(adeCodigo, false, null, null, responsavel);
                if (parcelasLiquidarParciais != null && !parcelasLiquidarParciais.isEmpty()) {
                    for (ParcelaDescontoTO parcelasParciais : parcelasLiquidarParciais) {
                        parcelas.add(parcelasParciais);
                    }
                }
            }
        } catch (Exception ex) {
            parcelas = new ArrayList<>();
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        Integer adePrazo = (autdes.getAttribute(Columns.ADE_PRAZO) != null ? (Integer) autdes.getAttribute(Columns.ADE_PRAZO) : 999);
        Integer adePrdPagas = (autdes.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) autdes.getAttribute(Columns.ADE_PRD_PAGAS) : 0);
        Integer prazoRestante = (autdes.getAttribute(Columns.ADE_PRAZO) != null ? adePrazo - adePrdPagas : -1);

        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("adeNumero", autdes.getAttribute(Columns.ADE_NUMERO).toString());
        model.addAttribute("autdes", autdes);
        model.addAttribute("prazoRestante", prazoRestante);
        model.addAttribute("exigeMotivo", isExigeMotivoOperacao(CodedValues.FUN_LIQUIDAR_PARCELA, responsavel));

        if (permiteLiquidarParcelaFutura) {
            // Determina a quantidade de parcelas "abertas" serão geradas para liquidação futura
            BigDecimal adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);

            Date periodoAtual = null;
            try {
                String orgCodigo = (String) autdes.getAttribute(Columns.ORG_CODIGO);
                periodoAtual = PeriodoHelper.getInstance().getPeriodoAnterior(orgCodigo, responsavel);
            } catch (PeriodoException e) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Define a lista de anos que deverá aparecer na listagem para seleção das parcelas
            Date dataContrato = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM) != null ? (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM) : DateHelper.addMonths((Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI), 360); // Coloquei 360 pois são equivalentes há 30 anos tempo de uma aposentadoria.
            Date dataInicio =  (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
            Date dataFim;
            if ((!parcelas.isEmpty() && parcelas.get(parcelas.size()-1).getPrdDataDesconto().compareTo(dataContrato) < 1) || periodoAtual.compareTo(dataInicio) < 0) {
                dataFim = dataContrato;
            } else {
                dataFim = DateHelper.addMonths(periodoAtual, adePrazo - adePrdPagas);
            }

            int anoInicio = DateHelper.getYear(dataInicio);
            int anoFim = DateHelper.getYear(dataFim);
            int[] anosParcelas = IntStream.rangeClosed(anoInicio, anoFim).toArray();
            model.addAttribute("anosParcelas", anosParcelas);

            Integer maxPrdNumero = 0;
            Map<String, ParcelaDescontoTO> parcelasPorPeriodo = new HashMap<>();
            for (ParcelaDescontoTO parcela : parcelas) {
                parcelasPorPeriodo.put(DateHelper.toPeriodString(parcela.getPrdDataDesconto()), parcela);
                maxPrdNumero = Math.max(Integer.valueOf(parcela.getPrdNumero()), maxPrdNumero);
            }

            List<ParcelaDescontoTO> parcelasLiquidacao = new ArrayList<>();
            Calendar periodoFim = Calendar.getInstance();
            periodoFim.setTime(dataFim);
            periodoFim.add(Calendar.MONTH, 1);
            periodoFim = DateHelper.clearHourTime(periodoFim);

            Calendar periodoParcela = Calendar.getInstance();
            periodoParcela.setTime(dataInicio);
            periodoParcela = DateHelper.clearHourTime(periodoParcela);

            while (periodoParcela.before(periodoFim)) {
                String periodoParcelaCorrente = DateHelper.toPeriodString(periodoParcela.getTime());
                if (parcelasPorPeriodo.containsKey(periodoParcelaCorrente)) {
                    ParcelaDescontoTO prd = parcelasPorPeriodo.get(periodoParcelaCorrente);

                    if (spdCodigos.contains(prd.getSpdCodigo())) {
                        parcelasLiquidacao.add(prd);
                    } else if (permiteLiquidarParcelaParcial && (prd.getSpdCodigo().equals(CodedValues.SPD_LIQUIDADAFOLHA) || prd.getSpdCodigo().equals(CodedValues.SPD_LIQUIDADAMANUAL)) &&
                            prd.getPrdVlrRealizado() != null && prd.getPrdVlrRealizado().compareTo(prd.getPrdVlrPrevisto()) < 0) {
                        parcelasLiquidacao.add(prd);
                    }
                } else if (periodoParcela.getTime().after(periodoAtual)) {
                    ParcelaDescontoTO prd = new ParcelaDescontoTO(adeCodigo, Integer.valueOf(++maxPrdNumero).shortValue());
                    prd.setPrdDataDesconto(DateHelper.toSQLDate(periodoParcela.getTime()));
                    prd.setPrdVlrPrevisto(adeVlr);
                    prd.setSpdCodigo(CodedValues.SPD_EMABERTO);
                    prd.setSpdDescricao(ApplicationResourcesHelper.getMessage("rotulo.em.aberto", responsavel));
                    parcelasLiquidacao.add(prd);
                }
                periodoParcela.add(Calendar.MONTH, 1);
            }

            if (permiteLiquidarParcelaParcial) {
                for (ParcelaDescontoTO parcela : parcelas) {
                    BigDecimal vlrPrevisto = parcela.getPrdVlrPrevisto();
                    BigDecimal vlrRealizado = parcela.getPrdVlrRealizado();
                    String spdCodigo = parcela.getSpdCodigo();
                    if (vlrRealizado != null && vlrRealizado.compareTo(vlrPrevisto) == 0 && (spdCodigo.equals(CodedValues.SPD_LIQUIDADAFOLHA) || spdCodigo.equals(CodedValues.SPD_LIQUIDADAMANUAL))) {
                       parcelasLiquidacao.remove(parcela);
                    }
                }
            } else {
                for (ParcelaDescontoTO parcela : parcelas) {
                    BigDecimal vlrPrevisto = parcela.getPrdVlrPrevisto();
                    BigDecimal vlrRealizado = parcela.getPrdVlrRealizado();
                    String spdCodigo = parcela.getSpdCodigo();
                    if (vlrRealizado != null && vlrRealizado.compareTo(vlrPrevisto) != 0 && (spdCodigo.equals(CodedValues.SPD_LIQUIDADAFOLHA) || spdCodigo.equals(CodedValues.SPD_LIQUIDADAMANUAL))) {
                       parcelasLiquidacao.remove(parcela);
                    }
                }
            }
            model.addAttribute("parcelas", parcelasLiquidacao);

        } else {
            model.addAttribute("parcelas", parcelas);
        }

        if (colocarEmCarenciaLiqUltParcela) {
            try {
                model.addAttribute("colocarEmCarenciaLiqUltParcela", parcelaController.consignacaoAptaCarenciaConclusao(adeCodigo, responsavel));
            } catch (ParcelaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        return viewRedirect("jsp/liquidarParcela/liquidarParcela", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "SER_CODIGO", required = true, defaultValue = "") String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        ParamSession paramSession = ParamSession.getParamSession(session);

        String adeCodigo = null;
        if (TextHelper.isNull(request.getParameter("ADE_CODIGO"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } else {
            adeCodigo = request.getParameter("ADE_CODIGO");
        }

        try {
            // Integra a parcela
            Map<String, BigDecimal> vlrRealizadoParcelas = new HashMap<>();
            String[] chavesParcelas = request.getParameterValues("selecionarCheckBox");
            if (chavesParcelas != null && chavesParcelas.length > 0) {
                for (String chaveParcela : chavesParcelas) {
                    String vlrRealizado = JspHelper.verificaVarQryStr(request, "vlrRealizado" + chaveParcela);
                    if (!TextHelper.isNull(vlrRealizado)) {
                        vlrRealizadoParcelas.put(chaveParcela, new BigDecimal(String.valueOf(NumberHelper.parse(vlrRealizado, NumberHelper.getLang()))));
                    }
                }
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.parcela", responsavel));
            }
            if (vlrRealizadoParcelas.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.preencha.valor.parcela.ser.realizado", responsavel));
            } else {
                String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
                String ocpMotivo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ADE_OBS")) ? JspHelper.verificaVarQryStr(request, "ADE_OBS") : "";

                if(!TextHelper.isNull(tmoCodigo)) {
                    TipoMotivoOperacaoTransferObject tipoMotivoOperacao = tipoMotivoOperacaoController.findMotivoOperacao(tmoCodigo, responsavel);
                    ocpMotivo = tipoMotivoOperacao.getTmoDescricao() + ", " + ocpMotivo;
                }

                parcelaController.integrarParcela(adeCodigo, vlrRealizadoParcelas, CodedValues.SPD_LIQUIDADAMANUAL, ocpMotivo.trim(), responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.concluido.sucesso", responsavel));

                if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                    session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                    session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
