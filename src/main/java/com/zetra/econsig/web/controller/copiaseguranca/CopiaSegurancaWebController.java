package com.zetra.econsig.web.controller.copiaseguranca;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.ArquivoDTO;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.CopiaSegurancaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaCopiaSegurancaExportar;
import com.zetra.econsig.job.process.ProcessaCopiaSegurancaImportar;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.persistence.entity.Agendamento;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusAgendamentoEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: CopiaSegurancaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Cópia de Segurança (Backup).</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/copiaSeguranca" })
public class CopiaSegurancaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CopiaSegurancaWebController.class);

    private static final String CHAVE_PROCESSO = "COPIASEGURANCA";
        private static final String CHAVE_SESSAO_MAP_ARQUIVOS = "indiceArquivosCopiaSeguranca";

    @Autowired
    private AgendamentoController agendamentoController;

    @RequestMapping(params = { "acao=iniciar" }, method = {RequestMethod.GET, RequestMethod.POST})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        // Remove da sessão mapa de índices dos arquivos disponíveis
        session.removeAttribute(CHAVE_SESSAO_MAP_ARQUIVOS);

        Processo processo = ControladorProcessos.getInstance().getProcesso(CHAVE_PROCESSO);
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSO, session);

        String formAction = SynchronizerToken.updateTokenInURL("../v3/copiaSeguranca?acao=agendar", request);
        String formActionExportar = SynchronizerToken.updateTokenInURL("../v3/agendarRelatorio", request);

        if (processo != null && !temProcessoRodando) {
            String linkRetorno = JspHelper.makeURL("../v3/copiaSeguranca?acao=iniciar", new HashMap<>());
            boolean erro = !TextHelper.isNull(session.getAttribute(CodedValues.MSG_ERRO));
            if (!erro) {
                // Seta mensagem de sucesso na geração do relatório
                session.setAttribute(CodedValues.MSG_INFO, processo.getMensagem());
            }
            // Redireciona para a página de cópia de segurança repassando os filtros para que fiquem preenchidos
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(linkRetorno, request)));
            return "jsp/redirecionador/redirecionar";

        } else if (temProcessoRodando) {
            model.addAttribute("temProcessoRodando", temProcessoRodando);
            return viewRedirect("jsp/copiaSeguranca/listarBackup", request, session, model, responsavel);
        }

        File diretorio = CopiaSegurancaHelper.getCaminhoArquivos(responsavel);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        ArrayList<Object> arquivos = null;
        Object[] temp = diretorio.listFiles(arq -> arq.getName().toLowerCase().endsWith(".zip") ||
                                                   arq.getName().toLowerCase().endsWith(".txt"));
        if (temp != null) {
            arquivos = new ArrayList<>();
            arquivos.addAll(Arrays.asList(temp));
        }
        if (!temProcessoRodando) {
            Collections.sort(arquivos, (Object o1, Object o2) -> {
                Long d1 = Long.valueOf(((File) o1).lastModified());
                Long d2 = Long.valueOf(((File) o2).lastModified());
                return d2.compareTo(d1);
            });
        }

        int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {
            //
        }

        int offset2 = 0;
        try {
            offset2 = Integer.parseInt(request.getParameter("offset2"));
        } catch (Exception ex) {
            //
        }

        int total = arquivos.size();

        // Monta lista de parâmetros através dos parâmetros de request
        Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("offset");
        params.remove("back");
        params.remove("linkRet");
        params.remove("linkRet64");
        params.remove("eConsig.page.token");
        params.remove("_skip_history_");
        params.remove("pager");
        params.remove("acao");

        List<String> requestParams = new ArrayList<>(params);

        String linkListagem = "../v3/copiaSeguranca?acao=iniciar";

        configurarPaginador(linkListagem, "rotulo.paginacao.titulo.copia.seguranca", total, size, requestParams, false, request, model);
        String linkPaginacaoAgendamento = linkListagem + "&indice=2";
        if (request.getQueryString() != null && !request.getQueryString().equals("")) {
            linkPaginacaoAgendamento += "&" + request.getQueryString();
        }
        linkPaginacaoAgendamento = SynchronizerToken.updateTokenInURL(linkPaginacaoAgendamento, request);

        List<ArquivoDTO> arquivosDTO = new ArrayList<>();

        if (arquivos != null && !arquivos.isEmpty()) {
            int i = 0;
            int j = offset == -1 ? ((arquivos.size() % size) == 0 ? (arquivos.size() - size) : arquivos.size() - (arquivos.size() % size)) : offset;
            while (arquivos.size() > j && i < size) {
                File arquivo = (File) arquivos.get(j);
                String tam = "";
                if (arquivo.length() > 1024.00) {
                    tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                } else {
                    tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                }
                String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                String nome = arquivo.getPath().substring(diretorio.getPath().length() + 1);
                String formato = "";
                if (nome.toLowerCase().endsWith(".txt")) {
                    formato = "text.gif";
                } else if (nome.toLowerCase().endsWith(".zip")) {
                    formato = "zip.gif";
                }
                j++;
                i++;
                arquivosDTO.add(new ArquivoDTO(arquivo.getName(), nome, formato, data, tam, null, null));
            }
        }

        try {
            model.addAttribute("listaTiposAgendamento", agendamentoController.lstTipoAgendamento(TipoAgendamentoEnum.getTipoAgendamentoRelatorio(), responsavel));
            Map<String, String> descricoes = (Map<String, String>) model.asMap().get("descricoes");
            if (descricoes == null) {
                descricoes = new HashMap<>();
                model.addAttribute("descricoes", descricoes);
            }
            descricoes.put("/WEB-INF/relatorios/campos_relatorio/campo_data_execucao_v4.jsp", ApplicationResourcesHelper.getMessage("rotulo.copia.seguranca.data.prevista", responsavel));
            descricoes.put("/WEB-INF/relatorios/campos_relatorio/campo_tipo_agendamento_v4.jsp", "*" + ApplicationResourcesHelper.getMessage("rotulo.copia.seguranca.tipo.agendamento", responsavel));
            descricoes.put("/WEB-INF/relatorios/campos_relatorio/campo_periodicidade_v4.jsp", "*" + ApplicationResourcesHelper.getMessage("rotulo.copia.seguranca.periodicidade", responsavel));

            List<String> sagCodigos = new ArrayList<>();
            sagCodigos.add(StatusAgendamentoEnum.AGUARDANDO_EXECUCAO.getCodigo());
            List<TransferObject> lstAgendamentos = agendamentoController.lstAgendamentos(null, sagCodigos, null, CopiaSegurancaHelper.CLASSE_AGENDAMENTO, null, null, null, null, offset2, 20, responsavel);
            List<Agendamento> agendamentos = new ArrayList<>();
            for (TransferObject agendamentoTO : lstAgendamentos) {
                Agendamento agendamento = new Agendamento();
                agendamento.setAgdCodigo((String)agendamentoTO.getAttribute(Columns.AGD_CODIGO));
                agendamento.setAgdDataPrevista((Date)agendamentoTO.getAttribute(Columns.AGD_DATA_PREVISTA));
                agendamento.setAgdDescricao((String)agendamentoTO.getAttribute(Columns.TAG_DESCRICAO));
                agendamentos.add(agendamento);
            }
            model.addAttribute("agendamentos", agendamentos);
        } catch (AgendamentoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("formAction", formAction);
        model.addAttribute("formActionExportar", formActionExportar);
        // Lista de cópias de segurança geradas
        model.addAttribute("arquivosDTO", arquivosDTO);
        model.addAttribute("offset", offset);

        model.addAttribute("linkPaginacaoAgendamento", linkPaginacaoAgendamento);
        model.addAttribute("offset2", offset2);

        if (!arquivosDTO.isEmpty()) {
            // Cria mapa de índice por nome de arquivo, para que na interface o usuário escolha 
            // o índice, ao invés do nome do arquivo, evitando vulnerabilidade de path traversal
            Map<Integer, String> indiceArquivos = new HashMap<>();
            for (int i = 0; i < arquivosDTO.size(); i++) {
                indiceArquivos.put(i, arquivosDTO.get(i).getNome());
            }
            session.setAttribute(CHAVE_SESSAO_MAP_ARQUIVOS, indiceArquivos);
        }

        return viewRedirect("jsp/copiaSeguranca/listarBackup", request, session, model, responsavel);
    }

    @PostMapping(params = { "acao=agendar" })
    public String agendar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            ParamSession paramSession = ParamSession.getParamSession(session);
            SynchronizerToken.saveToken(request);

            // Agenda o backup
            TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.AGD_TAG_CODIGO, request.getParameter("tagCodigo"));
            to.setAttribute(Columns.AGD_DATA_PREVISTA, request.getParameter("dataPrevista"));
            to.setAttribute(Columns.AGD_JAVA_CLASS_NAME, CopiaSegurancaHelper.CLASSE_AGENDAMENTO);
            to.setAttribute(Columns.AGD_DESCRICAO, "Backup");

            int periodicidade = Integer.parseInt(JspHelper.verificaVarQryStr(request, "periodicidade"));
            agendamentoController.insereAgendamento(to, new HashMap<>(), periodicidade, responsavel);

            // Verifica se o agendamento foi feito para o mesmo dia, e dá mensagem diferente ao usuário
            boolean agendaParaMesmoDia = false;
            String dataPrevistaStr = request.getParameter("dataPrevista");
            if (!TextHelper.isNull(dataPrevistaStr)) {
                try {
                    Date dataPrevista = DateHelper.parse(dataPrevistaStr, LocaleHelper.getDatePattern());
                    if (dataPrevista.compareTo(DateHelper.getSystemDate()) == 0) {
                        agendaParaMesmoDia = true;
                    }
                } catch (ParseException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage(agendaParaMesmoDia ? "mensagem.agendamento.copia.seguranca.mesmo.dia.sucesso" : "mensagem.agendamento.copia.seguranca.sucesso", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (NumberFormatException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.copia.seguranca.periodicidade.invalida", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        } catch (AgendamentoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @PostMapping(params = { "acao=desagendar" })
    public String desagendar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            // Cancela o agendamento
            String agdCodigo = request.getParameter("agendamento");
            if (TextHelper.isNull(agdCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.copia.seguranca.impossivel.cancelar.agendamento", responsavel));
            } else {
                agendamentoController.cancelaAgendamento(agdCodigo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.agendamento.copia.seguranca.cancelado.sucesso", responsavel));
            }
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (AgendamentoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @PostMapping(params = { "acao=exportar" })
    public String exportar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        // DESENV-16594: Verifica se existe algum processo de backup/restore executando, independente do arquivo ou usuário executando
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSO, session);

        if (!temProcessoRodando) {
            // Se não há cóppia de segurança sendo processada então inicia o processamento.
            ProcessaCopiaSegurancaExportar processaCopiaSegurancaExportar = new ProcessaCopiaSegurancaExportar(responsavel);
            processaCopiaSegurancaExportar.start();
            ControladorProcessos.getInstance().incluir(CHAVE_PROCESSO, processaCopiaSegurancaExportar);
        } else {
            // Se alguma cóppia de segurança já está em processamento, retorna mensagem de aviso ao usuário para tentar mais tarde
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.aviso.copia.seguranca.concorrente", responsavel));
        }

        paramSession.halfBack();
        return iniciar(request, response, session, model);
    }

    @PostMapping(params = { "acao=importar" })
    public String importar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        // DESENV-16594: Verifica se existe algum processo de backup/restore executando, independente do arquivo ou usuário executando
        String chave1 = "COPIASEGURANCA";
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

        if (!temProcessoRodando) {
            int indice = -1;
            
            try {
                indice = Integer.valueOf(JspHelper.verificaVarQryStr(request, "arquivo_indice"));
            } catch (NumberFormatException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            Map<Integer, String> indiceArquivos = (Map<Integer, String>) session.getAttribute(CHAVE_SESSAO_MAP_ARQUIVOS);
            session.removeAttribute(CHAVE_SESSAO_MAP_ARQUIVOS);

            String nomeArquivoEntrada = (indiceArquivos != null ? indiceArquivos.get(indice) : null);
            if (TextHelper.isNull(nomeArquivoEntrada)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            // Se não há cóppia de segurança sendo processada então inicia o processamento.
            ProcessaCopiaSegurancaImportar processaCopiaSegurancaImportar = new ProcessaCopiaSegurancaImportar(nomeArquivoEntrada, responsavel);
            processaCopiaSegurancaImportar.start();
            ControladorProcessos.getInstance().incluir(chave1, processaCopiaSegurancaImportar);
        } else {
            // Se alguma cóppia de segurança já está em processamento, retorna mensagem de aviso ao usuário para tentar mais tarde
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.aviso.copia.seguranca.concorrente", responsavel));
        }

        paramSession.halfBack();
        return iniciar(request, response, session, model);
    }
}