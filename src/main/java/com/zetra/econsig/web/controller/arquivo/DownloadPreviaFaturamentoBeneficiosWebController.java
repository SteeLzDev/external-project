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

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;

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
public class DownloadPreviaFaturamentoBeneficiosWebController extends DownloadWebController {

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/downloadArquivo" }, params = {"tipo=previafaturamentobeneficios"})
    public void downloadArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {
    	super.downloadArquivo(request, response, session, model);
    }
    
    @Override
    public FileStatus processar(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		Model model) throws IOException {
    	
    	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    	
    	String nomeArquivo = TextHelper.isNull(request.getParameter("arquivo_nome")) ? ApplicationResourcesHelper.getMessage("rotulo.include.get.file.desconhecido", responsavel) : request.getParameter("arquivo_nome");
    	String csaCodigo = request.getParameter("csaCodigo");
    	String msg = request.getAttribute("msg") != null ? request.getAttribute("msg").toString() : ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, nomeArquivo);
    	
    	String absolutePath = ParamSist.getDiretorioRaizArquivos();
        if (absolutePath != null) {
            absolutePath = new File(absolutePath).getCanonicalPath();
            String name = java.net.URLDecoder.decode(nomeArquivo, "UTF-8");
            String pathFile = absolutePath + File.separatorChar + "fatura" + File.separatorChar + "previa" + File.separatorChar + "csa" + File.separatorChar  + csaCodigo + File.separatorChar + nomeArquivo;
            
            File arquivo = new File(pathFile);
            if (!arquivo.exists() || !arquivo.getCanonicalPath().startsWith(absolutePath)) {
                return new FileStatus(false, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, name));
            }

            return new FileStatus(arquivo, true);
            
        }
    	
        return new FileStatus(false, msg);
    	
    }
}
