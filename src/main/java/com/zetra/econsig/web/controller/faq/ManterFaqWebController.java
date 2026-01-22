package com.zetra.econsig.web.controller.faq;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FaqTO;
import com.zetra.econsig.exception.FaqControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.CategoriaFaq;
import com.zetra.econsig.service.faq.FaqController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterFaqWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutenção Faq.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterFaq" })
public class ManterFaqWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractWebController.class);

    @Autowired
    private FaqController faqController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        List<TransferObject> lstFaqs = null;
        /* FILTRO DE FAQ */
        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }
        try {
            CustomTransferObject criterio = null;

            String campo = null;
            switch (filtro_tipo) {
                //Seta Criterio da Listagem
                case 2:
                    campo = Columns.FAQ_EXIBE_CSE;
                    break;
                case 3:
                    campo = Columns.FAQ_EXIBE_CSA;
                    break;
                case 4:
                    campo = Columns.FAQ_EXIBE_COR;
                    break;
                case 5:
                    campo = Columns.FAQ_EXIBE_ORG;
                    break;
                case 6:
                    campo = Columns.FAQ_EXIBE_SER;
                    break;
                case 7:
                    campo = Columns.FAQ_EXIBE_SUP;
                    break;
                case 8:
                    campo = Columns.FAQ_TITULO_1;
                    break;
                default:
                    campo = null;
                    break;
            }
            // Pesquisa feita pelo título
            if (campo != null) {
                criterio = new CustomTransferObject();
                if (filtro != null && (filtro_tipo == 8)) {
                    criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                } else {
                    criterio.setAttribute(campo, "");
                }
            }

            model.addAttribute("filtro_tipo", filtro_tipo);
            model.addAttribute("filtro", filtro);

            int total = 0;
            try {
                total = faqController.countFaq(criterio, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            lstFaqs = faqController.lstFaq(criterio, offset, size, responsavel);

            // Monta lista de parâmetros e link de paginação
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offset");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador("../v3/manterFaq?acao=iniciar", "rotulo.faq.listagem", total, size, requestParams, false, request, model);

            int qtdColunas = 4;

            model.addAttribute("qtdColunas", qtdColunas);
            model.addAttribute("lstFaqs", lstFaqs);
            model.addAttribute("filtro_tipo", filtro_tipo);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return viewRedirect("jsp/manterFaq/listarFaq", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        ParamSession paramSession = ParamSession.getParamSession(session);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Exclui o serviço
        if (request.getParameter("excluir") != null && request.getParameter("faqCodigo") != null) {
            try {
                FaqTO faqTO = new FaqTO(request.getParameter("faqCodigo"));
                // Remove o faq
                faqController.removeFaq(faqTO, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.faq.removido.sucesso", responsavel));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";

    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws FaqControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String faqCodigo = JspHelper.verificaVarQryStr(request, "faqCodigo");

        // Faz as substituições necessárias para que o editor possa ler o que foi salvo no banco de dados
        FaqTO faqTO = null;

        String operacao = JspHelper.verificaVarQryStr(request, "operacao");

        // Apenas lista o faq existente para edição posterior
        if (operacao.equals("editar")) {
            faqTO = new FaqTO(faqCodigo);
            faqTO = faqController.findFaq(faqTO, responsavel);

            if (faqTO.getFaqHtml().equals("S")) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.faq.texto.desformatado", responsavel));
            }
        } else {
            // Inserindo novo faq
            faqTO = new FaqTO(faqCodigo);
        }

        List<CategoriaFaq> lstCategoria = null;
        try {
            lstCategoria = faqController.lstCategoriaFaq(responsavel);
        } catch (FaqControllerException ex) {
            // Caso não exista nenhum faq lidamos com o dado com outra regra.
        }

        model.addAttribute("faqTO", faqTO);
        model.addAttribute("operacao", operacao);
        model.addAttribute("faqData", DateHelper.toDateTimeString(faqTO.getFaqData()));
        model.addAttribute("lstCategorias", lstCategoria);

        return viewRedirect("jsp/manterFaq/editarFaq", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String operacao = JspHelper.verificaVarQryStr(request, "operacao");

        String faqCodigo = JspHelper.verificaVarQryStr(request, "faqCodigo");
        String faqTitulo1 = JspHelper.verificaVarQryStr(request, "faqTitulo1");
        String faqTitulo2 = JspHelper.verificaVarQryStr(request, "faqTitulo2");
        String faqTexto = JspHelper.verificaVarQryStr(request, "innerTemp");

        // Faz as substituições necessárias para que o editor possa ler o que foi salvo no banco de dados
        faqTexto = faqTexto.replaceAll("&quot;", "\"");

        String faqSequencia = JspHelper.verificaVarQryStr(request, "faqSequencia");
        String faqExibeCse = JspHelper.verificaVarQryStr(request, "faqExibeCse");
        String faqExibeCsa = JspHelper.verificaVarQryStr(request, "faqExibeCsa");
        String faqExibeCor = JspHelper.verificaVarQryStr(request, "faqExibeCor");
        String faqExibeOrg = JspHelper.verificaVarQryStr(request, "faqExibeOrg");
        String faqExibeSer = JspHelper.verificaVarQryStr(request, "faqExibeSer");
        String faqExibeSup = JspHelper.verificaVarQryStr(request, "faqExibeSup");
        String faqData = JspHelper.verificaVarQryStr(request, "faqData");
        String faqExibeMobile = JspHelper.verificaVarQryStr(request, "exibeMobile");
        String faqCafCodigo = JspHelper.verificaVarQryStr(request, "cafCodigo");


        FaqTO faqTO = null;

        // Apenas lista o faq existente para edição posterior
        if (operacao.equals("editar")) {
            faqTO = new FaqTO(faqCodigo);
            faqTO = faqController.findFaq(faqTO, responsavel);

            if (faqTO.getFaqHtml().equals("S")) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.faq.texto.desformatado", responsavel));
            }
        } else {
            // Inserindo novo faq
            faqTO = new FaqTO(faqCodigo);
        }

        faqTO.setUsuCodigo(responsavel.getUsuCodigo());

        model.addAttribute("faqTO", faqTO);
        model.addAttribute("operacao", operacao);

        if (!faqTitulo1.equals("") && faqTitulo1 != null) {
            faqTO.setFaqTitulo1(faqTitulo1);
        }
        if (!faqTitulo2.equals("") && faqTitulo2 != null) {
            faqTO.setFaqTitulo2(faqTitulo2);
        }

        // Só salva a data corrente se for criação de novo faq
        if (operacao.equals("inserir")) {
            Calendar hoje = Calendar.getInstance();
            Date faqDate = hoje.getTime();
            faqTO.setFaqData(faqDate);
            faqData = DateHelper.toDateTimeString(faqDate);
        }

        model.addAttribute("faqData", faqData);
        List<CategoriaFaq> lstCategoria = null;
        try {
            lstCategoria = faqController.lstCategoriaFaq(responsavel);
        } catch (FaqControllerException ex) {
            // Caso não exista nenhum faq lidamos com o dado com outra regra.
        }
        model.addAttribute("lstCategorias", lstCategoria);

        if (faqSequencia != null && !faqSequencia.equals("")) {
            int faqSequenciaInt = Integer.parseInt(faqSequencia);
            faqTO.setFaqSequencia(Integer.valueOf(faqSequenciaInt));
        } else {
            faqTO.setFaqSequencia(null);
        }
        if (faqExibeCse != null && !faqExibeCse.equals("")) {
            faqTO.setFaqExibeCse(faqExibeCse);
        } else {
            faqTO.setFaqExibeCse("N");
        }
        if (faqExibeOrg != null && !faqExibeOrg.equals("")) {
            faqTO.setFaqExibeOrg(faqExibeOrg);
        } else {
            faqTO.setFaqExibeOrg("N");
        }
        if (faqExibeCsa != null && !faqExibeCsa.equals("")) {
            faqTO.setFaqExibeCsa(faqExibeCsa);
        } else {
            faqTO.setFaqExibeCsa("N");
        }
        if (faqExibeCor != null && !faqExibeCor.equals("")) {
            faqTO.setFaqExibeCor(faqExibeCor);
        } else {
            faqTO.setFaqExibeCor("N");
        }
        if (faqExibeSer != null && !faqExibeSer.equals("")) {
            faqTO.setFaqExibeSer(faqExibeSer);
        } else {
            faqTO.setFaqExibeSer("N");
        }
        if (faqExibeSup != null && !faqExibeSup.equals("")) {
            faqTO.setFaqExibeSup(faqExibeSup);
        } else {
            faqTO.setFaqExibeSup("N");
        }
        if (faqExibeMobile != null && !faqExibeMobile.equals("")) {
            faqTO.setFaqExibeMobile(faqExibeMobile);
        } else {
            faqTO.setFaqExibeMobile("N");
        }

        if (!faqTexto.equals("") && faqTexto != null) {
            faqTO.setFaqTexto(TextHelper.forHtmlContent(faqTexto));
        } else {
            // Aviso caso o faq esteja sendo salvo sem um texto
            throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.informe.texto.faq", responsavel));
        }

        if (faqCafCodigo != null && !faqCafCodigo.equals("")) {
            faqTO.setCafCodigo(faqCafCodigo);
        } else {
            faqTO.setCafCodigo(null);
        }

        // Faz o update se é edição do faq
        if (operacao.equals("editar")) {
            faqController.updateFaq(faqTO, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.faq.alteracoes.salvas.sucesso", responsavel));
            return viewRedirect("jsp/manterFaq/editarFaq", request, session, model, responsavel);

        } else if (faqTO.getFaqTitulo1() != null && !faqTO.getFaqTitulo1().equals("")
                && faqTO.getFaqTitulo2() != null && !faqTO.getFaqTitulo2().equals("")
                && faqTO.getFaqTexto() != null && !faqTO.getFaqTexto().equals("")) {
            // Inserindo novo faq
            faqCodigo = faqController.createFaq(faqTO, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.faq.criado.sucesso", responsavel));

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/manterFaq?acao=iniciar", request)));
            return "jsp/redirecionador/redirecionar";

        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
