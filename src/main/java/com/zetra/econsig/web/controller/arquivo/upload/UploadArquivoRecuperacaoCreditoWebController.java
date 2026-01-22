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
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: UploadArquivoRecuperacaoCreditoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Upload Arquivo de Recuperação de Crédito.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date:  $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoRecuperacaoCredito" })
public class UploadArquivoRecuperacaoCreditoWebController extends UploadArquivoWebController {

    @RequestMapping(params = { "acao=carregar" })
    public String carregarRecuperacaoCredito(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return carregar("recuperacaoCredito", false, false, false, false, TipoArquivoEnum.ARQUIVO_RECUPERACAO_CREDITO, request, response, session, model);
    }


    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadRecuperacaoCredito(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ConsignatariaControllerException{
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String tipo = "recuperacaoCredito";
        boolean selecionaEstOrgUploadMargemRetorno = false;
        boolean selecionaEstOrgUploadContracheque = false;

        try {
            upload(tipo, false, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, request, response, session, model);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarRecuperacaoCredito(request, response, session, model);
    }

    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        String path = "recuperacao_credito" + java.io.File.separatorChar;
        List<String> listPath = new ArrayList<>();
        listPath.add(path + "csa" + java.io.File.separatorChar + csaCodigo);

        return listPath;
    }
}
