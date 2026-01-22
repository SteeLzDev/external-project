package com.zetra.econsig.web.controller.servidor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.folha.ImpArqContrachequeController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarContrachequeServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Contracheque do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarContrachequeServidor" })
public class ListarContrachequeServidorWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarContrachequeServidorWebController.class);

    @Autowired
    private ImpArqContrachequeController impArqContrachequeController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Não valida o per-page-token se for servidor pois este acessa via menu que não repassa o token
        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        String serNomeCodificado = JspHelper.verificaVarQryStr(request, "SER_NOME");
        String serNome = TextHelper.isNull(serNomeCodificado) ? serNomeCodificado : TextHelper.decode64(JspHelper.verificaVarQryStr(request, "SER_NOME"));

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
            rseMatricula = responsavel.getRseMatricula();
            serNome = responsavel.getUsuNome();
            serNomeCodificado = TextHelper.encode64(serNome);
        }

        String link = "../v3/listarContrachequeServidor?acao=iniciar&RSE_CODIGO=" + rseCodigo + "&RSE_MATRICULA=" + rseMatricula + "&SER_NOME=" + serNomeCodificado + "&" + SynchronizerToken.generateToken4URL(request);

        if (serNome != null && serNome.length() > 25) {
            serNome = serNome.substring(0, 25) + " ...";
        }

        List<TransferObject> contracheques = null;
        String periodo = JspHelper.verificaVarQryStr(request, "PERIODO");
        try {
            if (!TextHelper.isNull(periodo)) {
                Date ccqPeriodo = DateHelper.parse(periodo, "yyyy-MM");
                contracheques = impArqContrachequeController.listarContrachequeRse(rseCodigo, ccqPeriodo, false, responsavel);
            } else {
                contracheques = impArqContrachequeController.listarContrachequeRse(rseCodigo, null, true, responsavel);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        }

        String texto = "";
        if (contracheques != null && contracheques.size() > 0) {
            TransferObject ccq = contracheques.get(0);
            texto = (String) ccq.getAttribute(Columns.CCQ_TEXTO);
            periodo = DateHelper.format((Date) ccq.getAttribute(Columns.CCQ_PERIODO), "yyyy-MM");
        } else {
            texto = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.contracheque.indisponivel", responsavel);
            if (TextHelper.isNull(periodo)) {
                periodo = DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM");
            }
        }

        String nomeMes = "";
        String anterior = "";
        String proximo = "";
        try {
            Date date = DateHelper.parse(periodo + "-01", "yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            nomeMes = DateHelper.getMonthName(date) + "/" + cal.get(Calendar.YEAR);
            if (DateHelper.getMonth(date) == 12) {
                nomeMes += " - " + ApplicationResourcesHelper.getMessage("rotulo.servidor.contracheque.periodo.decimo.terceiro", responsavel);
            }

            cal.add(Calendar.MONTH, -1);
            anterior = DateHelper.format(cal.getTime(), "yyyy-MM");
            cal.add(Calendar.MONTH, 2);
            proximo = DateHelper.format(cal.getTime(), "yyyy-MM");
        } catch (java.text.ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        }

        List<TransferObject> periodosDisponiveis = null;
        try {
            // recupera os últimos 24 contracheques disponíveis para o servidor
            periodosDisponiveis = impArqContrachequeController.listarContrachequeRse(rseCodigo, null, false, 24, true, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
        }

        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("rseMatricula", rseMatricula);
        model.addAttribute("serNomeCodificado", serNomeCodificado);
        model.addAttribute("serNome", serNome);
        model.addAttribute("link", link);
        model.addAttribute("contracheques", contracheques);
        model.addAttribute("periodo", periodo);
        model.addAttribute("texto", texto);
        model.addAttribute("nomeMes", nomeMes);
        model.addAttribute("anterior", anterior);
        model.addAttribute("proximo", proximo);
        model.addAttribute("periodosDisponiveis", periodosDisponiveis);

        return viewRedirect("jsp/editarServidor/listarContrachequeServidor", request, session, model, responsavel);
    }
}
