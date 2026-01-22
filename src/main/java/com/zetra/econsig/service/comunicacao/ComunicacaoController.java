package com.zetra.econsig.service.comunicacao;

import java.io.File;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Comunicacao;
import com.zetra.econsig.persistence.entity.ComunicacaoPermitida;
import com.zetra.econsig.persistence.entity.Usuario;

/**
 * <p>Title: ComunicacaoController</p>
 * <p>Description: interface Comunicacao Controller.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ComunicacaoController {
    public List<TransferObject> listComunicacoes(TransferObject criterio, AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> listComunicacoes(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> listComunicacoes(TransferObject criterio, boolean ordenacaoDesc, int offset, int count, AcessoSistema responsavel) throws ZetraException;

    public int countComunicacoes(TransferObject criterio, AcessoSistema responsavel) throws ZetraException;

    public String createComunicacao(TransferObject comunicacao, AcessoSistema responsavel) throws ZetraException;

    public String createComunicacao(TransferObject comunicacao, File anexoComunicacao, AcessoSistema responsavel) throws ZetraException;

    public List<String> createComunicacaoNseCodigo(TransferObject comunicacao, AcessoSistema responsavel) throws ZetraException;

    public List<String> createComunicacaoNseCodigo(TransferObject comunicacao, File anexoComunicacao, AcessoSistema responsavel) throws ZetraException;

    public String geraComunicacaoResposta(TransferObject comunicacao, String cmnCodigoPai, AcessoSistema responsavel) throws ZetraException;

    public Comunicacao findComunicacaoByPK(String cmnCodigo, AcessoSistema responsavel) throws ZetraException;

    public void logLeituraComunicacao(String cmnCodigo, AcessoSistema responsavel) throws ZetraException;

    public Usuario findResponsavelCmn(String cmnCodigo, AcessoSistema responsavel) throws ZetraException;

    public void bloqueiaCsaPorCmnPendente(AcessoSistema responsavel) throws ZetraException;

    public boolean temComunicacoesParaBloqueioCsa(String csaCodigo, AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> listaAssuntoComunicacao(AcessoSistema responsavel) throws ZetraException;

    public List<ComunicacaoPermitida> listaComunicacaoPermitida(AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> listarComunicacaoNaoLida(int diasAposEnvio, String papel, AcessoSistema responsavel) throws ZetraException;
}
