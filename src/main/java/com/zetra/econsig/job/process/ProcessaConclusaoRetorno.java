package com.zetra.econsig.job.process;

import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRetorno</p>
 * <p>Description: Classe para processamento de arquivos de retorno</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaConclusaoRetorno extends Processo {
    private final String orgCodigo;
    private final String estCodigo;
    private final String tipoEntidade;
    private final String codigoEntidade;

    private final AcessoSistema responsavel;

    public ProcessaConclusaoRetorno(String orgCodigo, String estCodigo, AcessoSistema responsavel) {
        this.orgCodigo = orgCodigo;
        this.estCodigo = estCodigo;

        if (!TextHelper.isNull(orgCodigo)) {
            tipoEntidade = "ORG";
            codigoEntidade = orgCodigo;
        } else if (!TextHelper.isNull(estCodigo)) {
            tipoEntidade = "EST";
            codigoEntidade = estCodigo;
        } else {
            tipoEntidade = "CSE";
            codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;
        }

        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {
            // Cria o delegate necessário para o processo
            ImpRetornoDelegate retDelegate = new ImpRetornoDelegate();
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

            retDelegate.finalizarIntegracaoFolha(tipoEntidade, codigoEntidade, responsavel);
            relatorioController.geraRelatorioIntegracao(estCodigo, orgCodigo, responsavel);

            // Coloca mensagem de sucesso
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.importacao.retorno.folha.sucesso", responsavel) + " "
                          + ApplicationResourcesHelper.getMessage("mensagem.informacao.parcelas.nao.retornadas.pela.folha.foram.marcadas.rejeitadas.relatorios.consignatarias.gerados", responsavel);

        } catch (ImpRetornoControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.conclusao.retorno", responsavel) + "<br>"
                    + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        } catch (RelatorioControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.conclusao.retorno", responsavel) + "<br>"
                    + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }
}
