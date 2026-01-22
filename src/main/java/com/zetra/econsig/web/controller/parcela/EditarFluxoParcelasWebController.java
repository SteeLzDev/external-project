package com.zetra.econsig.web.controller.parcela;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

/**
 * <p>Title: EditarFluxoParcelasWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar FLuxo de Parcelas.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarFluxoParcelas" })
public class EditarFluxoParcelasWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiquidarParcelaWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private PeriodoController periodoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.editar.fluxo.parcelas.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/editarFluxoParcelas");
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

        if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_FLUXO_PARCELAS_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

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

        try {
            List<String> spdCodigos = new ArrayList<>();
            spdCodigos.add(CodedValues.SPD_EMABERTO);

            // Recupera as parcelas que poderão ser editadas
            List<ParcelaDescontoTO> parcelas = parcelaController.findParcelas(adeCodigo, null, responsavel);

            String orgCodigo = (String) autdes.getAttribute(Columns.ORG_CODIGO);
            BigDecimal adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
            Integer adePrazo = (autdes.getAttribute(Columns.ADE_PRAZO) != null ? (Integer) autdes.getAttribute(Columns.ADE_PRAZO) : 999);
            Integer adePrdPagas = (autdes.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) autdes.getAttribute(Columns.ADE_PRD_PAGAS) : 0);

            Date periodoAtual = null;
            try {
                periodoAtual = PeriodoHelper.getInstance().getPeriodoAnterior(orgCodigo, responsavel);
            } catch (PeriodoException e) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Ordena pelo número das parcelas
            Collections.sort(parcelas, (o1, o2) -> {
                Integer i1 = Integer.valueOf(o1.getPrdNumero());
                Integer i2 = Integer.valueOf(o2.getPrdNumero());
                return i1.compareTo(i2);
            });

            // Acha o maior número de parcela já criado
            ParcelaDescontoTO ultimaParcela = null;
            for (ParcelaDescontoTO parcela : parcelas) {
                if (!parcela.getSpdCodigo().equals(CodedValues.SPD_EMABERTO)) {
                    ultimaParcela = parcela;
                }
            }
            ParcelaDescontoTO parcelaInicial = null;
            if (ultimaParcela != null) {
                parcelaInicial = new ParcelaDescontoTO(adeCodigo, Integer.valueOf(ultimaParcela.getPrdNumero() + 1).shortValue());
                parcelaInicial.setPrdDataDesconto(DateHelper.toSQLDate(DateHelper.addMonths(ultimaParcela.getPrdDataDesconto(), 1)));
            }

            // Define a lista de anos que deverá aparecer na listagem para seleção das parcelas
            Date dataInicio = (parcelaInicial != null ? parcelaInicial.getPrdDataDesconto() : (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI));
            Date dataFim = DateHelper.addMonths(periodoAtual, adePrazo - adePrdPagas);

            int anoInicio = DateHelper.getYear(dataInicio);
            int anoFim = DateHelper.getYear(dataFim);
            int[] anosParcelas = IntStream.rangeClosed(anoInicio, anoFim).toArray();
            model.addAttribute("anosParcelas", anosParcelas);

            Integer maxPrdNumero = parcelaInicial != null ? parcelaInicial.getPrdNumero().intValue() : 1;
            Map<String, ParcelaDescontoTO> parcelasPorPeriodo = new HashMap<>();
            for (ParcelaDescontoTO parcela : parcelas) {
                parcelasPorPeriodo.put(DateHelper.toPeriodString(parcela.getPrdDataDesconto()), parcela);
            }

            List<ParcelaDescontoTO> parcelasEdicao = new ArrayList<>();
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
                        parcelasEdicao.add(prd);
                    }
                    maxPrdNumero++;
                } else if (periodoParcela.getTime().after(periodoAtual)) {
                    ParcelaDescontoTO prd = new ParcelaDescontoTO(adeCodigo, maxPrdNumero.shortValue());
                    prd.setPrdDataDesconto(DateHelper.toSQLDate(periodoParcela.getTime()));
                    prd.setPrdVlrPrevisto(adeVlr);
                    prd.setSpdCodigo(CodedValues.SPD_EMABERTO);
                    prd.setSpdDescricao(ApplicationResourcesHelper.getMessage("rotulo.em.aberto", responsavel));
                    prd.setMneCodigo((String) autdes.getAttribute(Columns.ADE_MNE_CODIGO));
                    parcelasEdicao.add(prd);
                    maxPrdNumero++;
                }

                if (!PeriodoHelper.folhaMensal(responsavel)) {
                    Date proxPeriodo = periodoController.obtemPeriodoAposPrazo(orgCodigo, 1, periodoParcela.getTime(), false, responsavel);
                    periodoParcela.setTime(proxPeriodo);
                } else {
                    periodoParcela.add(Calendar.MONTH, 1);
                }
            }
            model.addAttribute("parcelas", parcelasEdicao);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("adeNumero", autdes.getAttribute(Columns.ADE_NUMERO).toString());
        model.addAttribute("autdes", autdes);

        return viewRedirect("jsp/editarFluxoParcelas/editarFluxoParcelas", request, session, model, responsavel);
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

        if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_FLUXO_PARCELAS_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

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
        } catch (AutorizacaoControllerException ex) {
            String msg = ex.getMessage();
            session.setAttribute(CodedValues.MSG_ERRO, msg);
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        try {
            String[] prdNumeros = request.getParameterValues("colPrdNumero");
            boolean parcelaAlterada = false;

            // Cria as parcelas na tb_parcela
            if (prdNumeros != null && prdNumeros.length > 0) {
                for (String prdNumero : prdNumeros) {
                    if (!TextHelper.isNull(prdNumero)) {
                        String vlrOld = JspHelper.verificaVarQryStr(request, "vlrPrevistoAnterior" + prdNumero);
                        String vlrNew = JspHelper.verificaVarQryStr(request, "vlrPrevisto" + prdNumero);
                        String dataDesconto = JspHelper.verificaVarQryStr(request, "dataDesconto" + prdNumero);

                        if (TextHelper.isNull(vlrNew) || TextHelper.isNull(dataDesconto)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }

                        java.sql.Date prdDataDesconto = DateHelper.toSQLDate(DateHelper.parse(dataDesconto, "MM/yyyy"));
                        BigDecimal prdVlrPrevistoAnterior = new BigDecimal(vlrOld.replace(",", "."));
                        BigDecimal prdVlrPrevistoNovo = new BigDecimal(vlrNew.replace(",", "."));

                        ParcelaDescontoTO parcelaTO = new ParcelaDescontoTO(adeCodigo, Short.valueOf(prdNumero));
                        parcelaTO.setSpdCodigo(CodedValues.SPD_EMABERTO);
                        parcelaTO.setPrdDataDesconto(prdDataDesconto);
                        parcelaTO.setPrdVlrPrevisto(prdVlrPrevistoNovo);

                        //Concatena o motivo fornecido pelo usuário com o motivo da alteração
                        String ocpMotivo = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.edicao.parcela", responsavel);
                        ocpMotivo += "<br>" + JspHelper.verificaVarQryStr(request, "ocpMotivo");

                        String ocp_obs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.edicao.parcela.arg0.arg1.arg2", responsavel, vlrOld, vlrNew, dataDesconto);
                        parcelaController.criaParcelaDesconto(parcelaTO, ocp_obs, responsavel);

                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.fluxo.parcela.concluido.sucesso", responsavel));
                        parcelaAlterada = true;

                        // Caso o movimento seja apenas inicial inclui ocorrência de alteração para que o sistema faça o envio da alteração do valor
                        if (ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel)) {
                            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, ocpMotivo, prdVlrPrevistoAnterior, prdVlrPrevistoNovo, null, prdDataDesconto, null, responsavel);
                        }
                    }
                }
            }

            if (!parcelaAlterada) {
                throw new ZetraException("mensagem.informe.parcela", responsavel);
            }

            /* Incluir uma ocorrência de alteração para o período subsequente, caso não exista parcela em aberto no período subsequente,
             * para que o sistema envie alteração para a folha retornando ao valor original da parcela
             */
            if (ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel)) {
                //List<String> prdNumerosSubsequentes = new ArrayList<String>();
                List<String> prdNumerosList = Arrays.asList(prdNumeros);

                for (String prdNumero : prdNumerosList) {
                    if (!TextHelper.isNull(prdNumero)) {
                        int parcelaInt = Integer.parseInt(prdNumero) + 1;
                        if (!prdNumerosList.contains(String.valueOf(parcelaInt)) && !TextHelper.isNull(request.getParameter("dataDesconto" + parcelaInt))) {

                            BigDecimal vlrNew = new BigDecimal(JspHelper.verificaVarQryStr(request, "vlrPrevisto" + prdNumero).replace(",", "."));
                            BigDecimal vlrOld = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
                            java.sql.Date prdDataDesconto = DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "dataDesconto" + parcelaInt), "MM/yyyy"));

                            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.edicao.parcela.correcao", responsavel), vlrNew, vlrOld, null, prdDataDesconto, null, responsavel);
                        }
                    }
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
