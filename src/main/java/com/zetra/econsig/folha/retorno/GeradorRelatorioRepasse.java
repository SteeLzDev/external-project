package com.zetra.econsig.folha.retorno;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.job.process.ProcessaRelatorio;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.RelatorioDAO;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.RelatorioRepasse;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: GeradorRelatorioRepasse</p>
 * <p> Description: Classe para geração do relatorio repasse.</p>
 * <p> Copyright: Copyright (c) 2014 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class GeradorRelatorioRepasse extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeradorRelatorioRepasse.class);

    private final AcessoSistema responsavel;

    public GeradorRelatorioRepasse(AcessoSistema responsavel) {
        super(null, null, null, false, responsavel);

        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        List<TransferObject> repasse = null;

        try {
            RelatorioDAO relatorioDAO = DAOFactory.getDAOFactory().getRelatorioDAO();
            repasse = relatorioDAO.selectRepasse();
        } catch (DAOException e) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.nao.possivel.gerar.relatorio.repasse", responsavel), e);
            return;
        }

        if (repasse != null && repasse.size() > 0) {
            try {
                String absolutePath = ParamSist.getDiretorioRaizArquivos();

                // Path e nome dos arquivos de relatório
                String pathSaidaRelatorios = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "repasse";

                File dirPathRelatorios = new File(pathSaidaRelatorios);
                if (!dirPathRelatorios.exists() && !dirPathRelatorios.mkdir()) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.arquivos.relatorio.repasse.nao.existe.nao.pode.ser.criado", responsavel));
                    return;
                }

                String nomeArqSaida = pathSaidaRelatorios + File.separatorChar + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.repasse", responsavel) + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");

                String pathCabRelatoriosDif = absolutePath + File.separatorChar + "txt" + File.separatorChar + "relatorio";

                String nomeArqTemplateCab = pathCabRelatoriosDif + File.separatorChar + "repasse_cabecalho.txt";
                String nomeArqTemplateRod = pathCabRelatoriosDif + File.separatorChar + "repasse_rodape.txt";

                // Configurações do Relatório PDF
                String fields[] = { "CSA_IDENTIFICADOR", "CSA_NOME", "VALOR_APURADO", "VALOR_TARIFACAO", "VALOR_LIQUIDO", "ORDEM_EXTRA" };

                Calendar periodoAtual = Calendar.getInstance();
                Calendar dataAtual = Calendar.getInstance();
                try {
                    PeriodoDelegate perDelegate = new PeriodoDelegate();
                    TransferObject periodoExportacao = perDelegate.obtemPeriodoExportacaoDistinto(null, null, responsavel);
                    periodoAtual.setTime((Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO));
                } catch (PeriodoException e) {
                    LOG.error("Erro ao recuperar período atual", e);
                    periodoAtual = dataAtual;
                }

                String subtitulo = null;
                String periodo = ApplicationResourcesHelper.getMessage("mensagem.informacao.gerar.relatorio.repasse.cabecalho.direita", responsavel, DateHelper.toPeriodMesExtensoString(periodoAtual.getTime()));
                String rodape_esquerda = (DateHelper.toDateTimeString(dataAtual.getTime()));

                // Cria um buffer que vai armazenar o texto do cabecalho do centro do relatorio
                StringBuilder sb = new StringBuilder();

                // verifica se o arquivo que contem o template do cabecalho central existe
                boolean existsArqCab = (new File(nomeArqTemplateCab)).exists();
                if (existsArqCab) {
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(nomeArqTemplateCab));
                        String linha = null;

                        // verifica se o template contendo o cabecalho central contém conteúdo
                        while ((linha = in.readLine()) != null) {
                            sb.append(linha + "\n");
                        }
                        in.close();
                        subtitulo = sb.toString();
                    } catch (FileNotFoundException ex) {
                        LOG.error(ex.getMessage(), ex);
                        LOG.debug("O arquivo '" + nomeArqTemplateCab + "' não foi encontrado.");
                    } catch (IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        LOG.debug("Erro ao processar o arquivo '" + nomeArqTemplateCab + "'.");
                    }
                }

                // verifica se o arquivo que contem o template do rodape esquerdo existe
                boolean existsArqRod = (new File(nomeArqTemplateRod)).exists();
                if (existsArqRod) {
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(nomeArqTemplateRod));
                        String linha = in.readLine();
                        if (linha != null) {
                            rodape_esquerda = (DateHelper.format(dataAtual.getTime(), LocaleHelper.getDateTimePattern())).concat("    ").concat(linha);
                        }
                        in.close();
                    } catch (FileNotFoundException ex) {
                        LOG.error(ex.getMessage(), ex);
                        LOG.debug("O arquivo '" + nomeArqTemplateRod + "' não foi encontrado.");
                    } catch (IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        LOG.debug("Erro ao processar o arquivo '" + nomeArqTemplateRod + "'.");
                    }
                }

                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, rodape_esquerda);
                parameters.put("SUBTITULO", subtitulo);
                parameters.put("PERIODO", periodo);

                Relatorio relatorio = new Relatorio("repasse", ApplicationResourcesHelper.getMessage("rotulo.relatorio.repasse.titulo", responsavel), "", RelatorioRepasse.class.getName(), null, "Repasse.jasper", "", "", "", "", "", true, false, "N", null);
                ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);

                // RELATORIO ESTATISTICO PDF
                parameters.put("FORMATO_ARQUIVO", "PDF");
                String reportName = reportController.makeReport("PDF", null, parameters, relatorio, DTOToList(repasse, fields), responsavel);
                FileHelper.rename(pathSaidaRelatorios + reportName, nomeArqSaida + ".pdf");

                // RELATORIO ESTATISTICO TXT
                parameters.put("FORMATO_ARQUIVO", "TEXT");
                reportName = reportController.makeReport("TEXT", null, parameters, relatorio, DTOToList(repasse, fields), responsavel);
                FileHelper.rename(pathSaidaRelatorios + reportName, nomeArqSaida + ".txt");

                if(ParamSist.paramEquals(CodedValues.TPC_GERA_RELATORIO_INTEGRACAO_XLS, CodedValues.TPC_SIM, responsavel)){
                    parameters.put("FORMATO_ARQUIVO", "XLS");
                    reportName = reportController.makeReport("XLS", null, parameters, relatorio, DTOToList(repasse, fields), responsavel);
                    FileHelper.rename(pathSaidaRelatorios + reportName, nomeArqSaida + ".xls");
                }

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}