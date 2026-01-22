package com.zetra.econsig.web.controller.arquivo.upload;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoPreviafaturamentobeneficios" })
public class UploadArquivoPreviaFaturamentoBeneficiosWebController extends UploadArquivoWebController{
    @RequestMapping(params = { "acao=carregar" })
    public String carregarPreviaFaturamentoBeneficios(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return carregar("previafaturamentobeneficios", false, false, false, false, TipoArquivoEnum.ARQUIVO_PREVIA_FATURAMENTO_BENEFICIOS, request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadPreviaFaturamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ValidaImportacaoControllerException, ConsignatariaControllerException, PeriodoException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String tipo = "previafaturamentobeneficios";
        boolean selecionaEstOrgUploadMargemRetorno = false;
        boolean selecionaEstOrgUploadContracheque = false;

        try {
            upload(tipo, true, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, request, response, session, model);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarPreviaFaturamentoBeneficios(request, response, session, model);
    }


    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
    	String path = "fatura" + File.separatorChar + "previa" + File.separatorChar + "csa" + File.separatorChar;
    	if (!TextHelper.isNull(csaCodigo)) {
    		path +=  csaCodigo + File.separatorChar;
    	}
        return Arrays.asList(path);
    }

    @Override
    protected String buscarPathDiretorioArquivos(HttpServletRequest request) {
    	String csaCodigo = request.getParameter("CSA_CODIGO_AUX") != null ? request.getParameter("CSA_CODIGO_AUX") : request.getParameter("CSA_CODIGO");
    	String path = "fatura" + File.separatorChar + "previa" + File.separatorChar + "csa" + File.separatorChar;
    	if (!TextHelper.isNull(csaCodigo)) {
    		path +=  csaCodigo + File.separatorChar;
    	}
    	return path;
    }

}
