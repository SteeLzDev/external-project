package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: CalculoMargemDAO</p>
 * <p>Description: Interface do DAO de Cálculo de Margem Registro Servidor.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CalculoMargemDAO {

    public void calcularMargemExtraUsada(String tipoEntidade, List<String> entCodigos, boolean controlaMargem) throws DAOException;
    public void calcularMargemExtraRestante(String tipoEntidade, List<String> entCodigos) throws DAOException;

    public void calcularMargemExtraCasadaDireita(String tipoEntidade, List<String> entCodigos, List<Short> marCodigos) throws DAOException;
    public void calcularMargemExtraCasadaEsquerda(String tipoEntidade, List<String> entCodigos, List<Short> marCodigos) throws DAOException;
    public void calcularMargemExtraCasadaLateral(String tipoEntidade, List<String> entCodigos, List<Short> marCodigos) throws DAOException;
    public void calcularMargemExtraCasadaMinimo(String tipoEntidade, List<String> entCodigos, List<Short> marCodigos) throws DAOException;
    public void calcularMediaMargem(int periodoMediaMargem) throws DAOException;
}
