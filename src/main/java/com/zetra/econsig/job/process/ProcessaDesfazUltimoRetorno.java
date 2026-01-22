package com.zetra.econsig.job.process;

import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaDesfazUltimoRetorno</p>
 * <p>Description: Classe para processamento da rotina que desfaz o último retorno</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaDesfazUltimoRetorno extends Processo {

    private final boolean recalcularMargem;
    private final boolean desfazerMovimento;
    private final String orgCodigo;
    private final String estCodigo;
    private final String[] parcelas;
    private final AcessoSistema responsavel;

    public ProcessaDesfazUltimoRetorno(boolean recalcularMargem, boolean desfazerMovimento, String orgCodigo, String estCodigo, String[] parcelas, AcessoSistema responsavel) {
        this.recalcularMargem = recalcularMargem;
        this.desfazerMovimento = desfazerMovimento;
        this.orgCodigo = orgCodigo;
        this.estCodigo = estCodigo;
        this.parcelas = parcelas;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {
            // Cria o delegate necessário para o processo
            ImpRetornoDelegate delegate = new ImpRetornoDelegate();
            // Chama rotina para desfazer o ultimo retorno
            delegate.desfazerUltimoRetorno(orgCodigo, estCodigo, recalcularMargem, desfazerMovimento, parcelas, responsavel);
            // Cria mensagem de sucesso para a importação
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel) + ". " + ApplicationResourcesHelper.getMessage("mensagem.informacao.ultimo.retorno.folha.desfeito", responsavel);
        } catch (ImpRetornoControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rotina.desfaz.retorno.folha", responsavel) + "<br>"
                + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }
}
