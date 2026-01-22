package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase.ContratosSemMargem;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RegraContratosNaoExportadosValidaMargem</p>
 * <p>Description: Classe com a implementação MYSQL da regra com a verificação dos contratos não exportados,
 * com validação da margem. Serve a sistemas de movimento mensal com restrição de validação de margem.
 * A classe estende a RegraContratosNaoExportados, já que não podem ser utilizadas em conjunto.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraContratosNaoExportadosValidaMargem extends RegraContratosNaoExportados {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraContratosNaoExportadosValidaMargem.class);

    @Override
    public void criarTabelasValidacao() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            query.append("create table if not exists tb_tmp_movimento_validacao_rcnevm (rse_codigo varchar(32),");
            query.append(" rse_margem_rest decimal(13,2), rse_margem_rest_2 decimal(13,2), rse_margem_rest_3 decimal(13,2),");
            query.append(" ade_codigo varchar(32), ade_vlr decimal(13,2), ade_inc_margem smallint,");
            query.append(" svc_prioridade int, cnv_prioridade int, ade_ano_mes_ini_ref date, ade_ano_mes_ini date, ade_data_ref datetime, ade_data datetime, ade_numero bigint(20) unsigned,");
            query.append(" primary key (ade_codigo))");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table if not exists tb_tmp_movimento_validacao (");
            query.append(" est_codigo varchar(32),");
            query.append(" est_identificador varchar(40),");
            query.append(" org_codigo varchar(32),");
            query.append(" org_identificador varchar(40),");
            query.append(" csa_codigo varchar(32),");
            query.append("  csa_identificador varchar(40),");
            query.append(" svc_codigo varchar(32),");
            query.append(" svc_identificador varchar(40),");
            query.append(" cnv_codigo varchar(32),");
            query.append(" cnv_cod_verba varchar(32),");
            query.append(" ser_nome varchar(100),");
            query.append(" ser_cpf varchar(19),");
            query.append(" rse_matricula varchar(20),");
            query.append(" ade_indice varchar(32),");
            query.append(" ade_prazo int,");
            query.append(" ade_data_ref datetime,");
            query.append(" ade_tipo_vlr varchar(1),");
            query.append(" ade_vlr_folha decimal(13,2),");
            query.append(" ade_ano_mes_ini date,");
            query.append(" ade_ano_mes_fim date,");
            query.append(" ade_cod_reg varchar(1),");
            query.append(" ade_vlr decimal(13,2),");
            query.append(" ade_numero int,");
            query.append(" KEY ix01 (rse_matricula, cnv_cod_verba));");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Cria uma tabela temporária com os contratos que seriam exportados.
     * Valida a margem do servidor de acordo com ExportaMovimentoBase
     * @return
     */
    @Override
    protected void criaTabelaTemporaria() throws ZetraException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            query.setLength(0);
            query.append("delete from tb_tmp_movimento_validacao_rcnevm");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tb_tmp_movimento_validacao_rcnevm (rse_codigo, rse_margem_rest, rse_margem_rest_2, rse_margem_rest_3, ade_codigo, ade_vlr, ade_inc_margem, svc_prioridade, cnv_prioridade, ade_ano_mes_ini_ref, ade_ano_mes_ini, ade_data_ref, ade_data, ade_numero)");
            query.append(" select rse.rse_codigo, rse.rse_margem_rest, rse.rse_margem_rest_2, rse.rse_margem_rest_3, ade.ade_codigo, ade.ade_vlr, ade.ade_inc_margem,");
            query.append(" coalesce(svc_prioridade, 9999999) + 0, coalesce(cnv_prioridade, 9999999) + 0, ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini, ade.ade_data_ref, ade.ade_data, ade.ade_numero");
            query.append(" from tb_parcela_desconto_periodo prd USE INDEX ()");
            query.append(" inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo)");
            query.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
            query.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");
            query.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)");
            query.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
            query.append(" inner join tb_orgao org on (org.org_codigo = rse.org_codigo)");
            query.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo)");
            query.append(" inner join tb_periodo_exportacao pex on (pex.org_codigo = org.org_codigo)");
            // -- Envia Servidor Excluido
            query.append(" left outer join tb_param_sist_consignante psi146 on (psi146.tpc_codigo = '146')");
            //-- Limite Valor Minimo Sistema
            query.append(" left outer join tb_param_sist_consignante psi101 on (psi101.tpc_codigo = '101')");
            query.append(" left outer join tb_param_sist_consignante psi184 on (psi184.tpc_codigo = '184')");
            // -- Limite Minimo Servico
            query.append(" left outer join tb_param_svc_consignante pse118 on (cnv.svc_codigo = pse118.svc_codigo and pse118.tps_codigo = '118')");
            query.append(" left outer join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo and oca.toc_codigo in ('6','7') and oca.oca_periodo > pex.pex_periodo)");
            query.append(" where (ade.sad_codigo in ('4','5','11','15') or (ade.sad_codigo in ('7', '8') and oca.oca_codigo is not null))");
            query.append(" and prd.spd_codigo = '4'");
            query.append(" and ade.ade_ano_mes_ini <= pex.pex_periodo");
            query.append(" and ade.ade_data < pex.pex_data_fim");
            query.append(" and ade.ade_int_folha = 1");
            query.append(" and (ifnull(psi184.psi_vlr, 'S') = 'S' or ade.ade_vlr >= ifnull(nullif(pse118.pse_vlr, ''), ifnull(nullif(psi101.psi_vlr, '') , 0)) + 0)");
            query.append(" and (ifnull(ade.ade_prazo, 9999999) > ifnull(ade.ade_prd_pagas, 0) or (ade.ade_vlr_sdo_ret IS NOT NULL AND ade.ade_vlr_sdo_ret > 0))");
            query.append(" and ((ifnull(psi146.psi_vlr, 'S') = 'N' and rse.srs_codigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')) or (ifnull(psi146.psi_vlr, 'S') = 'S'))");

            if (estCodigos != null && estCodigos.size() > 0) {
                query.append(" AND est.est_codigo IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append(" AND org.org_codigo IN (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Lista os contratos de servidores com margem negativa pela ordem de exportação
            // OBS: A ordenação é decrescente, pois a verificação é feita ao contrário, adicionando o valor dos
            // contratos removidos à margem restante até que a mesma seja positiva.
            query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_inc_margem ");
            query.append("from tb_tmp_movimento_validacao_rcnevm tmp ");
            query.append("where ((tmp.ade_inc_margem = '1' and tmp.rse_margem_rest   < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem = '2' and tmp.rse_margem_rest_2 < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem = '3' and tmp.rse_margem_rest_3 < 0.00) ");
            query.append("    OR (tmp.ade_inc_margem not in ('0','1','2','3') and (select mrs_margem_rest from tb_margem_registro_servidor mrs where mrs.rse_codigo = tmp.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
            query.append("order by tmp.rse_codigo, ");
            query.append("coalesce(tmp.svc_prioridade, 9999999) + 0 DESC, coalesce(tmp.cnv_prioridade, 9999999) + 0 DESC, coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_numero DESC");
            LOG.debug(query.toString());

            String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_inc_margem";
            List<TransferObject> contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
            ContratosSemMargem adeImpropria = (new ExportaMovimentoBase() {private static final long serialVersionUID = 1L;})
                    .obterContratosSemMargemMovimentoMensalv2(contratos, false);

            if (adeImpropria.getIntegralmenteSemMargem().size() > 0) {
                query.setLength(0);
                // Apaga os contratos que não devem ser lançados do último servidor
                query.append("delete from tb_tmp_movimento_validacao_rcnevm where ade_codigo in (:adeCodigos)");
                queryParams.addValue("adeCodigos", adeImpropria.getIntegralmenteSemMargem());
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // Depois de remover os contratos que não cabem na margem, cria tabela
            // da regra de contratos que não foram exportados
            query.setLength(0);
            query.append("delete from tb_tmp_movimento_validacao");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tb_tmp_movimento_validacao");
            query.append(" select est.est_codigo, est.est_identificador, org.org_codigo, org.org_identificador,");
            query.append(" csa.csa_codigo, csa.csa_identificador,");
            query.append(" svc.svc_codigo, svc.svc_identificador,");
            query.append(" cnv.cnv_codigo, cnv.cnv_cod_verba,");
            query.append(" ser.ser_nome, ser.ser_cpf, rse.rse_matricula,");
            if (consolidaDescontos) {
                query.append(" group_concat(distinct ade.ade_indice) as ade_indice, max(ade.ade_prazo) AS ade_prazo, max(ade.ade_data_ref) as ade_data_ref,");
                query.append(" group_concat(distinct ade.ade_tipo_vlr) as ade_tipo_vlr, sum(ade.ade_vlr_folha) as ade_vlr_folha,");
                query.append(" max(ade.ade_ano_mes_ini) as ade_ano_mes_ini, max(ade.ade_ano_mes_fim) as ade_ano_mes_fim, group_concat(distinct ade.ade_cod_reg) as ade_cod_reg,");
                query.append(" sum(prd.prd_vlr_previsto) as ade_vlr, max(ade.ade_numero) as ade_numero");
            } else {
                query.append(" ade.ade_indice, ade.ade_prazo, ade.ade_data_ref, ade.ade_tipo_vlr, ade.ade_vlr_folha, ade.ade_ano_mes_ini, ade.ade_ano_mes_fim, ade.ade_cod_reg,");
                query.append(" prd.prd_vlr_previsto as ade_vlr, ade.ade_numero");
            }
            query.append(" from tb_tmp_movimento_validacao_rcnevm tmp");
            query.append(" inner join tb_parcela_desconto_periodo prd on (prd.ade_codigo = tmp.ade_codigo)");
            query.append(" inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo)");
            query.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
            query.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");
            query.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo)");
            query.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)");
            query.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)");
            query.append(" inner join tb_servidor ser on (ser.ser_codigo = rse.ser_codigo)");
            query.append(" inner join tb_orgao org on (org.org_codigo = rse.org_codigo)");
            query.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo)");
            if (consolidaDescontos) {
                query.append(" group by rse.rse_codigo, csa.csa_codigo, cnv.cnv_cod_verba");
            }
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
