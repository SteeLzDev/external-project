package com.zetra.econsig.web.controller.pesquisaeventostotem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorio;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.service.totem.EventosTotemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarEventosTotemWebController</p>
 * <p>Description: Controlador Web responsável por listar e consultar os eventos do Totem.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarEventosTotem" })
public class ListarEventosTotemWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarEventosTotemWebController.class);

    @Autowired
    EventosTotemController eventosTotemController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.eventos.totem", responsavel));
        model.addAttribute("acaoFormulario", "../v3/listarEventosTotem");
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "pesquisar")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        return viewRedirect("jsp/consultarEventosTotem/consultarEventosTotem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=pesquisar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "pesquisar")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        int size = JspHelper.LIMITE;

        try {

            String matricula = request.getParameter("RSE_MATRICULA");
            String cpf = request.getParameter("SER_CPF");
            String periodoIni = (!TextHelper.isNull(request.getParameter("periodoIni")) ? DateHelper.reformat(request.getParameter("periodoIni").toString(), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00") : null);
            String periodoFim = (!TextHelper.isNull(request.getParameter("periodoFim")) ? DateHelper.reformat(request.getParameter("periodoFim").toString(), LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59") : null);
            String vlrPossuiFotoPesquisa = !TextHelper.isNull(request.getParameter("vlrPossuiFotoPesquisa")) && !request.getParameter("vlrPossuiFotoPesquisa").isEmpty() ? request.getParameter("vlrPossuiFotoPesquisa") : null;

            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute("matricula", matricula);
            criterio.setAttribute("cpf", cpf);
            criterio.setAttribute("periodoIni", periodoIni);
            criterio.setAttribute("periodoFim", periodoFim);
            criterio.setAttribute("vlrPossuiFotoPesquisa", vlrPossuiFotoPesquisa);
            criterio.setAttribute("size", JspHelper.LIMITE);
            criterio.setAttribute("offset", offset);

            List<TransferObject> eventos = eventosTotemController.listarEventosTotem(criterio, responsavel);

            int total = eventosTotemController.countEventosTotem(criterio, responsavel);

            if (eventos == null || eventos.isEmpty()) {
                String msg = ApplicationResourcesHelper.getMessage("mensagem.consultar.eventos.totem.erro.nenhum.registro", responsavel);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
                return iniciar(request, response, session, model);
            }

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");

            List<String> requestParams = new ArrayList<>(params);

            // Monta link de paginação
            String linkRet = "../v3/listarEventosTotem?acao=pesquisar" + "&RSE_MATRICULA=" + request.getParameter("RSE_MATRICULA") + "&SER_CPF=" + request.getParameter("SER_CPF") + "&periodoIni=" + request.getParameter("periodoIni") + "&periodoFim=" + request.getParameter("periodoFim");
            model.addAttribute("linkRet", linkRet);

            if (!TextHelper.isNull(request.getParameter("vlrPossuiFotoPesquisa"))) {
                linkRet += "&vlrPossuiFotoPesquisa=" + request.getParameter("vlrPossuiFotoPesquisa");
            }

            configurarPaginador(linkRet, "rotulo.evento.totem.listar.proposta", total, size, requestParams, false, request, model);

            model.addAttribute("eventos", eventos);
            model.addAttribute("matricula", matricula);
            model.addAttribute("cpf", cpf);
            model.addAttribute("periodoIni", periodoIni);
            model.addAttribute("periodoFim", periodoFim);
            model.addAttribute("vlrPossuiFotoPesquisa", TextHelper.isNull(request.getParameter("vlrPossuiFotoPesquisa")) ? "" : request.getParameter("vlrPossuiFotoPesquisa"));
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/consultarEventosTotem/listarEventosTotem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=detalhar" })
    public String detalhar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "pesquisar")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String evnCodigo = request.getParameter("EVN_CODIGO");
        String evnCodigoBiometria = request.getParameter("EVN_CODIGO_BIOMETRIA");

        if (TextHelper.isNull(evnCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {

            CustomTransferObject evento = eventosTotemController.buscaDetalheEvento(evnCodigo, evnCodigoBiometria, responsavel);

            model.addAttribute("evento", evento);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/consultarEventosTotem/detalharEventoTotem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=exportarResultado" })
    public ResponseEntity<String> exportarResultado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String chave = "Relatorio" + "|" + responsavel.getUsuCodigo();
        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());

        Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("eventos_totem");
        if (relatorio != null) {
            ProcessaRelatorio processaRel = ProcessaRelatorio.newInstance(relatorio.getClasseProcesso(), relatorio, parameterMap, session, responsavel);
            if (processaRel != null && responsavel.temPermissao(relatorio.getFuncoes())) {
                processaRel.start();
                ControladorProcessos.getInstance().incluir(chave, processaRel);
            }
        } else {
            return new ResponseEntity<>(Json.createObjectBuilder().build().toString(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Json.createObjectBuilder().build().toString(), HttpStatus.OK);

    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=verificarRelatorio" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> verificarRelatorio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        String relatorioDestino = (String) session.getAttribute("RELATORIO_TOTEM");
        JsonObjectBuilder result = Json.createObjectBuilder();
        if (!TextHelper.isNull(relatorioDestino)) {
            String[] relatorioDestinoPartes = relatorioDestino.split("/");
            result.add("nomeArquivo", relatorioDestinoPartes[relatorioDestinoPartes.length - 1]);
            session.removeAttribute("RELATORIO_TOTEM");
            return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
        }
        result.add("nomeArquivo", "erro");
        session.removeAttribute("RELATORIO_TOTEM");
        return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
    }
}
