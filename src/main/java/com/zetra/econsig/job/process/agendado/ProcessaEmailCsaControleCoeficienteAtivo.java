package com.zetra.econsig.job.process.agendado;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.command.EnviarEmailTaxasDesbloqueadasCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailTaxasExpiradasEm7DiasCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailTaxasExpiradasUltimoDiaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailTaxasNaoAtualizadasCommand;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.web.ApplicationContextProvider;


/**
 * <p>Title: ProcessaEmailCsaControleCoeficienteAtivo</p>
 * <p>Description: Processo períodico para envio de email de taxas expiradas, não cadastradas ou desbloqueadas.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEmailCsaControleCoeficienteAtivo extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEmailCsaControleCoeficienteAtivo.class);

    public ProcessaEmailCsaControleCoeficienteAtivo(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
        final EnviarEmailTaxasExpiradasEm7DiasCommand enviarEmailTaxasExpiradas7Dias = new EnviarEmailTaxasExpiradasEm7DiasCommand();
        final EnviarEmailTaxasExpiradasUltimoDiaCommand enviarEmailTaxasExpiradasUltimoDia = new EnviarEmailTaxasExpiradasUltimoDiaCommand();
        final EnviarEmailTaxasNaoAtualizadasCommand enviarEmailTaxasNaoAtualizadas = new EnviarEmailTaxasNaoAtualizadasCommand();
        final EnviarEmailTaxasDesbloqueadasCommand enviarEmailTaxasDesbloqueadas = new EnviarEmailTaxasDesbloqueadasCommand();

        // Aviso de bloqueio 1 semana antes
        List<TransferObject> csaExpiraEm7dias = consignatariaController.lstConsignatariaCoeficienteAtivoExpirado(7, AcessoSistema.getAcessoUsuarioSistema());
        if (csaExpiraEm7dias != null && !csaExpiraEm7dias.isEmpty()) {
            csaExpiraEm7dias.stream().forEach((csa -> {
                try {
                    enviarEmailTaxasExpiradas7Dias.setCsaCodigo(csa);
                    enviarEmailTaxasExpiradas7Dias.execute();
                } catch (ViewHelperException e) {
                    LOG.error("mensagem.erro.email.enviar", e);
                }
            }));
        }

        // Aviso de bloqueio no último dia
        List<TransferObject> csaExpiraHoje = consignatariaController.lstConsignatariaCoeficienteAtivoExpirado(0, AcessoSistema.getAcessoUsuarioSistema());
        if (csaExpiraHoje != null && !csaExpiraHoje.isEmpty()) {
            csaExpiraHoje.stream().forEach((csa -> {
                try {
                    enviarEmailTaxasExpiradasUltimoDia.setCsaCodigo(csa);
                    enviarEmailTaxasExpiradasUltimoDia.execute();
                } catch (ViewHelperException e) {
                    LOG.error("mensagem.erro.email.enviar", e);
                }
            }));
        }

        // Aviso para desbloquear de 2 em 2 dias
        List<TransferObject> csaTaxasBloqueadas = consignatariaController.lstConsignatariaCoeficienteBloqueado(AcessoSistema.getAcessoUsuarioSistema());
        if (csaTaxasBloqueadas != null && !csaTaxasBloqueadas.isEmpty()) {
            csaTaxasBloqueadas.stream().forEach((csa -> {
                try {
                    enviarEmailTaxasNaoAtualizadas.setCsaCodigo(csa);
                    enviarEmailTaxasNaoAtualizadas.execute();
                } catch (ViewHelperException e) {
                    LOG.error("mensagem.erro.email.enviar", e);
                }
            }));
        }

        // Aviso de desbloqueio
        List<TransferObject> csaDesbloqueadas = consignatariaController.lstConsignatariaCoeficienteAtivoDesbloqueado(AcessoSistema.getAcessoUsuarioSistema());
        if (csaDesbloqueadas != null && !csaDesbloqueadas.isEmpty()) {
            csaDesbloqueadas.stream().forEach((csa -> {
                try {
                    enviarEmailTaxasDesbloqueadas.setCsaCodigo(csa);
                    enviarEmailTaxasDesbloqueadas.execute();
                } catch (ViewHelperException e) {
                    LOG.error("mensagem.erro.email.enviar", e);
                }
            }));
        }

    }

}
