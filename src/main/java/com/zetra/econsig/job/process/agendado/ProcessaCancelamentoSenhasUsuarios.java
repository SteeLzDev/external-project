package com.zetra.econsig.job.process.agendado;

import java.util.List;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaCancelamentoSenhasUsuarios</p>
 * <p>Description: Processamento de Cancelamento de Senhas de Usuários Servidores</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaCancelamentoSenhasUsuarios extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaCancelamentoSenhasUsuarios.class);

    public ProcessaCancelamentoSenhasUsuarios(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        AcessoSistema responsavel = getResponsavel();

        // Verifica se usa senha de autorização (senha 2)
        if (ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            // Recupera o parâmetro que informa o prazo para invalidação de senhas de autorização de servidores
            String prazo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTD_DIAS_VALIDADE_SENHA_AUTORIZACAO, responsavel);
            if (TextHelper.isNull(prazo) || Integer.parseInt(prazo) == 0) {
                LOG.debug("Sistema não cancela senhas de autorizações.");
            } else {
                LOG.debug("Executa cancelamento de senhas de autorizações");
                UsuarioDelegate usuDelegate = new UsuarioDelegate();
                List<TransferObject> senhasAutorizacaoExpiradas = usuDelegate.listarSenhasExpiradasCancelamentoAut(responsavel);

                if (senhasAutorizacaoExpiradas != null && !senhasAutorizacaoExpiradas.isEmpty()) {
                    // Total de senhas canceladas
                    int qtdeSenhasCanceladas = 0;

                    for (TransferObject senhaVencida : senhasAutorizacaoExpiradas) {
                        String usuCodigo = (String) senhaVencida.getAttribute("USU_CODIGO");
                        String senha = (String) senhaVencida.getAttribute("SENHA");
                        if (!TextHelper.isNull(usuCodigo)) {
                            try {
                                usuDelegate.cancelaSenhaAutorizacao(usuCodigo, senha, responsavel);
                                qtdeSenhasCanceladas++;
                            } catch (UsuarioControllerException e) {
                                LOG.error("Erro ao cancelar a senha do usuario [" + usuCodigo + "].", e);
                            }
                        }
                    }

                    try {
                        // Grava log da operação de cancelamento automático
                        LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.UPDATE, Log.LOG_INFORMACAO);
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.automatico.senhas.autorizacao.expiradas.total.arg0", responsavel, String.valueOf(qtdeSenhasCanceladas)));
                        log.write();
                    } catch (LogControllerException e) {
                        LOG.error("Erro ao gravar log de cancelamento automático de senhas não utilizadas.", e);
                    }
                }
            }
        }
    }
}
