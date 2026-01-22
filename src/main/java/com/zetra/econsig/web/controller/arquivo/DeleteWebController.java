package com.zetra.econsig.web.controller.arquivo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.FileAbstractWebController;

/**
 * <p>Title: VisualizarIntegracaoWebController</p>
 * <p>Description: Controlador Web para realização de exclusão de arquivos</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class DeleteWebController extends FileAbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DeleteWebController.class);

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/excluirArquivo" })
    public String excluirArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String msg = ApplicationResourcesHelper.getMessage("mensagem.erro.delete", responsavel);
        request.setAttribute("msg", msg);

        FileStatus status = processar(request, response, session, model);
        if (!status.getResultado()) {
            session.setAttribute(CodedValues.MSG_ERRO, status.getMensagemErro());

        } else {
            Object arquivos = status.getArquivo() != null ? status.getArquivo() : status.getListaArquivos();
            // Gera log de remoção de arquivo
            try {
                LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE_FILE, Log.LOG_INFORMACAO);
                if (arquivos instanceof File) {
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.delete.arquivo.log", responsavel) + ": " + ((File) arquivos).getAbsolutePath());
                } else if (arquivos instanceof List<?>) {
                    for (int pos = 0; pos < ((List<File>) arquivos).size(); pos++) {
                        File fileInList = ((List<File>) arquivos).get(pos);
                        log.add(ApplicationResourcesHelper.getMessage("rotulo.delete.arquivo.log", responsavel) + ": " + fileInList.getAbsolutePath());
                    }
                }
                log.write();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }
            if (arquivos instanceof File) {
                String ext = JspHelper.verificaVarQryStr(request, "ext");
                if (ext == null || ext.equals("")) {
                    ((File) arquivos).delete();
                } else {
                    FileHelper.rename(((File) arquivos).getAbsolutePath(), ((File) arquivos).getAbsolutePath() + "." + ext);
                }
            } else if (arquivos instanceof List<?>) {
                for (int pos = 0; pos < ((List<File>) arquivos).size(); pos++) {
                    File fileInList = ((List<File>) arquivos).get(pos);
                    fileInList.delete();
                }
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.removido.sucesso", responsavel));
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
