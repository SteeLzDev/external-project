package com.zetra.econsig.helper.folha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ExportaMovimentoDelegate;
import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ExportaMovimentoHelper</p>
 * <p>Description: Helper Class para exportação de movmento financeiro</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExportaMovimentoHelper implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportaMovimentoHelper.class);
    private static final String NOME_CLASSE = ExportaMovimentoHelper.class.getName();

    /**
     * Exporta o movimento financeiro
     * @param orgCodigos  : lista de órgãos a serem exportados
     * @param estCodigos  : lista de estabelecimentos a serem exportados
     * @param verbas      : lista de verbas a serem exportadas
     * @param acao        : ação a ser executada: exportar ou reexportar
     * @param tipoArquivo : tipo de arquivo a ser gerado
     * @param periodo     : período a ser exportaçao
     * @param responsavel : usuário responsavel
     * @returns Nome do arquivo gerado na exportação
     * @throws ViewHelperException
     */
    private String exportaMovimento(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            return (new ExportaMovimentoDelegate()).exportaMovimentoFinanceiro(parametrosExportacao, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.folha.falha.exportacao", responsavel, ex, ex.getMessage());
        }
    }

    private List<String> getEstabelecimentos(AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> estabelecimentos = delegate.lstEstabelecimentos(null, responsavel);
        Iterator<TransferObject> it = estabelecimentos.iterator();
        List<String> codigos = new ArrayList<>();
        while (it.hasNext()) {
            codigos.add((String) it.next().getAttribute(Columns.EST_CODIGO));
        }
        return codigos;
    }

    private List<String> getOrgaos(AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> orgaos = delegate.lstOrgaos(null, responsavel);
        Iterator<TransferObject> it = orgaos.iterator();
        List<String> codigos = new ArrayList<>();
        while (it.hasNext()) {
            codigos.add((String) it.next().getAttribute(Columns.ORG_CODIGO));
        }
        return codigos;
    }

    public static List<String> getVerbas(AcessoSistema responsavel) throws ConvenioControllerException {
        ConvenioDelegate delegate = new ConvenioDelegate();
        List<TransferObject> convenios = delegate.lstConvenios(null, null, null, null, false, responsavel);
        Iterator<TransferObject> it = convenios.iterator();
        Map<String, String> verbas = new HashMap<>();
        while (it.hasNext()) {
            verbas.put((String) it.next().getAttribute(Columns.CNV_COD_VERBA), "ok");
        }
        return (new ArrayList<>(verbas.keySet()));
    }

    public static void imprimePeriodoExportacao(List<TransferObject> periodoExportacao) {
        for (TransferObject cto : periodoExportacao) {
            String linha = "";

            linha = Columns.getColumnName(Columns.PEX_ORG_CODIGO) + "=" + cto.getAttribute(Columns.PEX_ORG_CODIGO) + " | " +
                    Columns.getColumnName(Columns.PEX_DIA_CORTE) + "=" + cto.getAttribute(Columns.PEX_DIA_CORTE) + " | " +
                    Columns.getColumnName(Columns.PEX_DATA_INI) + "=" + cto.getAttribute(Columns.PEX_DATA_INI) + " | " +
                    Columns.getColumnName(Columns.PEX_DATA_FIM) + "=" + cto.getAttribute(Columns.PEX_DATA_FIM) + " | " +
                    Columns.getColumnName(Columns.PEX_PERIODO) + "=" + cto.getAttribute(Columns.PEX_PERIODO);

            LOG.debug(linha);
        }
    }

    @Override
    public int executar(String args[]) {
        try {
            if (args.length != 7) {
                LOG.debug("USE: java " + NOME_CLASSE + " " +
                        "[ESTABELECIMENTOS] [ORGAOS] VERBAS ACAO [PERIODO] TIPO_ARQUIVO RESPONSAVEL" +
                        "\nESTABELECIMENTOS: lista de códigos separados por vírgula. usar 'todos' para exportar todos os estabelecimentos" +
                        "\nORGAOS: lista de códigos separados por vírgula usar 'todos' para exportar todos os órgãos" +
                        "\nVERBAS: lista de verbas separadas por vírgula. usar 'todas' para exportar todas as verbas" +
                        "\nACAO: exportar / reexportar / periodo / ult_periodo" +
                        "\nPERIODO: (removido, passar branco)" +
                        "\nTIPO_ARQUIVO: 1-arquivo único 2-arquivos separados por 'entidade' 3-arquivos separados por 'verba' 4-arquivos separados por 'entidade' e 'verba'" +
                        "\nRESPONSAVEL: código do usuário" +
                        "\n***** Usar [] para indicar branco nos campos opcionais");
                return -1;
            } else {
                AcessoSistema responsavel = new AcessoSistema(args[6]);

                String est = args[0].substring(1, args[0].length() - 1);
                List<String> estCodigos = est.equals("") ? null : est.equalsIgnoreCase("todos") ? getEstabelecimentos(responsavel) : Arrays.asList(TextHelper.split(est, ","));
                String org = args[1].substring(1, args[1].length() - 1);
                List<String> orgCodigos = org.equals("") ? null : org.equalsIgnoreCase("todos") ? getOrgaos(responsavel) : Arrays.asList(TextHelper.split(org, ","));
                String tmp = args[2].endsWith("]") ? args[2].substring(1, args[2].length() - 1) : args[2];
                List<String> verbas = tmp.equals("") ? null : tmp.equalsIgnoreCase("todas") ? getVerbas(null) : Arrays.asList(TextHelper.split(tmp, ","));
                String acao = args[3];
                String opcao = args[5];
                String[] tiposArquivo = {"arquivo único",
                                         "arquivos separados por 'entidade'",
                                         "arquivos separados por 'verba'",
                                         "arquivos separados por 'entidade' e 'verba'"};

                LOG.debug("Exportando o movimento para os seguintes parametros:");
                LOG.debug("Estabelecimentos: " + estCodigos);
                LOG.debug("Orgaos..........: " + orgCodigos);
                LOG.debug("Verbas..........: " + verbas);
                LOG.debug("Acao............: " + acao);
                LOG.debug("Tipo de Arquivo.: " + opcao + " - " + tiposArquivo[Integer.parseInt(opcao) - 1]);
                LOG.debug("Responsavel.....: " + (responsavel != null ? responsavel.getUsuCodigo() : ""));

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
                psiVlr = (String) ps.getParam(CodedValues.TPC_CLASSE_EXPORTADOR, responsavel);
                LOG.debug("Classe específica para o Gestor: " + (psiVlr == null || psiVlr.equals("") ? "Nenhuma" : psiVlr));

                // Período de exportação
                psiVlr = (String) ps.getParam(CodedValues.TPC_SET_PERIODO_EXP_MOV_MES, responsavel);
                LOG.debug("Altera tabela de período de exportação? " + (psiVlr == null ? "S" : psiVlr));

                if (acao.equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo()) || acao.equals(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())) {
                    // Se é exportação, calcula o proximo período baseado na historico integração
                    List<TransferObject> periodoExportacao = new PeriodoDelegate().obtemPeriodoExpMovimento(orgCodigos, estCodigos, true, acao.equals(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo()), responsavel);
                    // Imprime o periodo de exportação
                    LOG.debug("Período de exportação: ");
                    imprimePeriodoExportacao(periodoExportacao);

                    ParametrosExportacao parametrosExportacao = new ParametrosExportacao();
                    parametrosExportacao.setOrgCodigos(orgCodigos)
                                        .setEstCodigos(estCodigos)
                                        .setVerbas(verbas)
                                        .setAcao(acao)
                                        .setOpcao(opcao)
                                        .setResponsavel(responsavel);
                    LOG.debug("Início da exportação de movimento financeiro: " + DateHelper.getSystemDatetime());
                    String arquivo = exportaMovimento(parametrosExportacao, responsavel);
                    LOG.debug("Término da exportação de movimento financeiro: " + DateHelper.getSystemDatetime());
                    LOG.debug("Arquivo gerado: " + arquivo);

                } else if (acao.equals("periodo") || acao.equals("ult_periodo")) {
                    // Obtém o periodo de exportação
                    List<TransferObject> periodoExportacao = new PeriodoDelegate().obtemPeriodoExpMovimento(orgCodigos, estCodigos, false, acao.equals("ult_periodo"), responsavel);
                    // Imprime o periodo de exportação
                    LOG.debug("Período de exportação: ");
                    imprimePeriodoExportacao(periodoExportacao);
                }
            }
            return 0;

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }
}