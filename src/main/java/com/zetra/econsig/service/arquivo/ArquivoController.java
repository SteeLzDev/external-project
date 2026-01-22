package com.zetra.econsig.service.arquivo;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Arquivo;
import com.zetra.econsig.persistence.entity.ArquivoRse;

/**
 * <p>Title: ArquivoController</p>
 * <p>Description: Interface Remota para o Session Bean para operações sobre arquivo</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ArquivoController {

    public void createArquivoServidor(TransferObject criterio, AcessoSistema responsavel) throws ArquivoControllerException;

    public void removeArquivoServidor(String arqCodigo, String serCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public TransferObject findArquivoServidor(String arqCodigo, String serCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public List<TransferObject> listArquivoServidor(String serCodigo, List<String> tarCodigos, AcessoSistema responsavel) throws ArquivoControllerException;

    public List<ArquivoRse> lstArquivoRse(String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public Arquivo getArquivoRse(String arqCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public TransferObject findArquivoResgistroServidorServidor(String arqCodigo, String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public void removeArquivoRegistroServidor(String arqCodigo, String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public void createArquivoRegistroServidor(String rseCodigo, String tarCodigo, TransferObject criterio, AcessoSistema responsavel) throws ArquivoControllerException;

    public List<TransferObject> lstArquivosRse(String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public void createArquivoMensagem(String menCodigo, String tarCodigo, String arqConteudo, String aseNome, AcessoSistema responsavel) throws CreateException, LogControllerException, ArquivoControllerException;

    public TransferObject findArquivoMensagem(String menCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public void removeArquivoMensagem(String arqCodigo, String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

    public void removeArquivoReconhecimentoFacial(String arqCodigo, String serCodigo, AcessoSistema responsavel) throws ArquivoControllerException;

}
