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
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoRelatorioCustomizado" })
public class UploadArquivoRelatorioCustomizadoWebController extends UploadArquivoWebController {

    @RequestMapping(params = { "acao=carregar" })
    public String carregarRelatorioCustomizado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return carregar("relatorioCustomizado", false, false, false, false, TipoArquivoEnum.ARQUIVO_RELATORIO_CUSTOMIZADO, request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadRelatorioCustomizado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ValidaImportacaoControllerException, ConsignatariaControllerException, PeriodoException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String tipo = "relatorioCustomizado";

        try {
            upload(tipo, true, false, false, request, response, session, model);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarRelatorioCustomizado(request, response, session, model);
    }

    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        List<String> listPath = new ArrayList<>();

        if(!csaCodigo.isEmpty()) {
            listPath.add("relatorio" + File.separatorChar + "csa" + File.separatorChar + "customizacoes" + java.io.File.separatorChar + csaCodigo + java.io.File.separatorChar);
        } else {
            listPath.add("relatorio" + File.separatorChar + "cse" + File.separatorChar + "customizacoes" + java.io.File.separatorChar);
        }

        return listPath;
    }

    @Override
    protected String buscarPathDiretorioArquivos(HttpServletRequest request) {
        return "relatorio" + File.separatorChar + "csa" + File.separatorChar + "customizacoes" + File.separatorChar;
    }

}
