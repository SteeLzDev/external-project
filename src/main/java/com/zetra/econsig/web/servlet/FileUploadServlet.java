package com.zetra.econsig.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: FileUploadServlet</p>
 * <p>Description: Servlet usado para upload de arquivos via JSON</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FileUploadServlet extends HttpServlet {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FileUploadServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HttpSession session = request.getSession();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
        int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? (Integer.valueOf(paramTamMaxArqAnexo)).intValue() : 200);
        File anexo = null;
        String hashDir = session.getId();
        String mensagem = "";

        try {
            UploadHelper uploadHelper = new UploadHelper();
            uploadHelper.processarRequisicao(getServletContext(), request, tamMaxArqAnexo * 1024);

            if (uploadHelper != null && uploadHelper.hasArquivosCarregados()) {
                String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                // O uploadHelper já é relativo ao diretório TPC_DIR_RAIZ_ARQUIVOS
                String diretorioDestinoUploadHelper = UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + hashDir;
                File diretorioTemporario = new File(diretorioRaizArquivos + File.separator + diretorioDestinoUploadHelper);

                // Cria o diretório caso não exista. Se já existir, não há problema,
                // pois os arquivos são sobrepostos. E é necessário para upload múltiplo.
                if (!diretorioTemporario.exists()) {
                    diretorioTemporario.mkdirs();
                }

                if (diretorioTemporario.canWrite()) {
                    // UploadHelper já assume os caminhos a partir do diretorioRaizArquivos
                    String[] extensoesPermitidas = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO;

                    // Verificar função que o responsável está tentando executar para definir quais extensões de arquivo são permitidas
                    if (responsavel.getFunCodigo().equals(CodedValues.FUN_RECALCULAR_MARGEM_PARCIAL) || responsavel.getFunCodigo().equals(CodedValues.FUN_TAXA_JUROS)) {
                        extensoesPermitidas = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_TXT;
                    }
                    
                    // Verificar função que o responsável está tentando executar para definir quais extensões de arquivo são permitidas para o caso de uso de editar relatório
                    if (responsavel.getFunCodigo().equals(CodedValues.FUN_EDT_RELATORIOS)) {
                        extensoesPermitidas = UploadHelper.EXTENSOES_PERMITIDAS_SUBRELATORIO;
                    }

                    anexo = uploadHelper.salvarArquivo(diretorioDestinoUploadHelper, extensoesPermitidas, null, null);
                } else {
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.durante.upload", responsavel);
                }
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.durante.upload", responsavel);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            mensagem = ex.getMessage();
        }

        JSONObject result = new JSONObject();
        result.put("mensagem", mensagem);
        if (anexo != null) {
            result.put("fileSize", anexo.length() / 1024);
            result.put("fileName", anexo.getName());
        } else {
            result.put("fileSize", 0.0);
            result.put("fileName", "");
        }

        /* Devido a resposta que chegava ao jquery conter "<pre style=", estou usando text/html em vez de text/json
         * The server response is parsed by the browser to create the document for the IFRAME.
         * If the server is using JSON to send the return object, then the Content-Type header
         * must be set to "text/html" in order to tell the browser to insert the text unchanged
         * into the document body. */
        response.setContentType("text/html");

        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServletException(ex.getMessage(), ex);
        }
        out.println(JSONObject.toJSONString(result));
        out.flush();
        out.close();
    }
}
