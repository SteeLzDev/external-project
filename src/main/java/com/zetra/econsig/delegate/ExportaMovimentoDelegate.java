package com.zetra.econsig.delegate;

import java.io.File;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ExportaMovimentoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ExportaMovimentoDelegate</p>
 * <p>Description: Delegate de processos de exportação de movimento</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExportaMovimentoDelegate extends AbstractDelegate {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportaMovimentoDelegate.class);

    private ExportaMovimentoController expController = null;

    private ExportaMovimentoController getExportaMovimentoController() throws ConsignanteControllerException {
        try {
            if (expController == null) {
                expController = ApplicationContextProvider.getApplicationContext().getBean(ExportaMovimentoController.class);
            }
            return expController;
        } catch (Exception ex) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public String exportaMovimentoFinanceiro(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ConsignanteControllerException {
        return exportaMovimentoFinanceiro(parametrosExportacao, null, responsavel);
    }

    public String exportaMovimentoFinanceiro(ParametrosExportacao parametrosExportacao, List<String> adeNumeros, AcessoSistema responsavel) throws ConsignanteControllerException {
        List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
        List<String> estCodigos = parametrosExportacao.getEstCodigos();
        String acao = parametrosExportacao.getAcao();
        String opcao = parametrosExportacao.getOpcao();
        try {
            // Executa fora da transação principal a criação das tabelas necessárias para a exportação do movimento financeiro
            getExportaMovimentoController().criarTabelasExportacaoMovFin(parametrosExportacao, responsavel);

            // Exporta o movimento
            String nomeArqLote = getExportaMovimentoController().exportaMovimentoFinanceiro(parametrosExportacao, adeNumeros, responsavel);

            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathLote = absolutePath + File.separatorChar + "movimento" + File.separatorChar + "cse";

            // Pega o código do órgão do usuário, caso este não seja de consignante. Não pode usar o gravado no
            // responsável pois na chamada via Script, este valor não será carregado
            try {
                UsuarioDelegate usuDelegate = new UsuarioDelegate();
                String orgCodigo = usuDelegate.isOrg((responsavel != null ? responsavel.getUsuCodigo() : null));
                if (orgCodigo != null) {
                    pathLote += File.separatorChar + orgCodigo;
                }
            } catch (UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // Insere histórico do arquivo
            try {
                String harObs = "";
                if (!TextHelper.isNull(acao)) {
                    harObs = acao.equalsIgnoreCase(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo())
                           ? ApplicationResourcesHelper.getMessage("rotulo.folha.exportar.movimento.financeiro", responsavel)
                           : ApplicationResourcesHelper.getMessage("rotulo.folha.reexportar.movimento.financeiro", responsavel);

                    harObs += ". ";
                }
                if (opcao.equals("1")) {
                    harObs += ApplicationResourcesHelper.getMessage("rotulo.folha.arquivo.unico", responsavel);
                } else if (opcao.equals("2")) {
                    harObs += ApplicationResourcesHelper.getMessage("rotulo.folha.arquivos.separados.est.org", responsavel);
                } else if (opcao.equals("3")) {
                    harObs += ApplicationResourcesHelper.getMessage("rotulo.folha.arquivos.separados.verba", responsavel);
                } else if (opcao.equals("4")) {
                    harObs += ApplicationResourcesHelper.getMessage("rotulo.folha.arquivos.separados.est.org.verba", responsavel);
                }

                java.util.Date pexPeriodo = null;
                try {
                    PeriodoDelegate perDelegate = new PeriodoDelegate();
                    TransferObject to = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
                    pexPeriodo = DateHelper.parse(to.getAttribute(Columns.PEX_PERIODO).toString(), "yyyy-MM-dd");
                } catch (Exception e) {
                    LOG.error("Não foi possível localizar o período atual de exportação.", e);
                }

                String harResultado = CodedValues.STS_ATIVO.toString();
                TipoArquivoEnum tipoArquivo = TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO;
                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                hisArqDelegate.createHistoricoArquivo(null, null, tipoArquivo, pathLote + File.separatorChar + nomeArqLote, harObs, null, pexPeriodo, harResultado, CodedValues.FUN_EXP_MOV_FINANCEIRO, responsavel);
            } catch (HistoricoArquivoControllerException e) {
                LOG.error("Não foi possível inserir o histórico do arquivo de exportação do movimento financeiro '" + nomeArqLote + "'.", e);
            }

            return nomeArqLote;
        } catch (ConsignanteControllerException ex) {
            throw ex;
        }
    }

    public void enviarEmailDownloadNaoRealizadoMovFin(AcessoSistema responsavel) throws ConsignanteControllerException {
        getExportaMovimentoController().enviarEmailDownloadNaoRealizadoMovFin(responsavel);
    }

    public String compactarAnexosAdePeriodo(List<String> orgCodigos, List<String> estCodigos, List<String> codVerbas, String zipFileNameOutPut, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getExportaMovimentoController().compactarAnexosAdePeriodo(orgCodigos, estCodigos, codVerbas, zipFileNameOutPut, responsavel);
    }

    public List<TransferObject> consultarMovimentoFinanceiro(String periodo, String rseMatricula, String serCpf, String orgIdentificador, String estIdentificador, String csaIdentificador, String svcIdentificador, String cnvCodVerba, AcessoSistema responsavel) throws ConsignanteControllerException {
        return getExportaMovimentoController().consultarMovimentoFinanceiro(periodo, rseMatricula, serCpf, orgIdentificador, estIdentificador, csaIdentificador, svcIdentificador, cnvCodVerba, responsavel);
    }
}
