package com.zetra.econsig.web.controller.beneficio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FaturamentoBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.beneficios.FaturamentoBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.controller.faturamento.ListarFaturamentoBeneficioWebController;

/**
 * <p>Title: ManterArquivoFaturamentoBeneficioWebController</p>
 * <p>Description:Alterar arquivos de beneficio</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterArquivoFaturamentoBeneficio" })
public class ManterArquivoFaturamentoBeneficioWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarFaturamentoBeneficioWebController.class);

    @Autowired
    private FaturamentoBeneficioController faturamentoBeneficioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String fatCodigo = JspHelper.verificaVarQryStr(request, "FAT_CODIGO");
            buscarDadosFaturamento(fatCodigo, model, responsavel);

            return viewRedirect("jsp/manterArquivoFaturamentoBeneficio/listarArquivoFaturamentoBeneficio", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    private void buscarDadosFaturamento(String fatCodigo, Model model, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.FAT_CODIGO, fatCodigo);
        List<TransferObject> findFaturamento = faturamentoBeneficioController.findFaturamento(criterio, responsavel);
        TransferObject fat = findFaturamento.get(0);

        model.addAttribute(Columns.FAT_CODIGO, fatCodigo);
        model.addAttribute(Columns.FAT_PERIODO, DateHelper.format((Date) fat.getAttribute(Columns.FAT_PERIODO), "MM/yyyy"));
        model.addAttribute(Columns.CSA_NOME, fat.getAttribute(Columns.CSA_NOME));
        model.addAttribute(Columns.FAT_DATA, DateHelper.format((Date) fat.getAttribute(Columns.FAT_PERIODO), LocaleHelper.getDateTimePattern()));

    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String fatCodigo = JspHelper.verificaVarQryStr(request, "FAT_CODIGO");
            buscarDadosFaturamento(fatCodigo, model, responsavel);

            String cbeNumero = JspHelper.verificaVarQryStr(request, "cbeNumero");
            String cpf = JspHelper.verificaVarQryStr(request, "cpf");
            String matricula = JspHelper.verificaVarQryStr(request, "matricula");

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.FAT_CODIGO, fatCodigo);
            criterio.setAttribute(Columns.AFB_RSE_MATRICULA, matricula);
            criterio.setAttribute(Columns.CBE_NUMERO, cbeNumero);
            criterio.setAttribute(Columns.BFC_CPF, cpf);

            int total = faturamentoBeneficioController.countArquivosFaturamento(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            criterio.setAttribute("OFFSET", offset);
            criterio.setAttribute("SIZE", size);
            List<TransferObject> arquivosFaturamento = faturamentoBeneficioController.findArquivosFaturamento(criterio, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.usuario", total, size, requestParams, false, request, model);

            model.addAttribute("arquivosFaturamento", arquivosFaturamento);

            return viewRedirect("jsp/manterArquivoFaturamentoBeneficio/listarArquivoFaturamentoBeneficio", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=detalhar" })
    public String detalhar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            model.addAttribute("detalhar", true);
            return editar(request, response, session, model);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String id = request.getParameter("AFB_CODIGO");

            listarTipoLancamento(model, responsavel);

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.AFB_CODIGO, Integer.valueOf(id));
            criterio.setAttribute("SIZE", 1);

            List<TransferObject> arquivosFaturamento = faturamentoBeneficioController.findArquivosFaturamento(criterio, responsavel);

            if (arquivosFaturamento != null && !arquivosFaturamento.isEmpty()) {

                TransferObject af = arquivosFaturamento.get(0);
                model.addAttribute("arquivoFaturamento", af);

                return viewRedirect("jsp/manterArquivoFaturamentoBeneficio/editarArquivoFaturamentoBeneficio", request, session, model, responsavel);
            } else {
                return iniciar(request, response, session, model);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            listarTipoLancamento(model, responsavel);

            String id = request.getParameter("AFB_CODIGO");

            String afbValorSubsidio = request.getParameter("AFB_VALOR_SUBSIDIO");
            String afbValorRealizado = request.getParameter("AFB_VALOR_REALIZADO");
            String afbValorNaoRealizado = request.getParameter("AFB_VALOR_NAO_REALIZADO");
            String afbValorTotal = request.getParameter("AFB_VALOR_TOTAL");

            if (TextUtils.isBlank(afbValorSubsidio) || TextUtils.isBlank(afbValorRealizado) || TextUtils.isBlank(afbValorNaoRealizado) || TextUtils.isBlank(afbValorTotal)) {

                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.AFB_CODIGO, Integer.valueOf(id));
                criterio.setAttribute("SIZE", 1);
                List<TransferObject> arquivosFaturamento = faturamentoBeneficioController.findArquivosFaturamento(criterio, responsavel);
                TransferObject af = arquivosFaturamento.get(0);
                af.setAttribute(Columns.AFB_VALOR_SUBSIDIO, NumberHelper.objectToBigDecimal(afbValorSubsidio));
                af.setAttribute(Columns.AFB_VALOR_REALIZADO, NumberHelper.objectToBigDecimal(afbValorRealizado));
                af.setAttribute(Columns.AFB_VALOR_NAO_REALIZADO, NumberHelper.objectToBigDecimal(afbValorNaoRealizado));
                af.setAttribute(Columns.AFB_VALOR_TOTAL, NumberHelper.objectToBigDecimal(afbValorTotal));
                model.addAttribute("arquivoFaturamento", af);

                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.faturamento.beneficio.arquivos.campos.obrigatorios", responsavel));
                return viewRedirect("jsp/manterArquivoFaturamentoBeneficio/editarArquivoFaturamentoBeneficio", request, session, model, responsavel);

            }

            TransferObject af = new CustomTransferObject();
            af.setAttribute(Columns.AFB_CODIGO, Integer.valueOf(id));
            af.setAttribute(Columns.AFB_VALOR_SUBSIDIO, NumberHelper.objectToBigDecimal(afbValorSubsidio));
            af.setAttribute(Columns.AFB_VALOR_REALIZADO, NumberHelper.objectToBigDecimal(afbValorRealizado));
            af.setAttribute(Columns.AFB_VALOR_NAO_REALIZADO, NumberHelper.objectToBigDecimal(afbValorNaoRealizado));
            af.setAttribute(Columns.AFB_VALOR_TOTAL, NumberHelper.objectToBigDecimal(afbValorTotal));

            af = faturamentoBeneficioController.salvarArquivoFaturamento(af, responsavel);

            model.addAttribute("arquivoFaturamento", af);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.faturamento.beneficio.arquivos.alterado.sucesso", responsavel));

            return viewRedirect("jsp/manterArquivoFaturamentoBeneficio/editarArquivoFaturamentoBeneficio", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String id = request.getParameter("AFB_CODIGO");
            faturamentoBeneficioController.excluirArquivoFaturamento(Integer.valueOf(id), responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.faturamento.beneficio.arquivos.excluido.sucesso", responsavel));

            return listar(request, response, session, model);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private void listarTipoLancamento(Model model, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException {
        model.addAttribute("tiposLancamentos", faturamentoBeneficioController.listarTipoLancamento(responsavel));
    }

    protected String getLinkAction() {
        return "../v3/manterArquivoFaturamentoBeneficio?acao=listar";
    }
}
