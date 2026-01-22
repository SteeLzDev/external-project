package com.zetra.econsig.job.process;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
/**
 * <p>Title: ProcessaEnvioEmailCsaAjustaConsignacoesMargem</p>
 * <p>Description: Classe para processamento de envio de email para csa que tiveram seus contratos alterados devido a ajuste de margem
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailCsaAjustaConsignacoesMargem extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailCsaAjustaConsignacoesMargem.class);

    private final List<TransferObject> autDes;
    private final CustomTransferObject decisaoJudicial;
    private final AcessoSistema responsavel;

    public ProcessaEnvioEmailCsaAjustaConsignacoesMargem(List<TransferObject> autDes, CustomTransferObject decisaoJudicial, AcessoSistema responsavel) {
        this.autDes = autDes;
        this.decisaoJudicial = decisaoJudicial;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
            LOG.info("ENVIANDO EMAIL ALERTA DE ALTERAÇÃO DE CONSIGNAÇÃO AJUSTE À MARGEM");
            try {
                EnviaEmailHelper.enviarEmailNotificacaoAlteracaoContratoAjustadosMargem(autDes, decisaoJudicial, responsavel);
            } catch (Exception ex) {
                // exceção no envio de email não faz rollback na operação
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
