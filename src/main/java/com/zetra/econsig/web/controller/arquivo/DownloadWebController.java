package com.zetra.econsig.web.controller.arquivo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.FileAbstractWebController;

import eu.medsea.mimeutil.MimeType;

/**
 * <p>Title: VisualizarIntegracaoWebController</p>
 * <p>Description: Controlador Web para realização de download de arquivos.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class DownloadWebController extends FileAbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DownloadWebController.class);

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/downloadArquivo" })
    public void downloadArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String msg = ApplicationResourcesHelper.getMessage("mensagem.erro.download", responsavel);
        request.setAttribute("msg", msg);

        FileStatus status = processar(request, response, session, model);

        if (!status.getResultado()) {
            session.setAttribute(CodedValues.MSG_ERRO, status.getMensagemErro());
        } else {
            boolean removeArquivo = false;
            File arquivo = status.getArquivo();

            if (arquivo.getName().endsWith(".crypt")) {
                File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(arquivo.getAbsolutePath(), false, responsavel);
                if (arquivoPlano != null) {
                    arquivo = arquivoPlano;
                    removeArquivo = true;
                }
            }

            // Gera log de download de arquivo
            try {
                LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.log", responsavel) + ": " + arquivo.getAbsolutePath());
                log.write();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }

            long tamanhoArquivoBytes = arquivo.length();

            Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(arquivo.getAbsolutePath());
            String mime = mimeSet != null && mimeSet.size() > 0 ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
            response.setContentType(mime);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + arquivo.getName() + "\"");

            if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
            } else {
                response.setContentLength((int) tamanhoArquivoBytes);
            }

            BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
            if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                org.apache.commons.io.IOUtils.copyLarge(entrada, response.getOutputStream());
            } else {
                org.apache.commons.io.IOUtils.copy(entrada, response.getOutputStream());
            }
            response.flushBuffer();
            entrada.close();

            if (removeArquivo) {
                // Caso o arquivo originalmente está criptografado e foi descriptogradado para o download
                // então remove o arquivo gerado para o envio ao usuário
                arquivo.delete();
            }
        }
    }
}
