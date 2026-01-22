package com.zetra.econsig.web.controller.exportarmovimento;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.folha.ExportaMovimentoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ExportarMovimentoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de listar exportação movimento financeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 29172 $
 * $Date: 2020-03-25 14:33:04 -0300 (qua, 25 mar 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/exportarMovimento" })
public class ExportarMovimentoWebController extends AbstractWebController {

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ExportaMovimentoController exportaMovimentoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        SynchronizerToken.saveToken(request);

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        Object param = ParamSist.getInstance().getParam(CodedValues.TPC_EXP_MOV_POR_ESTABELECIMENTO, responsavel);
        boolean expPorEstab = param != null && param.equals(CodedValues.TPC_SIM);

        model.addAttribute("rotuloEstabelecimento", ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel));
        model.addAttribute("rotuloOrgao", ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel));

        Boolean reexportarModel = (Boolean) model.asMap().get(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo());
        boolean reexportar = (reexportarModel != null) ? reexportarModel : false;
        model.addAttribute(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo(), reexportar);

        String estCodigoUsuario = null;

        // Verifica se é usuário de órgão, caso sim redireciona para v3/processarMovimento
        if (responsavel.isOrg() && (!responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) {
            // Pega as informações do órgão no banco de dados
            String orgao = responsavel.getCodigoEntidade();
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/processarMovimento?acao=iniciar&org_codigo=" + orgao + "&reexportar=" + ((reexportar) ? "true" : ""), request)));
            return "jsp/redirecionador/redirecionar";
        } else if (responsavel.isOrg()) {
            estCodigoUsuario = responsavel.getCodigoEntidadePai();
        }
        model.addAttribute("estCodigoUsuario", estCodigoUsuario);

        // Traz parâmetros para conferência do usuário
        List<TransferObject> paramSist = null;
        try {
            paramSist = parametroController.selectParamSistCse(null, null, null, null, responsavel);

            // Imprime parâmetros para conferência do usuário
            Iterator<TransferObject> itPar = paramSist.iterator();

            String parametros = "";

            while (itPar.hasNext()) {
                CustomTransferObject next = (CustomTransferObject) itPar.next();
                if (next.getAttribute(Columns.TPC_CODIGO).toString().equals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO) ||
                    next.getAttribute(Columns.TPC_CODIGO).toString().equals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL) ||
                    next.getAttribute(Columns.TPC_CODIGO).toString().equals(CodedValues.TPC_EXPORTA_LIQCANC_NAO_PAGAS) ||
                    next.getAttribute(Columns.TPC_CODIGO).toString().equals(CodedValues.TPC_FOLHA_ACEITA_ALTERACAO)) {

                    String valorParametro = (next.getAttribute(Columns.PSI_VLR) != null ? next.getAttribute(Columns.PSI_VLR).toString() : "");

                    if (next.getAttribute(Columns.TPC_DOMINIO) != null && next.getAttribute(Columns.TPC_DOMINIO).equals("SN")) {
                        if (valorParametro.equals(CodedValues.TPC_SIM)) {
                            valorParametro = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
                        } else if (valorParametro.equals(CodedValues.TPC_NAO)) {
                            valorParametro = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                        }
                    }

                    parametros += next.getAttribute(Columns.TPC_DESCRICAO) + " = " + valorParametro + "<BR>";
                }
            }

            if (parametros != "" && parametros.endsWith("<BR>")) {

                parametros = parametros.substring(0, parametros.length() - 4);
                session.setAttribute(CodedValues.MSG_ALERT, parametros);
            }

            if (expPorEstab) {
                List<TransferObject> estabelecimentos = null;

                CustomTransferObject criterio = null;
                estabelecimentos = consignanteController.lstEstabelecimentos(criterio, responsavel);

                model.addAttribute("estabelecimentos", estabelecimentos);
            } else {
                List<TransferObject> orgaos = null;
                orgaos = exportaMovimentoController.listaOrgaosExpMovFin(responsavel);

                model.addAttribute("orgaos", orgaos);
            }
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            paramSist = new ArrayList<>();
        } catch (ConsignanteControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("paramSist", paramSist);

        return viewRedirect("jsp/integrarFolha/listarExportacaoMovFinanceiro", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=reexportar" })
    public String reexportar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        model.addAttribute(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo(), Boolean.TRUE);

        return iniciar(request, response, session, model);
    }
}
