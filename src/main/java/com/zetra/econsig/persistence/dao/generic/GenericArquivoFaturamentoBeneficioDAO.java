package com.zetra.econsig.persistence.dao.generic;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.ArquivoFaturamentoBeneficioDAO;

/**
 * <p>Title: GenericArquivoFaturamentoBeneficioDAO</p>
 * <p>Description: Implementação Base do DAO de ArquivoFaturamentoBeneficioDAO</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericArquivoFaturamentoBeneficioDAO implements ArquivoFaturamentoBeneficioDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericArquivoFaturamentoBeneficioDAO.class);

    @Override
    public void removerArquivoFaturamentoBeneficio(List<String> orgCodigos, List<String> estCodigos, String periodo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            final StringBuilder query = new StringBuilder();
            query.append("DELETE ");
            query.append("FROM tb_arquivo_faturamento_ben ");
            query.append("WHERE 1 = 1 ");

            if (!TextHelper.isNull(periodo)) {
                query.append("AND EXISTS (SELECT 1 FROM tb_faturamento_beneficio fat ");
                query.append("WHERE (fat.fat_codigo = tb_arquivo_faturamento_ben.fat_codigo) ");
                query.append("AND FAT_PERIODO = :periodo) ");
                queryParams.addValue("periodo", periodo);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("AND EXISTS (SELECT 1 FROM tb_orgao org ");
                query.append("INNER JOIN tb_registro_servidor rse ON (org.org_codigo = rse.org_codigo) ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("WHERE (ade.ade_codigo = tb_arquivo_faturamento_ben.ade_codigo) ");
                query.append("AND org.org_codigo IN (:orgCodigos)) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("AND EXISTS (SELECT 1 FROM tb_orgao org ");
                query.append("INNER JOIN tb_registro_servidor rse ON (org.org_codigo = rse.org_codigo) ");
                query.append("INNER JOIN tb_aut_desconto ade ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("WHERE (ade.ade_codigo = tb_arquivo_faturamento_ben.ade_codigo) ");
                query.append("AND org.est_codigo IN (:estCodigos)) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            LOG.trace(query.toString());
            final int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
