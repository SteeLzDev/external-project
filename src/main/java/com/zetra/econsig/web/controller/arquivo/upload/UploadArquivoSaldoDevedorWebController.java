package com.zetra.econsig.web.controller.arquivo.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: UploadArquivoSaldoDevedorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Upload Arquivo de saldo devedor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoSaldodevedor" })
public class UploadArquivoSaldoDevedorWebController extends UploadArquivoWebController {

    @RequestMapping(params = { "acao=carregar" })
    public String carregarSaldoDevedor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return carregar("saldodevedor", true, false, false, false, TipoArquivoEnum.ARQUIVO_SALDO_DEVEDOR, request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadSaldoDevedor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ValidaImportacaoControllerException, ConsignatariaControllerException, PeriodoException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String tipo = "saldodevedor";
        final boolean selecionaEstOrgUploadMargemRetorno = false;
        final boolean selecionaEstOrgUploadContracheque = false;

        try {
            upload(tipo, false, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, request, response, session, model);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarSaldoDevedor(request, response, session, model);
    }

    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        final StringBuilder path = new StringBuilder("saldodevedor").append(java.io.File.separatorChar);
        final List<String> listPath = new ArrayList<>();

        if (responsavel.isCsaCor()) {
            csaCodigo = responsavel.getCsaCodigo();
        }

        if (responsavel.isCseSup() && TextHelper.isNull(csaCodigo)) {
            path.append("cse").append(java.io.File.separatorChar);
        } else {
            path.append("csa").append(java.io.File.separatorChar);
            listPath.add(path.append(csaCodigo).append(java.io.File.separatorChar).toString());
        }

        return listPath;
    }

    @Override
    protected String buscarPathDiretorioArquivos(HttpServletRequest request) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String csaCodigo = responsavel.isCsaCor() ? responsavel.getCodigoEntidade() : request.getParameter("CSA_CODIGO");
        StringBuilder path = new StringBuilder("saldodevedor").append(File.separatorChar).append("csa").append(File.separatorChar);

        if (responsavel.isCseSup() && TextHelper.isNull(csaCodigo)) {
            path = new StringBuilder("saldodevedor").append(File.separatorChar).append("cse").append(File.separatorChar);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            path.append(csaCodigo).append(File.separatorChar);
        }
        return path.toString();
    }
}
