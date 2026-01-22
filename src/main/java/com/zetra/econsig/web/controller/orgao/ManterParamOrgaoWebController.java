package com.zetra.econsig.web.controller.orgao;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ManterParamOrgaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de Manutenção de Parâmetros de Órgão</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterParamOrgao" })
public class ManterParamOrgaoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterParamOrgaoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(@RequestParam(value = "codigo", required = true, defaultValue = "") String codigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            OrgaoTransferObject orgao = recuperarOrgaoEdicao(codigo, session, responsavel);
            boolean podeEditarParamOrgao = responsavel.temPermissao(CodedValues.FUN_EDT_PARAM_ORGAO);

            List<TransferObject> parametrosOrgao = parametroController.selectParamOrgaoEditavel(codigo, responsavel);
            for (TransferObject param : parametrosOrgao) {
                boolean parametroEditavel = podeEditarParamOrgao && (
                        (responsavel.isSup() && param.getAttribute(Columns.TAO_SUP_ALTERA).equals("S")) ||
                        (responsavel.isCse() && param.getAttribute(Columns.TAO_CSE_ALTERA).equals("S")) ||
                        (responsavel.isOrg() && param.getAttribute(Columns.TAO_ORG_ALTERA).equals("S")));

                String taoCodigo = param.getAttribute(Columns.TAO_CODIGO).toString();
                String taoDominio = param.getAttribute(Columns.TAO_DOMINIO).toString();
                String taoVlrDefault = (String) param.getAttribute(Columns.TAO_VLR_DEFAULT);
                String paoVlr = (String) param.getAttribute(Columns.PAO_VLR);

                if (TextHelper.isNull(paoVlr)) {
                    if (!TextHelper.isNull(taoVlrDefault)) {
                        paoVlr = taoVlrDefault;
                    } else {
                        paoVlr = "";
                    }
                }

                String campoParam = JspHelper.montaValor(taoCodigo, taoDominio, TextHelper.forHtmlContent(paoVlr),
                        parametroEditavel, null, -1, -1, "form-control", null, null);

                param.setAttribute("campo_parametro", campoParam);
            }

            model.addAttribute("orgao", orgao);
            model.addAttribute("parametrosOrgao", parametrosOrgao);
            model.addAttribute("podeEditarParamOrgao", podeEditarParamOrgao);

            return viewRedirect("jsp/manterOrgao/editarParamOrgao", request, session, model, responsavel);
        } catch (ConsignanteControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "codigo", required = true, defaultValue = "") String codigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        // Não salva o token para poder chamar o iniciar ao final
        // SynchronizerToken.saveToken(request);

        try {
            // Recupera e valida se o órgão pode ser editado pelo usuário
            OrgaoTransferObject orgao = recuperarOrgaoEdicao(codigo, session, responsavel);
            boolean podeEditarParamOrgao = responsavel.temPermissao(CodedValues.FUN_EDT_PARAM_ORGAO);

            List<TransferObject> parametrosOrgao = parametroController.selectParamOrgaoEditavel(codigo, responsavel);
            for (TransferObject param : parametrosOrgao) {
                boolean parametroEditavel = podeEditarParamOrgao && (
                        (responsavel.isSup() && param.getAttribute(Columns.TAO_SUP_ALTERA).equals("S")) ||
                        (responsavel.isCse() && param.getAttribute(Columns.TAO_CSE_ALTERA).equals("S")) ||
                        (responsavel.isOrg() && param.getAttribute(Columns.TAO_ORG_ALTERA).equals("S")));

                if (parametroEditavel) {
                    String taoCodigo = param.getAttribute(Columns.TAO_CODIGO).toString();
                    String taoDominio = param.getAttribute(Columns.TAO_DOMINIO).toString();
                    String taoVlrDefault = (String) param.getAttribute(Columns.TAO_VLR_DEFAULT);
                    String paoVlrOld = (param.getAttribute(Columns.PAO_VLR) != null ? param.getAttribute(Columns.PAO_VLR).toString() : "");
                    String paoVlrNew = JspHelper.verificaVarQryStr(request, taoCodigo);

                    if (!TextHelper.isNull(paoVlrNew) && (taoDominio.equals("MONETARIO") || taoDominio.equals("FLOAT"))) {
                        try {
                            paoVlrNew = NumberHelper.reformat(paoVlrNew, NumberHelper.getLang(), "en");
                        } catch (java.text.ParseException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }

                    if (TextHelper.isNull(paoVlrNew)) {
                        if (!TextHelper.isNull(taoVlrDefault)) {
                            paoVlrNew = taoVlrDefault;
                        } else {
                            paoVlrNew = "";
                        }
                    }

                    if (!paoVlrNew.equals(paoVlrOld)) {
                        parametroController.updateParamOrgao(paoVlrNew, taoCodigo, orgao.getOrgCodigo(), responsavel);
                    }
                }
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.parametro.orgao.sucesso", responsavel));

            // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação
            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();

            return iniciar(codigo, request, response, session, model);
        } catch (ConsignanteControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private OrgaoTransferObject recuperarOrgaoEdicao(String codigo, HttpSession session, AcessoSistema responsavel) throws ConsignanteControllerException, InstantiationException, IllegalAccessException {
        OrgaoTransferObject orgao = consignanteController.findOrgao(codigo, responsavel);
        if (responsavel.isOrg()) {
            // Sendo usuário de órgão, valida se ele está editando parâmetro do seu órgão ou de um órgão de seu estabelecimento, caso tenha esta permissão
            if ((!responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && !orgao.getOrgCodigo().equals(responsavel.getOrgCodigo())) ||
                    (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && !orgao.getEstCodigo().equals(responsavel.getEstCodigo()))) {
                throw new ConsignanteControllerException("mensagem.erro.interno.contate.administrador", responsavel);
            }
        }
        return orgao;
    }
}
