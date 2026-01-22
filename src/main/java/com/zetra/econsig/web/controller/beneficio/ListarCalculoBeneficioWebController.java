package com.zetra.econsig.web.controller.beneficio;

import java.util.ArrayList;
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
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.CalculoBeneficio;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.CalculoBeneficioController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarCalculoBeneficioWebController</p>
 * <p>Description: Listar e consultar cálculo de benefícios</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarCalculoBeneficio" })
public class ListarCalculoBeneficioWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarCalculoBeneficioWebController.class);

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private CalculoBeneficioController calculoBeneficioController;

    @Autowired
    private BeneficiarioController beneficiarioController;


    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {

        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.lista.calculo.beneficios.titulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());

    }

    @RequestMapping(params = { "acao=consultar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            List<TransferObject> orgaos = consignanteController.lstOrgaos(null, responsavel);
            List<TransferObject> beneficios = beneficioController.listaBeneficio(null, responsavel);
            List<TransferObject> tipoBeneficiarios = beneficioController.listaTipoBeneficiario(null, responsavel);
            List<TransferObject> grauParentesco = beneficiarioController.listaGrauParentesco(null, responsavel);
            List<TransferObject> motivoDependencia = beneficiarioController.listarMotivoDependencia(null, responsavel);

            boolean alterarCalculoBeneficio = responsavel.temPermissao(CodedValues.FUN_ALTERAR_CALCULO_BENEFICIOS);

            boolean ativarTabela = false;
            boolean tabelaVazia = false;

            CustomTransferObject criterioAtivarTabela = new CustomTransferObject();
            criterioAtivarTabela.setAttribute("STATUS_REGRA", "1");
            List<TransferObject> regrasEmAberto = calculoBeneficioController.listaCalculoBeneficio(criterioAtivarTabela, 0, 0, responsavel);

            if (regrasEmAberto != null && regrasEmAberto.size() > 0) {
                ativarTabela = true;
            }

            CustomTransferObject criteriotabelaVazia = new CustomTransferObject();
            List<TransferObject> regrasGerais = calculoBeneficioController.listaCalculoBeneficio(criteriotabelaVazia, 0, 0, responsavel);

            if (regrasGerais == null || regrasGerais.size() == 0) {
                tabelaVazia = true;
            }

            String tipo = AcessoSistema.ENTIDADE_SUP;

            List<TransferObject> calculoBeneficios = null;

            CustomTransferObject criterio = new CustomTransferObject();

            try {
                criterio.setAttribute("BEN_CODIGO", JspHelper.verificaVarQryStr(request, "BEN_CODIGO"));
                criterio.setAttribute("TIB_CODIGO", JspHelper.verificaVarQryStr(request, "TIB_CODIGO"));
                criterio.setAttribute("ORG_CODIGO", JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
                criterio.setAttribute("GRP_CODIGO", JspHelper.verificaVarQryStr(request, "GRP_CODIGO"));
                criterio.setAttribute("MDE_CODIGO", JspHelper.verificaVarQryStr(request, "MDE_CODIGO"));
                criterio.setAttribute("STATUS_REGRA", JspHelper.verificaVarQryStr(request, "STATUS_REGRA"));
                criterio.setAttribute("DATA", JspHelper.verificaVarQryStr(request, "DATA"));

                if (JspHelper.verificaVarQryStr(request, "STATUS_REGRA").isEmpty()) {
                    criterio.setAttribute("STATUS_REGRA", "1");
                }

                int total = calculoBeneficioController.lstCountCalculoBeneficio(criterio, responsavel);
                int size = JspHelper.LIMITE;
                int offset = 0;

                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }

                calculoBeneficios = calculoBeneficioController.listaCalculoBeneficio(criterio, offset, size, responsavel);

                // Monta lista de parâmetros através dos parâmetros de request
                Set<String> params = new HashSet<>(request.getParameterMap().keySet());
                params.remove("offset");
                List<String> requestParams = new ArrayList<>(params);
                configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.usuario", total, size, requestParams, false, request, model);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                calculoBeneficios = new ArrayList<>();
            }

            model.addAttribute("podeAlterarCalculoBeneficio", alterarCalculoBeneficio);
            model.addAttribute("ativarTabela", ativarTabela);
            model.addAttribute("tabelaVazia", tabelaVazia);
            model.addAttribute("tipo", tipo);
            model.addAttribute("calculoBeneficios", calculoBeneficios);
            model.addAttribute("orgaos", orgaos);
            model.addAttribute("tipoBeneficiarios", tipoBeneficiarios);
            model.addAttribute("grauParentesco", grauParentesco);
            model.addAttribute("motivoDependencia", motivoDependencia);
            model.addAttribute("data", criterio.getAttribute("DATA"));
            model.addAttribute("statusRegra", criterio.getAttribute("STATUS_REGRA"));
            model.addAttribute("beneficios", beneficios);

            return viewRedirect("jsp/consultarCalculoBeneficio/listarCalculoBeneficio", request, session, model, responsavel);

        } catch (CalculoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=visualizar" })
    public String visualizarCalculoBeneficio(@RequestParam(value = "clbCodigo", required = false, defaultValue = "") String clbCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String clb_codigo;
        // Variavel que vem do método salvarBeneficio (quando o beneficio é novo)
        if (clbCodigo == null || !clbCodigo.equals("")) {
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
        List<TransferObject> motivoDependenia = null;

        try {
            calculoBeneficio = calculoBeneficioController.findCalculoBeneficioByCodigo(clbCodigo, responsavel);

            orgaos = consignanteController.lstOrgaos(new CustomTransferObject(), responsavel);

            beneficios = beneficioController.listaBeneficio(new CustomTransferObject(), responsavel);

            tipoBeneficiarios = beneficioController.listaTipoBeneficiario(new CustomTransferObject(), responsavel);

            grauParentesco = beneficiarioController.listaGrauParentesco(new CustomTransferObject(), responsavel);

            motivoDependenia = beneficiarioController.listarMotivoDependencia(new CustomTransferObject(), responsavel);

        } catch (BeneficioControllerException | CalculoBeneficioControllerException | ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("novo", false);
        model.addAttribute("podeEditar", false);
        model.addAttribute("clbCodigo", clb_codigo);
        model.addAttribute("calculoBeneficio", calculoBeneficio);
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("tipoBeneficiarios", tipoBeneficiarios);
        model.addAttribute("grauParentesco", grauParentesco);
        model.addAttribute("motivoDependenia", motivoDependenia);

        return viewRedirect("jsp/manterCalculoBeneficio/alterarCalculoBeneficio", request, session, model, responsavel);
    }

    private String getLinkAction() {
        return "../v3/consultarCalculoBeneficio?acao=consultar";
    }
}
