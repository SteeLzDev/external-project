package com.zetra.econsig.web.controller.termouso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: VisualizarSobreWebController</p>
 * <p>Description: Controlador Web para o caso de uso Visualizar Faq.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */
@Controller
public class VisualizarTermoUsoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarTermoUsoWebController.class);

    @Autowired
    private ArquivoController arquivoController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(value = { "/v3/visualizarTermoUso" }, method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException, UsuarioControllerException, ConsignanteControllerException, ViewHelperException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso.titulo", responsavel));

        File arqTermoDeUso = null;
        String absolutePath = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "termo_de_uso";
        String nomeArquivo = "cse.msg";
        if (responsavel.isOrg()) {
            nomeArquivo = "org.msg";
        } else if (responsavel.isSer()) {
            nomeArquivo = "ser.msg";
        } else if (responsavel.isCsa()) {
            nomeArquivo = "csa.msg";
        } else if (responsavel.isCor()) {
            nomeArquivo = "cor.msg";
        } else if (responsavel.isSup()) {
            nomeArquivo = "sup.msg";
        }
        arqTermoDeUso = new File(absolutePath, nomeArquivo);
        if (arqTermoDeUso == null || !arqTermoDeUso.exists()) {
            nomeArquivo = "geral.msg";
            arqTermoDeUso = new File(absolutePath, nomeArquivo);
            if (!arqTermoDeUso.exists()) {
                arqTermoDeUso = null;
            }
        }

        if (arqTermoDeUso == null) {
            try {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.termo.de.uso.nao.encontrado", responsavel));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean aceiteValido = false;
        java.util.Date dataUltimaAceitacao = null;
        java.util.Date dataTermoDeUso = null;
        String chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_CSE;
        if (responsavel.isOrg()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_ORG;
        } else if (responsavel.isSer()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_SER;
        } else if (responsavel.isCsa()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_CSA;
        } else if (responsavel.isCor()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_COR;
        } else if (responsavel.isSup()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_SUP;
        }

        boolean usuAutorizaEmailMarketing = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel).getUsuAutorizaEmailMarketing().equals(CodedValues.TPC_SIM);

        Object paramAceitacaoTermoDeUso = ParamSist.getInstance().getParam(chaveAceitacaoTermoDeUso, responsavel);
        if (!TextHelper.isNull(paramAceitacaoTermoDeUso)) {
            try {
                dataTermoDeUso = DateHelper.parse(paramAceitacaoTermoDeUso.toString(), "yyyy-MM-dd");
                List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO);

                CustomTransferObject filtro = new CustomTransferObject();
                filtro.setAttribute("tocCodigos", tocCodigos);
                filtro.setAttribute(Columns.OUS_USU_CODIGO, responsavel.getUsuCodigo());

                List<TransferObject> ocorrencias = usuarioController.lstOcorrenciaUsuario(filtro, -1, -1, responsavel);
                if (!ocorrencias.isEmpty()) {
                    dataUltimaAceitacao = (java.util.Date) ocorrencias.get(0).getAttribute(Columns.OUS_DATA);
                    if (dataUltimaAceitacao.compareTo(dataTermoDeUso) > 0) {
                        aceiteValido = true;
                        session.removeAttribute("AceitarTermoDeUso");
                    }
                }

            } catch (UsuarioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } catch (java.text.ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        ConsignanteTransferObject cse = null;
        OrgaoTransferObject org = null;
        EstabelecimentoTransferObject est = null;

        if (responsavel.isSer() || responsavel.isOrg()) {
            cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            est = consignanteController.findEstabelecimento(responsavel.getEstCodigo(), responsavel);
            org = consignanteController.findOrgao(responsavel.getOrgCodigo(), responsavel);
        } else {
            cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        }

        String msg = FileHelper.readAll(arqTermoDeUso.getAbsolutePath());
        msg = FileHelper.substituirDados(msg, cse, org, est);
        msg = msg.replaceAll("\n", "<br>");

        boolean exibeCheckEmailMarketingTermoUso = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CHECK_EMAIL_MARKETING_TERMO_DE_USO, responsavel);

        model.addAttribute("msg", msg);
        model.addAttribute("dataTermoDeUso", dataTermoDeUso);
        model.addAttribute("dataUltimaAceitacao", dataUltimaAceitacao);
        model.addAttribute("aceiteValido", aceiteValido);
        model.addAttribute("usuAutorizaEmailMarketing", usuAutorizaEmailMarketing);
        model.addAttribute("exibeCheckEmailMarketingTermoUso", exibeCheckEmailMarketingTermoUso);

        if (ParamSist.paramEquals(CodedValues.TPC_OMITIR_SERVIDORES_SEM_ACEITE_TERMO_DE_USO_PARA_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isSer()) {
            model.addAttribute("exibeUploadAutorizacaoAcessoDados", Boolean.TRUE);
        }

        try {
            model.addAttribute("dataUltimaAtualizacaoSistema", consignanteController.dataUltimaAtualizacaoSistema());
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/visualizarTermoUso/visualizarTermoUso", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/aceitarTermoUso" }, method = { RequestMethod.POST }, params = { "acao=aceitar" })
    public String aceitar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            if (ParamSist.paramEquals(CodedValues.TPC_OMITIR_SERVIDORES_SEM_ACEITE_TERMO_DE_USO_PARA_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isSer()) {
                try {
                    // Verifica se o anexo de autorização de dados a dados foi enviado
                    String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");
                    String tipoArquivo = TipoArquivoEnum.ARQUIVO_ANEXO_DOCUMENTO_REGISTRO_SERVIDOR.getCodigo();
                    String idAnexo = session.getId();

                    if (TextHelper.isNull(nomeAnexo)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.anexo.registro.servidor.autorizacao.acesso.dados.nao.encontrado", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    TransferObject conteudo = new CustomTransferObject();

                    File anexo = UploadHelper.retornaArquivoAnexoTemporario(nomeAnexo, idAnexo, responsavel);
                    byte[] fileContent = FileUtils.readFileToByteArray(anexo);
                    byte[] conteudoArquivoBase64 = Base64.getEncoder().encode(fileContent);

                    conteudo.setAttribute(Columns.ARQ_CONTEUDO, conteudoArquivoBase64);
                    conteudo.setAttribute(Columns.ARS_NOME, nomeAnexo);

                    arquivoController.createArquivoRegistroServidor(responsavel.getRseCodigo(), tipoArquivo, conteudo, responsavel);
                } catch (IOException e) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Cria ocorrência de inclusão de usuário
            CustomTransferObject ocorrencia = new CustomTransferObject();
            ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, responsavel.getUsuCodigo());
            ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, CodedValues.TOC_ACEITACAO_TERMO_DE_USO);
            ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, responsavel.getUsuCodigo());
            ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.aceitacao.termo.de.uso", responsavel));
            ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());

            usuarioController.createOcorrenciaUsuario(ocorrencia, responsavel);

            String usuAutoriza = request.getParameter("usuAutorizaEmailMarketing") != null ? request.getParameter("usuAutorizaEmailMarketing") : "N";
            UsuarioTransferObject usuario = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
            usuarioController.atualizarUsuarioAutorizacaoEmailMarketing(usuario, usuAutoriza, responsavel);

            session.removeAttribute("AceitarTermoDeUso");

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.termo.de.uso.aceito.com.sucesso", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(value = { "/v3/redirecionarTermoUsu" })
    public String redirecionarTermoUsu(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException {
        session.setAttribute("termo_usu", Boolean.TRUE);
        return "forward:/v3/autenticarUsuario";
    }

    @RequestMapping(value = { "/v3/redirecionarTermoSer" })
    public String redirecionarTermoSer(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException {
        session.setAttribute("termo_usu", Boolean.TRUE);
        return "forward:/v3/autenticar";
    }

    /**
     * DESENV-13251: Método criado para registrar na tabela tb_usuario, coluna: USU_AUTORIZA_EMAIL_MARKETING, se o usuário autorizou ou não o recebimento de mensagens externas por e-mail.
     * Considera-se como externas as mensagens que não são enviadas pelo eConsig.
     */
    @RequestMapping(value = { "/v3/atualizarAutorizacaoEmailMarketing" }, method = { RequestMethod.POST }, params = { "acao=atualizar" })
    public String atualizarAutorizacaoEmailMarketing(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws HQueryException, UsuarioControllerException, ViewHelperException, ConsignanteControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String usuAutoriza = JspHelper.verificaVarQryStr(request, "usuAutorizaEmailMarketing");

        try {
            UsuarioTransferObject usuario = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
            usuarioController.atualizarUsuarioAutorizacaoEmailMarketing(usuario, usuAutoriza, responsavel);
        } catch (UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.termo.de.uso.usuario.autoriza.email.marketing.atualizado", responsavel));

        Boolean termoUsu = (Boolean) session.getAttribute("termo_usu");

        if (termoUsu != null && (Boolean) session.getAttribute("termo_usu")) {
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(request, response, session, model);
    }
}
