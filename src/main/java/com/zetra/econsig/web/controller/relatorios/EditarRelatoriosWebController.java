package com.zetra.econsig.web.controller.relatorios;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: EditarRelatoriosWebController</p>
 * <p>Description: Controlador Web para o caso de uso EditarRelatorio.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: anderson.assis $
 * $Revision: 28565 $
 * $Date: 2020-05-13 16:23:03 -0200 (Qua, 13 mai 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarRelatorio" })
public class EditarRelatoriosWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarRelatoriosWebController.class);

    @Autowired
    private RelatorioController relatorioController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        List<TransferObject> relatorios = null;

        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        int offset = 0;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtro_tipo == 0) {
                criterio.setAttribute(Columns.REL_ATIVO, CodedValues.STS_INATIVO);
                // Desbloqueado
            } else if (filtro_tipo == 1) {
                criterio.setAttribute(Columns.REL_ATIVO, CodedValues.STS_ATIVO);
                // Outros
            } else if (!filtro.equals("") && filtro_tipo != -1) {
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.REL_CODIGO;
                        break;
                    case 3:
                        campo = Columns.REL_TITULO;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel) + ".");
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }

            offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ? Integer.parseInt(request.getParameter("offset")) : 0;
            int total = relatorioController.lstRelatorioCustomizado(criterio, -1, -1, responsavel).size();
            int size = JspHelper.LIMITE;

            relatorios = relatorioController.lstRelatorioCustomizado(criterio, offset, size, responsavel);

            configurarPaginador("../v3/editarRelatorio?acao=iniciar", "rotulo.listar.relatorio.titulo", total, size, null, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            relatorios = new ArrayList<>();
        }

        model.addAttribute("relatorios", relatorios);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("offset", offset);

        return viewRedirect("jsp/editarRelatorios/listarRelatoriosEditar", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=iniciarEdicao" })
    public String iniciarEdicao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String relCodigo = JspHelper.verificaVarQryStr(request, "REL_CODIGO");
        String tipo = request.getParameter("tipo");
        List<?> lstPapeis = null;
        List<?> papeis = null;
        Collection<?> filtrosRelatorio = null;
        try {
            papeis = usuarioController.lstPapel(responsavel);
        } catch (UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
        }

        Map<?, ?> relatorioFiltros = null;

        TransferObject relatorio = null;
        try {
            filtrosRelatorio = relatorioController.lstTipoFiltroRelatorioEditavel(responsavel);
            if (!TextHelper.isNull(relCodigo)) {
                relatorio = relatorioController.findRelEditavel(relCodigo, responsavel);
            }
        } catch (RelatorioControllerException ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.nao.encontrado", responsavel), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.nao.encontrado", responsavel));

            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (relatorio != null) {
            lstPapeis = (List<?>) relatorio.getAttribute("PAPEIS");
            relatorioFiltros = (Map<?, ?>) relatorio.getAttribute("FILTRO_RELATORIO");
        }

        model.addAttribute("relCodigo", relCodigo);
        model.addAttribute("relatorio", relatorio);
        model.addAttribute("tipo", tipo);
        model.addAttribute("lstPapeis", lstPapeis);
        model.addAttribute("papeis", papeis);
        model.addAttribute("relatorioFiltros", relatorioFiltros);
        model.addAttribute("filtrosRelatorio", filtrosRelatorio);

        return viewRedirect("jsp/editarRelatorios/editarRelatorios", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String relCodigo = JspHelper.verificaVarQryStr(request, "REL_CODIGO");

        try {
            // Faz upload do template do relatório
            ParamSist ps = ParamSist.getInstance();
            int maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 30;
            maxSize = maxSize * 1024 * 1024;

            UploadHelper uploadHelper = new UploadHelper();

            try {
                uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
            } catch (Throwable ex) {
                LOG.error(ex.getMessage(), ex);
                String msg = ex.getMessage();
                if (!TextHelper.isNull(msg)) {
                    session.setAttribute(CodedValues.MSG_ERRO, msg);
                }
            }

            relCodigo = uploadHelper.getValorCampoFormulario("REL_CODIGO");
            String relTitulo = uploadHelper.getValorCampoFormulario("REL_TITULO");
            String funDescricao = uploadHelper.getValorCampoFormulario("FUN_DESCRICAO");
            String itmDescricao = uploadHelper.getValorCampoFormulario("ITM_DESCRICAO");
            List<String> papCodigos = uploadHelper.getValoresCampoFormulario("PAP_CODIGO");
            String relTemplateSql = uploadHelper.getValorCampoFormulario("REL_TEMPLATE_SQL");
            String relAgendado = uploadHelper.getValorCampoFormulario("REL_AGENDADO");
            String relAgrupamento = uploadHelper.getValorCampoFormulario("REL_AGRUPAMENTO");
            String ordemAux = uploadHelper.getValorCampoFormulario("ORDENACAO_AUX");
            boolean removeTemplate = !TextHelper.isNull(uploadHelper.getValorCampoFormulario("REMOVE_TEMPLATE")) ? uploadHelper.getValorCampoFormulario("REMOVE_TEMPLATE").equals(CodedValues.TPC_SIM) : false;
            Map<String, Integer> ordenacao = null;
            Map<String, Map<String, String>> filtros = null;

            if (!TextHelper.isNull(ordemAux)) {
                String[] ordenacaoAux = ordemAux.split(",");
                ordenacao = new HashMap<>();
                for (int i = 0; i < ordenacaoAux.length;) {
                    ordenacao.put(ordenacaoAux[i], Integer.valueOf(++i));
                }
            }

            // Nova verificação de tentativa de alteração do banco, caso seja uma tentativa forçada...
            String[] requisicoes = { "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", "TRUNCATE" };
            for (int i = 0; i < requisicoes.length;) {
                String requisicao = requisicoes[i++];
                if (relTemplateSql.toUpperCase().indexOf(requisicao) > -1) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.palavra.reservada", responsavel, requisicao));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            List<String> f = uploadHelper.getValoresCampoFormulario("FILTRO");
            if (f != null && !f.isEmpty()) {
                filtros = new HashMap<>();
                Iterator<String> iteF = f.iterator();
                while (iteF.hasNext()) {
                    String[] valores = iteF.next().split(";");
                    String papCodigo = valores[0];
                    String tfrCodigo = valores[1];
                    String rfiExibe = valores[2];

                    if (!rfiExibe.equals(CodedValues.REL_FILTRO_NAO_EXISTENTE)) {
                        // <tfrCodigo, <papCodigo, rfiExibe>>
                        Map<String, String> papeis = filtros.get(tfrCodigo);
                        if (papeis == null) {
                            papeis = new HashMap<>();
                            filtros.put(tfrCodigo, papeis);
                        }
                        papeis.put(papCodigo, rfiExibe);
                    }
                }
            }

            if (TextHelper.isNull(relCodigo) || TextHelper.isNull(relTitulo) || TextHelper.isNull(relTemplateSql)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.informacoes.ausentes", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            if (papCodigos == null || papCodigos.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.relatorio.papel", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String idAnexo = session.getId();
            String nomeAnexoRelatorio = uploadHelper.getValorCampoFormulario("FILE1");
            String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
            File anexoRelatorio = null;
            File relatorioSalvo = null;

            if (!TextHelper.isNull(nomeAnexoRelatorio)) {
                anexoRelatorio = UploadHelper.retornaArquivoAnexoTemporario(nomeAnexoRelatorio, idAnexo, responsavel);
                File diretorioDefinitivo = new File(diretorioRaizArquivos + File.separatorChar + ReportManager.JASPER_TEMPLATE_DIRECTORY);
                if (!diretorioDefinitivo.exists() && !diretorioDefinitivo.mkdirs()) {
                    LOG.error("Não foi possível criar diretório destino para os arquivos de upload.");
                    throw new ZetraException("mensagem.erroInternoSistema", responsavel);
                }
                Files.copy(Paths.get(anexoRelatorio.getAbsolutePath()), Paths.get(diretorioRaizArquivos + ReportManager.JASPER_TEMPLATE_DIRECTORY + relCodigo + ".jrxml"), StandardCopyOption.REPLACE_EXISTING);
                relatorioSalvo = new File(diretorioRaizArquivos + ReportManager.JASPER_TEMPLATE_DIRECTORY + relCodigo + ".jrxml");
            }

            String relTemplateJasper = (relatorioSalvo != null) ? relatorioSalvo.getName() : (removeTemplate ? CodedValues.TEMPLATE_REL_EDITAVEL_JASPER : null);

            String tipo = uploadHelper.getValorCampoFormulario("tipo");
            if (tipo.equals("inserir")) {
                relatorioController.insereRelEditavel(relCodigo, relTitulo, funDescricao, itmDescricao, papCodigos, filtros, ordenacao, relTemplateSql, relAgrupamento, relTemplateJasper, relAgendado, responsavel);
            } else if (tipo.equals("editar")) {
                TransferObject funcaoTO = new CustomTransferObject();
                funcaoTO.setAttribute(Columns.REL_CODIGO, relCodigo);
                funcaoTO.setAttribute(Columns.REL_TITULO, relTitulo);
                funcaoTO.setAttribute(Columns.FUN_DESCRICAO, funDescricao);
                funcaoTO.setAttribute(Columns.ITM_DESCRICAO, itmDescricao);
                funcaoTO.setAttribute(Columns.REL_TEMPLATE_SQL, relTemplateSql);
                funcaoTO.setAttribute(Columns.REL_AGENDADO, relAgendado);
                funcaoTO.setAttribute(Columns.REL_TEMPLATE_JASPER, relTemplateJasper);
                funcaoTO.setAttribute(Columns.REL_AGRUPAMENTO, relAgrupamento);
                funcaoTO.setAttribute(Columns.REL_FUN_CODIGO, uploadHelper.getValorCampoFormulario("FUN_CODIGO"));
                funcaoTO.setAttribute(Columns.REL_TAG_CODIGO, uploadHelper.getValorCampoFormulario("TAG_CODIGO"));
                funcaoTO.setAttribute(Columns.ITM_CODIGO, uploadHelper.getValorCampoFormulario("ITM_CODIGO"));

                relatorioController.edtRelEditavel(funcaoTO, papCodigos, filtros, ordenacao, responsavel);

            } else {
                if (relatorioSalvo != null && relatorioSalvo.exists() && relatorioSalvo.isFile() && !relatorioSalvo.delete()) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.impossivel.remover.template.editavel", responsavel) + ": '" + relCodigo + "'.");
                }

                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.operacao.invalida", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Atualiza o cache de parâmetros
            JspHelper.limparCacheParametros();

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.relatorio.sucesso", responsavel));

            List<?> lstPapeis = null;

            TransferObject relatorio = null;
            try {
                if (!TextHelper.isNull(relCodigo)) {
                    relatorio = relatorioController.findRelEditavel(relCodigo, responsavel);
                }
            } catch (Exception ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.nao.encontrado", responsavel), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<?> papeis = null;
            Collection<?> filtrosRelatorio = null;
            try {
                papeis = usuarioController.lstPapel(responsavel);
            } catch (UsuarioControllerException e) {
                LOG.error(e.getMessage(), e);
            }

            Map<?, ?> relatorioFiltros = null;
            filtrosRelatorio = relatorioController.lstTipoFiltroRelatorioEditavel(responsavel);
            relatorioFiltros = (Map<?, ?>) relatorio.getAttribute("FILTRO_RELATORIO");
            lstPapeis = (List<?>) relatorio.getAttribute("PAPEIS");

            model.addAttribute("relCodigo", relCodigo);
            model.addAttribute("relTitulo", relTitulo);
            model.addAttribute("funDescricao", funDescricao);
            model.addAttribute("itmDescricao", itmDescricao);
            model.addAttribute("papCodigos", papCodigos);
            model.addAttribute("filtros", filtros);
            model.addAttribute("relTemplateSql", relTemplateSql);
            model.addAttribute("relAgendado", relAgendado);
            model.addAttribute("relAgrupamento", relAgrupamento);
            model.addAttribute("ordenacao", ordenacao);
            model.addAttribute("relatorio", relatorio);
            model.addAttribute("tipo", "editar");
            model.addAttribute("lstPapeis", lstPapeis);
            model.addAttribute("papeis", papeis);
            model.addAttribute("relatorioFiltros", relatorioFiltros);
            model.addAttribute("filtrosRelatorio", filtrosRelatorio);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/editarRelatorios/editarRelatorios", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String relCodigo = request.getParameter("codigo");

        // Exclui o relatório
        try {
            relatorioController.removeRelEditavel(relCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.excluir.relatorio.sucesso", responsavel));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        // Atualiza o cache de parâmetros
        JspHelper.limparCacheParametros();

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=bloquear" })
    public String bloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String relCodigo = request.getParameter("codigo");

        // Bloqueia/Desbloqueia o relatório

        try {
            String ativo = request.getParameter("status");
            ativo = ativo.equals(CodedValues.STS_ATIVO.toString()) ? CodedValues.STS_INATIVO.toString() : CodedValues.STS_ATIVO.toString();

            relatorioController.alterarStatusRelatorio(relCodigo, Short.valueOf(ativo), responsavel);
            session.setAttribute(CodedValues.MSG_INFO, (ativo.equals(CodedValues.STS_ATIVO.toString()) ? ApplicationResourcesHelper.getMessage("mensagem.desbloquear.relatorio.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.bloquear.relatorio.sucesso", responsavel)));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        return iniciar(request, response, session, model);
    }

}
