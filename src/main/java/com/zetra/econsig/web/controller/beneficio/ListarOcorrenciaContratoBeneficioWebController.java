package com.zetra.econsig.web.controller.beneficio;

import java.text.ParseException;
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
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ListarOcorrenciaContratoBeneficioWebController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarOcorrenciaContratoBeneficio" })
public class ListarOcorrenciaContratoBeneficioWebController extends AbstractWebController {

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

            CustomTransferObject criterio = new CustomTransferObject();

            String cbeCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.CBE_CODIGO));

            criterio.setAttribute(Columns.CBE_CODIGO, cbeCodigo);

            List<TransferObject> ocorrencias = contratoBeneficioController.listOcorrenciaContratosBeneficiosByCbeCodigo(criterio, false, responsavel);

            model.addAttribute("ocorrencias", ocorrencias);

            return viewRedirect("jsp/manterBeneficio/listarOcorrenciaContratoBeneficio", request, session, model, responsavel);

        } catch (ContratoBeneficioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
