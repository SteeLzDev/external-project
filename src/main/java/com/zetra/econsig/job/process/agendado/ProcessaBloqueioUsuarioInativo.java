package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaBloqueioUsuarioInativo</p>
 * <p>Description: Processamento de Bloqueio de Usuários Inativos</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBloqueioUsuarioInativo extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioUsuarioInativo.class);

    public ProcessaBloqueioUsuarioInativo(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa bloqueio automático de usuários inativos, se ainda não foi feito no dia
    	LOG.debug("Executa Bloqueio de Usuários Inativos");

    	// se sistema não está configurado para bloquear automaticamente usuário apenas na sua próxima autenticação, faz a verficação de bloqueio.
    	if(!ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_USU_INATIVIDADE_PROXIMA_AUTENTICACAO, getResponsavel())) {
    		UsuarioHelper.bloqueioAutomaticoPorInatividade(null, getResponsavel());
    	}
    }
}
