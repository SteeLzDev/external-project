package com.zetra.econsig.job.process.agendado;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;


/**
 * <p>Title: ProcessaReativacaoAutomaticaAde</p>
 * <p>Description: Processamento de Reativação Automática de Consignações</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaReativacaoAutomaticaAde extends ProcessoAgendadoPeriodico {
    public ProcessaReativacaoAutomaticaAde(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Lista as consignações suspensas que tenham data de reativação automática preenchida
        ConsignacaoDelegate delegate = new ConsignacaoDelegate();
        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        List<String> adeCodigos = adeDelegate.listarConsignacoesReativacaoAutomatica(getResponsavel());

        if (adeCodigos != null && !adeCodigos.isEmpty()) {
            for (String adeCodigo : adeCodigos) {
                try {
                    // Efetua a tentativa de reativação para cada consignação em transação isolada
                    delegate.reativarConsignacao(adeCodigo, getResponsavel());
                } catch (AutorizacaoControllerException ex) {
                    // Recupera os dados da consignação que não pode ser reativada
                    TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, getResponsavel());
                    Date dataReativacaoAutomatica = (Date) ade.getAttribute(Columns.ADE_DATA_REATIVACAO_AUTOMATICA);

                    // Envia e-mail com erro, caso não seja possível reativar a consignação
                    TransferObject dadosOperacao = adeDelegate.obtemDadosUsuarioUltimaOperacaoAde(adeCodigo, CodedValues.TOC_SUSPENSAO_CONTRATO, getResponsavel());
                    if (dadosOperacao != null && !TextHelper.isNull(dadosOperacao.getAttribute(Columns.USU_EMAIL))) {
                        String emailDestinatario = dadosOperacao.getAttribute(Columns.USU_EMAIL).toString();
                        String nomeDestinatario = dadosOperacao.getAttribute(Columns.USU_NOME).toString();
                        String dataReativacaoAutStr = DateHelper.toDateString(dataReativacaoAutomatica);
                        EnviaEmailHelper.enviarEmailErroReativacaoAutomatica(ade, emailDestinatario, nomeDestinatario, dataReativacaoAutStr, ex.getMessage(), getResponsavel());
                    }

                    // Se a data de reativação automática já passou de 7 dias, então zera a informação para que não fique tentando eternamente
                    if (dataReativacaoAutomatica != null && DateHelper.dayDiff(dataReativacaoAutomatica) > 7) {
                        delegate.removerDataReativacaoAutomatica(adeCodigo, getResponsavel());
                    }
                }
            }
        }
    }
}
