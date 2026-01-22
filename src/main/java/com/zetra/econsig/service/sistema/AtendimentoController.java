package com.zetra.econsig.service.sistema;

import java.util.List;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Atendimento;
import com.zetra.econsig.persistence.entity.AtendimentoMensagem;

/**
 * <p>Title: AtendimentoController</p>
 * <p>Description: Controlador de negócios para atendimento via chatbot</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AtendimentoController {

    Atendimento create(Atendimento atendimento, AcessoSistema responsavel) throws ZetraException;

    Atendimento update(Atendimento atendimento, AcessoSistema responsavel) throws ZetraException;

    Atendimento findByEmailAndSessao(String ateEmailUsuario, String ateIdSessao, AcessoSistema responsavel) throws ZetraException;

    AtendimentoMensagem addMensagem(AtendimentoMensagem atendimentoMensagem, AcessoSistema responsavel) throws ZetraException;

    List<AtendimentoMensagem> lstMensagensByAtendimento(String ateCodigo, AcessoSistema responsavel) throws ZetraException;
}
