package com.zetra.econsig.web.controller.relatorios;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Subrelatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.SubrelatorioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SubrelatorioWebController</p>
 * <p>Description: Webcontroller de subrelatorio</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarSubrelatorio" })
public class SubrelatorioWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarRelatoriosWebController.class);

    @Autowired
    private SubrelatorioController subrelatorioController;

    @Autowired
    private RelatorioController relatorioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        List<TransferObject> subrelatorios = null;
        final CustomTransferObject criterio = new CustomTransferObject();
        final String relCodigo = JspHelper.verificaVarQryStr(request, "relCodigo");
        criterio.setAttribute(Columns.SRE_REL_CODIGO, relCodigo);

        final int offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ? Integer.parseInt(request.getParameter("offset")) : 0;
        final int total = subrelatorioController.listarSubrelatorio(criterio, -1, -1, responsavel).size();
        final int size = JspHelper.LIMITE;

        subrelatorios = subrelatorioController.listarSubrelatorio(criterio, offset, size, responsavel);
        final TransferObject relatorio  = relatorioController.findRelEditavel(relCodigo, responsavel);
        final String tituloRelatorio = (String) relatorio.getAttribute(Columns.REL_TITULO);

        configurarPaginador("../v3/editarSubrelatorio?acao=iniciar&relCodigo=" + relCodigo, "rotulo.subrelatorio.lista", total, size, null, false, request, model);

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.subrelatorio.editavel", responsavel));
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("tituloRelatorio", tituloRelatorio);
        model.addAttribute("subrelatorios", subrelatorios);
        model.addAttribute("relCodigo", relCodigo);
        model.addAttribute("offset", offset);

        return viewRedirect("jsp/editarRelatorios/listarSubrelatorios", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=iniciarEdicao" })
    public String iniciarEdicao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        final String relCodigo = JspHelper.verificaVarQryStr(request, "relCodigo");
        Subrelatorio subrelatorio = null;

        if (tipo.equals("editar")) {
            final String sreCodigo = JspHelper.verificaVarQryStr(request, "sreCodigo");
            try {
                if (!TextHelper.isNull(relCodigo) && !TextHelper.isNull(sreCodigo)) {
                    subrelatorio = subrelatorioController.buscaSubrelatorioEditavel(sreCodigo, relCodigo);
                    model.addAttribute("sreCodigo", sreCodigo);
                }
            } catch (final FindException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.subrelatorio.nao.encontrado", responsavel), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.subrelatorio.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        final Map<String, TransferObject> relatorioFiltros = relatorioController.findRelatorioFiltro(relCodigo, responsavel);

        model.addAttribute("relatorioFiltros", relatorioFiltros);
        model.addAttribute("relCodigo", relCodigo);
        model.addAttribute("subrelatorio", subrelatorio);
        model.addAttribute("tipo", tipo);

        return viewRedirect("jsp/editarRelatorios/editarSubrelatorios", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        final UploadHelper uploadHelper = new UploadHelper();

        // Faz upload do template do relatório
        final ParamSist ps = ParamSist.getInstance();
        int maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 30;
        maxSize = maxSize * 1024 * 1024;

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
        } catch (final Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            final String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
        }

        final String relCodigo = JspHelper.verificaVarQryStr(request, "relCodigo");
        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        final String sreCodigo = uploadHelper.getValorCampoFormulario("sreCodigo");
        final String fonteDados = uploadHelper.getValorCampoFormulario("SRE_FONTE_DADOS");
        final String sreNomeParametro = uploadHelper.getValorCampoFormulario("SRE_NOME_PARAMETRO");
        final String sreTemplateSql = fonteDados.equals("N") ? uploadHelper.getValorCampoFormulario("SRE_TEMPLATE_SQL") : "";
        final boolean removerArquivo = !TextHelper.isNull(uploadHelper.getValorCampoFormulario("removeSubTemplatejasper")) ? uploadHelper.getValorCampoFormulario("removeSubTemplatejasper").equals(CodedValues.TPC_SIM) : false;

        // Nova verificação de tentativa de alteração do banco, caso seja uma tentativa forçada...
        final String[] requisicoes = new String[] { "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", "TRUNCATE" };
        for (int i = 0; i < requisicoes.length;) {
            final String requisicao = requisicoes[i++];
            if (sreTemplateSql.toUpperCase().indexOf(requisicao) > -1) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.palavra.reservada", responsavel, requisicao));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        final String idAnexo = session.getId();
        final String nomeAnexoSubrelatorio = uploadHelper.getValorCampoFormulario("FILE1");
        final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        File anexoSubrelatorio = null;

        if (!TextHelper.isNull(nomeAnexoSubrelatorio)) {
            anexoSubrelatorio = UploadHelper.retornaArquivoAnexoTemporario(nomeAnexoSubrelatorio, idAnexo, responsavel);
            final File diretorioDefinitivo = new File(diretorioRaizArquivos + File.separatorChar + ReportManager.JASPER_TEMPLATE_DIRECTORY);
            if (!diretorioDefinitivo.exists() && !diretorioDefinitivo.mkdirs()) {
                LOG.error("Não foi possível criar diretório destino para os arquivos de upload.");
                throw new ZetraException("mensagem.erroInternoSistema", responsavel);
            }
            Files.copy(Paths.get(anexoSubrelatorio.getAbsolutePath()), Paths.get(diretorioRaizArquivos + ReportManager.JASPER_TEMPLATE_DIRECTORY + nomeAnexoSubrelatorio), StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            if (tipo.equals("inserir")) {
                subrelatorioController.inserirSubrelatorio(relCodigo, nomeAnexoSubrelatorio, sreNomeParametro, sreTemplateSql, responsavel);
            } else if (tipo.equals("editar")) {
                subrelatorioController.editarSubrelatorio(sreCodigo, relCodigo, nomeAnexoSubrelatorio, sreNomeParametro, sreTemplateSql, removerArquivo, responsavel);
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.operacao.invalida", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Atualiza o cache de parâmetros
            JspHelper.limparCacheParametros();

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.relatorio.sucesso", responsavel));
            model.addAttribute("relCodigo", relCodigo);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String relCodigo = JspHelper.verificaVarQryStr(request, "relCodigo");
        final String sreCodigo = JspHelper.verificaVarQryStr(request, "sreCodigo");

        // Exclui o relatório
        try {
            subrelatorioController.removeSubrelatorioEditavel(sreCodigo, relCodigo);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.excluir.subrelatorio.sucesso", responsavel));
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        // Atualiza o cache de parâmetros
        JspHelper.limparCacheParametros();

        return iniciar(request, response, session, model);
    }
}
