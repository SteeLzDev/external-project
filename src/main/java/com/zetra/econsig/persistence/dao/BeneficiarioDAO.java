package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: BeneficiarioDAO</p>
 * <p>Description: Interface do DAO de Beneficiario</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface BeneficiarioDAO {

    public static final String INSERT_TMP_BENEFICIARIO_SQL = "INSERT INTO tb_tmp_imp_beneficiarios (nome_arquivo, ser_codigo, bfc_cpf, bfc_identificador, rse_codigo) values (?, ?, ?, ?, ?)";

    public void criaTabelaImportaBeneficiarios() throws DAOException;
    public void iniciarCargaBeneficiarios() throws DAOException;
    public void encerrarCargaBeneficiarios() throws DAOException;
    public void incluiBeneficiarios(String nomeArquivo, String serCodigo,String bfcCpf, String bfcIdentificador, String rseCodigo) throws DAOException;
    public void excluiBeneficiarios() throws DAOException;

}