package com.zetra.econsig.persistence.dao.generic;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.HistoricoMovFinDAO;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericHistoricoMovFinDAO</p>
 * <p>Description: Implementacao Genérica do DAO de histórico de movimento. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericHistoricoMovFinDAO implements HistoricoMovFinDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericHistoricoMovFinDAO.class);

    @Override
    public void limparTabelaArquivo() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            jdbc.update("DELETE FROM " + Columns.TB_ARQUIVO_MOVIMENTO_VALIDACAO, queryParams);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
