package com.zetra.econsig.persistence.dao;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;

/**
 * <p>Title: MargemDAO</p>
 * <p>Description: Interface do DAO de Margem, utilizado para
 * manipulação da tabela de margens e margens dos registros
 * servidores.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface MargemDAO {
    public static final String ACAO_DEFAULT      = "0";
    public static final String ACAO_BLOQUEADO    = "1";
    public static final String ACAO_DESBLOQUEADO = "2";

    public void criaTabelaHistoricoRse(List<String> orgCodigos, List<String> estCodigos) throws DAOException;
    public void insereOcorrenciaRseStatusAlterados(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException;
    public List<TransferObject> lstTabelaPostoTipoRseAlterados(List<String> orgCodigos, List<String> estCodigos) throws DAOException;
    public void criaAtualizaMargemExtraServidorBatch(List<MargemRegistroServidor> margensExtraAdd, String strPeriodo, String rseCodigo) throws UpdateException;
    public void criaTabelaArquivoMargem(List<Map<String, Object>> dados, String nomeArqConfMargemSaida, String nomeArqConfMargemEntrada, String nomeArqConfMargemTradutor, String nomeArquivoFinal, AcessoSistema responsavel) throws UpdateException;
    public void alinhaVinculosRse() throws DAOException;

    public void criaTabelaVariacaoMargemLimiteDefinidoCSA(String periodoAtual, boolean margemTotal, AcessoSistema responsavel) throws DAOException;
    public void bloqueiaVariacaoMargemLimiteDefinidoCSA(boolean margemTotal, AcessoSistema responsavel) throws DAOException;
    public List<TransferObject> lstCsaQntdaVerbaBloqLimiteVariacaoMargem(AcessoSistema responsavel) throws DAOException;
    public String montaQueryListaBloqVarMargemCsa(AcessoSistema responsavel) throws DAOException;

    public void criaTabelaHistoricoRseMargemComplementar() throws DAOException;
    public void insereHistoricoRseMargemComplementar(String rseCodigo) throws DAOException;
}
