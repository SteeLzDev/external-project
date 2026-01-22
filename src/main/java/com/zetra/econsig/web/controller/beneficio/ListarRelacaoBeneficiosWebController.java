package com.zetra.econsig.web.controller.beneficio;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: ListarRelacaoBeneficioWebController</p>
 * <p>Description: Listar Relação de Benefício</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/relacaoBeneficios" })
public class ListarRelacaoBeneficiosWebController extends AbstractConsultarServidorWebController {

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private ServidorController servidorController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.relacao.beneficios.titulo", responsavel, titulo));
        model.addAttribute("acaoFormulario", "../v3/relacaoBeneficios");
        model.addAttribute("omitirAdeNumero", true);
        model.addAttribute("imageHeader", "i-beneficios");

    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            //Valida o token de sessão para evitar a chamada direta à operação
            if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            CustomTransferObject criterio = new CustomTransferObject();

            String serCodigo = "";

            if (responsavel.isSer()) {
                rseCodigo = TextHelper.isNull(rseCodigo) ? responsavel.getRseCodigo() : rseCodigo;
                serCodigo = responsavel.getSerCodigo();
                criterio.setAttribute(Columns.SER_CODIGO, responsavel.getSerCodigo());
                criterio.setAttribute("isFluxoServidor", true);
            } else {
                try {
                    ServidorTransferObject serTransfer = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
                    serCodigo = serTransfer.getSerCodigo();
                    criterio.setAttribute("isFluxoServidor", false);
                    criterio.setAttribute(Columns.SER_CODIGO, serTransfer.getSerCodigo());
                } catch (ServidorControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            List<TransferObject> relacaoBeneficios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            boolean possuiBeneficiosAtivos = false;

            Iterator<?> it1 = relacaoBeneficios.iterator();
            while (it1.hasNext()) {
                CustomTransferObject relacaoBeneficio = (CustomTransferObject) it1.next();
                String scb_codigo = (String) relacaoBeneficio.getAttribute(Columns.SCB_CODIGO);
                if (!scb_codigo.equals(StatusContratoBeneficioEnum.CANCELADO.getCodigo())) {
                    possuiBeneficiosAtivos = true;
                    break;
                }
            }

            // Seta atributos no model
            model.addAttribute("relacaoBeneficios", relacaoBeneficios);
            model.addAttribute("possuiBeneficiosAtivos", possuiBeneficiosAtivos);
            model.addAttribute(Columns.SER_CODIGO, serCodigo);
            model.addAttribute(Columns.RSE_CODIGO, rseCodigo);

            return viewRedirect("jsp/manterBeneficio/listarRelacaoBeneficios", request, session, model, responsavel);

        } catch (BeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            CustomTransferObject criterio = new CustomTransferObject();
            if (responsavel.isSer()) {
                criterio.setAttribute(Columns.SER_CODIGO, responsavel.getSerCodigo());
                criterio.setAttribute("isFluxoServidor", true);
            } else {
                criterio.setAttribute("isFluxoServidor", false);
                criterio.setAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            }

            criterio.setAttribute(Columns.BEN_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            criterio.setAttribute("contratosAtivos", JspHelper.verificaVarQryStr(request, "contratosAtivos"));

            List<TransferObject> relacaoBeneficios = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            boolean permiteCancelarBeneficioSemAprovacao = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CANCELAR_BENEFICIO_SEM_APROVACAO, CodedValues.TPC_SIM, responsavel);

            // Seta atributos no model
            model.addAttribute("relacaoBeneficios", relacaoBeneficios);
            model.addAttribute(Columns.BEN_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BEN_CODIGO)));
            model.addAttribute(Columns.SER_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SER_CODIGO)));
            model.addAttribute(Columns.SCB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.SCB_CODIGO)));
            model.addAttribute(Columns.TIB_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.TIB_CODIGO)));
            model.addAttribute(Columns.RSE_CODIGO, JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO)));
            model.addAttribute("contratosAtivos", JspHelper.verificaVarQryStr(request, "contratosAtivos"));
            model.addAttribute("permiteCancelarBeneficioSemAprovacao", permiteCancelarBeneficioSemAprovacao);

            return viewRedirect("jsp/manterBeneficio/consultarRelacaoBeneficios", request, session, model, responsavel);

        } catch (BeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    public String getLinkAction() {
        return "../v3/relacaoBeneficios?acao=listar";
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        return listar(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "listar";
    }

}
