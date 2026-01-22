package com.zetra.econsig.web.controller.relatorios;

import static com.zetra.econsig.values.CodedValues.TPA_SIM;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.web.ArquivoDTO;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.relatorio.RelatorioHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorio;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.faq.FaqController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.sdp.EnderecoConjuntoHabitacionalController;
import com.zetra.econsig.service.sdp.PlanoDescontoController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.sistema.PenalidadeController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarRelatoriosWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Relatórios.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class ListarRelatoriosWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarRelatoriosWebController.class);

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private AgendamentoController agendamentoController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private ComunicacaoController comunicacaoController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private EnderecoConjuntoHabitacionalController enderecoConjuntoHabitacionalController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private MensagemController mensagemController;

    @Autowired
    private PenalidadeController penalidadeController;

    @Autowired
    private PlanoDescontoController planoDescontoController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @Autowired
    private RelatorioController relatorioController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private FaqController faqController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarRelatorio" })
    public String listarRelatorio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            SynchronizerToken.saveToken(request);

            final String chave1 = "Relatorio" + "|" + responsavel.getUsuCodigo();
            final ProcessaRelatorio processo = (ProcessaRelatorio) ControladorProcessos.getInstance().getProcesso(chave1);
            final boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

            String strTipo = JspHelper.verificaVarQryStr(request, "tipo");
            String formAction = SynchronizerToken.updateTokenInURL("../v3/executarRelatorio?tipoRelatorio=" + strTipo, request);
            final String formActionAgendamento = SynchronizerToken.updateTokenInURL("../v3/agendarRelatorio", request);

            String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }

            strTipo = strTipo.replace("integracao_csa_endpoint", "integracao_csa");
            final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio(strTipo);
            if (relatorio == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } else if (!relatorio.isAtivo()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.bloqueado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            request.setAttribute("tituloPagina", relatorio.getTitulo());

            final boolean relatorioCustomizaoOrg = responsavel.isOrg() && !TextHelper.isNull(relatorio.getCustomizado()) && CodedValues.TPC_SIM.equals(relatorio.getCustomizado());
            model.addAttribute("relatorioCustomizaoOrg", relatorioCustomizaoOrg);
            model.addAttribute("refreshTimeout", ("test".equals(activeProfile) ? 2 : 10) * 1000);

            if ((processo != null) && !temProcessoRodando) {
                final Map<String, String[]> parametros = new HashMap<>(processo.getParameterMap());
                parametros.remove("LOG_OBSERVACAO");
                final String linkRetorno = JspHelper.makeURL("../v3/listarRelatorio", parametros);

                // Se o processo finalizado gerava um relatório em HTML, redireciona para a página de pré-visualização
                if ((processo != null) && processo.isPreVisualizacao() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRE_VISUALIZAR_RELATORIOS, CodedValues.TPC_SIM, responsavel)) {
                    try {
                        final String arquivoZip = processo.getNomeArqRelatorio();
                        if (arquivoZip != null) {
                            String delimitador = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DELIMITADOR_CAMPOS_RELATORIO_TXT, responsavel);
                            if (TextHelper.isNull(delimitador)) {
                                delimitador = ";";
                            }

                            // Obtém o conteúdo do arquivo para visualização na página
                            final List<String[]> conteudo = FileHelper.getZippedTextFileContent(arquivoZip, delimitador);

                            // Apaga o arquivo pois não será mais necessário
                            FileHelper.delete(arquivoZip);
                            session.removeAttribute("arquivoRelatorio");

                            // Se o resultado é vazio ou só tem a linha de cabeçalho, redireciona para a página de geração
                            // com mensagem informativa que o relatório não tem resultado
                            if ((conteudo != null) && (conteudo.size() <= 1)) {
                                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.relatorio.nenhum.registro.encontrado", responsavel));
                            } else {
                                model.addAttribute("linkRetorno", linkRetorno);
                                model.addAttribute("conteudoRelatorio", conteudo);
                                return viewRedirect("jsp/listarRelatorios/visualizarRelatorio", request, session, model, responsavel);
                            }
                        }
                    } catch (final IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                } else {
                    final boolean erro = !TextHelper.isNull(session.getAttribute(CodedValues.MSG_ERRO));
                    if(!erro) {
                        // Seta mensagem de sucesso na geração do relatório
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.gerado.relatorio.sucesso", responsavel));
                    }
                    // Redireciona para a página de geração de relatório repassando os filtros para que fiquem preenchidos
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(linkRetorno, request)));
                    return "jsp/redirecionador/redirecionar";
                }
            } else if (temProcessoRodando) {
                model.addAttribute("strTipo", strTipo);
                model.addAttribute("temProcessoRodando", temProcessoRodando);
                model.addAttribute("relatorio", relatorio);

                return viewRedirect("jsp/listarRelatorios/listarRelatorios", request, session, model, responsavel);
            }

            final List<String> campos = new ArrayList<>();
            final List<String> recursos = new ArrayList<>();
            final Set<String> obrigatorios = new HashSet<>();
            final Map<String, String> parametro = new HashMap<>();
            final Map<String, String> descricao = new HashMap<>();

            final CustomTransferObject ctoRel = new CustomTransferObject();
            ctoRel.setAttribute(Columns.REL_CODIGO, strTipo);
            final List<TransferObject> lRelTipo = relatorioController.lstRelatorioTipo(ctoRel, responsavel);

            // DESENV-14619: Remove arquivos específicos do layout v4 da lista de filtros relatório
            final List<String> tfrCodigoSomenteV4 = new ArrayList<>();
            tfrCodigoSomenteV4.add("campo_sub_orgao");
            tfrCodigoSomenteV4.add("campo_unidade");
            tfrCodigoSomenteV4.add("campo_comprometimento_margem");
            tfrCodigoSomenteV4.add("campo_decisao_judicial");
            tfrCodigoSomenteV4.add("campo_info_group_decisao_judicial");
            tfrCodigoSomenteV4.add("campo_percentual_variacao_margem");
            tfrCodigoSomenteV4.add("campo_info_group_oca_ade");

            final Iterator<?> itRelTipo = lRelTipo.iterator();
            while (itRelTipo.hasNext()) {
                final CustomTransferObject to = (CustomTransferObject) itRelTipo.next();
                final String tfrCodigo = (to.getAttribute(Columns.RFI_TFR_CODIGO) != null) ? to.getAttribute(Columns.RFI_TFR_CODIGO).toString() : "";
                String rfiParametro = (to.getAttribute(Columns.RFI_PARAMETRO) != null) ? to.getAttribute(Columns.RFI_PARAMETRO).toString() : "";
                String tfrRecurso = (to.getAttribute(Columns.TFR_RECURSO) != null) ? to.getAttribute(Columns.TFR_RECURSO).toString() : "";
                final String tfrDescricao = (to.getAttribute(Columns.TFR_DESCRICAO) != null) ? to.getAttribute(Columns.TFR_DESCRICAO).toString() : "";
                if (!"".equals(tfrCodigo)) {
                    boolean obrigatorio = false;
                    if (responsavel.isCse()) {
                        obrigatorio = (to.getAttribute(Columns.RFI_EXIBE_CSE) != null) ? CodedValues.REL_FILTRO_OBRIGATORIO.equals(to.getAttribute(Columns.RFI_EXIBE_CSE).toString()) : false;
                    } else if (responsavel.isOrg()) {
                        obrigatorio = (to.getAttribute(Columns.RFI_EXIBE_ORG) != null) ? CodedValues.REL_FILTRO_OBRIGATORIO.equals(to.getAttribute(Columns.RFI_EXIBE_ORG).toString()) : false;
                    } else if (responsavel.isCsa()) {
                        obrigatorio = (to.getAttribute(Columns.RFI_EXIBE_CSA) != null) ? CodedValues.REL_FILTRO_OBRIGATORIO.equals(to.getAttribute(Columns.RFI_EXIBE_CSA).toString()) : false;
                    } else if (responsavel.isCor()) {
                        obrigatorio = (to.getAttribute(Columns.RFI_EXIBE_COR) != null) ? CodedValues.REL_FILTRO_OBRIGATORIO.equals(to.getAttribute(Columns.RFI_EXIBE_COR).toString()) : false;
                    } else if (responsavel.isSer()) {
                        obrigatorio = (to.getAttribute(Columns.RFI_EXIBE_SER) != null) ? CodedValues.REL_FILTRO_OBRIGATORIO.equals(to.getAttribute(Columns.RFI_EXIBE_SER).toString()) : false;
                    } else if (responsavel.isSup()) {
                        obrigatorio = (to.getAttribute(Columns.RFI_EXIBE_SUP) != null) ? CodedValues.REL_FILTRO_OBRIGATORIO.equals(to.getAttribute(Columns.RFI_EXIBE_SUP).toString()) : false;
                    }

                    if (responsavel.isCsaCor() && "provisionamento_margem".equals(strTipo) && "campo_ade_portabilidade_cartao".equals(tfrCodigo) && !TPA_SIM.equals(parametroController.getParamCsa(csaCodigo, CodedValues.TPA_CSA_PODE_VENDER_CONTRATO_CARTAO, responsavel))) {
                        continue;
                    }

                    campos.add(tfrRecurso.substring(tfrRecurso.lastIndexOf("/") + 1, tfrRecurso.lastIndexOf(".")));

                    if(relatorioCustomizaoOrg && "campo_nome".equals(tfrCodigo)) {
                        rfiParametro += ";"+responsavel.getUsuNome();
                    }

                    if(relatorioCustomizaoOrg && "campo_op_login".equals(tfrCodigo)) {
                        rfiParametro += ";"+responsavel.getUsuLogin();
                    }

                    final String sufixoLeiaute = "v4";//ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel).toString();
                    final String novoRecruso = "/WEB-INF" + tfrRecurso.replaceAll(".jsp", "") + "_" + sufixoLeiaute + ".jsp";

                    // Verifica se o JSP existe
                    //Resource resource1 = resourceLoader.getResource(novoRecruso);
                    //Resource resource2 = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + novoRecruso);
                    //if (resource1.exists() || resource2.exists()) {
                        tfrRecurso = novoRecruso;
                        Map<String, String> descricoes = (Map<String, String>) model.asMap().get("descricoes");
                        if (descricoes == null) {
                            descricoes = new HashMap<>();
                            model.addAttribute("descricoes", descricoes);
                        }
                        descricoes.put(tfrRecurso, (obrigatorio ? "*" : "") + tfrDescricao);
                        @SuppressWarnings("unchecked")
                        Map<String, String> parametros = (Map<String, String>) model.asMap().get("parametros");
                        if (parametros == null) {
                            parametros = new HashMap<>();
                        }
                        model.addAttribute("parametros", parametros);
                        parametros.put(tfrRecurso, rfiParametro);
                        @SuppressWarnings("unchecked")
                        Map<String, String> obrigatoriosMap = (Map<String, String>) model.asMap().get("obrigatoriosMap");
                        if (obrigatoriosMap == null) {
                            obrigatoriosMap = new HashMap<>();
                            model.addAttribute("obrigatoriosMap", obrigatoriosMap);
                        }
                        obrigatoriosMap.put(tfrRecurso, "true");
                    //} else {
                    //    System.out.println("cp ." + tfrRecurso + " ." + novoRecruso);
                    //}

                    recursos.add(tfrRecurso);

                    parametro.put(tfrRecurso, rfiParametro);
                    descricao.put(tfrRecurso, (obrigatorio ? "*" : "") + tfrDescricao);
                    if (obrigatorio) {
                        obrigatorios.add(tfrRecurso);
                    }

                    // Carrega as dependências dos jsps de filtros
                    carregarDependenciasCamposFiltro(strTipo, tfrCodigo, rfiParametro, request, model, responsavel);
                }
            }

            if (relatorio.isAgendado()) {
                formAction = SynchronizerToken.updateTokenInURL("../v3/agendarRelatorio?tipoRelatorio=" + strTipo, request);
            }

            if ("integracao_csa".equals(strTipo)) {
                formAction = SynchronizerToken.updateTokenInURL("../v3/listarRelatorio?tipo=" + strTipo + "_endpoint", request);
                if (!TextHelper.isNull(csaCodigo)) {
                    request.setAttribute("CSA_CODIGO", csaCodigo);
                }
            }

            final String path = RelatorioHelper.getCaminhoRelatorio(strTipo, csaCodigo, responsavel);
            final String pathCsa = (responsavel.isCseSup() && "alteracao_multiplas_ade".equals(strTipo)) ? path.replace("cse", "csa") : "";
            final File diretorio = new File(path);
            if (!diretorio.exists() && !diretorio.mkdirs()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final FileFilter filtro = arq -> arq.getName().toLowerCase().endsWith(".pdf") ||
                   arq.getName().toLowerCase().endsWith(".htm") ||
                   arq.getName().toLowerCase().endsWith(".zip") ||
                   arq.getName().toLowerCase().endsWith(".txt") ||
                   arq.getName().toLowerCase().endsWith(".xls") ||
                   arq.getName().toLowerCase().endsWith(".csv");

            ArrayList<File> arquivos = null;
            final File[] temp = diretorio.listFiles(filtro);
            if (temp != null) {
                arquivos = new ArrayList<>(Arrays.asList(temp));
            }

            final HashMap<Object, CustomTransferObject> orgaos = new HashMap<>();
            final HashMap<Object, CustomTransferObject> consignatarias = new HashMap<>();

            if (!temProcessoRodando) {
                if (responsavel.isCseSup() && "alteracao_multiplas_ade".equals(strTipo)) {
                    final File diretorioCsa = new File(pathCsa);
                    if (diretorioCsa.exists()) {
                        final List<String> codigosCsa = new ArrayList<>();
                        final String[] nome_subdir = diretorioCsa.list();
                        if (nome_subdir != null) {
                            for (final String element : nome_subdir) {
                                final File arq = new File(pathCsa + File.separatorChar + element);
                                if (arq.isDirectory()) {
                                    arquivos.addAll(Arrays.asList(arq.listFiles(filtro)));
                                    codigosCsa.add(element);
                                }
                            }
                        }
                        if (codigosCsa.size() > 0) {
                            List<?> consignatariasTO = null;
                            try {
                                CustomTransferObject criterio = new CustomTransferObject();
                                criterio.setAttribute(Columns.CSA_CODIGO, codigosCsa);

                                consignatariasTO = consignatariaController.lstConsignatarias(criterio, responsavel);
                                final Iterator<?> it = consignatariasTO.iterator();
                                while (it.hasNext()) {
                                    criterio = (CustomTransferObject) it.next();
                                    consignatarias.put(criterio.getAttribute(Columns.CSA_CODIGO), criterio);
                                }
                            } catch (final Exception ex) {
                                LOG.error(ex.getMessage(), ex);
                                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                    }
                } else if (responsavel.isCseSup() && !"integracao_csa".equals(strTipo)) {
                    final List<String> codigosOrgao = new ArrayList<>();
                    final String[] nome_subdir = diretorio.list();
                    if (nome_subdir != null) {
                        for (final String element : nome_subdir) {
                            final File arq = new File(path + File.separatorChar + element);
                            if (arq.isDirectory()) {
                                arquivos.addAll(Arrays.asList(arq.listFiles(filtro)));
                                codigosOrgao.add(element);
                            }
                        }
                    }
                    if (codigosOrgao.size() > 0) {
                        List<?> orgaosTO = null;
                        try {
                            CustomTransferObject criterio = new CustomTransferObject();
                            criterio.setAttribute(Columns.ORG_CODIGO, codigosOrgao);

                            orgaosTO = consignanteController.lstOrgaos(criterio, responsavel);
                            final Iterator<?> it = orgaosTO.iterator();
                            while (it.hasNext()) {
                                criterio = (CustomTransferObject) it.next();
                                orgaos.put(criterio.getAttribute(Columns.ORG_CODIGO), criterio);
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                }

                Collections.sort(arquivos, (f1, f2) -> {
                    final Long d1 = f1.lastModified();
                    final Long d2 = f2.lastModified();
                    return d2.compareTo(d1);
                });
            }

            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }

            int offset2 = 0;
            try {
                offset2 = Integer.parseInt(request.getParameter("offset2"));
            } catch (final Exception ex) {
            }

            final ArrayList<File> lstArquivos = new ArrayList<>();
            for (final File arquivo : arquivos) {
                final String nome = arquivo.getPath().substring(path.length() + 1);
                final String nomeCsa = !TextHelper.isNull(pathCsa) ? arquivo.getPath().substring(pathCsa.length() + 1) : "";
                final CustomTransferObject orgao = (nome.indexOf(File.separatorChar) != -1) ? (CustomTransferObject) orgaos.get(nome.substring(0, nome.indexOf(File.separatorChar))) : null;
                final CustomTransferObject consignataria = (nomeCsa.indexOf(File.separatorChar) != -1) ? (CustomTransferObject) consignatarias.get(nomeCsa.substring(0, nomeCsa.indexOf(File.separatorChar))) : null;
                if ((consignataria != null) || ((orgao != null) || (nome.indexOf(File.separatorChar) == -1))) {
                    lstArquivos.add(arquivo);
                }
            }
            arquivos.clear();
            arquivos.addAll(lstArquivos);

            final int total = arquivos.size();

            // Monta lista de parâmetros através dos parâmetros de request
            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            final List<String> requestParams = new ArrayList<>(params);

            final String linkListagem = "../v3/listarRelatorio?acao=iniciar";
            configurarPaginador(linkListagem, "rotulo.paginacao.titulo.relatorio.movimento", total, size, requestParams, false, request, model);

            String linkPaginacaoAgendamento = linkListagem + "&indice=2";
            if ((request.getQueryString() != null) && !"".equals(request.getQueryString())) {
                linkPaginacaoAgendamento += "&" + request.getQueryString();
            }
            linkPaginacaoAgendamento += "&tipo=" + strTipo;
            if ("integracao_csa".equals(strTipo)) {
                linkPaginacaoAgendamento += "&csaCodigo=" + csaCodigo;
            }
            linkPaginacaoAgendamento = SynchronizerToken.updateTokenInURL(linkPaginacaoAgendamento, request);

            final List<ArquivoDTO> arquivosDTO = new ArrayList<>();
            if ((arquivos != null) && !arquivos.isEmpty()) {
                // Paginação é realizada pelo datatable
                int i = 0;
                int j = 0;
                size = arquivos.size();

                while ((arquivos.size() > j) && (i < size)) {
                    final File arquivo = arquivos.get(j);
                    String tam = "";
                    if (arquivo.length() > 1024.00) {
                        tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                    } else {
                        tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                    }
                    final String data = DateHelper.toDateTimeString(new java.util.Date(arquivo.lastModified()));
                    final String nome = arquivo.getPath().substring(path.length() + 1);
                    final String nomeCsa = !TextHelper.isNull(pathCsa) ? arquivo.getPath().substring(pathCsa.length() + 1) : "";
                    String formato = "";
                    if (nome.toLowerCase().endsWith(".pdf")) {
                        formato = "pdf.gif";
                    } else if (nome.toLowerCase().endsWith(".txt") || nome.toLowerCase().endsWith(".csv")) {
                        formato = "text.gif";
                    } else if (nome.toLowerCase().endsWith(".zip")) {
                        formato = "zip.gif";
                    } else if (nome.toLowerCase().endsWith(".htm")) {
                        formato = "html.gif";
                    } else if (nome.toLowerCase().endsWith(".xls")) {
                        formato = "xls.gif";
                    }
                    final CustomTransferObject orgao = (nome.indexOf(File.separatorChar) != -1) ? (CustomTransferObject) orgaos.get(nome.substring(0, nome.indexOf(File.separatorChar))) : null;
                    final CustomTransferObject consignataria = (nomeCsa.indexOf(File.separatorChar) != -1) ? (CustomTransferObject) consignatarias.get(nomeCsa.substring(0, nomeCsa.indexOf(File.separatorChar))) : null;
                    String org_identificador = null, est_identificador = null;
                    j++;
                    i++;
                    org_identificador = (orgao != null) ? orgao.getAttribute(Columns.ORG_IDENTIFICADOR).toString() : "";
                    est_identificador = (orgao != null) ? orgao.getAttribute(Columns.EST_IDENTIFICADOR).toString() : "";

                    String csa_codigo = null, csa_identificador = null, csa_nome = null;
                    csa_codigo = (consignataria != null) ? consignataria.getAttribute(Columns.CSA_CODIGO).toString() : "";
                    csa_identificador = (consignataria != null) ? consignataria.getAttribute(Columns.CSA_IDENTIFICADOR).toString() : "";
                    csa_nome = (consignataria != null) ? consignataria.getAttribute(Columns.CSA_NOME).toString() : "";

                    arquivosDTO.add(new ArquivoDTO(arquivo.getName(), nome, formato, data, tam, org_identificador, est_identificador.toUpperCase(), csa_codigo, csa_identificador, csa_nome));
                }
            }

            final boolean podeExclRelatorio = !"integracao_csa".equals(strTipo) && !"integracao".equals(strTipo) && !"percentual_rejeito".equals(strTipo) && !"repasse".equals(strTipo) && !"recuperacao_credito".equals(strTipo) && !"alteracao_multiplas_ade".equals(strTipo);
            model.addAttribute("campos", campos);
            model.addAttribute("podeExclRelatorio", podeExclRelatorio);
            model.addAttribute("strTipo", strTipo);
            model.addAttribute("temProcessoRodando", temProcessoRodando);
            model.addAttribute("formAction", formAction);
            model.addAttribute("formActionAgendamento", formActionAgendamento);
            model.addAttribute("relatorio", relatorio);
            model.addAttribute("recursos", recursos);
            model.addAttribute("obrigatorios", obrigatorios);
            model.addAttribute("parametro", parametro);
            model.addAttribute("descricao", descricao);
            model.addAttribute("exibeColunaCsa", (consignatarias != null) && !consignatarias.isEmpty());
            if ("integracao_csa".equals(strTipo)) {
                model.addAttribute("csaCodigo", csaCodigo);
            }
            // Lista de relatórios gerados
            model.addAttribute("arquivosDTO", arquivosDTO);
            model.addAttribute("offset", offset);

            model.addAttribute("linkPaginacaoAgendamento", linkPaginacaoAgendamento);
            model.addAttribute("offset2", offset2);

            if ((responsavel.isCse() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_CSE, CodedValues.TPC_SIM, responsavel)) ||
                    (responsavel.isSup() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_SUP, CodedValues.TPC_SIM, responsavel)) ||
                    (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_CSA, CodedValues.TPC_SIM, responsavel))) {
                // Permite agendar relatório para o mesmo dia
                model.addAttribute("minDataExecucaoAgendamento", DateHelper.toDateString(DateHelper.getSystemDate()));
                model.addAttribute("permiteAgendamentoMesmoDia", Boolean.TRUE);
            } else {
                model.addAttribute("minDataExecucaoAgendamento", DateHelper.toDateString(DateHelper.dateAdd(DateHelper.getSystemDate(), "DIA", 1)));
            }

            // Exibe Botao que leva ao rodapé RELATORIOS DE CONSIGNAÇÕES
            boolean exibeBotaoRodape = false;
            if ("consignacoes".equals(strTipo) || "taxas".equals(strTipo)) {
                exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);
            }

            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);

            return viewRedirect("jsp/listarRelatorios/listarRelatorios", request, session, model, responsavel);
        } catch (final RelatorioControllerException | ParametroControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarRelatorio" })
    public String executarRelatorio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);
        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tipoRelatorio = request.getParameter("tipoRelatorio");

        final String chave = "Relatorio" + "|" + responsavel.getUsuCodigo();
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

        if (!temProcessoRodando) {
            final Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
            temProcessoRodando = true;

            // DESENV-17457 - Foi solicitado gerar o relatório sintético de ocorrência de consignação a partir do analítico quando o campo Agrupar por serviço estiver como true.
            // além disso caso o usuário não tenha permissão para executar o sintético, mas tem permissão para executar o analítico, precisamos garantir a execução do relatório.
            boolean permissaoOcorrencia = false;
            if("ocorrencia_autorizacao".equals(tipoRelatorio) && !TextHelper.isNull(parameterMap.get("chkCAMPOS"))) {
                final String[] strAgrupa = {"true"};
                final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio(tipoRelatorio);
                permissaoOcorrencia = responsavel.temPermissao(relatorio.getFuncoes());
                tipoRelatorio = "sint_ocorrencia_autorizacao";
                parameterMap.put("agruparServicoAnalitico", strAgrupa);
            }
            final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio(tipoRelatorio);
            if (relatorio != null) {
                final ProcessaRelatorio processaRel = ProcessaRelatorio.newInstance(relatorio.getClasseProcesso(), relatorio, parameterMap, session, responsavel);
                if ((processaRel != null) && (responsavel.temPermissao(relatorio.getFuncoes()) || permissaoOcorrencia)) {
                    processaRel.start();
                    ControladorProcessos.getInstance().incluir(chave, processaRel);
                }
            } else {
                temProcessoRodando = false;
            }
        } else {
            // Se o arquivo está sendo processado por outro usuário,
            // dá mensagem de erro ao usuário e permite que ele escolha outro arquivo
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.processando.arquivo", responsavel));
            temProcessoRodando = false;
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/agendarRelatorio" })
    public String agendarRelatorio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        final String tipoRelatorio = request.getParameter("tipo");
        final String formato = request.getParameter("formato");

        if ("HTML".equals(formato)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.agendado.pre.visualizacao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio(tipoRelatorio);
        request.setAttribute("tituloPagina", relatorio.getTitulo());
        String classeAgendamento = relatorio.getClasseAgendamento();

        // Caso não seja informada uma classe específica para agendamento de relatório, utiliza a classe genérica
        if (TextHelper.isNull(relatorio.getClasseAgendamento())) {
            classeAgendamento = com.zetra.econsig.job.jobs.RelatorioAgendadoJob.class.getName();
        }

        //Agenda o relatório
        final TransferObject to = new CustomTransferObject();
        to.setAttribute(Columns.AGD_TAG_CODIGO, request.getParameter("tagCodigo"));
        to.setAttribute(Columns.AGD_DATA_PREVISTA, request.getParameter("dataPrevista"));
        to.setAttribute(Columns.AGD_JAVA_CLASS_NAME, classeAgendamento);
        to.setAttribute(Columns.AGD_DESCRICAO, relatorio.getTitulo());
        to.setAttribute(Columns.AGD_REL_CODIGO, tipoRelatorio);

        final Map<String, List<String>> parametros = new HashMap<>();
        final List<String> listaRelatorio = new ArrayList<>();
        listaRelatorio.add(tipoRelatorio);
        parametros.put(Columns.getColumnName(Columns.REL_CODIGO), listaRelatorio);
        final Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            final String parametro = (String) enu.nextElement();
            if ((parametro != null) && (!"recurso".equals(parametro) && !"username".equals(parametro) && !"senhaRSA".equals(parametro) && !"segundaSenha".equals(parametro) && !"segundaSenhaPair".equals(parametro) && !"senha2aAutorizacao".equals(parametro) && !"funcaoAutenticada".equals(parametro) && !"funDescricao".equals(parametro) && !"timeInMilliseconds".equals(parametro) && !"_skip_history_".equals(parametro) && !"eConsig.page.token".equals(parametro))) {

                final String[] valores = request.getParameterValues(parametro);
                final List<String> lista = new ArrayList<>();
                for (int x = 0; (valores != null) && (x < valores.length); x++) {
                    lista.add(valores[x]);
                }
                parametros.put(parametro, lista);
            }
        }

        int periodicidade = 0;
        try {
            periodicidade = Integer.parseInt(JspHelper.verificaVarQryStr(request, "periodicidade"));
        } catch (final NumberFormatException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.periodicidade.invalida", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            agendamentoController.insereAgendamento(to, parametros, periodicidade, responsavel);
        } catch (final AgendamentoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Verifica se o agendamento foi feito para o mesmo dia, e dá mensagem diferente ao usuário
        boolean agendaParaMesmoDia = false;
        if ((responsavel.isCse() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_CSE, CodedValues.TPC_SIM, responsavel)) ||
                (responsavel.isSup() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_SUP, CodedValues.TPC_SIM, responsavel)) ||
                (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_CSA, CodedValues.TPC_SIM, responsavel))) {
            final String dataPrevistaStr = request.getParameter("dataPrevista");
            if (!TextHelper.isNull(dataPrevistaStr)) {
                try {
                    final Date dataPrevista = DateHelper.parse(dataPrevistaStr, LocaleHelper.getDatePattern());
                    if (dataPrevista.compareTo(DateHelper.getSystemDate()) == 0) {
                        agendaParaMesmoDia = true;
                    }
                } catch (final ParseException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage(agendaParaMesmoDia ? "mensagem.agendamento.relatorio.mesmo.dia.sucesso" : "mensagem.agendamento.relatorio.sucesso", responsavel));
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cancelarAgendamentoRelatorio" })
    public String cancelarAgendamento(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        //Valida o token de sessão para evitar a chamada direta à operação
        if ((request.getParameter("tipo") != null) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        //Cancela o agendamento
        final String agdCodigo = request.getParameter("agdCodigo");
        if (TextHelper.isNull(agdCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.impossivel.cancelar.agendamento", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        try {
            agendamentoController.cancelaAgendamento(agdCodigo, responsavel);
        } catch (final AgendamentoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.agendamento.relatorio.cancelado.sucesso", responsavel));
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    protected void carregarDependenciasCamposFiltro(String tipoRelatorio, String tfrCodigo, String rfiParametro, HttpServletRequest request, Model model, AcessoSistema responsavel) {
        try {
            String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
            String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");
            final String sboCodigo = JspHelper.verificaVarQryStr(request, "sboCodigo");
            final List<String> ncaCodigos = Arrays.asList(ArrayUtils.nullToEmpty(JspHelper.obterParametrosRequisicao(request, null, new String[]{"ncaCodigo"})));
            final List<String> nseCodigos = Arrays.asList(ArrayUtils.nullToEmpty(JspHelper.obterParametrosRequisicao(request, null, new String[]{"nseCodigo"})));
            final List<String> marCodigos = Arrays.asList(ArrayUtils.nullToEmpty(JspHelper.obterParametrosRequisicao(request, null, new String[]{"MAR_CODIGO"})));

            if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "periodo"))) {
                model.addAttribute("periodo", JspHelper.verificaVarQryStr(request, "periodo"));
            }
            if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "periodoIni"))) {
                model.addAttribute("periodoIni", JspHelper.verificaVarQryStr(request, "periodoIni"));
            }
            if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "periodoFim"))) {
                model.addAttribute("periodoFim", JspHelper.verificaVarQryStr(request, "periodoFim"));
            }
            if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "estCodigo"))) {
                model.addAttribute("estCodigo", JspHelper.verificaVarQryStr(request, "estCodigo"));
            }

            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }
            if (responsavel.isOrg()) {
                orgCodigo = responsavel.getOrgCodigo();
            }

            if ("campo_auditoria".equals(tfrCodigo)) {
                // Carrega a lista de tipo de log e tipo de entidades para auditoria
                final LogDelegate logDelegate = new LogDelegate();
                model.addAttribute("tipoLogAuditList", logDelegate.lstTiposLog());
                model.addAttribute("tipoEntAuditList", logDelegate.lstTiposEntidadesAuditoria(responsavel));

            } else if ("campo_beneficio".equals(tfrCodigo)) {
                // Carrega a lista de benefícios e adiciona ao model
                final CustomTransferObject criterio = new CustomTransferObject();
                if (!TextHelper.isNull(csaCodigo)) {
                    criterio.setAttribute(Columns.BEN_CSA_CODIGO, csaCodigo);
                }

                model.addAttribute("listaBeneficio", beneficioController.listaBeneficio(criterio, responsavel));

            } else if ("campo_categoria_assunto".equals(tfrCodigo)) {
                // Carrega a lista de assuntos
                model.addAttribute("listaAssuntos", comunicacaoController.listaAssuntoComunicacao(responsavel));

            } else if ("campo_cor".equals(tfrCodigo)) {
                // carrega a lista de correspondentes
                model.addAttribute("listaCorrespondentes", carregarListaCorrespondentes(csaCodigo, responsavel));

            } else if ("campo_cor_multiplo".equals(tfrCodigo)) {
                // carrega a lista de correspondentes
                final List<TransferObject> correspondentes = carregarListaCorrespondentes(csaCodigo, responsavel);

                if (correspondentes != null) {
                    // Adiciona opção de Nenhum
                    final Map<String,Object> nenhum = new HashMap<>();
                    nenhum.put(Columns.COR_CODIGO, "-1");
                    nenhum.put(Columns.COR_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel));
                    final CustomTransferObject auxNenhum = new CustomTransferObject();
                    auxNenhum.setAtributos(nenhum);
                    correspondentes.add(0, auxNenhum);
                }

                model.addAttribute("listaCorrespondentes", correspondentes);

            } else if ("campo_csa".equals(tfrCodigo) || "campo_csa_compra".equals(tfrCodigo) || "campo_csa_multiplo".equals(tfrCodigo) || "campo_csa_selec".equals(tfrCodigo)) {
                // carrega a lista de consignatárias
                model.addAttribute("listaConsignatarias", carregarListaConsignatarias(ncaCodigos, tipoRelatorio, responsavel));

            } else if ("campo_csa_inadimplencia".equals(tfrCodigo)) {
                // carrega a lista de consignatárias do projeto de inadimplência
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CSA_PROJETO_INADIMPLENCIA, CodedValues.TPC_SIM);
                model.addAttribute("listaConsignatariasInadimplencia", consignatariaController.lstConsignatarias(criterio, responsavel));

            } else if ("campo_csa_natureza_beneficio".equals(tfrCodigo)) {
                // carrega a lista de consignatárias que são operadoras de benefício
                if (responsavel.isSup()) {
                    final List<Consignataria> operadoras = consignatariaController.lstConsignatariaByNcaCodigo(NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo(), responsavel);

                    if ((operadoras != null) && !operadoras.isEmpty()) {
                        final List<TransferObject> operadorasBeneficios = new ArrayList<>();
                        for (final Consignataria consig : operadoras) {
                            final CustomTransferObject cto = new CustomTransferObject();
                            cto.setAttribute(Columns.CSA_IDENTIFICADOR, consig.getCsaIdentificador());
                            cto.setAttribute(Columns.CSA_CODIGO, consig.getCsaCodigo());
                            cto.setAttribute(Columns.CSA_NOME, consig.getCsaNome());
                            operadorasBeneficios.add(cto);
                        }

                        model.addAttribute("listaOperadorasBeneficio", operadorasBeneficios);
                    }
                }

            } else if ("campo_csa_natureza_svc".equals(tfrCodigo) || "campo_natureza_svc".equals(tfrCodigo)) {
                // carrega a lista de naturezas de serviço
                List<TransferObject> naturezas = null;
                if ("EBENEFICIOS".equals(rfiParametro)) {
                    naturezas = servicoController.lstNaturezasServicosBeneficios(false);
                } else {
                    naturezas = servicoController.lstNaturezasServicos(false);
                }
                model.addAttribute("listaNaturezasServico", naturezas);

            } else if ("campo_natureza_csa".equals(tfrCodigo)) {
                // carrega a lista de naturezas de consignatária
                model.addAttribute("listaNaturezasConsignataria", consignatariaController.lstNatureza());

            } else if ("campo_cse".equals(tfrCodigo)) {
                // carrega a lista de consignantes
                model.addAttribute("listaConsignantes", carregarListaConsignantes(responsavel));

            } else if ("campo_dados_servidor".equals(tfrCodigo)) {
                // carrega a lista de cargos de servidor
                model.addAttribute("listaCargosServidor", servidorController.lstCargo(responsavel));

            } else if ("campo_empresa_cor".equals(tfrCodigo) || "campo_emp_cor".equals(tfrCodigo)) {
                // carrega a lista de empresas correspondentes
                if ((responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_REL_EMPRESA_CORRESPONDENTE)) || responsavel.temPermissao(CodedValues.FUN_CONS_CORRESPONDENTES)) {
                    model.addAttribute("listaEmpresasCorrespondente", consignatariaController.lstEmpresaCorrespondente(null, -1, -1, responsavel));
                }

            } else if ("campo_endereco".equals(tfrCodigo)) {
                // carrega a lista de endereços de conjunto habitacional
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ECH_CSA_CODIGO, csaCodigo);

                model.addAttribute("listaEnderecos", enderecoConjuntoHabitacionalController.listaEndereco(criterio, -1, -1, responsavel));

            } else if ("campo_entidade".equals(tfrCodigo) || "campo_entidade_usu".equals(tfrCodigo)) {
                // carrega a lista de consignantes, órgãos, consignatárias e correspondentes
                model.addAttribute("listaConsignantes", carregarListaConsignantes(responsavel));
                model.addAttribute("listaOrgaos", carregarListaOrgaos(responsavel));
                model.addAttribute("listaConsignatarias", carregarListaConsignatarias(null, null, responsavel));
                model.addAttribute("listaCorrespondentes", carregarListaCorrespondentes(csaCodigo, responsavel));

            } else if ("campo_entidade_combo".equals(tfrCodigo)) {
                // carrega a lista de consignatárias
                model.addAttribute("listaConsignatarias", carregarListaConsignatarias(null, null, responsavel));

            } else if ("campo_est".equals(tfrCodigo)) {
                // carrega a lista de estabelecimentos
                model.addAttribute("listaEstabelecimentos", consignanteController.lstEstabelecimentos(null, responsavel));

            } else if ("campo_fun".equals(tfrCodigo) || "campo_funcao".equals(tfrCodigo)) {
                // carrega a lista de funções
                model.addAttribute("listaFuncoes", usuarioController.lstFuncoesPermitidasUsuario(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), responsavel));

            } else if ("campo_grupo_servico".equals(tfrCodigo)) {
                // carrega a lista de grupos de serviço
                model.addAttribute("listaGrupossServico", convenioController.lstGrupoServicos(false, responsavel));

            } else if ("campo_margens".equals(tfrCodigo)) {
                // carrega a lista de margens
                model.addAttribute("listaMargens", margemController.lstMargemRaiz(responsavel));

            } else if ("campo_mensagens".equals(tfrCodigo)) {
                // carrega a lista de mensagens
                model.addAttribute("listaMensagens", mensagemController.lstMensagem(null, -1, -1, responsavel));

            } else if ("campo_operacao_por_consignataria".equals(tfrCodigo)) {
                // carrega a lista de naturezas de serviço
                model.addAttribute("listaNaturezasServico", servicoController.lstNaturezasServicos(false));

            } else if ("campo_org".equals(tfrCodigo)) {
                // carrega a lista de órgãos
                model.addAttribute("listaOrgaos", carregarListaOrgaos(responsavel));

            } else if ("campo_plano".equals(tfrCodigo)) {
                // carrega a lista de planos de desconto
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());

                model.addAttribute("listaPlanosDesconto", planoDescontoController.lstPlanoDesconto(criterio, responsavel));

            } else if ("campo_posto".equals(tfrCodigo)) {
                // carrega a lista de postos de registro servidor
                model.addAttribute("listaPostos", postoRegistroServidorController.lstPostoRegistroServidor(null, -1, -1, responsavel));

            } else if ("campo_status_contrato_beneficio".equals(tfrCodigo)) {
                // Carrega a lista de status de contrato benefício e adiciona ao model
                model.addAttribute("listaStatusContratoBeneficio", contratoBeneficioController.listAllStatusContratoBeneficio(responsavel));

            } else if ("campo_svc".equals(tfrCodigo) || "campo_svc_selec".equals(tfrCodigo)) {
                // carrega a lista de serviços
                model.addAttribute("listaServicos", carregarListaServicos(false, nseCodigos, marCodigos, csaCodigo, responsavel));

            } else if ("campo_svc_taxas".equals(tfrCodigo)) {
                // carrega a lista de serviços que tem prazos cadastrados
                model.addAttribute("listaServicos", carregarListaServicos(true, null, null, null, responsavel));

            } else if ("campo_taxas".equals(tfrCodigo)) {
                // carrega a lista de prazos
                model.addAttribute("listaPrazosServico", simulacaoController.getSvcPrazo(null, null, true, responsavel));

            } else if ("campo_tipo_agendamento".equals(tfrCodigo)) {
                // carrega a lista de tipos de agendamentos
                final Relatorio relatorioTagPage = ConfigRelatorio.getInstance().getRelatorio(tipoRelatorio);
                List<String> tipos = Arrays.asList(relatorioTagPage.getArrayTipoAgendamento());
                if (tipos.isEmpty()) {
                    tipos = TipoAgendamentoEnum.getTipoAgendamentoRelatorio();
                }

                model.addAttribute("listaTiposAgendamento", agendamentoController.lstTipoAgendamento(tipos, responsavel));

            } else if ("campo_tipo_documento".equals(tfrCodigo)) {
                // carrega a lista de tipos de documentos
                final List<String> tarCodigos = new ArrayList<>();
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_RG.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_CPF.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_COMPROVANTE_RESIDENCIA.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_CERTIDAO_CASAMENTO.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_UNIAO_ESTAVEL.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_CERTIDAO_NASCIMENTO.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_MATRICULA_FREQUENCIA_ESCOLAR.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_TUTELA_CURATELA.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_DECLARACAO_CARENCIA.getCodigo());
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_ATESTADO_MEDICO.getCodigo());

                model.addAttribute("listaTiposDocumento", historicoArquivoController.lstTiposArquivoByTarCodigos(tarCodigos, responsavel));

            } else if ("campo_tipo_motivo_operacao".equals(tfrCodigo)) {
                // carrega a lista de motivos de operação
                List<TransferObject> tipoMotivoOperacao = null;
                if ("USUARIO".equalsIgnoreCase(rfiParametro)) {
                    tipoMotivoOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoUsuario(null, responsavel);
                } else if ("CONSIGNACAO".equalsIgnoreCase(rfiParametro)) {
                    tipoMotivoOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(null, responsavel);
                } else if ("BENEFICIO_SAUDE".equalsIgnoreCase(rfiParametro)) {
                    tipoMotivoOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoBeneficioSaude(null, responsavel);
                }
                model.addAttribute("listaTiposMotivoOperacao", tipoMotivoOperacao);

            } else if ("campo_tipo_ocorrencia".equals(tfrCodigo)) {
                // carrega a lista de tipos de ocorrência
                List<TransferObject> tipoOcorrencia = null;

                if ("USUARIO".equalsIgnoreCase(rfiParametro)) {
                    tipoOcorrencia = sistemaController.lstTipoOcorrencia(CodedValues.TOC_CODIGOS_USUARIO, responsavel);
                } else if ("AUTORIZACAO".equalsIgnoreCase(rfiParametro)) {
                    tipoOcorrencia = sistemaController.lstTipoOcorrencia(CodedValues.TOC_CODIGOS_AUTORIZACAO, responsavel);
                } else if ("CONSIGNATARIA".equalsIgnoreCase(rfiParametro)) {
                    tipoOcorrencia = sistemaController.lstTipoOcorrencia(CodedValues.TOC_CODIGOS_CONSIGNATARIA, responsavel);
                } else if ("REGISTRO_SERVIDOR".equalsIgnoreCase(rfiParametro)) {
                    tipoOcorrencia = sistemaController.lstTipoOcorrencia(CodedValues.TOC_CODIGOS_REGISTRO_SERVIDOR, responsavel);
                } else {
                    // caso nenhum parâmetro seja informado, lista os tipos de ocorrência de autorização (usado pelos relatórios customizados)
                    tipoOcorrencia = sistemaController.lstTipoOcorrencia(CodedValues.TOC_CODIGOS_AUTORIZACAO, responsavel);
                }
                model.addAttribute("listaTiposOcorrencia", tipoOcorrencia);

            } else if ("campo_tipo_penalidade".equals(tfrCodigo)) {
                // carrega a lista de tipos de penalidade
                model.addAttribute("listaTiposPenalidade", penalidadeController.lstTiposPenalidade(responsavel));

            } else if ("campo_tipo_registro_servidor".equals(tfrCodigo)) {
                // carrega a lista de tipos de registro servidor
                model.addAttribute("listaTipoRegistroServidor", servidorController.lstTipoRegistroServidor(responsavel));

            } else if ("campo_verba".equals(tfrCodigo)) {
                final String csaCodigoCampoVerbaGestorPage = (!TextHelper.isNull(csaCodigo) && csaCodigo.contains(";")) ? csaCodigo.split(";")[0] : csaCodigo;

                // carrega a lista de códigos de verba de reajuste
                model.addAttribute("listaCodVerbaReajuste", convenioController.getCsaCodVerbaReajuste(csaCodigoCampoVerbaGestorPage, responsavel));

            } else if ("campo_status_proposta".equals(tfrCodigo)) {
                // carrega a lista status de proposta
                model.addAttribute("listaStatusProposta", leilaoSolicitacaoController.listarStatusPropostaLeilao(responsavel));

            } else if ("campo_sub_orgao".equals(tfrCodigo)) {
                // carrega a lista de sub orgãos
                model.addAttribute("listaSubOrgao", carregarListaSubOrgaos(orgCodigo, responsavel));

            } else if ("campo_unidade".equals(tfrCodigo)) {
                // carrega a lista de unidade
                final List<TransferObject> subOrgaos = carregarListaSubOrgaos(orgCodigo, responsavel);
                //Variável criada para verificar se o sboCodigo existe na lista de subOrgaos listada pelo orgCodigo ao recarregar a página, para impedir de listar unidades indevidamente.
                boolean sboCodigoExiste = false;
                if ((subOrgaos != null) && !subOrgaos.isEmpty()) {
                    for (final TransferObject sub : subOrgaos) {
                        if (sub.getAttribute(Columns.SBO_CODIGO).equals(sboCodigo.split(";")[0])) {
                            sboCodigoExiste = true;
                        }
                    }
                }
                model.addAttribute("listaUnidade", sboCodigoExiste ? carregarListaUnidade(sboCodigo, responsavel) : null);
            } else if ("campo_decisao_judicial".equals(tfrCodigo)) {
                // carrega a lista de tipo justiça
                model.addAttribute("lstTipoJustica", sistemaController.lstTipoJustica(responsavel));
            } else if ("campo_faq".equals(tfrCodigo)) {
                // carrega a lista de FAQs
                model.addAttribute("itensFaq", faqController.lstFaq(null, -1, -1, responsavel));
            } else if("campo_csa_saldo_devedor_servidor".equals(tfrCodigo)) {
                model.addAttribute("lstCsaSaldoDevedorServidor", consignatariaController.lstConsignatariasSaldoDevedorServidor(responsavel));
            }

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private List<TransferObject> carregarListaConsignantes(AcessoSistema responsavel) {
        List<TransferObject> consignantes = null;
        try {
            final ConsignanteTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            consignantes = new ArrayList<>();
            consignantes.add(cse);
        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return consignantes;
    }

    private List<TransferObject> carregarListaOrgaos(AcessoSistema responsavel) {
        List<TransferObject> orgaos = null;
        if (responsavel.isCseSup() || responsavel.isCsaCor() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) {
            try {
                CustomTransferObject criterio = null;
                if (responsavel.isOrg()) {
                    criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.EST_CODIGO, responsavel.getCodigoEntidadePai());
                }
                if (responsavel.isCsaCor()) {
                    final String corCodigo = (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidade() : null;
                    final String csaCodigo = (responsavel.isCsa()) ? responsavel.getCodigoEntidade() : ((responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidadePai() : null);
                    orgaos = convenioController.getOrgCnvAtivo(csaCodigo, corCodigo, responsavel);
                } else {
                    orgaos = consignanteController.lstOrgaos(criterio, responsavel);
                }
            } catch (ConsignanteControllerException | ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return orgaos;
    }

    private List<TransferObject> carregarListaConsignatarias(List<String> ncaCodigos, String tipoRelatorio, AcessoSistema responsavel) {
        List<TransferObject> consignatarias = null;
        if (responsavel.isCseSupOrg() || responsavel.isSer()) {
            try {
                 if (responsavel.isCseSup()) {
                    if (!TextHelper.isNull(tipoRelatorio) && "taxas_efetivas".equals(tipoRelatorio)) {
                        consignatarias = consignatariaController.lstConsignatariaCoeficienteAtivo(responsavel);
                    } else if ((ncaCodigos != null) && !ncaCodigos.isEmpty()) {
                        consignatarias = consignatariaController.lstConsignatariaByNaturezas(ncaCodigos, responsavel);
                    } else {
                        consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
                    }
                } else if (responsavel.isSer()) {
                    consignatarias = consignatariaController.lstConsignatariaSerTemAde(responsavel.getSerCodigo(), responsavel.getRseCodigo(), false, responsavel);
                } else {
                    consignatarias = convenioController.getCsaCnvAtivo(null, responsavel.getOrgCodigo(), responsavel);
                }
            } catch (ConvenioControllerException | ConsignatariaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return consignatarias;
    }

    private List<TransferObject> carregarListaCorrespondentes(String csaCodigo, AcessoSistema responsavel) {
        List<TransferObject> correspondentes = null;

        if (responsavel.isCseSupOrg() || responsavel.isCsa() || (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) {
            try {
                final CustomTransferObject criterio = new CustomTransferObject();
                final String csaCodigoCampoCorGestorPage = csaCodigo.split(";")[0];
                // Para CSE/SUP/ORG só monta o combo com correspondentes da consignatária selecionada
                if (responsavel.isCseSupOrg() && !TextHelper.isNull(csaCodigoCampoCorGestorPage)) {
                    criterio.setAttribute(Columns.COR_CSA_CODIGO, csaCodigoCampoCorGestorPage);
                }
                if (responsavel.isCsaCor()) {
                    criterio.setAttribute(Columns.COR_CSA_CODIGO, responsavel.getCsaCodigo());
                }
                if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                    criterio.setAttribute(Columns.COR_CODIGO, responsavel.getCodigoEntidade());
                }
                if (!criterio.getAtributos().isEmpty()) {
                    // Lista somente os correspondentes ativos ou bloqueados
                    final List<Short> statusCor = new ArrayList<>();
                    statusCor.add(CodedValues.STS_ATIVO);
                    statusCor.add(CodedValues.STS_INATIVO);
                    statusCor.add(CodedValues.STS_INATIVO_CSE);
                    criterio.setAttribute(Columns.COR_ATIVO, statusCor);

                    correspondentes = consignatariaController.lstCorrespondentes(criterio, responsavel);
                }
            } catch (final ConsignatariaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return correspondentes;
    }

    private List<TransferObject> carregarListaServicos(boolean servicosComPrazo, List<String> nseCodigos, List<String> marCodigos, String csaCodigo, AcessoSistema responsavel) {
        List<TransferObject> servicos = null;
        try {
            CustomTransferObject criterio = null;
            if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
                servicos = servicoController.lstServicoByNaturezas(nseCodigos, responsavel);
            } else if (!responsavel.isCseSup()) {
                criterio = new CustomTransferObject();
                if (responsavel.isCsa()) {
                    criterio.setAttribute(Columns.CNV_CSA_CODIGO, responsavel.getCodigoEntidade());
                }
                if (responsavel.isCor()) {
                    criterio.setAttribute(Columns.CNV_CSA_CODIGO, responsavel.getCodigoEntidadePai());
                }
                if (responsavel.isOrg()) {
                    criterio.setAttribute(Columns.CNV_ORG_CODIGO, responsavel.getCodigoEntidade());
                }
                if (responsavel.isSer()) {
                    criterio.setAttribute(Columns.CNV_ORG_CODIGO, responsavel.getOrgCodigo());
                }

                criterio.setAttribute("verificaConvenioPossuiContratos", true);
                servicos = convenioController.listCnvScvCodigo(criterio, responsavel);

            } else if (responsavel.isCseSupOrg() && servicosComPrazo) {
                servicos = simulacaoController.getSvcPrazo(null, null, false, responsavel);
            } else {
                criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.MAR_CODIGO, marCodigos);
                if(!TextHelper.isNull(csaCodigo)) {
                    final String[] csaCodigoArray = csaCodigo.split(";");
                    if(csaCodigoArray.length > 0) {
                        criterio.setAttribute(Columns.CNV_CSA_CODIGO, csaCodigoArray[0]);
                    } else {
                        criterio.setAttribute(Columns.CNV_CSA_CODIGO, csaCodigo);
                    }
                    criterio.setAttribute("FILTRO_RELATORIO", Boolean.TRUE);
                    servicos = convenioController.listCnvScvCodigo(criterio, responsavel);
                } else {
                    servicos = convenioController.lstServicos(criterio, responsavel);
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return servicos;
    }

    private List<TransferObject> carregarListaSubOrgaos(String orgCodigo, AcessoSistema responsavel) {
        List<TransferObject> listaSubOrgaos = null;
        if (!TextHelper.isNull(orgCodigo)) {
            final String orgCodigoFiltro = orgCodigo.split(";")[0];
            try {
                listaSubOrgaos = servidorController.lstSubOrgao(responsavel, orgCodigoFiltro);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return listaSubOrgaos;
    }

    private List<TransferObject> carregarListaUnidade(String sboCodigo, AcessoSistema responsavel) {
        List<TransferObject> listaUnidade = null;
        if (!TextHelper.isNull(sboCodigo)) {
            final String sboCodigoFiltro = sboCodigo.split(";")[0];
            try {
                listaUnidade = servidorController.lstUnidade(responsavel, sboCodigoFiltro);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return listaUnidade;
    }
}
