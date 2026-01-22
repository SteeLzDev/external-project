package com.zetra.econsig.job.process;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaCancelamentoInadimplenciaBeneficio</p>
 * <p>Description: Classe para processamento para cancelamento de beneficíos por inadimplência.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: marcos.nolasco $
 * $Revision: 28048 $
 * $Date: 2020-12-29 08:34:41 -0300 (ter, 29 dez 2020) $
 */
public class ProcessaCancelamentoInadimplenciaBeneficio extends Processo {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaCancelamentoInadimplenciaBeneficio.class);

    private final String arquivoLote;
    private final AcessoSistema responsavel;

    public ProcessaCancelamentoInadimplenciaBeneficio(String arquivoLote, AcessoSistema responsavel) {
        this.arquivoLote = arquivoLote;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        try {
            ContratoBeneficioController contratoBeneficioController = ApplicationContextProvider.getApplicationContext().getBean(ContratoBeneficioController.class);
            contratoBeneficioController.cancelarContratoBeneficioInadimplencia(arquivoLote, responsavel);
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel, arquivoLote);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.cancelar.beneficio.inadimplente", responsavel) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }

}
