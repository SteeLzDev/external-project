package com.zetra.econsig.persistence.dao.oracle;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.CalendarioFolhaDAO;

/**
 * <p>Title: OracleCalendarioFolhaDAO</p>
 * <p>Description: Implementação do DAO de CalendarioFolha para Oracle.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleCalendarioFolhaDAO implements CalendarioFolhaDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleCalendarioFolhaDAO.class);

    @Override
    public void consolidarCalendarioFolha(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException {
        if (!PeriodoHelper.folhaMensal(responsavel)) {
            int qtdPeriodos = PeriodoHelper.getQuantidadePeriodosFolha(responsavel);

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            try {
                final StringBuilder query = new StringBuilder();

                query.append("insert into tb_tmp_calendario_quinzenal (org_codigo, periodo, num_periodo, data_ini, data_fim, sequencia)");
                query.append(" select");
                query.append(" org.org_codigo,");
                query.append(" coalesce(coalesce(cfo.cfo_periodo, cfe.cfe_periodo), cfc.cfc_periodo),");
                query.append(" coalesce(coalesce(cfo.cfo_num_periodo, cfe.cfe_num_periodo), cfc.cfc_num_periodo),");
                query.append(" coalesce(coalesce(cfo.cfo_data_ini, cfe.cfe_data_ini), cfc.cfc_data_ini),");
                query.append(" coalesce(coalesce(cfo.cfo_data_fim, cfe.cfe_data_fim), cfc.cfc_data_fim),");
                query.append(" ((extract(year from coalesce(coalesce(cfo.cfo_periodo,  cfe.cfe_periodo),  cfc.cfc_periodo)) * ").append(qtdPeriodos).append(") +");
                query.append("    coalesce(coalesce(cfo.cfo_num_periodo, cfe.cfe_num_periodo), cfc.cfc_num_periodo))");
                query.append(" from tb_consignante cse");
                query.append(" inner join tb_estabelecimento est on (est.cse_codigo = cse.cse_codigo)");
                query.append(" inner join tb_orgao org on (org.est_codigo = est.est_codigo)");
                query.append(" left outer join tb_calendario_folha_cse cfc on (cfc.cse_codigo = cse.cse_codigo)");
                query.append(" left outer join tb_calendario_folha_est cfe on (cfe.est_codigo = est.est_codigo");
                query.append("   and (cfc.cfc_periodo is null or cfc.cfc_periodo = cfe.cfe_periodo)");
                query.append(" )");
                query.append(" left outer join tb_calendario_folha_org cfo on (cfo.org_codigo = org.org_codigo");
                query.append("   and (cfc.cfc_periodo is null or cfc.cfc_periodo = cfo.cfo_periodo)");
                query.append("   and (cfe.cfe_periodo is null or cfe.cfe_periodo = cfo.cfo_periodo)");
                query.append(" )");
                query.append(" where 1=1");

                if (orgCodigos != null && orgCodigos.size() > 0) {
                    query.append(" and (org.org_codigo in (:orgCodigos))");
                    queryParams.addValue("orgCodigos", orgCodigos);
                }
                if (estCodigos != null && estCodigos.size() > 0) {
                    query.append(" and (est.est_codigo in (':estCodigos))");
                    queryParams.addValue("estCodigos", estCodigos);
                }

                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public void criarTabelaCalendarioQuinzenal(AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            query.append("CALL dropTableIfExists('tb_tmp_calendario_quinzenal')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_calendario_quinzenal (");
            query.append(" org_codigo varchar2(32) not null,");
            query.append(" periodo timestamp not null,");
            query.append(" num_periodo smallint not null,");
            query.append(" data_ini timestamp not null,");
            query.append(" data_fim timestamp not null,");
            query.append(" sequencia number(11,0) not null,");
            query.append(" primary key (org_codigo, periodo)");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index ix_cal_quinzenal on tb_tmp_calendario_quinzenal (org_codigo, sequencia)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
