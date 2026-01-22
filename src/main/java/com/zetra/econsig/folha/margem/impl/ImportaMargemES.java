package com.zetra.econsig.folha.margem.impl;

import static com.zetra.econsig.helper.texto.TextHelper.sqlJoin;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ImportaMargemException;
import com.zetra.econsig.folha.margem.ImportaMargemBase;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ImportaMargemES</p>
 * <p>Description: Implementação específica para o sistema do Gov. ES para rotina de valores de margens compulsórios e cartão.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaMargemES extends ImportaMargemBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaMargemES.class);

    @Override
    public void preRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
    	/*
         * REGRA:
         * 1. Servidores que não optaram por utilizar cartão , ou seja, o termo relacionado a adesão o mais recente está recusado ou
         *    não tem reserva de cartão para ele, então a margem cartão será zero e teto compulsório será de 30%
         * 2. Servidores que não tem termo de adesão com aceito ou recusado referente ao termo de adesão, porém ter reserva de cartão ativa então a
         *    margem cartão deve ter 10% e o compulsório teto de 20%
         * 3. Servidores com tem termo de adesão com aceito terão a margem cartão 10% e teto compulsório 20%
         * 4. Depois de definido o teto do compulsório de acordo com o opção de utilização de cartão (S/N) conseguiremos saber se o valor que a folha
         *    Enviou para o compulsório está dentro do teto ou não. Se o que a folha enviou de compulsório passou do teto, a diferença
         *    precisa ser retirada da margem de empréstimo.
         *
         * SOLUÇÃO:
         * 1. Criar uma tabela temporária com todas as opções mais recentes do usuário, onde a coluna TAD_CLASSE_ACAO tenha o valor da classe criada
         *    para o ES. (com.zetra.econsig.helper.termoadesao.TermoAdesaoAcaoES), a coluna terá o rse_codigo e a coluna LTU_TERMO_ACEITO
         * 2. Criar uma tabela temporário com os valores que serão da margem, essa tabela terá mais de um insert, sendo um insert para cada lógica,
         *    Essa tabela terá as colunas rse_codigo, rse_margem (margem empréstimo), vlt_teto_compulsorio, dif_comp_margem, rse_margem_3 (cartao)
         * 2.1 Insert de todos os servidores que optaram por cartão ou tenha reserva de cartão, consideranto então o teto de 20% e cartão 10% do salário
         * 2.2 Insert de todos os servidores que NÃO Optaram por cartão OU não tenham reserva de cartão, considerando então o teto de 30% e cartão 0.
         * 3. Update para pegar a diferença do valor do compulsório para o valor do teto (teto-compulsorio)
         * 4. update para diminuir o rse_margem caso o a diferença contenha valor negativo, ou seja, o compulsório é maior que o teto, então tiramos a
         *    diferença da margem.
         * 5. Com os valores definidios, atualizamos o rse_margem e o rse_margem_3
         */

    	try {
			criacaoTabelasAuxiliares(responsavel);
			calculaValorCheioMargem(tipoEntidade, entCodigos, responsavel);
		} catch (final ExportaMovimentoException ex) {
			LOG.error(ex.getMessage(), ex);
            throw new ImportaMargemException(ex);
		}
    }

    private void criacaoTabelasAuxiliares(AcessoSistema responsavel) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();

            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_aux_leitura_termos");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_aux_leitura_termos ( ");
            query.append("rse_codigo varchar(32), ");
            query.append("ltu_termo_aceito char(1), ");
            query.append("key tmp_aux_leitura_termos_IDX1 (rse_codigo) ");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_aux_calc_margem");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_aux_calc_margem ( ");
            query.append("rse_codigo varchar(32), ");
            query.append("rse_margem decimal(13,2), ");
            query.append("vlt_teto_compulsorio decimal(13,2) DEFAULT '0.00', ");
            query.append("dif_comp_margem decimal(13,2) DEFAULT '0.00', ");
            query.append("rse_margem_3 decimal(13,2) DEFAULT '0.00', ");
            query.append("key tmp_aux_calc_margem_IDX1 (rse_codigo) ");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Como em tabelas temporárias não se pode fazer insert na temporária usando dela mesmo em uma subconsulta
            // precisamos dessa tabelas com os rse_codigos já existentes na tmp_aux_leitura_termos
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_aux_rse_calc_margem");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_aux_rse_calc_margem ( ");
            query.append("rse_codigo varchar(32), ");
            query.append("key tmp_aux_rse_calc_margem_IDX1 (rse_codigo) ");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void calculaValorCheioMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("entCodigos", entCodigos);

        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();

            // Inserimos todos os registros servidores utilizaram da opção via termo de adesão
            query.append("INSERT INTO tmp_aux_leitura_termos (rse_codigo, ltu_termo_aceito) ");
            query.append("SELECT rse.rse_codigo, ltu_termo_aceito ");
            query.append("FROM tb_registro_servidor rse ");
            query.append("INNER JOIN tb_orgao org ON (rse.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (est.est_codigo = org.est_codigo) ");
            query.append("INNER JOIN tb_servidor ser ON (rse.ser_codigo = ser.ser_codigo) ");
            query.append("INNER JOIN tb_usuario_ser serUsu ON (serUsu.ser_codigo = ser.ser_codigo) ");
            query.append("INNER JOIN tb_usuario usu ON (usu.usu_codigo = serUsu.usu_codigo) ");
            query.append("INNER JOIN tb_leitura_termo_usuario ltu ON (usu.usu_codigo = serUsu.usu_codigo) ");
            query.append("INNER JOIN tb_termo_adesao tad ON (tad.tad_codigo = ltu.tad_codigo) ");
            if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            	query.append(" WHERE usu.usu_login = concat(concat(concat(concat(est.est_identificador, '-'), org.org_identificador), '-'), rse.rse_matricula) ");
            } else  {
            	query.append(" WHERE usu.usu_login = concat(concat(est.est_identificador, '-'), rse.rse_matricula) ");
            }

            switch (tipoEntidade) {
			case "RSE":
				query.append("AND rse.rse_codigo IN (:entCodigos) ");
				break;
			case "EST":
				query.append("AND est.est_codigo IN (:entCodigos) ");
				break;
			case "ORG":
				query.append("AND org.org_codigo IN (:entCodigos) ");
				break;
			case null:
			default:
				break;
			}

            query.append("AND tad.tad_classe_acao = 'com.zetra.econsig.helper.termoadesao.TermoAdesaoAcaoES' ");
            query.append("AND ltu.ltu_data = (SELECT MAX(ltu_data) FROM tb_leitura_termo_usuario ltu1 WHERE ltu.usu_codigo = ltu1.usu_codigo) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_aux_rse_calc_margem SELECT rse_codigo FROM tmp_aux_leitura_termos");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Inserimos os registro servidores que tem cartão, porém ainda não abriu para ceite ou recusa do termo.
            query.setLength(0);
            query.append("INSERT INTO tmp_aux_leitura_termos (rse_codigo, ltu_termo_aceito) ");
            query.append("SELECT DISTINCT ade.rse_codigo, 'S' ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (est.est_codigo = org.est_codigo) ");
            query.append("INNER JOIN tb_relacionamento_servico rel ON (rel.svc_codigo_origem = cnv.svc_codigo AND rel.tnt_codigo='").append(CodedValues.TNT_CARTAO).append("') ");
        	query.append("WHERE ade.sad_codigo ='").append(CodedValues.SAD_DEFERIDA).append("' ");

        	switch (tipoEntidade) {
			case "RSE":
				query.append("AND ade.rse_codigo IN (:entCodigos) ");
				break;
			case "EST":
				query.append("AND est.est_codigo IN (:entCodigos) ");
				break;
			case "ORG":
				query.append("AND org.org_codigo IN (:entCodigos) ");
				break;
			case null:
			default:
				break;
			}

            query.append("AND NOT EXISTS (SELECT 1 FROM tmp_aux_rse_calc_margem tmp WHERE ade.rse_codigo = tmp.rse_codigo) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // limpamos para atualizar o rse que existem
            query.setLength(0);
            query.append("DELETE FROM tmp_aux_rse_calc_margem");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_aux_rse_calc_margem SELECT rse_codigo FROM tmp_aux_leitura_termos");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Inserimos todos os registros servidores que ainda não existem na lista como não, pois não tem cartão, somente os não excluídos.
            query.setLength(0);
            query.append("INSERT INTO tmp_aux_leitura_termos (rse_codigo, ltu_termo_aceito) ");
            query.append("SELECT DISTINCT rse.rse_codigo, 'N' ");
            query.append("FROM tb_registro_servidor rse ");
            query.append("INNER JOIN tb_orgao org ON (rse.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (est.est_codigo = org.est_codigo) ");
            query.append("WHERE rse.srs_codigo IN (").append(sqlJoin(CodedValues.SRS_ATIVOS)).append(") ");

            switch (tipoEntidade) {
			case "RSE":
				query.append("AND rse.rse_codigo IN (:entCodigos) ");
				break;
			case "EST":
				query.append("AND est.est_codigo IN (:entCodigos) ");
				break;
			case "ORG":
				query.append("AND org.org_codigo IN (:entCodigos) ");
				break;
			case null:
			default:
				break;
			}

            query.append("AND NOT EXISTS (SELECT 1 FROM tmp_aux_rse_calc_margem tmp WHERE rse.rse_codigo = tmp.rse_codigo) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Insere valores margem
            query.setLength(0);
            query.append("INSERT INTO tmp_aux_calc_margem (rse_codigo, rse_margem, vlt_teto_compulsorio, rse_margem_3) ");
            query.append("SELECT tmp.rse_codigo, COALESCE(rse.rse_salario, '0.00')*0.4, ");
            query.append("CASE WHEN tmp.ltu_termo_aceito ='S' THEN COALESCE(rse.rse_salario, '0.00')*0.2 ELSE COALESCE(rse.rse_salario, '0.00')*0.3 END, ");
            query.append("CASE WHEN tmp.ltu_termo_aceito ='S' THEN COALESCE(rse.rse_salario, '0.00')*0.1 ELSE '0.00' END ");
            query.append("FROM tmp_aux_leitura_termos tmp ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = tmp.rse_codigo) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Calcula diferença do teto para o compulsório
            query.setLength(0);
            query.append("UPDATE tmp_aux_calc_margem tmp ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = tmp.rse_codigo) ");
            query.append("SET tmp.dif_comp_margem = tmp.vlt_teto_compulsorio-COALESCE(rse.rse_descontos_comp, '0.00') ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Retira da margem empréstimo o valor do compulsório excedente
            query.setLength(0);
            query.append("UPDATE tmp_aux_calc_margem tmp ");
            query.append("SET tmp.rse_margem = tmp.rse_margem+tmp.dif_comp_margem ");
            query.append("WHERE tmp.dif_comp_margem < 0 ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Atualizar os valores das margens
            query.setLength(0);
            query.append("UPDATE tmp_aux_calc_margem tmp ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = tmp.rse_codigo) ");
            query.append("SET rse.rse_margem = tmp.rse_margem, rse.rse_margem_3 = tmp.rse_margem_3 ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
