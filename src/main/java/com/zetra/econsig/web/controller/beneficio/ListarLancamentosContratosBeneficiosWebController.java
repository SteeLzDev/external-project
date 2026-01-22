package com.zetra.econsig.web.controller.beneficio;

import java.text.ParseException;
import java.util.ArrayList;
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
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ListarLancamentosContratosBeneficiosWebController</p>
 * <p>Description: Listar lançamentos contrato de benefício</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarLancamentosContratosBeneficios" })
public class ListarLancamentosContratosBeneficiosWebController extends AbstractWebController {

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServidorControllerException, PeriodoException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String prdDataDesconto = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.PRD_DATA_DESCONTO));
            String cbeCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_CODIGO));

            List<Date> listaPeriodos = new ArrayList<>();
            Date date = null;
            if (!prdDataDesconto.isEmpty()) {
                date = DateHelper.parse(prdDataDesconto, "yyyy-MM-dd");
                model.addAttribute("prd_data_desconto", prdDataDesconto);
            }

            List<TransferObject> lancamentos = contratoBeneficioController.listLancamentosContratosBeneficiosByDataAndCbeCodigo(cbeCodigo, date, responsavel);
            List<TransferObject> lancamentosPeriodo = contratoBeneficioController.listLancamentosContratosBeneficiosByDataAndCbeCodigo(cbeCodigo, null, responsavel);

            int contadorPeriodo = 0;
            for (TransferObject lancamento : lancamentosPeriodo) {
                Date periodoLancamento = (Date) lancamento.getAttribute(Columns.PRD_DATA_DESCONTO);
                if(!listaPeriodos.contains(periodoLancamento)) {
                    contadorPeriodo ++;
                    listaPeriodos.add(periodoLancamento);
                }
                if(contadorPeriodo == 12) {
                    break;
                }
            }

            if(TextHelper.isNull(lancamentos) || lancamentos.isEmpty()) {
                ParamSession paramSession = ParamSession.getParamSession(session);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relacao.lancamentos.nao.existe", responsavel));
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";
            }

            model.addAttribute("listaPeriodos", listaPeriodos);
            model.addAttribute("lancamentos", TextHelper.isNull(date) ? new ArrayList<>() : lancamentos);
            model.addAttribute("lancamentosInfo", lancamentos.get(0));
            model.addAttribute(Columns.CBE_CODIGO, cbeCodigo);
            model.addAttribute("contratosAtivos", JspHelper.verificaVarQryStr(request, "contratosAtivos"));
            model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute(Columns.TIB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
            model.addAttribute(Columns.BEN_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            model.addAttribute(Columns.BFC_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO)));

            return viewRedirect("jsp/manterBeneficio/listarLancamentosContratosBeneficios", request, session, model, responsavel);

        } catch (ContratoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
