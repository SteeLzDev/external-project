package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ProcessaCalculoMargem</p>
 * <p>Description: Classe para processamento de calculo de margem</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaCalculoMargem extends Processo {

    private final String tipoEntidade;
    private List<String> codigoEntidades;
    private final AcessoSistema responsavel;

    public ProcessaCalculoMargem(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) {
        this.tipoEntidade = tipoEntidade;
        if (codigoEntidade != null) {
            codigoEntidades = new ArrayList<>();
            codigoEntidades.add(codigoEntidade);
        }
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    public ProcessaCalculoMargem(String tipoEntidade, List<String> codigoEntidades, AcessoSistema responsavel) {
        this.tipoEntidade = tipoEntidade;
        this.codigoEntidades = codigoEntidades;
        this.responsavel = responsavel;

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();
    }

    @Override
    protected void executar() {
        try {
            // Cria os delegates necessários
            ServidorDelegate serDelegate = new ServidorDelegate();

            // Executa processo de recalculo de margem
            serDelegate.recalculaMargemComHistorico(tipoEntidade, codigoEntidades, responsavel);

            // Seta mensagem de sucesso na sessão do usuário
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.recalculo.margens.verificar.existe.rotinas.externas.executar.conjuntamente.sucesso", responsavel);

        } catch (MargemControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.recalculo.margens", responsavel) + "<br>"
                + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
        }
    }
}
