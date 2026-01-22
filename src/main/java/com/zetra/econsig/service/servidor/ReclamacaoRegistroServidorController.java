package com.zetra.econsig.service.servidor;


import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReclamacaoRegistroServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ReclamacaoRegistroServidorController</p>
 * <p>Description: Interface bean para manipulação de reclamação de servidor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ReclamacaoRegistroServidorController {

    public List<TransferObject> listaReclamacaoRegistroServidor (CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public int countReclamacaoRegistroServidor(CustomTransferObject criterio, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public String createReclamacaoRegistroServidor(CustomTransferObject reclamacaoRegistroServidor, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public CustomTransferObject buscaReclamacao(String rrsCodigo, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public List<TransferObject> lstReclamacaoMotivo(String rrsCodigo, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public String createTipoMotivoReclamacao(String tmrDescricao, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public void updateTipoMotivoReclamacao(TransferObject motivoReclamacao, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public void removeTipoMotivoReclamacao(String tmrCodigo, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public int countTipoMotivoReclamacao(int offset, int count, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public List<TransferObject> lstTipoMotivoReclamacao(AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public List<TransferObject> lstTipoMotivoReclamacao(int offset, int count, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;

    public TransferObject findTipoMotivoReclamacao(String tmrCodigo, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException;
}