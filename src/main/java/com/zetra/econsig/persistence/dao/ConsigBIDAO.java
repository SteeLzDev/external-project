package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ConsigBIDAO</p>
 * <p>Description: DAO para carga das tabelas de Business Intelligence</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ConsigBIDAO {

    /**
     * Executa a atualização das dimensões e das tabelas de fato
     * @throws DAOException
     */
    public void atualizarBI(boolean populaDados) throws DAOException;

    /**
     * Executa a atualização das tabelas de dimensões:
     * -- tb_dimensao_categoria_servidor : dcs
     * -- tb_dimensao_comprometimento    : dco
     * -- tb_dimensao_consignataria      : dca
     * -- tb_dimensao_localizacao        : dlo
     * -- tb_dimensao_origem_contrato    : doc
     * -- tb_dimensao_prazo_contrato     : dpc
     * -- tb_dimensao_servico            : dse
     * -- tb_dimensao_status_contrato    : dsc
     * -- tb_dimensao_tempo              : dte
     * -- tb_dimensao_termino_contrato   : dtc
     * -- tb_dimensao_tipo_margem        : dtm
     * @throws DAOException
     */
    public void atualizarDimensoes() throws DAOException;

    /**
     * Cria as tabelas auxiliares necessárias para a geração das tabelas
     * de fatos: origem e término de contrato e data de exclusão do servidor.
     * @throws DAOException
     */
    public void atualizarTabelasAuxiliares() throws DAOException;

    /**
     * Executa a atualização da tabela de fato de contrato:
     * -- tb_fato_contrato               : fac
     * @throws DAOException
     */
    public void atualizarFatoContrato(boolean populaDados) throws DAOException;

    /**
     * Executa a atualização da tabela de fato de parcela:
     * -- tb_fato_parcela                : fap
     * @throws DAOException
     */
    public void atualizarFatoParcela(boolean populaDados) throws DAOException;

    /**
     * Executa a atualização da tabela  de fato de margem:
     * -- tb_fato_margem                 : fam
     * @throws DAOException
     */
    public void atualizarFatoMargem(boolean populaDados) throws DAOException;
}
