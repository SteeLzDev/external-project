package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ServidorDAO</p>
 * <p>Description: Interface do DAO de Servidor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ServidorDAO {
    public void calculaMargemRestante(String tipoEntidade, List<String> entCodigos) throws DAOException;

    public void calculaMargemUsada(String tipoEntidade, List<String> entCodigos, boolean controlaMargem) throws DAOException;

    public void calculaMargem1CasadaMargem3(String tipoEntidade, List<String> entCodigos) throws DAOException;
    public void calculaMargem1CasadaMargem3Esq(String tipoEntidade, List<String> entCodigos) throws DAOException;
    public void calculaMargem1CasadaMargem3Lateral(String tipoEntidade, List<String> entCodigos) throws DAOException;

    public void calculaMargens123Casadas(String tipoEntidade, List<String> entCodigos) throws DAOException;
    public void calculaMargens123CasadasEsq(String tipoEntidade, List<String> entCodigos) throws DAOException;

    public void zeraMargem(String tipoEntidade, List<String> entCodigos, boolean zerarMargemExclusao) throws DAOException;

    public void excluiServidores(String tipoEntidade, List<String> entCodigos, boolean geraTransferidos, boolean zerarMargemExclusao) throws DAOException;

    public String obtemServidoresTransferidos(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws DAOException;

    public List<TransferObject> obtemServidoresTransferidos(String rseCodigo, AcessoSistema responsavel) throws DAOException;

    public String buscaImgServidor(String serCpf, String rseCodigo) throws DAOException;

    public void setRseQtdAdeDefault(List<String> cnvCodigo) throws DAOException;

    public void createRelRegistroServidor(String rseCodigoOrigem, String rseCodigoDestino, String tntCodigo, AcessoSistema responsavel) throws DAOException;

    public List<TransferObject> buscarMargemServidor(String rseCodigo, AcessoSistema responsavel) throws DAOException;
}