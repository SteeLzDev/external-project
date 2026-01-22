package com.zetra.econsig.web.controller.registroservidor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.servidor.AbstractServidorWebController;

import eu.medsea.mimeutil.MimeType;

/**
 * <p>Title: EditarAnexosRegistroServidorWebController</p>
 * <p>Description: Controlador Web para o caso de Editar anexos do registro do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 *  $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarAnexosRegistroServidor" })
public class EditarAnexosRegistroServidorWebController extends AbstractServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarAnexosRegistroServidorWebController.class);

    @Autowired
    private ArquivoController arquivoController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if(responsavel.isCsaCor() || (!responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR) && !responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ANEXOS_REGISTRO_SERVIDOR))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            List<TransferObject> listArquivoRse = arquivoController.lstArquivosRse(rseCodigo, responsavel);

            List<String> tarCodigos = new ArrayList<>();
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_GENERICO.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_DOCUMENTO_REGISTRO_SERVIDOR.getCodigo());

            List<TransferObject> tipoArquivos = historicoArquivoController.lstTiposArquivoByTarCodigos(tarCodigos, responsavel);

            boolean novoAnexo = JspHelper.verificaVarQryStr(request, "NOVO_ANEXO") !=null && JspHelper.verificaVarQryStr(request, "NOVO_ANEXO").equals("true");

            model.addAttribute("arquivosRse", listArquivoRse);
            model.addAttribute("permiteInserirAnexo",responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR));
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("tipoArquivos", tipoArquivos);
            model.addAttribute("novoAnexo", novoAnexo);

            return viewRedirect("jsp/editarRegistroServidor/editarAnexosRegistroServidor", request, session, model, responsavel);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if(responsavel.isCsaCor() || !responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if(!responsavel.isCseSupOrg() || !responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");
        String tipoArquivo = JspHelper.verificaVarQryStr(request, "tipoArquivo");

        if (TextHelper.isNull(nomeAnexo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.anexo.documento", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } else if (TextHelper.isNull(tipoArquivo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.anexo.registro.servidor.tipo.arquivo.ausente", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String[] anexosName = !TextHelper.isNull(nomeAnexo) ? nomeAnexo.split(";") : null;
            String idAnexo = session.getId();

            List<TransferObject> conteudos = new ArrayList<>();
            if (anexosName != null) {
                for (String nomeAnexoCorrente : anexosName) {
                    try {
                        TransferObject conteudo = new CustomTransferObject();

                        File anexo = UploadHelper.retornaArquivoAnexoTemporario(nomeAnexoCorrente, idAnexo, responsavel);
                        byte[] fileContent = FileUtils.readFileToByteArray(anexo);
                        byte[] conteudoArquivoBase64 = Base64.getEncoder().encode(fileContent);

                        conteudo.setAttribute(Columns.ARQ_CONTEUDO, conteudoArquivoBase64);
                        conteudo.setAttribute(Columns.ARS_NOME, nomeAnexoCorrente);

                        conteudos.add(conteudo);
                    } catch (IOException e) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ARQ_CONTEUDO, conteudos);

                arquivoController.createArquivoRegistroServidor(rseCodigo, tipoArquivo, criterio, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.anexo.registro.servidor.sucesso", responsavel));
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
        return iniciar(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=download" })
    public void download(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token no dowload mas não salva um novo porque invalida o botão cancelar da interface que ainda utilizará o mesmo token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            }

            String arqCodigo = request.getParameter("arqCodigo");
            String rseCodigo = request.getParameter("rseCodigo");
            TransferObject arquivoRse = arquivoController.findArquivoResgistroServidorServidor(arqCodigo, rseCodigo, responsavel);
            String arqConteudo = (String) arquivoRse.getAttribute(Columns.ARQ_CONTEUDO);

            try {
                LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
                log.setArquivo(arqCodigo);
                log.setRegistroServidor(rseCodigo);
                log.write();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }

            byte[] conteudoArquivoBase64 = Base64.getDecoder().decode(arqConteudo);

            ParamSist ps = ParamSist.getInstance();
            String path = ParamSist.getDiretorioRaizArquivos() + "anexo" + File.separatorChar + "tmpAnexoRegistroServidor" + File.separatorChar + responsavel.getUsuCodigo();
            String fileName = (String) arquivoRse.getAttribute(Columns.ARS_NOME);

            File diretorioDefinitivo = new File(path);
            if (!diretorioDefinitivo.exists() && !diretorioDefinitivo.mkdirs()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.criar.diretorio.arquivo", responsavel));
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.criar.diretorio.arquivo", responsavel));
            }

            String filepath = path + File.separatorChar + fileName;
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(conteudoArquivoBase64);
            fos.close();

            File arquivo = new File(filepath);
            long tamanhoArquivoBytes = arquivo.length();

            Set<MimeType> mimeCollection = FileHelper.detectContentType(arquivo);
            String contentType = mimeCollection != null && !mimeCollection.isEmpty() ? mimeCollection.toArray()[0].toString() : "application/pdf";

            response.setContentType(contentType);
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

            arquivo.delete();

        } catch (IOException | ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
    }

    @RequestMapping(params = { "acao=somenteDownload" })
    public void somenteDownload(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            if(!responsavel.isCseSupOrg() || !responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String arqCodigo = request.getParameter("arqCodigo");
            String rseCodigo = request.getParameter("rseCodigo");
            arquivoController.removeArquivoRegistroServidor(arqCodigo, rseCodigo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.anexo.registro.servidor.remover.sucesso", responsavel));

            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
            return iniciar(rseCodigo, request, response, session, model);

        } catch (ArquivoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
