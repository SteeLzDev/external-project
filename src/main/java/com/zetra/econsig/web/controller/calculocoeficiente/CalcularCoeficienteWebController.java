package com.zetra.econsig.web.controller.calculocoeficiente;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: CalcularCoeficienteWebController</p>
 * <p>Description: Controlador Web para ajax de cáculo de coeficiente.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class CalcularCoeficienteWebController extends AbstractWebController {

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/calcularCoeficiente" }, params = { "acao=calcular" })
    @ResponseBody
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParametroControllerException, SimulacaoControllerException, ParseException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String paramAlteraVlrLiberado = JspHelper.verificaVarQryStr(request, "ALTERA_VLR_LIBERADO");
        String cftVlr = JspHelper.verificaVarQryStr(request, "CFT_VLR");

        String adeCodigo = request.getParameter("ADE_CODIGO");

        //Busca a autorização
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        CustomTransferObject cftdes = null;

        try {
            cftdes = simulacaoController.findCdeByAdeCodigo(adeCodigo, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        BigDecimal vlrParcela = new BigDecimal(autdes.getAttribute(Columns.ADE_VLR).toString());
        boolean alteraVlrLiberado = paramAlteraVlrLiberado.equals("S");
        BigDecimal vlrLiberado = new BigDecimal(cftdes.getAttribute(Columns.CDE_VLR_LIBERADO).toString());

        //Busca as taxas utilizadas no contrato
        BigDecimal adeTac = null, adeOp = null;
        try {
            List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
            tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
            Map<String, String> taxas = autorizacaoController.getParamSvcADE(adeCodigo, tpsCodigos, responsavel);
            adeTac = taxas.get(CodedValues.TPS_TAC_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_TAC_FINANCIADA).toString());
            adeOp = taxas.get(CodedValues.TPS_OP_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_OP_FINANCIADA).toString());
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        BigDecimal cftVlrNovo = new BigDecimal(NumberHelper.reformat(cftVlr, NumberHelper.getLang(), "en", 2, 8));

        String adePeriodicidade = (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE);
        int przVlr = Integer.parseInt(autdes.getAttribute(Columns.ADE_PRAZO).toString());
        String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
        String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();

        BigDecimal retorno = simulacaoController.alterarValorTaxaJuros(alteraVlrLiberado, vlrParcela, vlrLiberado, cftVlrNovo, adeTac, adeOp, przVlr, orgCodigo, svcCodigo, csaCodigo, adePeriodicidade, responsavel);

        return "{\"valor\":\""+ NumberHelper.format((retorno).doubleValue(), NumberHelper.getLang()) +"\"}";
    }

}
