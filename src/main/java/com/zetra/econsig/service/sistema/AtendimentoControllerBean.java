package com.zetra.econsig.service.sistema;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Atendimento;
import com.zetra.econsig.persistence.entity.AtendimentoHome;
import com.zetra.econsig.persistence.entity.AtendimentoMensagem;
import com.zetra.econsig.persistence.entity.AtendimentoMensagemHome;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AtendimentoControllerBean</p>
 * <p>Description: Controlador de negócios para atendimento via chatbot</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AtendimentoControllerBean implements AtendimentoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtendimentoControllerBean.class);

    private TransferObject atendimentoToTransferObject(Atendimento atendimento) {
        TransferObject to = new CustomTransferObject();
        to.setAttribute(Columns.ATE_CODIGO, atendimento.getAteCodigo());
        to.setAttribute(Columns.ATE_USU_CODIGO, atendimento.getUsuario() != null ? atendimento.getUsuario().getUsuCodigo() : null);
        to.setAttribute(Columns.ATE_NOME_USUARIO, atendimento.getAteNomeUsuario());
        to.setAttribute(Columns.ATE_EMAIL_USUARIO, atendimento.getAteEmailUsuario());
        to.setAttribute(Columns.ATE_DATA_INICIO, atendimento.getAteDataInicio());
        to.setAttribute(Columns.ATE_DATA_ULT_MENSAGEM, atendimento.getAteDataUltMensagem());
        to.setAttribute(Columns.ATE_ID_SESSAO, atendimento.getAteIdSessao());
        to.setAttribute(Columns.ATE_IP_ACESSO, atendimento.getAteIpAcesso());

        return to;
    }

    @Override
    public Atendimento create(Atendimento atendimento, AcessoSistema responsavel) throws ZetraException {
        try {
            Date dataAtual = Calendar.getInstance().getTime();
            atendimento.setAteDataInicio(dataAtual);
            atendimento.setAteDataUltMensagem(dataAtual);
            atendimento = AtendimentoHome.create(atendimento);

            // Gravar log do atendimento criado
            LogDelegate log = new LogDelegate(responsavel, Log.ATENDIMENTO_CHATBOT, Log.CREATE, Log.LOG_INFORMACAO);
            log.setAtendimento(atendimento.getAteCodigo());
            log.getUpdatedFields(atendimentoToTransferObject(atendimento).getAtributos(), null);
            log.write();

            return atendimento;
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Atendimento update(Atendimento atendimento, AcessoSistema responsavel) throws ZetraException {
        try {
            Atendimento atendimentoOld = AtendimentoHome.findByPrimaryKey(atendimento.getAteCodigo());

            Date dataAtual = Calendar.getInstance().getTime();
            atendimento.setAteDataUltMensagem(dataAtual);
            AtendimentoHome.update(atendimento);

            // Gravar log da atualização do atendimento
            LogDelegate log = new LogDelegate(responsavel, Log.ATENDIMENTO_CHATBOT, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setAtendimento(atendimento.getAteCodigo());
            log.getUpdatedFields(atendimentoToTransferObject(atendimento).getAtributos(), atendimentoToTransferObject(atendimentoOld).getAtributos());
            log.write();

            return atendimento;
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Atendimento findByEmailAndSessao(String ateEmailUsuario, String ateIdSessao, AcessoSistema responsavel) throws ZetraException {
        try {
            Atendimento atendimento = null;
            List<Atendimento> atendimentos = AtendimentoHome.findByAteEmailUsuarioAndAteIdSessao(ateEmailUsuario, ateIdSessao);
            if (atendimentos != null && !atendimentos.isEmpty()) {
                atendimento = atendimentos.get(0);
                // Verificar se o atendimento encontrado é do usuário corrente
                if (atendimento.getUsuario() != null && !atendimento.getUsuario().getUsuCodigo().equals(responsavel.getUsuCodigo())) {
                    // Se o atendimento não é do mesmo usuário autenticado, ou não tem usuário autenticado,
                    // então não reutiliza este atendimento
                    atendimento = null;
                }
            }

            return atendimento;
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public AtendimentoMensagem addMensagem(AtendimentoMensagem atendimentoMensagem, AcessoSistema responsavel) throws ZetraException {
        try {
            // Recupera a maior sequência para o código de atendimento
            int sequencia = AtendimentoMensagemHome.selectMaxAmeSequenciaByAteCodigo(atendimentoMensagem.getAteCodigo());

            Date dataAtual = Calendar.getInstance().getTime();
            atendimentoMensagem.setAmeData(dataAtual);
            atendimentoMensagem.setAmeSequencia(sequencia + 1);
            return AtendimentoMensagemHome.create(atendimentoMensagem);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<AtendimentoMensagem> lstMensagensByAtendimento(String ateCodigo, AcessoSistema responsavel) throws ZetraException {
        try {
            return AtendimentoMensagemHome.findByAteCodigoOrderByAmeSequencia(ateCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
