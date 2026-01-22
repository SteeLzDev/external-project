package com.zetra.econsig.web.controller.arquivo.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ValidaImportacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: UploadArquivoCadastroDependenteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Upload Arquivo de cadastro de dependente.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoCadastroDependentes" })
public class UploadArquivoCadastroDependenteWebController extends UploadArquivoWebController {

    @RequestMapping(params = { "acao=carregar" })
    public String carregarDependente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String tipo = "cadastroDependentes";
        boolean comentario = ParamSist.paramEquals(CodedValues.TPC_INTEGRA_JIRA, CodedValues.TPC_SIM, responsavel);
        boolean selecionaEstOrgUploadMargemRetorno = responsavel.isCseSup() && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_SELEC_ENT_UPL_ARQ_MARGEM_RETORNO, responsavel);
        boolean selecionaEstOrgUploadContracheque = false;

        return carregar(tipo, true, comentario, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, TipoArquivoEnum.ARQUIVO_CADASTRO_DEPENDENTE, request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ValidaImportacaoControllerException, ConsignatariaControllerException, PeriodoException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String tipo = "cadastroDependentes";
        boolean selecionaEstOrgUploadMargemRetorno = responsavel.isCseSup() && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_SELEC_ENT_UPL_ARQ_MARGEM_RETORNO, responsavel);
        boolean selecionaEstOrgUploadContracheque = false;

        try {
            upload(tipo, true, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, request, response, session, model);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarDependente(request, response, session, model);
    }

    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        String path = "beneficiarios" + java.io.File.separatorChar;
        List<String> listPath = new ArrayList<>();
        listPath.add(path + "cse" + java.io.File.separatorChar);
        return listPath;
    }

    @Override
    protected String buscarPathDiretorioArquivos(HttpServletRequest request) {
        String path = "beneficiarios" + File.separatorChar;
        return path;
    }

}
