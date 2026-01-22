package com.zetra.econsig.web.controller.parcela;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
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
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: LiquidarParcelaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Reimplantar Parcela Manual.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reimplantarParcelaManual" })
public class ReimplantarParcelaManualWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReimplantarParcelaManualWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ParcelaController parcelaController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.reimplantar.parcela.manual.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/reimplantarParcelaManual");
        model.addAttribute("imageHeader", "i-operacional");
    }

    public String getLinkAction() {
        return "../v3/reimplantarParcelaManual?acao=listar";
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listar(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "listar";
    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            if(!responsavel.isCsa() && !responsavel.isSup()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if(ParamSist.paramEquals(CodedValues.TPC_PRESERVA_PRD_REJEITADA, CodedValues.TPC_SIM,responsavel) && ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD, CodedValues.TPC_NAO,responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.info.reimplantar.parcela.manual.reimplante.automatico", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String adeNumero = JspHelper.verificaVarQryStr(request, "ADE_NUMERO");
            String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");

            List<Long> adeNumeroList = new ArrayList<>();

            if (!TextHelper.isNull(adeNumero)) {
                adeNumeroList.add(Long.parseLong(adeNumero));
            }
            if (adeNumeros != null && adeNumeros.length > 0) {
                for (String numero : adeNumeros) {
                    adeNumeroList.add(Long.parseLong(numero));
                }
            }

            String csaCodigo = responsavel.isCsa() ? responsavel.getCsaCodigo() : null;

            List<TransferObject> consignacoesReimplanteManual = pesquisarConsignacaoController.listaContratosParcelasReimplanteManual(rseCodigo, csaCodigo, adeNumeroList, responsavel);

            if (TextHelper.isNull(consignacoesReimplanteManual) || consignacoesReimplanteManual.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.info.reimplantar.parcela.manual.reimplante.nao.existe", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Seta atributos no model
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("consignacoesReimplanteManual", consignacoesReimplanteManual);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offsetReimplantarParcela");
            List<String> requestParams = new ArrayList<>(params);
            int sizeReimplanteParcela = JspHelper.LIMITE;
            int totalReimplantarParcela = pesquisarConsignacaoController.countContratosParcelasReimplanteManual(rseCodigo, csaCodigo, adeNumeroList, responsavel);

            configurarPaginador("ReimplantarParcela", "../v3/reimplantarParcelaManual", "rotulo.paginacao.titulo.rotulo.paginacao.titulo.bloqueio.pendencia.saldo.devedor", totalReimplantarParcela, sizeReimplanteParcela, requestParams, false, request, model);

            return viewRedirect("jsp/reimplantarParcela/listarConsignacoesReimplanteParcelaManual", request, session, model, responsavel);

        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.valor.numerico.generico", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

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

        // Status de parcelas que podem ser reimplantadas manualmente
        List<String> spdCodigos = new ArrayList<>();
        spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
        spdCodigos.add(CodedValues.SPD_SEM_RETORNO);

        // Recupera as parcelas que poderão ser reimplantadas manualmente
        List<ParcelaDescontoTO> parcelas = null;
        try {
                parcelas = parcelaController.findParcelasReimplantarManual(adeCodigo, spdCodigos, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        // Define a lista de anos que deverá aparecer na listagem para seleção das parcelas
        Date dataContrato = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM) != null ? (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM) : DateHelper.addMonths((Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI), 360); // Coloquei 360 pois são equivalentes há 30 anos tempo de uma aposentadoria.
        Date dataInicio =  (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);

        int anoInicio = DateHelper.getYear(dataInicio);
        int anoFim = DateHelper.getYear(dataContrato);
        int[] anosParcelas = IntStream.rangeClosed(anoInicio, anoFim).toArray();
        model.addAttribute("anosParcelas", anosParcelas);

        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("autdes", autdes);
        model.addAttribute("adeVlr",autdes.getAttribute(Columns.ADE_VLR));
        model.addAttribute("parcelas", parcelas);

        return viewRedirect("jsp/reimplantarParcela/reimplantarParcelaManual", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

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
            Map<String, BigDecimal> vlrPrevistoParcelas = new HashMap<>();
            String[] prdNumeros = request.getParameterValues("selecionarCheckBox");
            if (prdNumeros != null && prdNumeros.length > 0) {
                for (String prdNumero : prdNumeros) {
                    String vlrPrevisto = JspHelper.verificaVarQryStr(request, "vlrPrevisto" + prdNumero);
                    String dataDesconto = JspHelper.verificaVarQryStr(request, "dataDesconto" + prdNumero);
                    if (!TextHelper.isNull(vlrPrevisto)) {
                        vlrPrevistoParcelas.put(prdNumero + ";" + dataDesconto, new BigDecimal(String.valueOf(NumberHelper.parse(vlrPrevisto, NumberHelper.getLang()))));
                    }
                }
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.parcela", responsavel));
            }

            String ocpMotivo = JspHelper.verificaVarQryStr(request, "ocpMotivo");

            parcelaController.reimplentarParcela(adeCodigo, vlrPrevistoParcelas, CodedValues.SPD_EMABERTO, ocpMotivo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.reimplantar.parcela.sucesso", responsavel));

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
