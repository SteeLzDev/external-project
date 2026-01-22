package com.zetra.econsig.folha.exportacao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: Bauru</p>
 * <p>Description: Implementações específicas para a Prefeitura de Bauru.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Bauru extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Bauru.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            // Remove da tabela de exportação as ADE que não cabem na margem
            LOG.debug("Bauru.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
            removerContratosSemMargemMovimentoMensal();
            LOG.debug("fim - Bauru.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Remove da tabela de exportação as consignações na lista passada por parâmetro
     * @param adeCodigos
     * @param stat
     * @throws DataAccessException
     */
    @Override
    protected void excluirContratos(List<String> adeCodigos) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("adeCodigos", adeCodigos);
        // Só remove os contratos feitos até 07/04/2011, após esta data, todos devem ser enviados.
        String query = "delete from tb_tmp_exportacao where ade_codigo in (:adeCodigos) and ade_data < '2011-04-07'";
        LOG.debug(query);
        jdbc.update(query, queryParams);
    }
}
