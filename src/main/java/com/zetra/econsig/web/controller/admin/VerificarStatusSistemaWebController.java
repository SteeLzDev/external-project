package com.zetra.econsig.web.controller.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusAgendamentoEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: VerificarStatusSistemaWebController</p>
 * <p>Description: Controlador Web para a página de status do sistema.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class VerificarStatusSistemaWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VerificarStatusSistemaWebController.class);

    @Autowired
    private AgendamentoController agendamentoController;

    @Autowired
    private ConsignanteController consignanteController;

    @RequestMapping(value = { "/v3/verificarStatusSistema" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        String ipsAcessoLiberado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO, responsavel);
        if (TextHelper.isNull(ipsAcessoLiberado)) {
            ipsAcessoLiberado = "127.0.0.1";
        }

        if (!JspHelper.validaDDNS(JspHelper.getRemoteAddr(request), ipsAcessoLiberado)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // recupera lista de exceções de status a verificar na tela, de acordo com parâmetro de sistema.
        String excecoes = request.getParameter("desconsiderar");
        List<String> lstExcecoes = null;
        if (!TextHelper.isNull(excecoes)) {
            String[] excArray = excecoes.split(",|;");
            lstExcecoes = new ArrayList<>();
            for (int i = 0; i < excArray.length; i++) {
                lstExcecoes.add(i, excArray[i].trim());
            }
        }

        /*
         * debugLevel é um parametro opcional, que pode assumir os valores:
         * - 0: interrompe a verificação no primeiro erro e mostra somente OK ou ERRO como resposta.
         * - 1: interrompe a verificação no primeiro erro e mostra qual item deu erro.
         * - 2: faz a verificação de todos os itens, mostrando a situação de cada item.
         * - 3: faz a verificação do status do sistema.
         */
        int debugLevel = 0;
        try {
            debugLevel = Integer.parseInt(request.getParameter("debugLevel"));
        } catch (Exception ex) {
        }

        /*
         * Armazena os resultados de cada um dos testes efetuados, para depois
         * serem impressos na página
         */
        Map<String, String> result = new HashMap<>();

        String[] tests = { "JBOSS-EJB", "STATUS", "VERSION", "LOG", "BACKUP", "AGENDAMENTO", "FILESYSTEM-SPACE" };
        String[] testsNames = { ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel), ApplicationResourcesHelper.getMessage("rotulo.status.status", responsavel), ApplicationResourcesHelper.getMessage("rotulo.status.versao", responsavel), ApplicationResourcesHelper.getMessage("rotulo.status.log", responsavel), ApplicationResourcesHelper.getMessage("rotulo.status.backup", responsavel), ApplicationResourcesHelper.getMessage("rotulo.status.agendamento", responsavel), ApplicationResourcesHelper.getMessage("rotulo.status.espaco.disco", responsavel) };

        if (lstExcecoes != null && !lstExcecoes.isEmpty()) {
            List<String> testsList = new ArrayList<>();
            List<String> testsNamesList = new ArrayList<>();

            for (int i = 0; i < tests.length; i++) {
                if (!lstExcecoes.contains(tests[i])) {
                    testsList.add(tests[i]);
                    testsNamesList.add(testsNames[i]);
                }
            }

            tests = new String[testsList.size()];
            testsNames = new String[testsList.size()];
            for (int i = 0; i < testsList.size(); i++) {
                String remanescente = testsList.get(i);
                String nome_rem = testsNamesList.get(i);
                tests[i] = remanescente;
                testsNames[i] = nome_rem;
            }
        }

        // recupera a lista de testes a fazer já sem as exceções, se houver
        List<String> testesEfetivos = null;

        if (debugLevel == 3) {
            testesEfetivos = new ArrayList<>();
            testesEfetivos.add("JBOSS-EJB");
            testesEfetivos.add("STATUS");
        } else {
            testesEfetivos = Arrays.asList(tests);
        }

        // LOG
        if (testesEfetivos.contains("LOG")) {
            try {
                // recupera o último log da data atual e iforma erro caso o log não exista
                LogDelegate logDelegate = new LogDelegate();
                List<TransferObject> logDataAtual = logDelegate.getLogDataAtual();

                if (logDataAtual == null || logDataAtual.size() == 0) {
                    result.put("LOG", "ERRO");
                } else {
                    TransferObject cto = logDataAtual.get(0);
                    java.util.Date logData = (java.util.Date) cto.getAttribute(Columns.LOG_DATA);

                    if (TextHelper.isNull(logData)) {
                        result.put("LOG", "ERRO");
                    } else {
                        result.put("LOG-OBS", DateHelper.toDateTimeString(logData));
                        result.put("LOG", "OK");
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                result.put("LOG", "ERRO");
            }

            if (result.get("LOG").equals("ERRO") && debugLevel < 2) {
                return redirecionarStatusSemFormatacao((debugLevel > 0) ? ApplicationResourcesHelper.getMessage("rotulo.status.log", responsavel) + ":" + ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel), request, response, session, model);
            }
        }

        // BACKUP
        if (testesEfetivos.contains("BACKUP")) {
            try {
                // recupera o último backup do consignante e verifica a sua validade
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CSE_CODIGO, CodedValues.CSE_CODIGO_SISTEMA);
                criterio.setAttribute(Columns.TOC_CODIGO, CodedValues.TOC_BACKUP_BASE_DADOS);
                int count = 0;
                int offset = 1;
                List<TransferObject> lstOcorrencia = consignanteController.lstOcorrenciaConsignante(criterio, count, offset, responsavel);

                if (lstOcorrencia == null || lstOcorrencia.size() == 0) {
                    result.put("BACKUP", "ERRO");
                } else {
                    TransferObject cto = lstOcorrencia.get(0);
                    java.util.Date oceData = (java.util.Date) cto.getAttribute(Columns.OCE_DATA);

                    if (TextHelper.isNull(oceData)) {
                        result.put("BACKUP", "ERRO");
                    } else {
                        result.put("BACKUP-OBS", DateHelper.toDateTimeString(oceData));
                        // se a diferença entre a data do backup e a data atual for maior que 1 dia será considerado inválido
                        result.put("BACKUP", (DateHelper.dayDiff(oceData) > 1 ? "ERRO" : "OK"));
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                result.put("BACKUP", "ERRO");
            }

            if (result.get("BACKUP").equals("ERRO") && debugLevel < 2) {
                return redirecionarStatusSemFormatacao((debugLevel > 0) ? ApplicationResourcesHelper.getMessage("rotulo.status.backup", responsavel) + ":" + ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel), request, response, session, model);
            }
        }

        // JBOSS-EJB
        if (testesEfetivos.contains("JBOSS-EJB")) {
            boolean ok = true;
            try {
                ConsignanteTransferObject cto = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                if (testesEfetivos.contains("STATUS")) {
                    boolean indisponivel = cto.getCseAtivo().equals(CodedValues.STS_INDISP) || (ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel).getGrauRestricao() == ControleRestricaoAcesso.GrauRestricao.RestricaoGeral);
                    if (indisponivel) {
                        result.put("STATUS", "ERRO");
                        result.put("STATUS-OBS", "<font style=\"FONT-WEIGHT: bold\" color=\"red\">" + LoginHelper.getMensagemSistemaIndisponivel() + "</font>");
                    } else {
                        result.put("STATUS", "OK");
                        result.put("STATUS-OBS", ApplicationResourcesHelper.getMessage("rotulo.status.disponivel", responsavel));
                    }

                    if (result.get("STATUS").equals("ERRO") && debugLevel < 2) {
                        return redirecionarStatusSemFormatacao((debugLevel > 0) ? ApplicationResourcesHelper.getMessage("rotulo.status.status", responsavel) + ":" + ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel), request, response, session, model);
                    } else if (debugLevel == 3) {
                        return redirecionarStatusSemFormatacao(result.get("STATUS").equals("ERRO") ? ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.status.ok", responsavel), request, response, session, model);
                    }
                }

                String cseNome = cto.getCseNome();
                if (TextHelper.isNull(cseNome)) {
                    ok = false;
                } else {
                    result.put("JBOSS-EJB-OBS", cseNome);
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                ok = false;
            }
            result.put("JBOSS-EJB", (ok ? "OK" : "ERRO"));

            if (result.get("JBOSS-EJB").equals("ERRO") && debugLevel < 2) {
                return redirecionarStatusSemFormatacao((debugLevel > 0) ? ApplicationResourcesHelper.getMessage("rotulo.status.ejb", responsavel) + ":" + ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel), request, response, session, model);
            }
        }

        // FILESYSTEM
        if (testesEfetivos.contains("FILESYSTEM-SPACE")) {
            String path = ParamSist.getDiretorioRaizArquivos();
            File dir = new File(path);
            if (!dir.exists() || dir.list() == null || dir.list().length <= 0) {
                result.put("FILESYSTEM-SPACE", "ERRO");
                String erro = "";
                if (!dir.exists()) {
                    erro = ApplicationResourcesHelper.getMessage("mensagem.status.erro.diretorio.raiz.existe", responsavel);
                } else if (dir.list() != null) {
                    erro = ApplicationResourcesHelper.getMessage("mensagem.status.erro.diretorio.raiz.leitura", responsavel);
                } else {
                    erro = ApplicationResourcesHelper.getMessage("mensagem.status.erro.diretorio.raiz.vazio", responsavel);
                }
                result.put("FILESYSTEM-SPACE-OBS", "<font style=\"FONT-WEIGHT: bold\" color=\"red\">" + erro + "</font>");
            } else {
                // FILESYSTEM FREE SPACE
                StringBuilder message = new StringBuilder("<pre>");
                result.put("FILESYSTEM-SPACE", "OK");
                List<File> allPartitions = FileHelper.getFileSystemPartitions();
                List<File> partitions = new ArrayList<>();

                // Remove diretórios que não devem ser exibidos
                final String REGEX_DIRETORIOS_NAO_EXIBIR = ".*(jail|caagent).*";

                for (int i = 0; i < allPartitions.size(); i++) {
                    File arquivo = allPartitions.get(i);
                    if (!arquivo.getAbsolutePath().matches(REGEX_DIRETORIOS_NAO_EXIBIR)) {
                        partitions.add(arquivo);
                    }
                }

                for (File testFile : partitions) {
                    double totalSpace = testFile.getTotalSpace();
                    double freeSpace = testFile.getFreeSpace();
                    double free = (freeSpace / totalSpace) * 100.00;
                    boolean criticalSize = (free < 10);
                    if (debugLevel > 1) {
                        message.append(TextHelper.formataMensagem(testFile.getAbsolutePath(), " ", 16, true) + " - " + ApplicationResourcesHelper.getMessage("rotulo.status.free", responsavel) + ": <font face=\"Arial\" style=\"FONT-WEIGHT: bold\" color=\"" + (criticalSize ? "red" : "black") + "\">" + Math.floor(free) + " %</font>\n");
                    }
                    if (criticalSize) {
                        result.put("FILESYSTEM-SPACE", "ERRO");
                    }
                }
                message.append("</pre>");
                result.put("FILESYSTEM-SPACE-OBS", message.toString());
            }

            if (result.get("FILESYSTEM-SPACE").equals("ERRO") && debugLevel < 2) {
                return redirecionarStatusSemFormatacao((debugLevel > 0) ? ApplicationResourcesHelper.getMessage("rotulo.status.space", responsavel) + ":" + ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel), request, response, session, model);
            }
        }

        // VERSAO ATUAL
        if (testesEfetivos.contains("VERSION")) {
            List<String> versoesEstaveis = new ArrayList<>();
            // recupera a url com as versões estáveis do sistema
            String urlVersoesEstaveis = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_VERSOES_ESTAVEIS, responsavel);
            if (!TextHelper.isNull(urlVersoesEstaveis)) {
                try {
                    // recupera as versões estáveis do sistema
                    URL vURL = URI.create(urlVersoesEstaveis).toURL();
                    HttpURLConnection connection = (HttpURLConnection) vURL.openConnection();
                    if (connection != null) {
                        BufferedReader rd = null;
                        rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line = null;
                        while ((line = rd.readLine()) != null) {
                            if (line.trim().length() > 0) {
                                versoesEstaveis.add(line);
                            }
                        }
                        rd.close();
                    }
                } catch (Exception ex) {
                    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.status.info.acesso.versoes.estaveis", responsavel));
                }
            }

            // VERSAO
            String versao = ApplicationResourcesHelper.getMessage("release.tag", responsavel);
            if (!TextHelper.isNull(versao)) {
                if (versoesEstaveis.isEmpty() || versoesEstaveis.contains(versao)) {
                    result.put("VERSION", "OK");
                } else {
                    result.put("VERSION", "ERRO");
                }
                result.put("VERSION-OBS", versao);
            } else {
                result.put("VERSION", "ERRO");
            }

            if (result.get("VERSION").equals("ERRO") && debugLevel < 2) {
                return redirecionarStatusSemFormatacao((debugLevel > 0) ? ApplicationResourcesHelper.getMessage("rotulo.status.version", responsavel) + ":" + ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel), request, response, session, model);
            }
        }

        // AGENDAMENTO
        if (testesEfetivos.contains("AGENDAMENTO")) {
            try {
                List<String> sagCodigos = new ArrayList<>();
                sagCodigos.add(StatusAgendamentoEnum.EXECUCAO_DIARIA.getCodigo());

                List<String> tagCodigos = new ArrayList<>();
                tagCodigos.add(TipoAgendamentoEnum.PERIODICO_DIARIO.getCodigo());

                // qtde de horas limite para
                int horasLimite = 30;

                int totalAgendamentos = 0;
                totalAgendamentos = agendamentoController.countOcorrenciaAgendamentoComErro(null, sagCodigos, tagCodigos, horasLimite, responsavel);

                if (totalAgendamentos == 0) {
                    result.put("AGENDAMENTO-OBS", ApplicationResourcesHelper.getMessage("rotulo.status.ok", responsavel));
                    result.put("AGENDAMENTO", "OK");
                } else {
                    result.put("AGENDAMENTO-OBS", ApplicationResourcesHelper.getMessage("mensagem.status.processos.pendentes", responsavel, Integer.toString(totalAgendamentos)));
                    result.put("AGENDAMENTO", "ERRO");
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                result.put("AGENDAMENTO", "ERRO");
            }

            if (result.get("AGENDAMENTO").equals("ERRO") && debugLevel < 2) {
                return redirecionarStatusSemFormatacao((debugLevel > 0) ? ApplicationResourcesHelper.getMessage("rotulo.status.agendamento", responsavel) + ":" + ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.status.erro", responsavel), request, response, session, model);
            }
        }

        if (debugLevel < 2) {
            return redirecionarStatusSemFormatacao(ApplicationResourcesHelper.getMessage("rotulo.status.ok", responsavel), request, response, session, model);
        }

        model.addAttribute("tests", tests);
        model.addAttribute("testsNames", testsNames);
        model.addAttribute("result", result);

        return viewRedirectNoSuffix("jsp/verificarStatusSistema/exibirStatusSistemaComFormatacao", request, session, model, AcessoSistema.getAcessoUsuarioSistema());
    }

    private String redirecionarStatusSemFormatacao(String resultado, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("resultado", resultado);
        return viewRedirectNoSuffix("jsp/verificarStatusSistema/exibirStatusSistemaSemFormatacao", request, session, model, AcessoSistema.getAcessoUsuarioSistema());
    }
}
