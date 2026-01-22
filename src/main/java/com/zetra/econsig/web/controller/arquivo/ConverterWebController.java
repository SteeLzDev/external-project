package com.zetra.econsig.web.controller.arquivo;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
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
 * <p>Description: Controlador Web para conversão de arquivos.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/converterArquivo" })
public class ConverterWebController extends FileAbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConverterWebController.class);

    @RequestMapping(params = { "acao=iniciar" })
    public String converterArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String msg = ApplicationResourcesHelper.getMessage("mensagem.status.erro.compact.converter", responsavel);
        request.setAttribute("msg", msg);

        FileStatus status = processar(request, response, session, model);

        if (!status.getResultado()) {
            session.setAttribute(CodedValues.MSG_ERRO, msg);
        } else {
            File arquivo = status.getArquivo();

            if (arquivo.getName().endsWith(".crypt")) {
                File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(arquivo.getAbsolutePath(), true, responsavel);
                if (arquivoPlano != null) {
                    arquivo = arquivoPlano;
                }
            }

            String fileName = arquivo.getAbsolutePath();

            // Gera log de conversão de arquivo
            try {
                LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.CONVERT_FILE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.compact.arquivo.log", responsavel) + ": " + fileName);
                log.write();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }
            try {
                if (fileName.toLowerCase().endsWith(".txt")) {
                    String zipFileName = arquivo.getParent() + File.separatorChar + arquivo.getName().toLowerCase().replaceAll("txt", "zip");
                    FileHelper.zip(fileName, zipFileName);
                } else {
                    String outputPath = fileName.substring(0, fileName.lastIndexOf(File.separatorChar));
                    FileHelper.unZip(fileName, outputPath);
                }
                arquivo.delete();
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, msg);
                LOG.error(ex.getMessage(), ex);
            }
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

}
