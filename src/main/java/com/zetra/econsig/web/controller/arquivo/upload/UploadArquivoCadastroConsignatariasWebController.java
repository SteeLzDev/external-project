package com.zetra.econsig.web.controller.arquivo.upload;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
 * <p>Title: UploadArquivoCadastroConsignatarias</p>
 * <p>Description: Controlador Web para o caso de uso Upload Arquivo de lote de cadastro de consignatárias.</p>
 * <p>Copyright: Copyright (c) 2002-2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoCadastroConsignatarias" })
public class UploadArquivoCadastroConsignatariasWebController extends UploadArquivoWebController {

    private static final String CADASTRO_CONSIGNATARIAS = "cadastroConsignatarias";

    @RequestMapping(params = { "acao=carregar" })
    public String carregarCadastroConsignatarias(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return carregar(CADASTRO_CONSIGNATARIAS, true, false, false, false, TipoArquivoEnum.ARQUIVO_LOTE_CADASTRO_CONSIGNATARIA, request, response, session, model);
    }

    @PostMapping(params = { "acao=upload" })
    public String uploadMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            upload(CADASTRO_CONSIGNATARIAS, true, false, false, request, response, session, model);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarCadastroConsignatarias(request, response, session, model);
    }

    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        final String path = CADASTRO_CONSIGNATARIAS + java.io.File.separatorChar;
        final List<String> listPath = new ArrayList<>();

        if (responsavel.isOrg() && (((!selecionaEstOrgUploadMargemRetorno || !selecionaEstOrgUploadContracheque) && temPermissaoEst) || ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && AcessoSistema.ENTIDADE_EST.equals(papCodigo)))) {
            listPath.add(path + "est" + java.io.File.separatorChar + responsavel.getCodigoEntidadePai());
        } else if (responsavel.isOrg()) {
            listPath.add(path + "cse" + java.io.File.separatorChar + responsavel.getCodigoEntidade());
        } else if (responsavel.isCseSup()) {
           if (AcessoSistema.ENTIDADE_EST.equals(papCodigo)) {
                if (TextHelper.isNull(estCodigo)) {
                    throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
                }
                listPath.add(path + "est" + java.io.File.separatorChar + estCodigo);
            } else if (AcessoSistema.ENTIDADE_CSE.equals(papCodigo)) {
                listPath.add(path + "cse");
            } else {
                listPath.add(path + "cse");
            }
        }
        return listPath;
    }
}
