package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.HistoricoArquivoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioDocumentoBeneficiarioTipoValidade</p>
 * <p>Description: Classe para processamento de relatorio de Documentos do Beneficiário por Tipo e Validade
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public final class ProcessaRelatorioDocumentoBeneficiarioTipoValidade extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioDocumentoBeneficiarioTipoValidade.class);

    public ProcessaRelatorioDocumentoBeneficiarioTipoValidade(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

    }

    @Override
    protected void executar() {
        try {
            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.exclusao.documento.beneficiario.tipo.validade", responsavel), responsavel, parameterMap, null);
            String strFormato = getStrFormato();
            StringBuilder subTitulo = new StringBuilder();

            HistoricoArquivoDelegate hadDelegate = new HistoricoArquivoDelegate();

            List<TransferObject> tipoDocumentoDescricao = null;

            // Pega as datas corretamente
            Map<String, String> datas = getFiltroPeriodo(parameterMap, subTitulo, null, session, responsavel);
            String dataIni = datas.get("PERIODO_INICIAL");
            String dataFim = datas.get("PERIODO_FINAL");

            String tipoDocumento = getParametro("tipoDocumento", parameterMap);
            List<String> tipoDocumentoList = null;

            if (tipoDocumento != null && !TextHelper.isNull(tipoDocumento)) {
                String aux[] = tipoDocumento.split(",");
                tipoDocumentoList = Arrays.asList(aux);

                tipoDocumentoDescricao = hadDelegate.lstTiposArquivoByTarCodigos(tipoDocumentoList, responsavel);
            } else {

                List<String> tarCodigos = new ArrayList<>();
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

                tipoDocumentoDescricao = hadDelegate.lstTiposArquivoByTarCodigos(tarCodigos, responsavel);
            }

            String tipoDocumentoDescricaoString = ApplicationResourcesHelper.getMessage("rotulo.anexo.beneficiario.tipo.documento", responsavel) + ": ";

            for (TransferObject td : tipoDocumentoDescricao) {
                tipoDocumentoDescricaoString += td.getAttribute(Columns.TAR_DESCRICAO);
                if (!td.getAttribute(Columns.TAR_CODIGO).equals(tipoDocumentoDescricao.get(tipoDocumentoDescricao.size() - 1).getAttribute(Columns.TAR_CODIGO))) {
                    tipoDocumentoDescricaoString += ", ";
                }
            }

            criterio.setAttribute("dataIni", dataIni);
            criterio.setAttribute("dataFim", dataFim);
            criterio.setAttribute("tipoDocumento", tipoDocumentoList);

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo().toUpperCase());
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put("TIPODOCUMENTO", tipoDocumentoDescricaoString);
            parameters.put("RESPONSAVEL", responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

            geraZip(nome.toString(), reportName);

        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}
