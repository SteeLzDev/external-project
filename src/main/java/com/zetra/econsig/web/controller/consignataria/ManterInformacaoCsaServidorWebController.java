package com.zetra.econsig.web.controller.consignataria;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.InformacaoCsaServidor;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ManterInformacaoCsaServidorWebController</p>
 * <p>Description: Web Controller responsavel pela manutencao das informacoes de servidores para csa.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = {"/v3/manterInformacaoCsaServidor"} )
public class ManterInformacaoCsaServidorWebController extends ControlePaginacaoWebController {

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listarInformacaoCsaServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException, ServidorControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        List<TransferObject> informacaoServidor = null;
        final CustomTransferObject criterio = new CustomTransferObject();

        final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        final String serCodigo = JspHelper.verificaVarQryStr(request, "SER_CODIGO");

        criterio.setAttribute(Columns.ICS_CSA_CODIGO, csaCodigo);
        criterio.setAttribute(Columns.ICS_SER_CODIGO, serCodigo);

        informacaoServidor = consignatariaController.lstInformacaoCsaServidor(criterio, -1, -1, responsavel);
        final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.informacao.csa.servidor.lista", responsavel));
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("informacaoServidor", informacaoServidor);
        model.addAttribute("nomeServidor", servidor.getSerNome());
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("serCodigo", serCodigo);

        return viewRedirect("jsp/manterInformacaoCsaServidor/listarInformacaoCsaServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarInformacaoCsaServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        InformacaoCsaServidor infoServidor = null;

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String icsCodigo = JspHelper.verificaVarQryStr(request, "ICS_CODIGO");
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        final String serCodigo = JspHelper.verificaVarQryStr(request, "SER_CODIGO");
        if (!TextHelper.isNull(icsCodigo)) {
            infoServidor = consignatariaController.findInformacaoCsaServidorByIcsCodigo(icsCodigo, responsavel);
        }

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("serCodigo", serCodigo);
        model.addAttribute("infoServidor", infoServidor);

        return viewRedirect("jsp/manterInformacaoCsaServidor/editarInformacaoCsaServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarInformacaoCsaServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException, HQueryException, ServidorControllerException, MissingPrimaryKeyException, UpdateException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        final String serCodigo = JspHelper.verificaVarQryStr(request, "SER_CODIGO");
        final String icsCodigo = JspHelper.verificaVarQryStr(request, "ICS_CODIGO");
        final String icsValor = JspHelper.verificaVarQryStr(request, "ICS_VALOR");

        if(TextHelper.isNull(icsValor)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.informacao.csa.servidor.campo.valor", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if(!TextHelper.isNull(icsCodigo)) {
            consignatariaController.updateInformacaoCsaServidor(icsCodigo, csaCodigo, serCodigo, icsValor, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.servidor.sucesso.editar", responsavel));
        }else {
            consignatariaController.createInformacaoCsaServidor(csaCodigo, serCodigo, icsValor, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.servidor.sucesso.criar", responsavel));
        }


        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("serCodigo", serCodigo);
        return listarInformacaoCsaServidor(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirInformacaoCsaServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException, HQueryException, ServidorControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String icsCodigo = JspHelper.verificaVarQryStr(request, "ICS_CODIGO");
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        final String serCodigo = JspHelper.verificaVarQryStr(request, "SER_CODIGO");

        if (!TextHelper.isNull(icsCodigo)) {
            consignatariaController.removeInformacaoCsaServidor(icsCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.servidor.sucesso.excluir", responsavel));
        }else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("serCodigo", serCodigo);

        return listarInformacaoCsaServidor(request, response, session, model);
    }


}
