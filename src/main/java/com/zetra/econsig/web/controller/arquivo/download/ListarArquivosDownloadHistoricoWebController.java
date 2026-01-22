package com.zetra.econsig.web.controller.arquivo.download;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarArquivosDownloadHistoricoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Download de arquivos Cópia de Segurança</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarArquivosDownloadHistorico" })
public class ListarArquivosDownloadHistoricoWebController extends ControlePaginacaoWebController {

    @RequestMapping(params = { "acao=iniciar" })
    public String listarArquivoDownload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        SynchronizerToken.saveToken(request);
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String absolutePath = ParamSist.getDiretorioRaizArquivos();

        final String tipo = "copia_seguranca";

        String pathCopiaSeguranca = absolutePath + File.separatorChar + tipo;

        FileFilter filtro = new FileFilter() {
            @Override
            public boolean accept(File arq) {
                String arqName = arq.getName().toLowerCase();
                for (String element : UploadHelper.EXTENSOES_PERMITIDAS_DOWNLOAD_COPIA_SEGURANCA) {
                    if (arqName.endsWith(element)) {
                        return true;
                    }
                }
                return false;
            }
        };

        File dirCopiaSeguranca = new File(pathCopiaSeguranca);
        if (!dirCopiaSeguranca.exists() && !dirCopiaSeguranca.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.copia.seguranca.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        File[] temp = dirCopiaSeguranca.listFiles(filtro);
        List<File> arquivos = new ArrayList<>();
        arquivos.addAll(Arrays.asList(temp));

        // Ordena os arquivos baseado na data de modificação
        Collections.sort(arquivos, (f1, f2) -> {
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        });

        List<ArquivoDownload> arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivos, pathCopiaSeguranca, null, responsavel);

        model.addAttribute("arquivos", arquivosPaginaAtual);
        model.addAttribute("tipo", tipo);
        return viewRedirect("jsp/listarArquivosDownloadCopiaSeguranca/listarArquivosDownloadCopiaSeguranca", request, session, model, responsavel);
    }

}
