package com.zetra.econsig.folha.exportacao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: Contagem </p>
 * <p>Description: Implementações específicas para a Prefeitura de Contagem.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Contagem extends ExportaMovimentoBase {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Contagem.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            LOG.debug("Inicio - Contagem.marcacaoDosContratos: " + DateHelper.getSystemDatetime());
            StringBuilder query = new StringBuilder();

            // Seta todos os contratos para serem exportados
            query.setLength(0);
            query.append("update tb_tmp_exportacao set percentual_padrao = coalesce(nullif(ade_indice, 'A'), 'S')");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Busca todas as consignações que potencialmente podem não ser enviadas para a folha, sendo que somente
            // a margem 1 deve ser levada em consideração para a possibilidade de ser arcada com 'N'.
            // A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
            // contratos removidos à margem restante até que a mesma seja positiva.
            query.setLength(0);
            query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, ade.ade_inc_margem, tmp.rse_margem_rest ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) ");
            query.append("where ade.ade_inc_margem = '").append(CodedValues.INCIDE_MARGEM_SIM).append("' ");
            // Regra1: Contratos celebrados com ADE_ANO_MES_INI <= 04/2013, todas as linhas deverão ser enviadas no movimento financeiro como "S".
            query.append("and (ade.ade_ano_mes_ini > '2013-04-01' or coalesce(ade.ade_indice, 'X') = 'A') ");
            // Regra de exceção, setada manualmente: respeita o valor no ade_indice
            query.append("and (ade.ade_indice is null or coalesce(ade.ade_indice, 'X') = 'A') ");
            query.append("order by tmp.rse_codigo, ");
            query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
            LOG.debug(query.toString());

            String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_inc_margem,rse_margem_rest";
            List<TransferObject> contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
            List<String> adeImpropria = obterContratosSemMargemMovimentoMensal(contratos);

            if (adeImpropria.size() > 0) {
                // Modifica todos os contratos para 'S' esceto os que foram listados como impróprios
                query.setLength(0);
                query.append("update tb_tmp_exportacao set percentual_padrao = 'N' where ade_codigo in (:adeImpropria)");
                queryParams.addValue("adeImpropria", adeImpropria);
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Regra2: Contratos celebrados com ADE_ANO_MES_INI > 04/2013, o contrato com maior prioridade e/ou mais antigo, sempre deverá ser enviado como "S", e o restante como "N", se não cabem na margem.
                query.setLength(0);
                query.append("drop temporary table if exists tmp_exp_pos_data_base");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("create temporary table tmp_exp_pos_data_base (ade_codigo varchar(32), rse_codigo varchar(32), sequencia int auto_increment, primary key (ade_codigo), key ix01 (rse_codigo), key ix02 (sequencia))");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("insert into tmp_exp_pos_data_base (ade_codigo, rse_codigo) ");
                query.append("select tmp.ade_codigo, tmp.rse_codigo ");
                query.append("from tb_tmp_exportacao tmp ");
                query.append("where tmp.percentual_padrao = 'N' ");
                query.append("and tmp.ade_indice is null ");
                query.append("order by tmp.rse_codigo, ");
                query.append("coalesce(tmp.svc_prioridade, 9999999) + 0, coalesce(tmp.cnv_prioridade, 9999999) + 0, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini), coalesce(tmp.ade_data_ref, tmp.ade_data), tmp.ade_numero");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("drop temporary table if exists tmp_exp_pos_data_base_1");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("create temporary table tmp_exp_pos_data_base_1 (rse_codigo varchar(32), sequencia_min int, primary key (rse_codigo))");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("insert into tmp_exp_pos_data_base_1 (rse_codigo, sequencia_min) ");
                query.append("select tmp.rse_codigo, min(tmp.sequencia) ");
                query.append("from tmp_exp_pos_data_base tmp ");
                query.append("group by tmp.rse_codigo");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("update tb_tmp_exportacao tmp ");
                query.append("set tmp.percentual_padrao = 'S' ");
                query.append("where tmp.percentual_padrao = 'N' ");
                query.append("and exists (");
                query.append("select 1 from tmp_exp_pos_data_base tmp2 ");
                query.append("inner join tmp_exp_pos_data_base_1 tmp3 on (tmp2.rse_codigo = tmp3.rse_codigo and tmp2.sequencia = tmp3.sequencia_min) ");
                query.append("where tmp.ade_codigo = tmp2.ade_codigo ");
                query.append("and tmp.rse_codigo = tmp2.rse_codigo)");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            LOG.debug("fim - Contagem.marcaDosContratos: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException | DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
