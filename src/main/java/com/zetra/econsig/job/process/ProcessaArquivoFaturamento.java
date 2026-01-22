package com.zetra.econsig.job.process;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.beneficios.FaturamentoBeneficioController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaArquivoFaturamento</p>
 * <p>Description: Classe para processamento para gerar arquivo de faturamento de benefícios.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaArquivoFaturamento extends Processo {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaArquivoFaturamento.class);

    private final String fatCodigo;
    private final AcessoSistema responsavel;

    public ProcessaArquivoFaturamento(String fatCodigo, AcessoSistema responsavel) {
        this.fatCodigo = fatCodigo;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        try {
            FaturamentoBeneficioController faturamentoBeneficioController = ApplicationContextProvider.getApplicationContext().getBean(FaturamentoBeneficioController.class);
            String nomeArquivo = faturamentoBeneficioController.gerarArquivoFaturamentoPrincipal(fatCodigo, responsavel);
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel, nomeArquivo);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.arquivo.faturamento.beneficio", responsavel) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }

}
