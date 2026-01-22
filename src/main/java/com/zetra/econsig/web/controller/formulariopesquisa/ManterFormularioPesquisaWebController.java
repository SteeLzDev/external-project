package com.zetra.econsig.web.controller.formulariopesquisa;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FormularioPesquisaTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaControllerException;
import com.zetra.econsig.exception.FormularioPesquisaRespostaControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.FormularioPesquisa;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaController;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaRespostaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>
 * Title: ManterFormularioPesquisaWebController
 * </p>
 * <p>
 * Description: Controlador Web para o caso de uso Formulário de Pesquisa
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-2025
 * </p>
 * <p>
 * Company: Salt
 * </p>
 * $
 * $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/formularioPesquisa" })
public class ManterFormularioPesquisaWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
            .getLog(ManterFormularioPesquisaWebController.class);

    @Autowired
    public FormularioPesquisaController service;

    @Autowired
    public FormularioPesquisaRespostaController formularioPesquisaRespostaController;

    @Value("${licenca.surveyJS}")
    private String licencaSurveyJS;

    @RequestMapping(params = { "acao=listar" })
    public String listarFormularioPesquisa(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        CustomTransferObject criterio = new CustomTransferObject();

        List<TransferObject> formulariosPesquisa;
        try {
            formulariosPesquisa = service.listFormularioPesquisa(criterio, -1, -1, responsavel);
            model.addAttribute("formulariosPesquisa", formulariosPesquisa);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO,
                    ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterFormularioPesquisa/listarFormularioPesquisa", request, session, model,
                responsavel);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)
            throws InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper
                        .getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model,
                        responsavel);
            }

            SynchronizerToken.saveToken(request);

            String fpeCodigo = request.getParameter("fpeCodigo");

            service.deleteFormularioPesquisa(fpeCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO,
                    ApplicationResourcesHelper.getMessage("mensagem.excluir.formulario.pesquisa.sucesso", responsavel));

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", TextHelper
                .encode64(SynchronizerToken.updateTokenInURL("../v3/formularioPesquisa?acao=listar", request)));
        return "jsp/redirecionador/redirecionar";

    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String fpeCodigo = request.getParameter("fpeCodigo");

        FormularioPesquisaTO formularioPesquisaTO = null;
        if (!TextHelper.isNull(fpeCodigo)) {
            try {
                formularioPesquisaTO = service.findByPrimaryKey(fpeCodigo);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return listarFormularioPesquisa(request, response, session, model);
            }
        }

        model.addAttribute("formularioPesquisaTO", formularioPesquisaTO);
        model.addAttribute("licencaSurveyJS", licencaSurveyJS);

        return viewRedirect("jsp/manterFormularioPesquisa/editarFormularioPesquisa", request, session, model,
                responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper
                    .getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model,
                    responsavel);
        }

        SynchronizerToken.saveToken(request);

        try {
            String fpeJsonBase64 = JspHelper.verificaVarQryStr(request, "fpeJson");
            String fpeJson = new String(java.util.Base64.getDecoder().decode(fpeJsonBase64)); 
            FormularioPesquisa fpe = new FormularioPesquisa(JspHelper.verificaVarQryStr(request, "fpeCodigo"));
            fpe.setFpeNome(JspHelper.verificaVarQryStr(request, "fpeNome"));
            fpe.setFpeBloqueiaSistema(Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "fpeBloqueiaSistema")));
            fpe.setFpePublicado(Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "fpePublicado")));
            fpe.setFpeJson(fpeJson);

            String fpeDtFim = JspHelper.verificaVarQryStr(request, "fpeDtFim");

            if (fpeDtFim != null) {
                SimpleDateFormat formatter = new SimpleDateFormat(LocaleHelper.getDatePattern());
                fpe.setFpeDtFim(formatter.parse(fpeDtFim));
            }

            if (TextHelper.isNull(fpe.getFpeCodigo())) {
                service.createFormularioPesquisa(fpe);
            } else {
                service.updateFormularioPesquisa(fpe, responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO,
                    ApplicationResourcesHelper.getMessage("mensagem.salvo.formulario.pesquisa.sucesso", responsavel,
                            fpe.getFpeNome()));

        } catch (CreateException | UpdateException | FindException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper
                    .getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model,
                    responsavel);
        } catch (FormularioPesquisaRespostaControllerException | FormularioPesquisaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return listarFormularioPesquisa(request, response, session, model);
    }

    @RequestMapping(params = { "acao=updateStatus" })
    public String updateStatus(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper
                    .getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model,
                    responsavel);
        }

        SynchronizerToken.saveToken(request);

        try {
            String fpeCodigo = JspHelper.verificaVarQryStr(request, "fpeCodigo");
            FormularioPesquisaTO fpe = service.findByPrimaryKey(fpeCodigo);

            if (fpe.isFpePublicado()) {
                service.despublicar(fpeCodigo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO,
                        ApplicationResourcesHelper.getMessage("mensagem.despublicado.form.pesquisa.sucesso",
                                responsavel, fpe.getFpeNome()));
            } else {
                service.publicar(fpeCodigo);
                session.setAttribute(CodedValues.MSG_INFO,
                        ApplicationResourcesHelper.getMessage("mensagem.publicado.form.pesquisa.sucesso", responsavel,
                                fpe.getFpeNome()));
            }

        } catch (UpdateException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper
                    .getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model,
                    responsavel);
        } catch (FormularioPesquisaRespostaControllerException | FormularioPesquisaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return listarFormularioPesquisa(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarPesquisas" })
    public String listarPesquisa(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            Model model) {

        SynchronizerToken.saveToken(request);
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.FPE_PUBLICADO, Boolean.TRUE);
        List<TransferObject> formulariosPesquisa;
        try {
            formulariosPesquisa = service.listFormularioPesquisa(criterio, -1, -1, responsavel);
            model.addAttribute("formulariosPesquisa", formulariosPesquisa);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO,
                    ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        model.addAttribute("licencaSurveyJS", licencaSurveyJS);
        return viewRedirect("jsp/manterFormularioPesquisa/listarPesquisa", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=exibir" })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> proxyHandshake(HttpServletRequest request, HttpServletResponse response,
            @RequestBody(required = false) byte[] body) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String fpeCodigo = JspHelper.verificaVarQryStr(request, "fpeCodigo");
        List<TransferObject> formulariosPesquisa;
        try {
            formulariosPesquisa = service.listFormularioPesquisaRespostaDash(fpeCodigo, responsavel);
        } catch (FormularioPesquisaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultadoFinal = new HashMap<>();
        List<Object> fprList = new ArrayList<>();

        for (TransferObject pesquisa : formulariosPesquisa) {
            String fpeJsonStr = (String) pesquisa.getAttribute(Columns.FPE_JSON);
            String fprJsonStr = (String) pesquisa.getAttribute(Columns.FPR_JSON);

            if (!resultadoFinal.containsKey("fpe") && fpeJsonStr != null) {
                try {
                    Object fpeJson = mapper.readValue(fpeJsonStr, Object.class);
                    resultadoFinal.put("fpe", fpeJson);
                } catch (IOException ex) {
                    resultadoFinal.put("fpe", fpeJsonStr);
                }
            }

            if (fprJsonStr != null) {
                try {
                    Object fprJson = mapper.readValue(fprJsonStr, Object.class);
                    fprList.add(fprJson);
                } catch (IOException e) {
                    fprList.add(fprJsonStr);
                }
            }
        }

        resultadoFinal.put("fpr", fprList);
        return ResponseEntity.ok(resultadoFinal);
    }
}

