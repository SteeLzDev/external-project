package com.zetra.econsig.web.controller.folha;

import java.text.ParseException;
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

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CalendarioTO;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: CalendarioWebController</p>
 * <p>Description: REST Controller para manutenção de calendário.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 27758 $
 * $Date: 2019-09-06 15:26:28 -0300 (sex, 06 set 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarCalendario" })
public class CalendarioWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CalendarioWebController.class);

    @Autowired
    private CalendarioController calendarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String anoMes = JspHelper.verificaVarQryStr(request, "ANO-MES");

        String rotuloCalendarioEditarCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.calendario.editar.clique.aqui", responsavel);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(anoMes) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (TextHelper.isNull(anoMes)) {
            anoMes = DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM");
        }

        String nomeMes = "";
        String anterior = "";
        String proximo = "";
        try {
            Date date = DateHelper.parse(anoMes + "-01", "yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            nomeMes = DateHelper.getMonthName(date) + "/" + cal.get(Calendar.YEAR);

            cal.add(Calendar.MONTH, -1);
            anterior = DateHelper.format(cal.getTime(), "yyyy-MM");
            cal.add(Calendar.MONTH, 2);
            proximo = DateHelper.format(cal.getTime(), "yyyy-MM");
        } catch (java.text.ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
        }

        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("ANO-MES", anoMes);
        try {
            List<CalendarioTO> calendario = calendarioController.lstCalendarioBase(criterio, responsavel);
            model.addAttribute("calendario", calendario);
        } catch (CalendarioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("nomeMes", nomeMes);
        model.addAttribute("anterior", anterior);
        model.addAttribute("proximo", proximo);
        model.addAttribute("rotuloCalendarioEditarCliqueAqui", rotuloCalendarioEditarCliqueAqui);

        return viewRedirect("jsp/calendario/listarCalendario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Obtém os dados necessários da requisição
        String calDataStr = !TextHelper.isNull(request.getParameter("CAB_DATA")) ? request.getParameter("CAB_DATA") : (String) model.asMap().get("CAB_DATA");
        java.util.Date calData = null;
        try {
            calData = DateHelper.parse(calDataStr, "yyyy-MM-dd");
            model.addAttribute("calData", calData);
        } catch (ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return iniciar(request, response, session, model);
        }

        // Carrega as informações para exibição
        TransferObject calendario = null;
        try {
            calendario = calendarioController.findCalendarioBase(calData, responsavel);
            model.addAttribute("calendario", calendario);
        } catch (CalendarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return iniciar(request, response, session, model);
        }

        return viewRedirect("jsp/calendario/editarCalendario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        //Um novo token será gerado na chamada do método a seguir "exibirTelaEdicao"

        // Salva as alterações realizadas
        Date calData = null;
        if (!TextHelper.isNull(request.getParameter("CAB_DATA"))) {
            // Obtém os dados necessários da requisição
            String calDataStr = request.getParameter("CAB_DATA");
            try {
                calData = DateHelper.parse(calDataStr, "yyyy-MM-dd");
            } catch (ParseException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                return iniciar(request, response, session, model);
            }

            try {
                CalendarioTO calendario = new CalendarioTO(calData);
                calendario.setCalDescricao(JspHelper.verificaVarQryStr(request, "CAB_DESCRICAO"));
                calendario.setCalDiaUtil(JspHelper.verificaVarQryStr(request, "CAB_DIA_UTIL"));

                calendarioController.updateCalendario(calendario, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.calendario.alteracoes.salvas.sucesso", responsavel));

                // Limpa o cache de parâmetros de dia de repasse
                RepasseHelper.getInstance().reset();
            } catch (CalendarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        model.addAttribute("CAB_DATA", calData);

        return editar(request, response, session, model);
    }
}
