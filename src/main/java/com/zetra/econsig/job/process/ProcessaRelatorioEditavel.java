package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dinamico.RelatorioEditavelInfo;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioEditavel</p>
 * <p> Description: Classe para processamento de relatorios editáveis.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioEditavel extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioEditavel.class);

    public ProcessaRelatorioEditavel(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
    }

    @Override
    protected void executar() {
        try {
            StringBuilder nomeRelatorio = new StringBuilder(getNomeArquivo(relatorio.getTipo(), responsavel, parameterMap, null));
            String titulo = relatorio.getTitulo();
            StringBuilder subTitulo = new StringBuilder();

            //          campo_data_periodo
            Map<String, String> periodo = getFiltroPeriodo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_matricula
            String rseMatricula = getFiltroRseMatricula(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_cpf
            String cpf = getFiltroCpf(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_entidade campo_entidade_usu
            //          campo_cse
            String cseCodigo = getFiltroCseCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_csa campo_csa_compra campo_csa_gestor campo_csa_selec
            String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_cor campo_cor_gestor
            String corCodigo = getFiltroCorCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_org
            List<String> orgCodigo = getFiltroOrgCodigoIn(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_est
            String estCodigo = getFiltroEstCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_svc campo_svc_selec campo_svc_taxas
            List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_natureza_svc
            List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_status_convenio
            List<String> scvCodigos = getFiltroScvCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_status_contrato
            List<String> sadCodigos = getFiltroSadCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_status_login
            List<String> stuCodigos = getFiltroStuCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_status_parcela
            List<String> spdCodigos = getFiltroSpdCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_status_servidor
            List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_empresa_cor campo_emp_cor
            String ecoCodigo = getFiltroEcoCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_funcao
            List<String> funCodigos = getFiltroFunCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_tipo_agendamento
            List<String> tagCodigos = getFiltroTagCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_tipo_ocorrencia
            List<String> tocCodigos = getFiltroTocCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_tipo_motivo_operacao
            List<String> tmoCodigos = getFiltroTmoCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_grupo_servico
            String tgsCodigos = getFiltroTgsCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_ordenacao campo_ordenacao_ade campo_ordenacao_mov_fin campo_taxas
            String ordenacao = getFiltroOrdenacao(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_pendencia_vencida
            Boolean pendenciaVencida = getFiltroPendenciaVencida(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_tipo_penalidade
            List<String> tpeCodigos = getFiltroTpeCodigo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_origem_contrato
            List<String> origemAde = getFiltroOrigemContrato(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_info_group campo_info_group_ade
            List<String> campos = getFiltroCampos(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_termino_contrato
            List<String> termino = getFiltroTermino(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_op_login
            String opLogin = getFiltroLoginResponsavel(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_periodicidade
            String periodicidade = getFiltroPeriodicidade(parameterMap, subTitulo, nomeRelatorio, session, responsavel);
            //          campo_formato_relatorio campo_formato_relatorio_pdf
            String formato = getStrFormato();
            //          campo_nome
            String nome = getFiltroNome(parameterMap, subTitulo, nomeRelatorio, session, responsavel);

            if(responsavel.isOrg() && relatorio.getTemplateSql().contains("<@campo_nome>")) {
                nome = responsavel.getUsuNome();
            }

            if(responsavel.isOrg() && relatorio.getTemplateSql().contains("<@campo_op_login>")) {
                opLogin = responsavel.getUsuLogin();
            }


            HashMap<String, Object> parameters = new HashMap<>();

            //TODO Melhorar a implementação das chaves que serão utilizadas para setar as cláusulas na query
            criterio.setAttribute("<@campo_data_periodo>", periodo.get("PERIODO"));
            criterio.setAttribute("<@campo_data_inclusao_ini>", periodo.get("PERIODO_INICIAL"));
            criterio.setAttribute("<@campo_data_inclusao_fim>", periodo.get("PERIODO_FINAL"));
            criterio.setAttribute("<@campo_matricula>", rseMatricula);
            criterio.setAttribute("<@campo_cpf>", cpf);
            criterio.setAttribute("<@campo_cse>", cseCodigo);
            criterio.setAttribute("<@campo_csa>", csaCodigo);
            criterio.setAttribute("<@campo_cor>", corCodigo);
            criterio.setAttribute("<@campo_org>", orgCodigo);
            criterio.setAttribute("<@campo_est>", estCodigo);
            criterio.setAttribute("<@campo_svc>", svcCodigos);
            criterio.setAttribute("<@campo_natureza_svc>", nseCodigos);
            criterio.setAttribute("<@campo_status_convenio>", scvCodigos);
            criterio.setAttribute("<@campo_status_contrato>", sadCodigos);
            criterio.setAttribute("<@campo_status_login>", stuCodigos);
            criterio.setAttribute("<@campo_status_parcela>", spdCodigos);
            criterio.setAttribute("<@campo_status_servidor>", srsCodigos);
            criterio.setAttribute("<@campo_empresa_cor>", ecoCodigo);
            criterio.setAttribute("<@campo_funcao>", funCodigos);
            criterio.setAttribute("<@campo_tipo_agendamento>", tagCodigos);
            criterio.setAttribute("<@campo_tipo_ocorrencia>", tocCodigos);
            criterio.setAttribute("<@campo_tipo_motivo_operacao>", tmoCodigos);
            criterio.setAttribute("<@campo_grupo_servico>", tgsCodigos);
            criterio.setAttribute("<@campo_ordenacao>", ordenacao);
            criterio.setAttribute("<@campo_pendencia_vencida>", pendenciaVencida);
            criterio.setAttribute("<@campo_tipo_penalidade>", tpeCodigos);
            criterio.setAttribute("<@campo_origem_contrato>", origemAde);
            criterio.setAttribute("<@campo_info_group>", campos);
            criterio.setAttribute("<@campo_termino_contrato>", termino);
            criterio.setAttribute("<@campo_op_login>", opLogin);
            criterio.setAttribute("<@campo_periodicidade>", periodicidade);
            criterio.setAttribute("<@campo_nome>", nome);

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio.toString());
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, formato);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());

            if (!TextHelper.isNull(relatorio.getModeloDinamico())) {
                RelatorioEditavelInfo relEditavelInfo = new RelatorioEditavelInfo(relatorio);
                relEditavelInfo.setCriterios(criterio);
                relEditavelInfo.buildJRXML(parameters, responsavel);
            }

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(formato, criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nomeRelatorio.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

}
