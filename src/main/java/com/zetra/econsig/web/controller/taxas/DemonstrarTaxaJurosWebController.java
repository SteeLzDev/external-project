package com.zetra.econsig.web.controller.taxas;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

@Controller
@RequestMapping(value = "/v3/demonstrarTaxaJuros")
public class DemonstrarTaxaJurosWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DemonstrarTaxaJurosWebController.class);

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        boolean usaCalculoCom365Dias = ParamSist.paramEquals(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, CodedValues.TPC_SIM, responsavel);
        boolean usaDiasUtiasRepasse = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_DIA_PAGTO_PRIMEIRA_PARCELA, CodedValues.TPC_SIM, responsavel);
        int diaRepasse = ParamSist.getIntParamSist(CodedValues.TPC_DIA_PAGTO_PRIMEIRA_PARCELA, 1, responsavel);
        int carenciaRepasse = ParamSist.getIntParamSist(CodedValues.TPC_QTD_MESES_PARA_PAGTO_PRIMEIRA_PARCELA, 0, responsavel);

        model.addAttribute("usaCalculoCom365Dias", usaCalculoCom365Dias);
        model.addAttribute("usaDiasUtiasRepasse", usaDiasUtiasRepasse);
        model.addAttribute("diaRepasse", diaRepasse);
        model.addAttribute("carenciaRepasse", carenciaRepasse);

        return viewRedirect("jsp/demonstrarTaxaJuros/demonstrarTaxaJuros", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=demonstrar" })
    public String demonstrar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String orgCodigo = responsavel.getOrgCodigo();

            boolean usaCalculoCom365Dias = ParamSist.paramEquals(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, CodedValues.TPC_SIM, responsavel);
            boolean usaDiasUtiasRepasse = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_DIA_PAGTO_PRIMEIRA_PARCELA, CodedValues.TPC_SIM, responsavel);
            int diaRepasse = ParamSist.getIntParamSist(CodedValues.TPC_DIA_PAGTO_PRIMEIRA_PARCELA, 1, responsavel);
            int carenciaRepasse = ParamSist.getIntParamSist(CodedValues.TPC_QTD_MESES_PARA_PAGTO_PRIMEIRA_PARCELA, 0, responsavel);

            Integer prazo = (TextHelper.isNum(request.getParameter("prazo")) ? Integer.valueOf(request.getParameter("prazo")) : null);
            BigDecimal vlrLiberado = NumberHelper.parseDecimal(request.getParameter("vlrLiberado"));
            BigDecimal vlrParcela = NumberHelper.parseDecimal(request.getParameter("vlrParcela"));

            Date dataContrato = null;
            Date dataIni = null;
            try {
                dataContrato = DateHelper.parse(request.getParameter("dataContrato"), LocaleHelper.getDatePattern());
                dataIni = DateHelper.parse(request.getParameter("dataIni"), LocaleHelper.getDatePattern());
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.data.invalida", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (prazo == null || vlrLiberado == null || vlrParcela == null || dataContrato == null || dataIni == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.todos.campos", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            int diasCarencia = SimulacaoHelper.calculateDC(dataContrato, dataIni, orgCodigo, responsavel);
            double periodoCarencia = 1 + (diasCarencia / 30.0);
            Date dataRepasse = DateHelper.addDays(dataContrato, diasCarencia + 30);
            String repasse = DateHelper.format(dataRepasse, LocaleHelper.getDatePattern());

            BigDecimal taxaJurosCalc = SimulacaoHelper.calcularTaxaJuros(vlrLiberado, vlrParcela, prazo, dataContrato, dataIni, orgCodigo, responsavel);
            String taxaJuros = (taxaJurosCalc != null ? NumberHelper.format(taxaJurosCalc.doubleValue(), LocaleHelper.getLocale(), 2, 8) : "");

            model.addAttribute("usaCalculoCom365Dias", usaCalculoCom365Dias);
            model.addAttribute("usaDiasUtiasRepasse", usaDiasUtiasRepasse);
            model.addAttribute("diaRepasse", diaRepasse);
            model.addAttribute("carenciaRepasse", carenciaRepasse);
            model.addAttribute("periodoCarencia", periodoCarencia);
            model.addAttribute("taxaJuros", taxaJuros);
            model.addAttribute("repasse", repasse);

            return viewRedirect("jsp/demonstrarTaxaJuros/demonstrarTaxaJuros", request, session, model, responsavel);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
