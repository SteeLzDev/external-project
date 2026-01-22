package com.zetra.econsig.web.controller.taxas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarTaxaJurosWebController</p>
 * <p>Description: Controlador Web para o caso de uso listar taxa de juros.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */
@Controller
@RequestMapping(value = "/v3/listarTaxaJuros")
public class ListarTaxaJurosWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarTaxaJurosWebController.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

            }
            SynchronizerToken.saveToken(request);

            String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            String titulo = JspHelper.verificaVarQryStr(request, "titulo");
            String tipo = JspHelper.verificaVarQryStr(request, "tipo");

            if (svcCodigo.equals("") || titulo.equals("")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica se o sistema está configurado para trabalhar com o CET.
            boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            // Se o período foi informado, reformata para que a data
            // esteja de acordo com o parâmetro de serviço sobre a
            // data de abertura da taxa de juros.
            String periodo = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!periodo.equals("")) {
                try {
                    // Busca o parâmetro de serviço que diz a data de abertura da taxa de juros
                    ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                    String dia = "01";
                    if (paramSvcCse.getTpsDataAberturaTaxaRef() != null && paramSvcCse.getTpsDataAberturaTaxaRef().equalsIgnoreCase("D") && paramSvcCse.getTpsDataAberturaTaxa() != null) {
                        dia = paramSvcCse.getTpsDataAberturaTaxa();
                        dia = TextHelper.formataMensagem(dia, "0", 2, false);
                    }
                    periodo = DateHelper.reformat(periodo, "MM/yyyy", "yyyy-MM-" + dia);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    periodo = "";
                }
            }

            List<PrazoTransferObject> prazos = null;
            // Seleciona prazos ativos.
            try {
                prazos = simulacaoController.findPrazoAtivoByServico(svcCodigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String periodoE = "";
            Locale loc = LocaleHelper.getLocaleObject();
            SimpleDateFormat sf = new SimpleDateFormat("MMMM-yyyy", loc);
            if (!periodo.equals("")) {
                periodoE = sf.format(DateHelper.parse(periodo, "yyyy-MM-dd"));
            } else {
                periodoE = sf.format(DateHelper.getSystemDatetime());
            }

            Short prz_vlr = null;
            PrazoTransferObject pto = null;
            List<TransferObject> coeficientes = null;
            Iterator<PrazoTransferObject> it = null;
            Map<Short, List<TransferObject>> rankings = new LinkedHashMap<>();
            int maxSizeRanking = 0;
            it = prazos.iterator();
            while (it.hasNext()) {
                pto = it.next();
                prz_vlr = pto.getPrzVlr();

                try {
                    coeficientes = simulacaoController.getTaxas(periodo, null, svcCodigo, Integer.valueOf(prz_vlr.intValue()), true, false, responsavel);
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    coeficientes = new ArrayList<>();
                }

                rankings.put(prz_vlr, coeficientes);
                if (coeficientes.size() > maxSizeRanking) {
                    maxSizeRanking = coeficientes.size();
                }

            }

            model.addAttribute("prazos", prazos);
            model.addAttribute("tipo", tipo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("titulo", titulo);
            model.addAttribute("temCET", temCET);
            model.addAttribute("periodoE", periodoE);
            model.addAttribute("periodo", periodo);
            model.addAttribute("rankings", rankings);
            model.addAttribute("maxSizeRanking", maxSizeRanking);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/listarTaxaJuros/ranking", request, session, model, responsavel);
    }

}
