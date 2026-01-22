package com.zetra.econsig.web.controller.arquivo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AnexoCredenciamentoCsa;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.FileAbstractWebController;

import eu.medsea.mimeutil.MimeType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class DownloadAnexoCredenciamentoWebController extends FileAbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DownloadAnexoCredenciamentoWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @PostMapping(value={"/v3/downloadArquivosCredenciamento"}, params={"tipo=anexo_credenciamento"})
    public void downloadArquivosCredenciamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String creCodigo = JspHelper.verificaVarQryStr(request, "creCodigo");
            String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
            String dwnTermoAditivo = JspHelper.verificaVarQryStr(request, "termoAditivo");

            String pathFile = ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + "credenciamento" + File.separatorChar + "csa"
                    + File.separatorChar + csaCodigo + File.separatorChar;

            List<String> arquivos = new ArrayList<>();

            List<AnexoCredenciamentoCsa> lstAnexo = consignatariaController.lstAnexoCredenciamentoCsa(creCodigo, responsavel);

            for(AnexoCredenciamentoCsa anexo : lstAnexo) {
                if(dwnTermoAditivo.isEmpty()) {
                    String ancCodigo = anexo.getAncCodigo() + ".zip";
                    String ancNome = anexo.getAncNome();
                    File verificaArquivo = new File(pathFile + ancCodigo);
                    if (verificaArquivo.exists()) {
                        arquivos.add(pathFile + ancCodigo);
                    } else {
                        verificaArquivo = new File(pathFile + ancNome);
                        if (verificaArquivo.exists()) {
                            arquivos.add(pathFile + ancNome);
                        }
                    }
                } else if (dwnTermoAditivo.equals("S")) {
                    if (anexo.getTarCodigo().equals("66")) {
                        String ancCodigo = anexo.getAncCodigo() + ".zip";
                        String ancNome = anexo.getAncNome();
                        File verificaArquivo = new File(pathFile + ancCodigo);
                        if (verificaArquivo.exists()) {
                            arquivos.add(pathFile + ancCodigo);
                        } else {
                            verificaArquivo = new File(pathFile + ancNome);
                            if (verificaArquivo.exists()) {
                                arquivos.add(pathFile + ancNome);
                            }
                        }
                    }
                }
            }
            
            // Remove duplicados
            arquivos = arquivos.stream().distinct().collect(Collectors.toList());

            ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
            String nomeFinalZip = null;
            if (dwnTermoAditivo.isEmpty()) {
                nomeFinalZip = consignataria.getCsaNomeAbreviado().replace(" ", "_") + "_" + consignataria.getCsaIdentificador() + "_credenciamento.zip";
            } else if (dwnTermoAditivo.equals("S")){
                nomeFinalZip = consignataria.getCsaNomeAbreviado().replace(" ", "_") + "_" + consignataria.getCsaIdentificador() + "_termo_aditivo.zip";
            }
            String arquivoZip = pathFile+nomeFinalZip;

            FileHelper.zip(arquivos, arquivoZip);

            File arquivo = new File(arquivoZip);

            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.log", responsavel) + ": " + arquivo.getAbsolutePath());
            log.write();

            Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(arquivo.getAbsolutePath());
            String mime = mimeSet != null && !mimeSet.isEmpty() ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
            response.setContentType(mime);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + arquivo.getName() + "\"");

            long tamanhoArquivoBytes = arquivo.length();
            BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
            if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
                IOUtils.copyLarge(entrada, response.getOutputStream());
            } else {
                response.setContentLength((int) tamanhoArquivoBytes);
                IOUtils.copy(entrada, response.getOutputStream());
            }

            response.flushBuffer();
            entrada.close();

            FileHelper.delete(arquivoZip);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel));
        }
    }
}
