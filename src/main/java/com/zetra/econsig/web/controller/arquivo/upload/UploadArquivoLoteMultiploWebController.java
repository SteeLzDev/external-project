package com.zetra.econsig.web.controller.arquivo.upload;

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
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: UploadArquivoLoteMultiploWebController</p>
 * <p>Description: Controlador Web para o caso de uso Upload Arquivo de lote múltiplo.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoLotemultiplo" })
public class UploadArquivoLoteMultiploWebController extends UploadArquivoWebController {

    @RequestMapping(params = { "acao=carregar" })
    public String carregarLoteMultiplo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return carregar("lotemultiplo", false, false, false, false, TipoArquivoEnum.ARQUIVO_LOTE, request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadLoteMultiplo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ValidaImportacaoControllerException, ConsignatariaControllerException, PeriodoException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String tipo = "lotemultiplo";
        boolean selecionaEstOrgUploadMargemRetorno = false;
        boolean selecionaEstOrgUploadContracheque = false;

        try {
            upload(tipo, false, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, request, response, session, model);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarLoteMultiplo(request, response, session, model);
    }

    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        String path = "lote" + java.io.File.separatorChar;
        List<String> listPath = new ArrayList<>();
        listPath.add(path + "cse" + java.io.File.separatorChar);

        return listPath;
    }
}
