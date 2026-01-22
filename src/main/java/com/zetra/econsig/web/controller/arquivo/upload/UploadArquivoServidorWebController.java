package com.zetra.econsig.web.controller.arquivo.upload;

import java.io.File;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.ImagemServidor;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/uploadArquivoServidor"})
public class UploadArquivoServidorWebController extends AbstractWebController {

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadArquivoServidorWebController.class);

    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UploadControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            SynchronizerToken.saveToken(request);


            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            String linkVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
            String action = "../v3/uploadArquivoServidor?acao=salvar&RSE_CODIGO=" + rseCodigo + "&" + SynchronizerToken.generateToken4URL(request);

            model.addAttribute("linkVoltar", linkVoltar);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("action", action);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }
        return viewRedirect("jsp/uploadArquivo/uploadArquivoServidor", request, session, model, responsavel);

    }

    @RequestMapping(params = {"acao=salvar"})
    public String salvarImagem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UploadControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            SynchronizerToken.saveToken(request);
            ParamSist ps = ParamSist.getInstance();
            String tipo = JspHelper.verificaVarQryStr(request, "TIPO");

            if (tipo.equals(CodedValues.PSC_BOOLEANO_NAO)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.imagem.tipo.errado", responsavel));
            } else {
                String pathRaiz = ParamSist.getDiretorioRaizArquivos();
                if (pathRaiz == null) {
                    throw new ZetraException("mensagem.erro.configuracao.diretorio.integracao.invalida", responsavel);
                }

                File arquivoSalvo;
                int maxSize = 0;


                maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVO_FOTO_SERVIDOR, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVO_FOTO_SERVIDOR, responsavel).toString()) : 0;
                if (maxSize == 0){
                    throw new ZetraException("mensagem.erro.configuracao.tamanho.arquivo.imagem.servidor", responsavel);
                }
                maxSize = maxSize * 1024 * 1024;

                String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
                CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                String cpfServidor = (String) servidor.getAttribute(Columns.SER_CPF);
                String subDiretorio = (String) ps.getParam(CodedValues.TPC_DIR_IMG_SERVIDORES, responsavel);
                UploadHelper uploadHelper = new UploadHelper();
                String salved = null;
                boolean updated = false;


                CustomTransferObject existImagem = pesquisarServidorController.getImagemServidor(cpfServidor, responsavel);

                if (existImagem == null) {
                    uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
                    arquivoSalvo = uploadHelper.salvarArquivo(subDiretorio);

                    ImagemServidor img = new ImagemServidor();
                    img.setCpf(cpfServidor);
                    img.setNomeArquivo(arquivoSalvo.getName());
                    salved = pesquisarServidorController.salvarImagemServidor(img, responsavel);
                } else {
                    pathRaiz += File.separatorChar + subDiretorio + File.separatorChar + existImagem.getAttribute(Columns.IMS_NOME_ARQUIVO);
                    File oldFile = new File(pathRaiz);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }

                    uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
                    arquivoSalvo = uploadHelper.salvarArquivo(subDiretorio);

                    ImagemServidor img = new ImagemServidor();
                    img.setCpf(cpfServidor);
                    img.setNomeArquivo(arquivoSalvo.getName());
                    updated = pesquisarServidorController.updateImagemServidor(img, responsavel);

                }

                if (updated || !Objects.requireNonNull(salved).isEmpty()) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.imagem.salva.sucesso", responsavel));
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.imagem.salva.falhou", responsavel));
                }
            }
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.upload.erro.interno", responsavel) + " " + ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
