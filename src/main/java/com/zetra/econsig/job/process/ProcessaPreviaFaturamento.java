package com.zetra.econsig.job.process;

import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.beneficios.FaturamentoBeneficioController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaPreviaFaturamento</p>
 * <p>Description: Classe para processamento para validação da prévia do faturamento de benefícios.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaPreviaFaturamento extends Processo {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaPreviaFaturamento.class);

    private final String fatCodigo;
    private final List<String> arquivosPrevia;
    private final AcessoSistema responsavel;

    public ProcessaPreviaFaturamento(String fatCodigo, List<String> arquivosPrevia, AcessoSistema responsavel) {
        this.fatCodigo = fatCodigo;
        this.arquivosPrevia = arquivosPrevia;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        try {
            FaturamentoBeneficioController faturamentoBeneficioController = ApplicationContextProvider.getApplicationContext().getBean(FaturamentoBeneficioController.class);
            String nomeArquivo = faturamentoBeneficioController.validarPreviaFaturamento(fatCodigo, arquivosPrevia, responsavel);
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel, nomeArquivo);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            // Determina mensagem de erro
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.previa.faturamento.beneficio", responsavel) + "<br>"
                          + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }

}
