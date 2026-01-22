package com.zetra.econsig.job.process.integracao.orientada;

import com.zetra.econsig.delegate.ImportaHistoricoDelegate;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.job.process.Processo;

/**
 * <p>Title: ProcessaHistorico</p>
 * <p>Description: Classe para geração de histórico de teste para integração orientada</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaHistorico extends Processo {
    private final int pctRse;
    private final int qtdAdePorRse;
    private final boolean criarParcelas;
    private final String nseCodigo;
    private final boolean apagarAntigos;

    private final AcessoSistema responsavel;

    public ProcessaHistorico(int pctRse, int qtdAdePorRse, boolean criarParcelas, String nseCodigo, boolean apagarAntigos, AcessoSistema responsavel) {
        this.pctRse = pctRse;
        this.qtdAdePorRse = qtdAdePorRse;
        this.criarParcelas = criarParcelas;
        this.nseCodigo = nseCodigo;
        this.apagarAntigos = apagarAntigos;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        String horaInicioStr = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
        try {
            ImportaHistoricoDelegate ihd = new ImportaHistoricoDelegate();
            if (apagarAntigos) {
                ihd.apagarHistoricoTesteOrientado(responsavel);
            }
            // 50, 2, true, null
            ihd.gerarHistoricoTesteOrientado(pctRse, qtdAdePorRse, criarParcelas, nseCodigo, responsavel);
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.sucesso.processamento.geracao.historico", responsavel, horaInicioStr);
        } catch (ConsignanteControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.geracao.historico", responsavel, horaInicioStr);
        }
    }
}
