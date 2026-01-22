package com.zetra.econsig.web.controller.beneficio;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.CalculoBeneficioControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.CalculoBeneficio;
import com.zetra.econsig.persistence.entity.GrauParentesco;
import com.zetra.econsig.persistence.entity.MotivoDependencia;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.TipoBeneficiario;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.CalculoBeneficioController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: AlterarCalculoBeneficioWebController</p>
 * <p>Description: Alterar cálculo de benefícios(Editar, novo e excluir)</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alterarCalculoBeneficio" })
public class AlterarCalculoBeneficioWebController extends ControlePaginacaoWebController {

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private CalculoBeneficioController calculoBeneficioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @RequestMapping(params = { "acao=novo" })
    public String novoCalculoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        List<TransferObject> orgaos = null;
        List<TransferObject> beneficios = null;
        List<TransferObject> tipoBeneficiarios = null;
        List<TransferObject> grauParentesco = null;
        List<TransferObject> motivoDependencia = null;

        try {
            orgaos = consignanteController.lstOrgaos(new CustomTransferObject(), responsavel);

            beneficios = beneficioController.listaBeneficio(new CustomTransferObject(), responsavel);

            tipoBeneficiarios = beneficioController.listaTipoBeneficiario(new CustomTransferObject(), responsavel);

            grauParentesco = beneficiarioController.listaGrauParentesco(new CustomTransferObject(), responsavel);

            motivoDependencia = beneficiarioController.listarMotivoDependencia(null, responsavel);

        } catch (ConsignanteControllerException | BeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }

        model.addAttribute("podeEditar", true);
        model.addAttribute("novo", true);
        model.addAttribute("calculoBeneficio", new CalculoBeneficio());
        model.addAttribute("clbCodigo", "");
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("tipoBeneficiarios", tipoBeneficiarios);
        model.addAttribute("grauParentesco", grauParentesco);
        model.addAttribute("motivoDependencia", motivoDependencia);

        return viewRedirect("jsp/manterCalculoBeneficio/alterarCalculoBeneficio", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarCalculoBeneficio(@RequestParam(value = "clbCodigo", required = false, defaultValue = "") String clbCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String clb_codigo;
        // Variavel que vem do método salvarBeneficio (quando o beneficio é novo)
        if (clbCodigo != null && !clbCodigo.equals("")) {
            clb_codigo = clbCodigo;
        } else {
            // Alterando um beneficio ja existente
            clb_codigo = JspHelper.verificaVarQryStr(request, "clbCodigo");
        }

        CalculoBeneficio calculoBeneficio = null;

        List<TransferObject> orgaos = null;
        List<TransferObject> beneficios = null;
        List<TransferObject> tipoBeneficiarios = null;
        List<TransferObject> grauParentesco = null;
        List<TransferObject> motivoDependencia = null;


        try {
            calculoBeneficio = calculoBeneficioController.findCalculoBeneficioByCodigo(clbCodigo, responsavel);

            orgaos = consignanteController.lstOrgaos(new CustomTransferObject(), responsavel);

            beneficios = beneficioController.listaBeneficio(new CustomTransferObject(), responsavel);

            tipoBeneficiarios = beneficioController.listaTipoBeneficiario(new CustomTransferObject(), responsavel);

            grauParentesco = beneficiarioController.listaGrauParentesco(new CustomTransferObject(), responsavel);

            motivoDependencia = beneficiarioController.listarMotivoDependencia(new CustomTransferObject(), responsavel);

        } catch (BeneficioControllerException | CalculoBeneficioControllerException | ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("novo", false);
        model.addAttribute("podeEditar", true);
        model.addAttribute("clbCodigo", clb_codigo);
        model.addAttribute("calculoBeneficio", calculoBeneficio);
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("tipoBeneficiarios", tipoBeneficiarios);
        model.addAttribute("grauParentesco", grauParentesco);
        model.addAttribute("motivoDependencia", motivoDependencia);

        return viewRedirect("jsp/manterCalculoBeneficio/alterarCalculoBeneficio", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirCalculoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws CalculoBeneficioControllerException, ConsignanteControllerException, BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String clbCodigo = JspHelper.verificaVarQryStr(request, "clbCodigo");
            CalculoBeneficio calculoBeneficio = new CalculoBeneficio();
            calculoBeneficio.setClbCodigo(clbCodigo);
            calculoBeneficioController.remove(calculoBeneficio, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.remover.calculo.beneficio.exibicao.sucesso", responsavel));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.calculo.beneficio.erro.remover", responsavel));
        }
        return "forward:/v3/consultarCalculoBeneficio?acao=consultar&_skip_history_=true";
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarCalculoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws CalculoBeneficioControllerException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        Orgao org = new Orgao();

        if (!JspHelper.verificaVarQryStr(request, "orgCodigo").isEmpty()) {
            org.setOrgCodigo(JspHelper.verificaVarQryStr(request, "orgCodigo"));
        } else {
            org = null;
        }

        TipoBeneficiario tib = new TipoBeneficiario();

        if (!JspHelper.verificaVarQryStr(request, "tibCodigo").isEmpty()) {
            tib.setTibCodigo(JspHelper.verificaVarQryStr(request, "tibCodigo"));
        } else {
            tib = null;
        }

        GrauParentesco grp = new GrauParentesco();

        if (!JspHelper.verificaVarQryStr(request, "grpCodigo").isEmpty()) {
            grp.setGrpCodigo(JspHelper.verificaVarQryStr(request, "grpCodigo"));
        } else {
            grp = null;
        }

        MotivoDependencia mde = new MotivoDependencia();

        if (!JspHelper.verificaVarQryStr(request, "mdeCodigo").isEmpty()) {
            mde.setMdeCodigo(JspHelper.verificaVarQryStr(request, "mdeCodigo"));
        } else {
            mde = null;
        }

        Beneficio ben = new Beneficio();
        ben.setBenCodigo(JspHelper.verificaVarQryStr(request, "benCodigo"));

        BigDecimal clbValorMensalidade = null;
        BigDecimal clbValorSubsidio = null;
        BigDecimal clbFaixaSalarialIni = null;
        BigDecimal clbFaixaSalarialFim = null;
        Short clbFaixaEtariaIni = null;
        Short clbFaixaEtariaFim = null;

        if (!JspHelper.verificaVarQryStr(request, "clbValorMensalidade").isEmpty()) {
            clbValorMensalidade = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "clbValorMensalidade"), NumberHelper.getLang(), "en"));
        }

        if (!JspHelper.verificaVarQryStr(request, "clbValorSubsidio").isEmpty()) {
            clbValorSubsidio = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "clbValorSubsidio"), NumberHelper.getLang(), "en"));
        }

        if (!JspHelper.verificaVarQryStr(request, "clbFaixaEtariaIni").isEmpty()) {
            clbFaixaEtariaIni = Short.parseShort(JspHelper.verificaVarQryStr(request, "clbFaixaEtariaIni"));
        }

        if (!JspHelper.verificaVarQryStr(request, "clbFaixaEtariaFim").isEmpty()) {
            clbFaixaEtariaFim = Short.parseShort(JspHelper.verificaVarQryStr(request, "clbFaixaEtariaFim"));
        }

        if (!JspHelper.verificaVarQryStr(request, "clbFaixaSalarialIni").isEmpty()) {
            clbFaixaSalarialIni = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "clbFaixaSalarialIni"), NumberHelper.getLang(), "en"));
        }

        if (!JspHelper.verificaVarQryStr(request, "clbFaixaSalarialFim").isEmpty()) {
            clbFaixaSalarialFim = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "clbFaixaSalarialFim"), NumberHelper.getLang(), "en"));
        }

        if (clbFaixaSalarialIni != null && clbFaixaSalarialFim != null && clbFaixaSalarialIni.compareTo(clbFaixaSalarialFim) == 1) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.salario.ini.maior.faixa.salario.fim", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (clbFaixaEtariaIni != null && clbFaixaEtariaFim != null && clbFaixaEtariaIni.compareTo(clbFaixaEtariaFim) == 1) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.etaria.ini.maior.faixa.etaria.fim", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!JspHelper.verificaVarQryStr(request, "clbCodigo").equals("")) {

            CalculoBeneficio calculoBeneficio = new CalculoBeneficio();

            calculoBeneficio.setClbCodigo(JspHelper.verificaVarQryStr(request, "clbCodigo"));

            calculoBeneficio.setOrgao(org);
            calculoBeneficio.setBeneficio(ben);
            calculoBeneficio.setTipoBeneficiario(tib);
            calculoBeneficio.setGrauParentesco(grp);
            calculoBeneficio.setMotivoDependencia(mde);
            calculoBeneficio.setClbValorMensalidade(clbValorMensalidade);
            calculoBeneficio.setClbValorSubsidio(clbValorSubsidio);
            calculoBeneficio.setClbFaixaEtariaIni(clbFaixaEtariaIni);
            calculoBeneficio.setClbFaixaEtariaFim(clbFaixaEtariaFim);
            calculoBeneficio.setClbFaixaSalarialIni(clbFaixaSalarialIni);
            calculoBeneficio.setClbFaixaSalarialFim(clbFaixaSalarialFim);

            try {
                calculoBeneficioController.update(calculoBeneficio, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.beneficio.exibicao.sucesso", responsavel));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } else {
            //Cria um novo cálculo benefício
            try {
                calculoBeneficioController.create(tib, org, ben, grp, mde, null, null, clbValorMensalidade, clbValorSubsidio, clbFaixaEtariaIni, clbFaixaEtariaFim, clbFaixaSalarialIni, clbFaixaSalarialFim, responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.novo.calculo.beneficio.exibicao.sucesso", responsavel));
            } catch (Exception ex) {
                ex.printStackTrace();
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return "forward:/v3/consultarCalculoBeneficio?acao=consultar";
    }

    @RequestMapping(params = { "acao=ativarTabela" })
    public String ativarTabelaCalculoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            calculoBeneficioController.ativarTabelaIniciada(responsavel);
        } catch (CalculoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return "forward:/v3/consultarCalculoBeneficio?acao=consultar&_skip_history_=true";
    }

    @RequestMapping(params = { "acao=iniciarTabela" })
    public String iniciarTabelaCalculoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            calculoBeneficioController.iniciarTabelaVigente(responsavel);
        } catch (CalculoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return "forward:/v3/consultarCalculoBeneficio?acao=consultar&_skip_history_=true";
    }

    @RequestMapping(params = { "acao=novoReajuste" })
    public String novoReajusteCalculoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        try {
            TransferObject criterio = new CustomTransferObject();

            List<TransferObject> orgaos = consignanteController.lstOrgaos(null, responsavel);
            List<TransferObject> beneficios = beneficioController.listaBeneficio(null, responsavel);
            List<TransferObject> tipoBeneficiarios = beneficioController.listaTipoBeneficiario(null, responsavel);
            List<TransferObject> grauParentesco = beneficiarioController.listaGrauParentesco(null, responsavel);
            List<TransferObject> motivoDependencia = beneficiarioController.listarMotivoDependencia(null, responsavel);

            criterio.setAttribute("BEN_CODIGO", JspHelper.verificaVarQryStr(request, "BEN_CODIGO"));
            criterio.setAttribute("TIB_CODIGO", JspHelper.verificaVarQryStr(request, "TIB_CODIGO"));
            criterio.setAttribute("ORG_CODIGO", JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
            criterio.setAttribute("GRP_CODIGO", JspHelper.verificaVarQryStr(request, "GRP_CODIGO"));
            criterio.setAttribute("MDE_CODIGO", JspHelper.verificaVarQryStr(request, "MDE_CODIGO"));
            criterio.setAttribute("STATUS_REGRA", "1");

            int total = calculoBeneficioController.lstCountCalculoBeneficio(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;

            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offset");
            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.usuario", total, size, requestParams, false, request, model);

            List<TransferObject> listaCalculoBeneficioEmAberto = calculoBeneficioController.listaCalculoBeneficio(criterio, offset, size, responsavel);

            model.addAttribute("listaCalculoBeneficio", listaCalculoBeneficioEmAberto);
            model.addAttribute("orgaos", orgaos);
            model.addAttribute("tipoBeneficiarios", tipoBeneficiarios);
            model.addAttribute("beneficios", beneficios);
            model.addAttribute("grauParentesco", grauParentesco);
            model.addAttribute("motivoDependencia", motivoDependencia);

            return viewRedirect("jsp/manterCalculoBeneficio/aplicarReajusteCalculoBeneficio", request, session, model, responsavel);

        } catch (CalculoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvarReajuste" })
    public String salvarReajusteCalculoBeneficio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            BigDecimal valorDoReajuste = new BigDecimal(0.00);
            try {
                valorDoReajuste = new BigDecimal(JspHelper.verificaVarQryStr(request, "valorReajuste").replace(",", "."));
            } catch (NumberFormatException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficio.reajuste.valor.informar", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CalculoBeneficio calculoBeneficio = new CalculoBeneficio();
            List<String> clbCodigosReajuste = new ArrayList<>();

            boolean aplicarReajusteTodos = JspHelper.verificaVarQryStr(request, "APLICAR_REAJUSTE_TODOS").equals("TODOS");
            if (aplicarReajusteTodos) {
                // monta a lista de itens de cálculos para aplicar o reajuste
                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute("BEN_CODIGO", JspHelper.verificaVarQryStr(request, "BEN_CODIGO"));
                criterio.setAttribute("TIB_CODIGO", JspHelper.verificaVarQryStr(request, "TIB_CODIGO"));
                criterio.setAttribute("ORG_CODIGO", JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
                criterio.setAttribute("GRP_CODIGO", JspHelper.verificaVarQryStr(request, "GRP_CODIGO"));
                criterio.setAttribute("MDE_CODIGO", JspHelper.verificaVarQryStr(request, "MDE_CODIGO"));
                criterio.setAttribute("STATUS_REGRA", "1");
                List<TransferObject> listaCalculoBeneficioReajuste = calculoBeneficioController.listaCalculoBeneficio(criterio, 0, 0, responsavel);
                if (listaCalculoBeneficioReajuste != null && listaCalculoBeneficioReajuste.size() > 0) {
                    for (TransferObject calculoBeneficioTO : listaCalculoBeneficioReajuste) {
                        clbCodigosReajuste.add((String) calculoBeneficioTO.getAttribute(Columns.CLB_CODIGO));
                    }
                }
            } else if (request.getParameterValues("chkAplicarReajuste") != null) {
                clbCodigosReajuste = new ArrayList<>(Arrays.asList(request.getParameterValues("chkAplicarReajuste")));
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficio.reajuste.calculo.beneficio.informar", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            if (clbCodigosReajuste != null && clbCodigosReajuste.size() > 0) {
                for (String clbCodigo : clbCodigosReajuste) {
                    calculoBeneficio = calculoBeneficioController.findCalculoBeneficioByCodigo(clbCodigo, responsavel);

                    if (JspHelper.verificaVarQryStr(request, "aplicarSobreBeneficio").equals("true") && calculoBeneficio.getClbValorMensalidade() != null) {
                        Float valorMensalidadeReajustado = calculoBeneficio.getClbValorMensalidade().floatValue() + ((valorDoReajuste.floatValue() / 100) * calculoBeneficio.getClbValorMensalidade().floatValue());
                        calculoBeneficio.setClbValorMensalidade(new BigDecimal(valorMensalidadeReajustado));
                    }

                    if (JspHelper.verificaVarQryStr(request, "aplicarSobreSubsidio").equals("true") && calculoBeneficio.getClbValorSubsidio() != null) {
                        Float valorSubsidioReajustado = calculoBeneficio.getClbValorSubsidio().floatValue() + ((valorDoReajuste.floatValue() / 100) * calculoBeneficio.getClbValorSubsidio().floatValue());
                        calculoBeneficio.setClbValorSubsidio(new BigDecimal(valorSubsidioReajustado));
                    }

                    if (JspHelper.verificaVarQryStr(request, "aplicarSobreFaixaSalarial").equals("true")) {
                        if (calculoBeneficio.getClbFaixaEtariaIni() != null) {
                            Float faixaSalarialIni = calculoBeneficio.getClbFaixaSalarialIni().floatValue() + ((valorDoReajuste.floatValue() / 100) * calculoBeneficio.getClbFaixaSalarialIni().floatValue());
                            calculoBeneficio.setClbFaixaSalarialIni(new BigDecimal(faixaSalarialIni));
                        }
                        if (calculoBeneficio.getClbFaixaSalarialFim() != null) {
                            Float faixaSalarialFim = calculoBeneficio.getClbFaixaSalarialFim().floatValue() + ((valorDoReajuste.floatValue() / 100) * calculoBeneficio.getClbFaixaSalarialFim().floatValue());
                            calculoBeneficio.setClbFaixaSalarialFim(new BigDecimal(faixaSalarialFim));
                        }
                    }

                    calculoBeneficioController.update(calculoBeneficio, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.beneficio.reajuste.aplicado.sucesso", responsavel));
                }
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.beneficio.reajuste.calculo.beneficio.informar", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

        } catch (CalculoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return novoReajusteCalculoBeneficio(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluirTabelaIniciada" })
    public String excluirTabelaIniciada(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws CalculoBeneficioControllerException, ConsignanteControllerException, BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        try {
            calculoBeneficioController.excluirTabelaIniciada(responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.remover.tabela.iniciada.calculo.beneficio.sucesso", responsavel));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.remover.tabela.iniciada", responsavel));
        }
        return "forward:/v3/consultarCalculoBeneficio?acao=consultar&_skip_history_=true";
    }

    private String getLinkAction() {
        return "../v3/alterarCalculoBeneficio?acao=novoReajuste";
    }

}
