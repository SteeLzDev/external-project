package com.zetra.econsig.web.controller.despesacomum;

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

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DespesaComumControllerException;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sdp.DespesaComumController;
import com.zetra.econsig.service.sdp.DespesaIndividualController;
import com.zetra.econsig.service.sdp.EnderecoConjuntoHabitacionalController;
import com.zetra.econsig.service.sdp.PlanoDescontoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusDespesaComumEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ConsultarDespesaComumWebController</p>
 * <p>Description: Controlador Web para caso de uso consultar despesa comum</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 25329 $
 * $Date: 2020-07-05 18:15:21 -0300 (Dom, 05 jul 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarDespesaComum" })
public class ConsultarDespesaComumWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarDespesaComumWebController.class);

    @Autowired
    private DespesaIndividualController despesaIndividualController;

    @Autowired
    private DespesaComumController despesaComumController;

    @Autowired
    private EnderecoConjuntoHabitacionalController enderecoConjuntoHabitacionalController;

    @Autowired
    private PlanoDescontoController planoDescontoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws DespesaComumControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        String tipo = !JspHelper.verificaVarQryStr(request, "tipo").isEmpty() ? JspHelper.verificaVarQryStr(request, "tipo").toLowerCase() : "cons_despesa_comum";
        boolean isConsultaDespesaComum = tipo.equals("cons_despesa_comum");

        String titulo = "";

        if (isConsultaDespesaComum) {
            titulo = ApplicationResourcesHelper.getMessage("rotulo.despesa.comum.consultar.titulo", responsavel).toUpperCase();
        } else {
            titulo = ApplicationResourcesHelper.getMessage("rotulo.despesa.comum", responsavel).toUpperCase();
        }

        String csaCodigo = responsavel.getCsaCodigo();

        if (TextHelper.isNull(csaCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.permissao.usuario", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String echCodigo = JspHelper.verificaVarQryStr(request, "ECH_CODIGO");
        String plaCodigo = JspHelper.verificaVarQryStr(request, "PLA_CODIGO");
        List<TransferObject> despesasComuns = new ArrayList<>();

        if (isConsultaDespesaComum && (!TextHelper.isNull(echCodigo) || !TextHelper.isNull(plaCodigo))) {
            TransferObject criterios = new CustomTransferObject();
            criterios.setAttribute(Columns.DEC_ECH_CODIGO, echCodigo);
            criterios.setAttribute(Columns.DEC_PLA_CODIGO, plaCodigo);

            int total = despesaComumController.countDespesasComuns(criterios, responsavel);
            if (total > 0) {
                despesasComuns = despesaComumController.findDespesasComuns(criterios, responsavel);
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.despesa.comum.nao.encontrado", responsavel));
            }

            // Monta lista de parametros atraves dos parametros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parametros abaixo
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador("../v3/consultarDespesaComum?acao=iniciar", "rotulo.paginacao.titulo.despesa.comum", total, JspHelper.LIMITE, requestParams, false, request, model);
        }

        String linkRet = JspHelper.verificaVarQryStr(request, "linkRet");

        List<TransferObject> enderecos = null;
        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ECH_CSA_CODIGO, csaCodigo);
            enderecos = enderecoConjuntoHabitacionalController.listaEndereco(criterio, -1, -1, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            enderecos = new ArrayList<>();
        }

        if (enderecos == null || enderecos.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.enderecos.nao.cadastrado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> planos = null;

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            if (tipo.equals("despesa_comum")) {
                criterio.setAttribute(Columns.NPL_CODIGO, false); // evita listar planos de taxa de uso no lanï¿½amento
            }
            planos = planoDescontoController.lstPlanoDesconto(criterio, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            planos = new ArrayList<>();
        }

        if (planos == null || planos.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.planos.nao.cadastrado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("tipo", tipo);
        model.addAttribute("isConsultaDespesaComum", isConsultaDespesaComum);
        model.addAttribute("titulo", titulo);
        model.addAttribute("echCodigo", echCodigo);
        model.addAttribute("plaCodigo", plaCodigo);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("despesasComuns", despesasComuns);
        model.addAttribute("enderecos", enderecos);
        model.addAttribute("planos", planos);

        return viewRedirect("jsp/manterDespesaComum/pesquisarDespesaComum", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws DespesaComumControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String reqColumnsStr = "";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
        String csaNome = responsavel.getNomeEntidade();
        String decCodigo = JspHelper.verificaVarQryStr(request, "decCodigo");
        String cancelar = JspHelper.verificaVarQryStr(request, "cancelar");

        TransferObject despesaComum = despesaComumController.findDespesaComum(decCodigo, responsavel);
        String statusDespesaComum = (String) despesaComum.getAttribute(Columns.SDC_CODIGO);

        if (TextHelper.isNull(decCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!TextHelper.isNull(cancelar) && cancelar.equals("1") && responsavel.temPermissao(CodedValues.FUN_CANCELAR_DESPESA_COMUM) && statusDespesaComum.equals(StatusDespesaComumEnum.ATIVO.getCodigo())) {
            try {
                despesaComumController.cancelarDespesaComum(decCodigo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.cancelar.despesa.comum.sucesso", responsavel));
                despesaComum = despesaComumController.findDespesaComum(decCodigo, responsavel);
                statusDespesaComum = (String) despesaComum.getAttribute(Columns.SDC_CODIGO);
            } catch (DespesaComumControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }
        List<TransferObject> despesasIndividuais = null;
        try {
            despesasIndividuais = despesaIndividualController.findDespesasIndividuais(decCodigo, responsavel);
        } catch (DespesaIndividualControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        List<TransferObject> hist = despesaComumController.findOcorrencias(decCodigo, responsavel);

        model.addAttribute("msgErro", msgErro);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("decCodigo", decCodigo);
        model.addAttribute("despesaComum", despesaComum);
        model.addAttribute("statusDespesaComum", statusDespesaComum);
        model.addAttribute("despesasIndividuais", despesasIndividuais);
        model.addAttribute("hist", hist);

        return viewRedirect("jsp/manterDespesaComum/editarDespesaComum", request, session, model, responsavel);
    }
}
