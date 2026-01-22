package com.zetra.econsig.job.process;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.AlterarMultiplasConsignacoesParametros;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.MargemAlteracaoMultiplaAdeBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioAlteracaoMultiplasConsignacoes</p>
 * <p> Description: Classe para processamento do Relatório de Alteração de Multiplas Consignações</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioAlteracaoMultiplasConsignacoes extends ProcessaRelatorio {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioAlteracaoMultiplasConsignacoes.class);

    private List<TransferObject> ades;
    private AlterarMultiplasConsignacoesParametros parametros;
    private List<MargemTO> margemAntes;
    private List<MargemTO> margemDepois;

    public ProcessaRelatorioAlteracaoMultiplasConsignacoes(List<TransferObject> ades, AlterarMultiplasConsignacoesParametros parametros, List<MargemTO> margemAntes, List<MargemTO> margemDepois, AcessoSistema responsavel) {
        super(ConfigRelatorio.getInstance().getRelatorio("alteracao_multiplas_ade"), new HashMap<String, String[]>(), null, false, responsavel);

        this.ades = ades;
        this.parametros = parametros;
        this.margemAntes = margemAntes;
        this.margemDepois = margemDepois;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    public ProcessaRelatorioAlteracaoMultiplasConsignacoes(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
    }

    @Override
    protected void executar() {
        try {
            String hoje = getHoje("ddMMyyHHmmss");
            String titulo = relatorio.getTitulo();
            StringBuilder subtitulo = new StringBuilder();

            TransferObject ade = null;
            LinkedHashSet<Short> incidencias = new LinkedHashSet<>();
            if (ades != null && !ades.isEmpty()) {
                ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);

                ade = ades.get(0);

                for (TransferObject autorizacao : ades) {
                    incidencias.add(Short.valueOf(autorizacao.getAttribute(Columns.ADE_INC_MARGEM).toString()));
                    ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(autorizacao.getAttribute(Columns.CSA_CODIGO).toString(), responsavel);
                    autorizacao.setAttribute(Columns.CSA_NOME, consignataria.getCsaNome());
                }
            }

            ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

            String serCodigo = ade.getAttribute(Columns.SER_CODIGO).toString();
            ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

            String serNome = servidor.getSerNome();
            String serCpf = !TextHelper.isNull(servidor.getSerCpf()) ? servidor.getSerCpf() : "";
            String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA).toString();

            // Remove formatação do cpf, somente numérico
            String cpfNormalizado = !TextHelper.isNull(serCpf) ? serCpf.replaceAll("[^a-zA-Z0-9]", "") : "";
            // Remove caracter especial do nome do servidor
            String nomeNormalizado = Normalizer.normalize(serNome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "_").toLowerCase();

            // Nome do relatório será a concatenação do nome do servidor, matrícula e cpf
            String nome = nomeNormalizado + "_" + rseMatricula + (!TextHelper.isNull(cpfNormalizado) ? "_" + cpfNormalizado : "") + "_" + hoje;
            String strFormato = "PDF";
            String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

            if (!parametros.isRestaurarValor() && (parametros.getVlrTotalNovo() == null || parametros.getVlrTotalNovo().signum() <= 0)) {
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("mensagem.erro.alterar.multiplo.consignacao.valor.total.invalido", responsavel));
            }

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(true, session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

            List<Object[]> conteudo = DTOToList(ades, new String[] {
                    Columns.CSA_NOME,
                    Columns.ADE_NUMERO,
                    Columns.SVC_DESCRICAO,
                    Columns.ADE_VLR,
                    "adeVlrNovo",
                    Columns.ADE_PRAZO,
                    "adePrazoNovo",
                    "vlrParcelaExtra"
            });

            List<MargemAlteracaoMultiplaAdeBean> margens = new ArrayList<>();

            Map<Short, BigDecimal> mapMargemAntes = new HashMap<>();
            for (MargemTO margemTO : margemAntes) {
                mapMargemAntes.put(margemTO.getMarCodigo(), margemTO.getMrsMargemRest());
            }

            for (MargemTO margemTO : margemDepois) {
                if (incidencias.contains(margemTO.getMarCodigo())) {
                    ExibeMargem exibeMargem = new ExibeMargem(margemTO, responsavel);
                    margens.add(new MargemAlteracaoMultiplaAdeBean(margemTO.getMarCodigo(), margemTO.getMarDescricao(), mapMargemAntes.get(margemTO.getMarCodigo()), margemTO.getMrsMargemRest(), exibeMargem.isExibeValor(), exibeMargem.isSemRestricao()));
                }
            }

            parameters.put("SER_NOME", serNome);
            parameters.put("SER_CPF", serCpf);
            parameters.put("RSE_MATRICULA", rseMatricula);

            String margemLimite = !TextHelper.isNull(parametros.getMarCodigo()) ? MargemHelper.getInstance().getMarDescricao(parametros.getMarCodigo(), responsavel) : "";

            parameters.put("VLR_TOTAL_NOVO", parametros.getVlrTotalNovo());
            parameters.put("PERCENTUAL_MARGEM", parametros.getPercentualMargem());
            parameters.put("MARGEM_LIMITE", margemLimite);

            parameters.put("ALTERAR_PRAZO", parametros.isAlterarPrazo());
            parameters.put("RESTAURAR_VALOR", parametros.isRestaurarValor());
            parameters.put("BLOQUEAR_SERVIDOR", parametros.isBloquearServidor());
            parameters.put("DESBLOQUEAR_SERVIDOR", parametros.isDesbloquearServidor());
            parameters.put("BLOQUEAR_SERVIDOR_CONSIGNACOES", parametros.isBloquearRegistroServidor());
            parameters.put("DESBLOQUEAR_SERVIDOR_CONSIGNACOES", parametros.isDesbloquearRegistroServidor());

            parameters.put("MARGEM", margens);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, conteudo, responsavel);
            String reportNameZip = geraZip(nome.toString(), reportName);

            setMensagem(reportNameZip, relatorio.getTipo(), relatorio.getTitulo(), session);

        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

}
