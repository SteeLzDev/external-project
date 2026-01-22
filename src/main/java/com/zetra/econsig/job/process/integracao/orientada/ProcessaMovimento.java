package com.zetra.econsig.job.process.integracao.orientada;

import java.time.LocalDate;
import java.util.List;

import com.zetra.econsig.delegate.ExportaMovimentoDelegate;
import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.folha.ExportaMovimentoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaHistorico</p>
 * <p>Description: Classe para geração de histórico de teste para integração orientada</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaMovimento extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaMovimento.class);

    private final List<String> estCodigos;
    private final List<String> orgCodigos;
    private final List<String> verbas;
    private final String acao;
    private final String opcao;

    private final AcessoSistema responsavel;

    public ProcessaMovimento(List<String> estCodigos, List<String> orgCodigos, List<String> verbas, String acao, String opcao, AcessoSistema responsavel) {
        this.estCodigos = estCodigos;
        this.orgCodigos = orgCodigos;
        this.verbas = verbas;
        this.acao = acao;
        this.opcao = opcao;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        String horaInicioStr = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
        try {
            String[] tiposArquivo = {"arquivo único",
                                     "arquivos separados por 'entidade'",
                                     "arquivos separados por 'verba'",
                                     "arquivos separados por 'entidade' e 'verba'"};

            boolean habilitaAmbienteDeTestes = ParamSist.paramEquals(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, CodedValues.TPC_SIM, responsavel);

            LOG.debug("**** Integração Orientada ****");
            LOG.debug("Exportando o movimento para os seguintes parametros:");
            LOG.debug("Estabelecimentos.......: " + estCodigos);
            LOG.debug("Orgaos.................: " + orgCodigos);
            LOG.debug("Verbas.................: " + verbas);
            LOG.debug("Acao...................: " + acao);
            LOG.debug("Tipo de Arquivo........: " + opcao + " - " + tiposArquivo[Integer.parseInt(opcao) - 1]);
            LOG.debug("Responsavel............: " + (responsavel != null ? responsavel.getUsuCodigo() : ""));
            LOG.debug("Ambiente de Testes.....: " + habilitaAmbienteDeTestes);

            // Se for ambiente de testes temos que ajustar o periodo
            // Assim o usuario atual pode exportar o movimento dele para ver como o sistema funciona.
            if (habilitaAmbienteDeTestes) {
                LOG.debug("Ajustando o periodo para o usuario poder gerar o movimento");
                LocalDate localDate = LocalDate.now().minusDays(1);

                LOG.debug("Dia calculado para o ser setado como novo dia de corte: " + localDate);
                LOG.debug("Tipo Entidade..: " + responsavel.getTipoEntidade());
                LOG.debug("Codigo Entidade: " + responsavel.getCodigoEntidade());
                CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
                calendarioController.updateTodosCalendarioFolha(localDate.getDayOfMonth(), responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), true, responsavel);
            }

            // Parametros de sistema relativo à importação
            ParamSist ps = ParamSist.getInstance();
            String psiVlr = (String) ps.getParam(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, responsavel);
            LOG.debug("Consolida descontos? " + (psiVlr == null ? "N" : psiVlr));
            psiVlr = (String) ps.getParam(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, responsavel);
            LOG.debug("Exportação somente inicial? " + (psiVlr == null ? "N" : psiVlr));
            if (psiVlr == null || psiVlr.equals(CodedValues.TPC_NAO)) {
                psiVlr = (String) ps.getParam(CodedValues.TPC_ENVIA_EXCLUSOES_MOVIMENTO_MENSAL, responsavel);
                LOG.debug("Envia exclusões? " + (psiVlr == null ? "N" : psiVlr));
            }
            psiVlr = (String) ps.getParam(CodedValues.TPC_CONSOLIDA_EXC_INC_COMO_ALT, responsavel);
            LOG.debug("Consolida exclusão e inclusão como alteração? " + (psiVlr == null ? "N" : psiVlr));
            psiVlr = (String) ps.getParam(CodedValues.TPC_EXPORTA_LIQCANC_NAO_PAGAS, responsavel);
            LOG.debug("Exporta ADE liquidada não paga? " + (psiVlr == null ? "N" : psiVlr));

            // Parâmetros de reimplante automático
            psiVlr = (String) ps.getParam(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, responsavel);
            LOG.debug("Reimplantação automática? " + (psiVlr == null ? "N" : psiVlr));
            psiVlr = (String) ps.getParam(CodedValues.TPC_CSA_ALTERA_REIMPLANTACAO, responsavel);
            LOG.debug("Permitir às consignatárias optarem por reimplante? " + (psiVlr == null ? "N" : psiVlr));
            psiVlr = (String) ps.getParam(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, responsavel);
            LOG.debug("Padrão para parâmetro de serviço de reimplantação automática? " + (psiVlr == null ? "N" : psiVlr));
            psiVlr = (String) ps.getParam(CodedValues.TPC_PRESERVA_PRD_REJEITADA, responsavel);
            LOG.debug("Preservação de parcela rejeitada? " + (psiVlr == null ? "N" : psiVlr));
            psiVlr = (String) ps.getParam(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD, responsavel);
            LOG.debug("Permitir às consignatárias optarem por preservação de parcelas? " + (psiVlr == null ? "N" : psiVlr));
            psiVlr = (String) ps.getParam(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, responsavel);
            LOG.debug("Padrão para parâmetro de serviço de preservação de parcelas? " + (psiVlr == null ? "N" : psiVlr));

            // Período de exportação
            psiVlr = (String) ps.getParam(CodedValues.TPC_SET_PERIODO_EXP_MOV_MES, responsavel);
            LOG.debug("Altera tabela de período de exportação? " + (psiVlr == null ? "S" : psiVlr));

            // Se é exportação, calcula o proximo período baseado na historico integração
            List<TransferObject> periodoExportacao = new PeriodoDelegate().obtemPeriodoExpMovimento(orgCodigos, estCodigos, true, responsavel);
            // Imprime o periodo de exportação
            LOG.debug("Período de exportação: ");
            ExportaMovimentoHelper.imprimePeriodoExportacao(periodoExportacao);

            psiVlr = (String) ps.getParam(CodedValues.TPC_CLASSE_EXPORTADOR, responsavel);
            LOG.debug("Classe específica para o Gestor: " + (psiVlr == null || psiVlr.equals("") ? "Nenhuma" : psiVlr));

            LOG.debug("Início da exportação de movimento financeiro: " + DateHelper.getSystemDatetime());
            ParametrosExportacao parametrosExportacao = new ParametrosExportacao();
            parametrosExportacao.setOrgCodigos(orgCodigos)
                                .setEstCodigos(estCodigos)
                                .setVerbas(verbas)
                                .setAcao(acao)
                                .setOpcao(opcao)
//                                .setTipoArquivo(opcao)
                                .setResponsavel(responsavel);
            String arquivo = (new ExportaMovimentoDelegate()).exportaMovimentoFinanceiro(parametrosExportacao, responsavel);
            LOG.debug("Término da exportação de movimento financeiro: " + DateHelper.getSystemDatetime());
            LOG.debug("Arquivo gerado: " + arquivo);

            mensagem = ApplicationResourcesHelper.getMessage("mensagem.sucesso.processamento.exportacao.movimento", responsavel, horaInicioStr, arquivo);
        } catch (ConsignanteControllerException | PeriodoException | CalendarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.exportacao.movimento", responsavel, horaInicioStr);
        }
    }
}
