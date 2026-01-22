package com.zetra.econsig.web.controller.servidor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.ConsultaMargemSemSenha;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AutorizarMargemConsignatariaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Autorizar Margem para Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/autorizarMargemConsignataria" })
public class AutorizarMargemConsignatariaWebController extends AbstractWebController {

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CSA_CONSULTA_MARGEM_SEM_SENHA, CodedValues.TPC_SIM);
            final List<TransferObject> consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
            if ((consignatarias == null) || consignatarias.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.consignatarias.consulta.margem.vazia", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            final Map<String, ConsultaMargemSemSenha> hashConsulta = new HashMap<>();

            final List<ConsultaMargemSemSenha> consignatariasConsultaList = consignatariaController.listaConsignatariaConsultaMargemSemSenhaByRseCodigo(responsavel.getRseCodigo(), responsavel);
            for (final ConsultaMargemSemSenha consignatariaConsulta : consignatariasConsultaList) {
                hashConsulta.put(consignatariaConsulta.getCsaCodigo(), consignatariaConsulta);
            }

            model.addAttribute("consignatarias", consignatarias);
            model.addAttribute("hashConsulta", hashConsulta);
        } catch (final ConsignatariaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/autorizarMargemConsignataria/autorizarMargemConsignataria", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final int diasValidadeAutorizacao = ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel);

            if (diasValidadeAutorizacao > 0) {
                final String[] consignatariasCodigo = JspHelper.verificaVarQryStr(request, "consignatariasCheck").split(";");

                for (final String consignatariaCodigo : consignatariasCodigo) {
                    final String[] vlrs = consignatariaCodigo.split("_");
                    final String checkbox = vlrs[0];
                    final String csaCodigo = vlrs[1];
                    final String exists = vlrs[2];
                    final String cssCodigo = vlrs.length == 4 ? vlrs[3] : null;
                    if ("S".equals(checkbox) && "N".equals(exists)) {
                        consignatariaController.createConsignatariaConsultaMargemSemSenha(responsavel.getRseCodigo(), csaCodigo, DateHelper.getSystemDatetime(), DateHelper.addDays(DateHelper.getSystemDatetime(), diasValidadeAutorizacao), responsavel);
                    } else if ("N".equals(checkbox) && "S".equals(exists) && !TextHelper.isNull(cssCodigo)) {
                        consignatariaController.updateConsignatariaConsultaMargemSemSenha(cssCodigo, "S", responsavel);
                    }
                }
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.consulta.margem.tpc", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (final ConsignatariaControllerException | UpdateException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=validarPermissaoDesautorizacao" })
    @ResponseBody
    public ResponseEntity<String> verificaExistenciaOperacoesEmAndamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException {
        JsonArrayBuilder csasSemPermissaoDesautorizacao = Json.createArrayBuilder();
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        final String[] consignatariasCodigo = request.getParameter("consignatariasCheck").toString().split(";");

        if(consignatariasCodigo != null && consignatariasCodigo.length > 0) {
            final Map<String, String> csasComOperacoesEmAndamento = consignatariaController.findCsasComOperacoesEmAndamentoByRseCodigo(responsavel);

            for(final String consignatariaCodigo : consignatariasCodigo) {
                final String[] vlrs = consignatariaCodigo.split("_");
                final String checkbox = vlrs[0];
                final String csaCodigo = vlrs[1];
                final String exists = vlrs[2];
                final String isDisabled = vlrs.length == 5 ? vlrs[4] : vlrs[3];
                if("N".equals(checkbox) && "S".equals(exists) && "N".equals(isDisabled)) {
                    if(csasComOperacoesEmAndamento.containsKey(csaCodigo)) {
                        JsonObjectBuilder csaSemPermissaoJson = Json.createObjectBuilder();
                        csaSemPermissaoJson.add("csaCodigo", csaCodigo)
                                           .add("csaNomeAbrev", csasComOperacoesEmAndamento.get(csaCodigo));
                        csasSemPermissaoDesautorizacao.add(csaSemPermissaoJson.build());
                    }
                }
            }
        }
       return new ResponseEntity<>(csasSemPermissaoDesautorizacao.build().toString(), HttpStatus.OK);
    }
}
