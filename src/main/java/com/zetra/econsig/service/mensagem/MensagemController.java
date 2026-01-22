package com.zetra.econsig.service.mensagem;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MensagemTO;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.rest.request.MensagemRestResponse;

/**
 * <p>Title: MensagemController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public interface MensagemController {

    public MensagemTO findMensagem(MensagemTO mensagem, AcessoSistema responsavel) throws MensagemControllerException;

    public String createMensagem(MensagemTO mensagem, AcessoSistema responsavel) throws MensagemControllerException;

    public void createMensagemCsa(String menCodigo, String csaCodigo, AcessoSistema responsavel) throws MensagemControllerException;

    public void updateMensagem(MensagemTO mensagem, AcessoSistema responsavel) throws MensagemControllerException;

    public void removeMensagem(MensagemTO mensagem, AcessoSistema responsavel) throws MensagemControllerException;

    public void removeMensagemCsa(String menCodigo, AcessoSistema responsavel) throws MensagemControllerException;

    public int countMensagem(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException;

    public List<TransferObject> lstMensagem(CustomTransferObject criterio, int offset, int rows, AcessoSistema responsavel) throws MensagemControllerException;

    public List<TransferObject> lstMensagemBoasVindas(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException;

    public List<TransferObject> pesquisaMensagem(AcessoSistema responsavel, int rows, boolean mensagemLida) throws MensagemControllerException;

    public int countPesquisaMensagem(AcessoSistema responsavel) throws MensagemControllerException;

    public List<TransferObject> lstConsignatarias(String menCodigo, AcessoSistema responsavel) throws MensagemControllerException;

    public int enviaMensagemEmail(MensagemTO mensagem, List<String> papeis, boolean incluirBloqueadas, AcessoSistema responsavel) throws MensagemControllerException;

    public int countMensagemUsuarioSemLeitura(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException;

    public List<MensagemTO> lstMensagemUsuarioSemLeitura(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException;

    public void createLeituraMensagemUsuario(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException;

    public List<String> getDestinatarioMensagemPermitidaEmail(AcessoSistema responsavel) throws MensagemControllerException;

    public List<TransferObject> lstMensagemCsaBloqueio(String csaCodigo, AcessoSistema responsavel) throws MensagemControllerException;

    public List<MensagemRestResponse> parseToResponse(List<TransferObject> result);
}
