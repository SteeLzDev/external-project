package com.zetra.econsig.web.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

import eu.medsea.mimeutil.MimeType;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ViewImageServlet</p>
 * <p>Description: Servlet para exibição das imagens externas do sistema.
 * Substitui o jsp /img/view.jsp</p>
 * <p>Copyright: Copyright (c) 2006-2023</p>
 * <p>Company: ZetraSoft</p>
 */
public class ViewImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ViewImageServlet.class);

    public static boolean imageNotNullOrBlank(String name, AcessoSistema responsavel) {
        try {
            final File arquivo = getImage(name, responsavel);
            if (arquivo != null) {
                // espacador.gif -> 43 bytes
                // blank.gif -----> 71 bytes
                return arquivo.length() > 100;
            }
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return false;
    }

    public static File getImage(String name, AcessoSistema responsavel) throws IOException {
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String subDiretorio = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DIR_IMG_SERVIDORES, responsavel);
        String[] subDiretorioPermitidosImagens  = {"login" ,"imagem", "img", !TextHelper.isNull(subDiretorio) ? subDiretorio : null};
        
        if (!TextHelper.isNull(absolutePath)) {
            absolutePath = new File(absolutePath).getCanonicalPath();

            File arquivo = null;

            if (responsavel.isCsaCor() || responsavel.isCseSupOrg() || responsavel.isSer()) {
                final String fileName = absolutePath
                        + File.separatorChar + "imagem"
                        + File.separatorChar + responsavel.getTipoEntidade()
                        + File.separatorChar + responsavel.getCodigoEntidade()
                        + File.separatorChar + name;
                arquivo = new File(fileName);
            }

            if ((arquivo == null) || !arquivo.exists()) {
            	String fileName = absolutePath
            			+ File.separatorChar + "imagem"
            			+ File.separatorChar + name;
            	arquivo = new File(fileName);

            	if (!arquivo.exists()) {
            		for (final String subDiretorioPermitido : subDiretorioPermitidosImagens) {
            			if (!TextHelper.isNull(subDiretorioPermitido) && name.toLowerCase().contains(subDiretorioPermitido.toLowerCase())) {
            				fileName = absolutePath 
            						+ File.separatorChar + name;
            				arquivo = new File(fileName);
            			}
            		}
            	}
            }

            if (arquivo.exists() && arquivo.getCanonicalPath().startsWith(absolutePath)) {
                return arquivo;
            }
        }
        return null;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final HttpSession session = request.getSession();

        String name = JspHelper.verificaVarQryStr(request, "nome");
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!name.equals("")) {
            name = java.net.URLDecoder.decode(name, "UTF-8");
            if (name.indexOf("..") != -1) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.impossivel.download.imagem", responsavel));
                request.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
                return;
            }

            final File arquivo = getImage(name, responsavel);
            if (arquivo != null) {
                final ServletOutputStream out = response.getOutputStream();
                final Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(arquivo.getAbsolutePath());
                final String mime = (mimeSet != null) && (!mimeSet.isEmpty()) ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
                response.setContentType(mime);
                response.setContentLength((int) arquivo.length());

                try (final BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo))) {
                    int i = 0;
                    while ((i = entrada.read()) != -1) {
                        out.write(i);
                    }
                }

            } else {
                // Caso não encontre a imagem, exibe um pixel branco no lugar
                request.getRequestDispatcher("../img/espacador.gif").include(request, response);
            }
        }
    }
}