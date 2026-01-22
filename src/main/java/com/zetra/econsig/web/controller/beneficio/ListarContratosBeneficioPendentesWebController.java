package com.zetra.econsig.web.controller.beneficio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarContratosBeneficioPendentes" })
public class ListarContratosBeneficioPendentesWebController extends ControlePaginacaoWebController {

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            String csaCodigo = null;

            if (responsavel.isSup()) {
                csaCodigo = request.getParameter("CSA_CODIGO");
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean podeListar = responsavel.temPermissao(CodedValues.FUN_LST_CONTRATOS_PENDENTES_BENEFICIO);
            boolean edtBeneficioAvancado = responsavel.temPermissao(CodedValues.FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO);

            if (!podeListar) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject criterio = new CustomTransferObject();

            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            String ncaCodigo = NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo();

            List<TransferObject> inclusaoPendentes = new ArrayList<>();
            List<TransferObject> exclusaoPendentes = new ArrayList<>();
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            int totalInclusoesPendentes = 0;
            int offsetIncluPen = 0;
            int sizeIncluPen = JspHelper.LIMITE;

            int totalExclusoesPendentes = 0;
            int offsetExcluPen = 0;
            int sizeExcluPen = JspHelper.LIMITE;

            // Monta lista de parâmetros através dos parâmetros de request
            params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offsetIncluPen");
            params.remove("offsetExcluPen");

            List<String> requestParams = new ArrayList<>(params);
            requestParams = new ArrayList<>(params);

            if (!TextHelper.isNull(csaCodigo)) {
                offsetIncluPen = request.getParameter("offsetIncluPen") != null ? Integer.parseInt(request.getParameter("offsetIncluPen")) : 0 ;
                offsetExcluPen = request.getParameter("offsetExcluPen") != null ? Integer.parseInt(request.getParameter("offsetExcluPen")) : 0 ;

                inclusaoPendentes = contratoBeneficioController.listarContratosBeneficiosPendentesInclusao(criterio, offsetIncluPen, sizeIncluPen, responsavel);
                totalInclusoesPendentes = (int) contratoBeneficioController.countContratosBeneficiosPendentesInclusao(criterio, responsavel);

                exclusaoPendentes = contratoBeneficioController.listarContratosBeneficiosPendentesExclusao(criterio, offsetExcluPen, sizeExcluPen, responsavel);
                totalExclusoesPendentes = (int) contratoBeneficioController.countContratosBeneficiosPendentesExclusao(criterio, responsavel);
            }

            configurarPaginador("IncluPen", "../v3/listarContratosBeneficioPendentes", "rotulo.beneficio.contratos.pendentes.inclusao.lista", totalInclusoesPendentes, sizeIncluPen, requestParams, false, request, model);
            configurarPaginador("ExcluPen", "../v3/listarContratosBeneficioPendentes", "rotulo.beneficio.contratos.pendentes.exclusao.lista", totalExclusoesPendentes, sizeExcluPen, requestParams, false, request, model);

            List<Consignataria> lstConsignatarias = consignatariaController.lstConsignatariaByNcaCodigo(ncaCodigo, responsavel);

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("inclusaoPendentes", inclusaoPendentes);
            model.addAttribute("exclusaoPendentes", exclusaoPendentes);
            model.addAttribute("lstConsignatarias", lstConsignatarias);
            model.addAttribute("edtBeneficioAvancado", edtBeneficioAvancado);

            return viewRedirect("jsp/manterBeneficio/listarContratosPendentes", request, session, model, responsavel);

        } catch (ContratoBeneficioControllerException | ConsignatariaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
