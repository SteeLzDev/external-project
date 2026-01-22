package com.zetra.econsig.web.controller.servidor;

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
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
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
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import eu.medsea.mimeutil.MimeType;

/**
 * <p>Title: CadastrarDispensaValidacaoDigitalServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Cadastrar Dispensa Validacao Digital Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cadastrarDispensaValidacaoDigitalServidor" })
public class CadastrarDispensaValidacaoDigitalServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CadastrarDispensaValidacaoDigitalServidorWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ArquivoController arquivoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);

            List<String> tarCodigos = new ArrayList<>();
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_DISPENSA_VALIDACAO_DIGITAL_SER.getCodigo());
            List<TransferObject> arquivos = arquivoController.listArquivoServidor(servidor.getSerCodigo(), tarCodigos, responsavel);

            model.addAttribute("servidor", servidor);
            model.addAttribute("arquivos", arquivos);

        } catch (ArquivoControllerException | ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/editarServidor/cadastrarDispensaValidacaoDigitalServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "SER_CODIGO", required = true, defaultValue = "") String serCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);
            ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

            // verifica anexo obrigatorio
            String dispensaDigital = JspHelper.verificaVarQryStr(request, "dispensaDigital");
            String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");
            String idAnexo = session.getId();
            String[] anexosName = !TextHelper.isNull(nomeAnexo) ? nomeAnexo.split(";") : null;

            List<TransferObject> conteudos = new ArrayList<>();
            // Se informou anexos
            if (anexosName != null) {
                for (String nomeAnexoCorrente : anexosName) {
                    try {
                        TransferObject conteudo = new CustomTransferObject();

                        File anexo = UploadHelper.retornaArquivoAnexoTemporario(nomeAnexoCorrente, idAnexo, responsavel);
                        byte[] fileContent = FileUtils.readFileToByteArray(anexo);
                        byte[] conteudoArquivoBase64 = Base64.getEncoder().encode(fileContent);

                        conteudo.setAttribute(Columns.ARQ_CONTEUDO, conteudoArquivoBase64);
                        conteudo.setAttribute(Columns.ASE_NOME, nomeAnexoCorrente);

                        conteudos.add(conteudo);
                    } catch (IOException e) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
            }

            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.SER_CODIGO, servidor.getSerCodigo());
            criterio.setAttribute(Columns.ARQ_CONTEUDO, conteudos);
            criterio.setAttribute(Columns.SER_DISPENSA_DIGITAL, dispensaDigital);

            TransferObject tipoMotivoOperacao = null;
            if (request.getParameter("TMO_CODIGO") != null) {
                tipoMotivoOperacao = new CustomTransferObject();
                tipoMotivoOperacao.setAttribute(Columns.SER_CODIGO, serCodigo);
                tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                tipoMotivoOperacao.setAttribute(Columns.OCS_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
            }

            servidorController.cadastrarDispensaDigitalServidor(criterio, tipoMotivoOperacao, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.alterado.sucesso", responsavel));

            // Voltar para edição de servidor
            paramSession.halfBack();

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
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
            String serCodigo = request.getParameter("serCodigo");
            TransferObject arquivoServidor = arquivoController.findArquivoServidor(arqCodigo, serCodigo, responsavel);
            String arqConteudo = arquivoServidor.getAttribute(Columns.ARQ_CONTEUDO).toString();

            // Gera log de download de arquivo
            try {
                LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
                log.setArquivo(arqCodigo);
                log.setServidor(serCodigo);
                log.write();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }

            byte[] conteudoArquivoBase64 = Base64.getDecoder().decode(arqConteudo);

            ParamSist ps = ParamSist.getInstance();
            String path = ParamSist.getDiretorioRaizArquivos() + "anexo" + File.separatorChar + "tmpDispensaDigital" + File.separatorChar + responsavel.getUsuCodigo();
            String fileName = arquivoServidor.getAttribute(Columns.ASE_NOME).toString();

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
            String contentType = mimeCollection != null && mimeCollection.size() > 0 ? mimeCollection.toArray()[0].toString() : "application/pdf";

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

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String arqCodigo = request.getParameter("arqCodigo");
            String serCodigo = request.getParameter("serCodigo");
            arquivoController.removeArquivoServidor(arqCodigo, serCodigo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.boleto.servidor.removido.sucesso", responsavel));

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (ArquivoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
