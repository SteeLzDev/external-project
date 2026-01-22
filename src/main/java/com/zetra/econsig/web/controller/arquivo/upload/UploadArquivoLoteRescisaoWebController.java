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
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: UploadArquivoLoterescisaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Upload Arquivo de lote de Rescisão.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoLoterescisao" })
public class UploadArquivoLoteRescisaoWebController extends UploadArquivoWebController {

    @RequestMapping(params = { "acao=carregar" })
    public String carregarLote(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return carregar("loterescisao", false, false, false, false, TipoArquivoEnum.ARQUIVO_LOTE_RESCISAO, request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadLote(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, IOException, ValidaImportacaoControllerException, ConsignatariaControllerException, PeriodoException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String tipo = "loterescisao";
        boolean selecionaEstOrgUploadMargemRetorno = false;
        boolean selecionaEstOrgUploadContracheque = false;

        try {
            upload(tipo, false, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, request, response, session, model);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return carregarLote(request, response, session, model);
    }

    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        String path = "rescisao" + java.io.File.separatorChar;
        List<String> listPath = new ArrayList<>();

        if (responsavel.isOrg() && (((!selecionaEstOrgUploadMargemRetorno || !selecionaEstOrgUploadContracheque) && temPermissaoEst) || ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && papCodigo.equals(AcessoSistema.ENTIDADE_EST)))) {
            listPath.add(path + "est" + java.io.File.separatorChar + responsavel.getCodigoEntidadePai());
        } else if (responsavel.isOrg()) {
            listPath.add(path + "cse" + java.io.File.separatorChar + responsavel.getCodigoEntidade());
        } else if (responsavel.isCseSup()) {
            if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && !TextHelper.isNull(papCodigo)) {
                if (papCodigo.equals(AcessoSistema.ENTIDADE_ORG)) {
                    if (TextHelper.isNull(orgCodigo)) {
                        throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
                    }
                    listPath.add(path + "cse" + java.io.File.separatorChar + orgCodigo);
                } else if (papCodigo.equals(AcessoSistema.ENTIDADE_EST)) {
                    if (TextHelper.isNull(estCodigo)) {
                        throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
                    }
                    listPath.add(path + "est" + java.io.File.separatorChar + estCodigo);
                } else if (papCodigo.equals(AcessoSistema.ENTIDADE_CSE)) {
                    listPath.add(path + "cse");
                }
            } else {
                listPath.add(path + "cse");
            }
        }

        return listPath;
    }
}
