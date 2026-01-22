package com.zetra.econsig.persistence.dao.mysql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.generic.GenericAutorizacaoDAO;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlAutorizacaoDAO</p>
 * <p>Description: Implementacao do DAO de autorizacao para o MySql</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlAutorizacaoDAO extends GenericAutorizacaoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlAutorizacaoDAO.class);

    /**
     * Conclui as autorizações de desconto que não foram pagas no mês.
     * @param responsavel : usuário responsável pela operação
     * @throws DAOException
     */
    @Override
    public void concluiAdesNaoPagas(AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final boolean cseReimplanta = ParamSist.paramEquals(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, CodedValues.TPC_SIM, responsavel);
            final boolean cseConclui = ParamSist.paramEquals(CodedValues.TPC_CONCLUI_NAO_PAGAS, CodedValues.TPC_SIM, responsavel);
            final boolean csaDefineReimplante = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_REIMPLANTACAO, CodedValues.TPC_SIM, responsavel);
            final boolean csaDefineConclusao = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_CONCLUSAO_NAO_PAGAS, CodedValues.TPC_SIM, responsavel);
            final String defaultReimplante = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_NAO, responsavel);
            final String defaultConclusao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_CONCLUI_NAO_PG, CodedValues.TPC_NAO, responsavel);

            queryParams.addValue("defaultReimplante", defaultReimplante);
            queryParams.addValue("defaultConclusao", defaultConclusao);

            if ((!cseReimplanta || (cseReimplanta && csaDefineReimplante)) && cseConclui) {
                LOG.debug("CONCLUI ADE NÃO-PAGA: " + DateHelper.getSystemDatetime());

                // Monta o FROM das querys.
                StringBuilder from = new StringBuilder();
                from.append(Columns.TB_AUTORIZACAO_DESCONTO);
                from.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
                from.append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO).append(") ");
                from.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
                from.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
                from.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
                from.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(") ");
                if (cseReimplanta && csaDefineReimplante) {
                    from.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA).append(" pscReimplante ON (");
                    from.append(" pscReimplante.svc_codigo = ").append(Columns.CNV_SVC_CODIGO).append(" AND ");
                    from.append(" pscReimplante.csa_codigo = ").append(Columns.CNV_CSA_CODIGO).append(" AND ");
                    from.append(" pscReimplante.tps_codigo = '").append(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA).append("' AND ");
                    from.append(" COALESCE(pscReimplante.psc_ativo, '1') = '1')");
                }
                if (cseConclui && csaDefineConclusao) {
                    from.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA).append(" pscConclusao ON (");
                    from.append(" pscConclusao.svc_codigo = ").append(Columns.CNV_SVC_CODIGO).append(" AND ");
                    from.append(" pscConclusao.csa_codigo = ").append(Columns.CNV_CSA_CODIGO).append(" AND ");
                    from.append(" pscConclusao.tps_codigo = '").append(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA).append("' AND ");
                    from.append(" COALESCE(pscConclusao.psc_ativo, '1') = '1')");
                }

                // Monta o WHERE das querys.
                // Conclui todos os contratos deferidos, em andamento ou em estoque mensal
                // que têm o ade_vlr_folha nulo ou zero. Não testar por ade_paga = 'N', pois há casos
                // de contratos não-pagos que continuam na folha (ade_paga = 'N' e
                // ade_vlr_folha not null), por exemplo STATUS = N, P, T na AER.
                StringBuilder where = new StringBuilder();
                where.append("COALESCE(").append(Columns.ADE_VLR_FOLHA).append(", 0) = 0 ");
                where.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" in ('");
                where.append(CodedValues.SAD_DEFERIDA).append("', '").append(CodedValues.SAD_EMANDAMENTO);
                where.append("', '").append(CodedValues.SAD_ESTOQUE_MENSAL).append("') ");
                where.append(" AND ").append(Columns.ADE_ANO_MES_INI).append(" <= ");
                where.append(" (SELECT MAX(").append(Columns.PEX_PERIODO).append(") FROM ").append(Columns.TB_PERIODO_EXPORTACAO);
                where.append(" WHERE ").append(Columns.PEX_ORG_CODIGO).append(" = ").append(Columns.CNV_ORG_CODIGO).append(") ");
                if (cseReimplanta && csaDefineReimplante) {
                    where.append(" AND COALESCE(pscReimplante.psc_vlr, :defaultReimplante) = 'N' ");
                }
                if (cseConclui && csaDefineConclusao) {
                    where.append(" AND COALESCE(pscConclusao.psc_vlr, :defaultConclusao) = 'S' ");
                }

                StringBuilder query = new StringBuilder();
                query.append("SET @rownum := 0;");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                // Monta a query de inserção de ocorrência de conclusão de contratos.
                query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, ADE_CODIGO, OCA_DATA, OCA_PERIODO, TOC_CODIGO, USU_CODIGO, OCA_OBS) ");
                query.append("SELECT CONCAT('N', ");
                query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
                query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
                query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
                query.append(Columns.ADE_CODIGO).append(", ");
                query.append("NOW(), PEX_PERIODO_POS, ");
                query.append("'").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("', ");
                query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', ");
                query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel)).append("'");
                query.append(" FROM ").append(from);
                query.append(" WHERE ").append(where);

                // Insere as ocorrências
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                // Monta a query de atualização de status dos contratos.
                query.append(" UPDATE ").append(from);
                query.append(" SET ").append(Columns.ADE_SAD_CODIGO).append(" = '");
                query.append(CodedValues.SAD_CONCLUIDO).append("' ");
                query.append(" WHERE ").append(where);

                // Altera o status dos contratos.
                LOG.trace(query.toString());
                int rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                LOG.debug("FIM CONCLUI ADE NÃO-PAGA: " + DateHelper.getSystemDatetime());
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Atualiza as autorizações fazendo sad_codigo = 'Em Andamento' ou 'Concluido',
     * ade_prd_pagas++ apenas para as parcelas que foram Liquidadas. Se a lista de
     * adeCodigos é nula, ou vazia, realiza a operação para todas as autorizações.
     * @param orgCodigos  : Lista de códigos de órgão
     * @param estCodigos  : Lista de códigos de estabelecimento
     * @param adeCodigos  : Lista de códigos de consignação
     * @param atrasado    : True se importação de retorno atrasado
     * @param responsavel : Responsável pela alteração
     * @throws DAOException
     */
    @Override
    public void atualizaAdeExportadas(List<String> orgCodigos, List<String> estCodigos, List<String> adeCodigos, boolean atrasado, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        Connection conn = null;
        PreparedStatement stat = null;
        int linhas = 0;
        try {
            conn = DBHelper.makeConnection();

            String complementoAde = "";
            String complementoWhere = "";
            String complementoJoin = "";

            if (estCodigos != null && estCodigos.size() > 0) {
                complementoWhere = " AND cnv.org_codigo IN (SELECT org.org_codigo FROM tb_orgao org WHERE org.est_codigo IN (:estCodigos)) ";
                complementoJoin = " INNER JOIN tb_verba_convenio vco on (ade.VCO_CODIGO = vco.VCO_CODIGO) "
                                + " INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                                + " INNER JOIN tb_orgao org on (cnv.ORG_CODIGO = org.ORG_CODIGO and org.est_codigo IN (:estCodigos)) ";
                queryParams.addValue("estCodigos", estCodigos);
            } else if (orgCodigos != null && orgCodigos.size() > 0) {
                complementoWhere = " AND cnv.org_codigo IN (:orgCodigos) ";
                complementoJoin = " INNER JOIN tb_verba_convenio vco on (ade.VCO_CODIGO = vco.VCO_CODIGO) "
                                + " INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                                + " INNER JOIN tb_orgao org on (cnv.ORG_CODIGO = org.ORG_CODIGO and org.ORG_CODIGO IN (:orgCodigos)) ";
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            final StringBuilder query = new StringBuilder();

            LOG.debug("*****************************************************************");
            LOG.debug("CALCULA PARCELAS PAGAS ::::");

            if (adeCodigos != null && adeCodigos.size() > 0) {
                LOG.debug("Numero de registros: " + adeCodigos.size());
                LOG.debug("Passo 1: Criação da tabela");

                // Criando tabela temporaria com os ade_codigos informados
                query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_ade_pagas_all");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CREATE TEMPORARY TABLE tb_tmp_ade_pagas_all (ade_codigo VARCHAR(32) NOT NULL, KEY `IX_ADE` (`ade_codigo`))");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                int count = 0;

                // Insere os registros na tabela a cada conjunto de 5 mil registros
                stat = conn.prepareStatement("INSERT INTO tb_tmp_ade_pagas_all (ade_codigo) VALUES (?)");

                for (String adeCodigo : adeCodigos) {
                    stat.setString(1, adeCodigo);
                    stat.addBatch();
                    count++;

                    if (count % 5000 == 0) {
                        stat.executeBatch();
                        stat.clearBatch();

                        LOG.debug("Linhas inseridas: " + count);
                    }
                }
                stat.executeBatch();
                stat.clearBatch();
                LOG.debug("Linhas inseridas: " + count);

                // Revome os contratos duplicados
                query.setLength(0);
                query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_ade_pagas");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CREATE TEMPORARY TABLE tb_tmp_ade_pagas (ade_codigo VARCHAR(32) NOT NULL, KEY `IX_ADE` (`ade_codigo`))");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("INSERT INTO tb_tmp_ade_pagas SELECT DISTINCT ADE_CODIGO FROM tb_tmp_ade_pagas_all");
                LOG.trace(query.toString());
                int totalLinhas = jdbc.update(query.toString(), queryParams);

                LOG.debug("Total de linhas inseridas: " + totalLinhas);

                // Se foi passada a lista de autorizações, monta join com a tabela temporária
                complementoAde = " INNER JOIN tb_tmp_ade_pagas tmpAde ON (ade.ade_codigo = tmpAde.ade_codigo) ";
            }

            LOG.debug("*****************************************************************");
            LOG.debug("Passo 2: Atualização dos contratos");

            recalcularParcelasPagas(complementoAde, responsavel);

            if (atrasado && adeCodigos != null && adeCodigos.size() > 0) {
                // Se retorno atrasado, verifica se teve parcelas pagas com data anterior
                // à data do contrato, para reduzir o prazo.
                reduzPrazoPeloRetornoAtrasado(complementoAde, responsavel);
            }

            LOG.debug("FIM CALCULA PARCELAS PAGAS ::::");

            LOG.debug("*****************************************************************");

            // Parametros para conclusao de contratos
            final String sisReimplanta = (String) ParamSist.getInstance().getParam(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, responsavel);
            final String sisPreserva = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PRESERVA_PRD_REJEITADA, responsavel);
            final String csaReimplanta = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CSA_ALTERA_REIMPLANTACAO, responsavel);
            final String csaPreserva = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD, responsavel);
            final String paramCarenciaConclusaoSoComSdv = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_APENAS_COM_SDV, responsavel);
            final String defaultReimplante = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_NAO, responsavel);
            final String defaultPreservacao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_NAO, responsavel);

            queryParams.addValue("defaultReimplante", defaultReimplante);
            queryParams.addValue("defaultPreservacao", defaultPreservacao);

            // Recupera o parametro de sistema que informa se a folha possui carencia
            // para a conclusão dos contratos.
            final int carenciaFolha = Integer.parseInt((String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, "0", responsavel));
            queryParams.addValue("carenciaFolha", carenciaFolha);

            LOG.debug("*****************************************************************");
            LOG.debug("carencia da folha: " + carenciaFolha);
            LOG.debug("*****************************************************************");

            // se há carência para conclusão de contratos apenas se este tiver capital devido,
            // seta o campo ade_carencia_final para o valor do parâmetro. Assim, apenas estes contratos
            // ficarão em carência. Os demais serão concluídos.
            if (paramCarenciaConclusaoSoComSdv != null) {
                Integer carenciaConclusao = Integer.parseInt(paramCarenciaConclusaoSoComSdv);
                if (carenciaConclusao > 0) {
                    atualizaCarenciaFinal(carenciaConclusao, (atrasado ? complementoAde : null), responsavel);
                }
            }

            String rownum = "SET @rownum := 0; ";
            LOG.trace(rownum);
            jdbc.update(rownum, queryParams);

            // DESENV-17809: Criando tabela temporaria com a maior data entre ade_ano_mes_fim, prd.prd_data_desconto, pdp.prd_data_desconto para comparação na conclusão dos contratos
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_data_ultima_parcela");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_data_ultima_parcela (ade_codigo varchar(32) not null, data_encerramento date not null, data_ultima_parcela date not null, PRIMARY KEY (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tmp_data_ultima_parcela (ade_codigo, data_encerramento, data_ultima_parcela) ");
            query.append("select ade.ade_codigo, ");
            query.append("  greatest(coalesce(max(prd.prd_data_desconto), ade_ano_mes_fim), coalesce(max(pdp.prd_data_desconto), ade_ano_mes_fim), ade.ade_ano_mes_fim), ");
            query.append("  greatest(coalesce(max(prd.prd_data_desconto), ade_ano_mes_ini), coalesce(max(pdp.prd_data_desconto), ade_ano_mes_ini), min(pex.pex_periodo)) ");
            query.append("from tb_aut_desconto ade ");
            if (atrasado) {
                query.append(complementoAde);
            }
            if (TextHelper.isNull(complementoJoin)) {
                query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
                query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            } else {
                query.append(complementoJoin);
            }
            query.append("inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) ");
            query.append("left outer join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo and prd.spd_codigo in ('5','6','7')) ");
            query.append("left outer join tb_parcela_desconto_periodo pdp on (ade.ade_codigo = pdp.ade_codigo and pdp.spd_codigo in ('4','5','6','7','8')) ");
            query.append("where ade.sad_codigo not in ('3','7','8','9') ");
            query.append("  and ade.ade_prazo is not null ");
            query.append("  and ade.ade_int_folha <> 0 ");
            query.append(complementoWhere);
            query.append("group by ade.ade_codigo, ade_ano_mes_fim, ade_ano_mes_ini");
            LOG.trace(query.toString());
            int totalLinhasTmpDataUltimaParcela = jdbc.update(query.toString(), queryParams);
            LOG.debug("Total de linhas inseridas na tmp_data_ultima_parcela: " + totalLinhasTmpDataUltimaParcela);


            String insertOcorrencia = "INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, ade_codigo, oca_data, oca_periodo, toc_codigo, usu_codigo, oca_obs) "
                    + "SELECT CONCAT('Z',"
                    + " DATE_FORMAT(NOW(), '%y%m%d%H%i%S'),"
                    + " SUBSTRING(LPAD(ade.ade_numero, 12, '0'), 1, 12),"
                    + " SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), "
                    + "ade.ade_codigo, NOW(), pex.pex_periodo_pos, :tocCodigo ,'1', :ocaObs FROM ";

            String tabelaSis = "tb_aut_desconto ade "
                    + "INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) "
                    + "INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) "
                    + "INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) "
                    + "LEFT OUTER JOIN tmp_data_ultima_parcela dup on (ade.ade_codigo = dup.ADE_CODIGO) ";

            String tabelaCsa = "tb_aut_desconto ade "
                    + "INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) "
                    + "INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) "
                    + "INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) "
                    + "LEFT OUTER JOIN tmp_data_ultima_parcela dup on (ade.ade_codigo = dup.ADE_CODIGO) "
                    + "LEFT OUTER JOIN tb_param_svc_consignataria psc ON (cnv.svc_codigo = psc.svc_codigo AND cnv.csa_codigo = psc.csa_codigo AND psc.tps_codigo = '36' AND (psc.psc_ativo = '1' OR psc.psc_ativo is null)) ";

            if (atrasado) {
                tabelaSis += complementoAde;
                tabelaCsa += complementoAde;
            }

            // Atenção: Não conclui contratos que tenham
            // Valor de saldo devedor setado, mesmo que seja zero. O controle de contratos com saldo
            // devedor é feito em outra rotina no DAO ControleSaldoDvImpRetornoDAO
            String whereConclusao = " WHERE pex.pex_periodo >= DATE_ADD(dup.data_encerramento, INTERVAL coalesce(ade.ade_carencia_final, 0) + :carenciaFolha MONTH) "
                    + "AND pex.pex_periodo = dup.data_ultima_parcela "
                    + "AND ade.ade_int_folha = 1 "
                    + "AND ade.ade_vlr_sdo_ret IS NULL "
                    + complementoWhere;

            String whereCarencia = " WHERE pex.pex_periodo >= DATE_ADD(ade.ade_ano_mes_fim, INTERVAL :carenciaFolha MONTH) "
                    + "AND ade.ade_int_folha = 1 "
                    + "AND ade_carencia_final IS NOT NULL "
                    + "AND ade_carencia_final > 0 "
                    + complementoWhere;

            /*
             * Se o parametro do sistema de reimplante for nulo, vazio ou igual a N ou
             * se o parametro de sistema de reimplante for igual a S e o parmetro de sistema
             * de preservar parcela for igual a N entao não será necessario verificar os demais parametros,
             * sendo que os contratos serão concluídos quando a data final for alcançada,
             * independentemente do número de parcelas pagas.
             */
            if ((sisReimplanta == null || sisReimplanta.equals(CodedValues.TPC_NAO) || sisReimplanta.equals("")) ||
                    (sisReimplanta.equals(CodedValues.TPC_SIM) && sisPreserva != null && sisPreserva.equals(CodedValues.TPC_NAO))) {

                LOG.debug("*****************************************************************");
                LOG.debug("Ocorrência de conclusão para contratos em andamento quando sistema não reimplanta ou reimplanta e não preserva parcela");
                query.setLength(0);
                query.append(insertOcorrencia).append(tabelaSis).append(whereConclusao).append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
                queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_CONTRATO);
                queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);

                LOG.debug("*****************************************************************");
                LOG.debug("Ocorrência de conclusão sem desconto para contratos deferidos, em carência e em estoque quando sistema não reimplanta ou reimplanta e não preserva parcela");
                query.setLength(0);
                query.append(insertOcorrencia).append(tabelaSis).append(whereConclusao).append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO_SEM_DESCONTO, "','")).append("') ");
                queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
                queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);

                LOG.debug("*****************************************************************");
                LOG.debug("Altera status para concluído quando sistema não reimplanta ou reimplanta e não preserva parcela");
                query.setLength(0);
                query.append("UPDATE ").append(tabelaSis).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
                query.append(whereConclusao).append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO, "','")).append("') ");
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);

                LOG.debug("*****************************************************************");
                LOG.debug("Insere ocorrência de carência final para contratos em andamento quando sistema não reimplanta ou reimplanta e não preserva parcela");
                query.setLength(0);
                query.append(insertOcorrencia).append(tabelaSis).append(whereCarencia).append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
                queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_EMCARENCIA));
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);

                LOG.debug("*****************************************************************");
                LOG.debug("Insere ocorrência de carência final para contratos deferidos quando sistema não reimplanta ou reimplanta e não preserva parcela");
                query.setLength(0);
                query.append(insertOcorrencia).append(tabelaSis).append(whereCarencia).append(" AND ade.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");
                queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMCARENCIA));
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);

                // Insere a ocorrência de carência final para os contratos em estoque
                LOG.debug("*****************************************************************");
                LOG.debug("Insere ocorrência de carência final para contratos em estoque quando sistema não reimplanta ou reimplanta e não preserva parcela");
                query.setLength(0);
                query.append(insertOcorrencia).append(tabelaSis).append(whereCarencia).append(" AND ade.sad_codigo = '").append(CodedValues.SAD_ESTOQUE_MENSAL).append("' ");
                queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_ESTOQUE_MENSAL, CodedValues.SAD_EMCARENCIA));
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);

                LOG.debug("*****************************************************************");
                LOG.debug("Altera o status dos contratos para em carência quando sistema não reimplanta ou reimplanta e não preserva parcela");
                query.setLength(0);
                query.append("UPDATE ").append(tabelaSis).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_EMCARENCIA).append("' ");
                query.append(whereCarencia).append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ALTERACAO_CARENCIA, "','")).append("') ");
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);

                // Se os parâmetros de sistema de reimplante e de preservação forem ambos S
            } else if (sisReimplanta.equals(CodedValues.TPC_SIM) &&
                    (sisPreserva == null || sisPreserva.equals("") || sisPreserva.equals(CodedValues.TPC_SIM))) {
                /*
                 * Se a CSA não pode decidir por preservação de parcela, então vai preservar para todos os casos.
                 * Contratos serão concluídos quando a data final for alcançada e o número de parcelas pagas for
                 * igual ao prazo.
                 */
                if (csaPreserva == null || csaPreserva.equals("") || csaPreserva.equals(CodedValues.TPC_NAO)) {
                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de conclusão em contratos quando sistema preserva parcela");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaSis).append(whereConclusao);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_CONTRATO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de conclusão sem desconto em contratos em carência quando sistema preserva parcela");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaSis).append(whereConclusao);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMCARENCIA).append("' AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Altera o status dos contratos para concluído quando sistema preserva parcela");
                    query.setLength(0);
                    query.append("UPDATE ").append(tabelaSis).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
                    query.append(whereConclusao).append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO_PRESERVA_PRD, "','")).append("') AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de em carência em contratos em andamento quando sistema preserva parcela");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaSis).append(whereCarencia);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_EMCARENCIA));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Altera o status dos contratos em andamento para em carência quando sistema preserva parcela");
                    query.setLength(0);
                    query.append("UPDATE ").append(tabelaSis).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_EMCARENCIA).append("' ");
                    query.append(whereCarencia).append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' AND ade.ade_prazo <= ade.ade_prd_pagas  ");
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    // Se a CSA pode optar pela preservação de parcela.
                } else {
                    String comp1 = "";
                    String comp2 = "";
                    boolean csaPodeReimplantar = false;

                    /*
                     * Há o reimplante, ou porque a CSA pode optar por ele e o fez, ou
                     * porque a CSA não pode optar e portanto o parâmetro de sistema
                     * determina que haja o reimplante.
                     */
                    if (csaReimplanta != null && csaReimplanta.equals(CodedValues.TPC_SIM)) {
                        comp1 = " LEFT OUTER JOIN tb_param_svc_consignataria psc1 on (psc1.svc_codigo = cnv.svc_codigo and psc1.csa_codigo = cnv.csa_codigo and psc1.tps_codigo = '35' AND (psc1.psc_ativo = '1' OR psc1.psc_ativo is null))";
                        comp2 = " AND ifnull(psc1.psc_vlr, :defaultReimplante) = 'S' ";
                        csaPodeReimplantar = true;
                    }

                    // Início - CSA optou por não preservar parcelas.

                    // DESENV-16079 - Quando a CSA optou por não preservar existe a possibilidade dela fazer o reimplante da parcela manual função 515
                    // sendo assim é necessário verificar se existe parcela em aberto maior que a data fim, se existir estes contratos não podem ser concluídos.
                    String ignoraContratosParcelasReimplanteManual = " AND NOT EXISTS (SELECT pdp.ade_codigo FROM tb_parcela_desconto_periodo pdp WHERE ade.ade_codigo = pdp.ade_codigo AND ade.ade_prazo IS NOT NULL AND pdp.prd_data_desconto > pex.pex_periodo) ";

                    LOG.debug("*****************************************************************");
                    LOG.debug("Insere ocorrência de conclusão para contratos em andamento cuja CSA optou por não preservar parcelas");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereConclusao);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
                    query.append(comp2);
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'N' ");
                    query.append(ignoraContratosParcelasReimplanteManual);
                    queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_CONTRATO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de conclusâo sem desconto em contratos deferidos, em carência e em estoque cuja CSA optou por não preservar parcelas");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereConclusao);
                    query.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO_SEM_DESCONTO, "','")).append("') ");
                    query.append(comp2);
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'N' ");
                    query.append(ignoraContratosParcelasReimplanteManual);
                    queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Altera o status para concluído para os contratos cuja CSA optou por não preservar parcelas");
                    query.setLength(0);
                    query.append("UPDATE ").append(tabelaCsa).append(comp1).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
                    query.append(whereConclusao);
                    query.append(comp2);
                    query.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO, "','")).append("') ");
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'N' ");
                    query.append(ignoraContratosParcelasReimplanteManual);
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de em carência para contratos em andamento cuja CSA optou por não preservar parcelas");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereCarencia);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
                    query.append(comp2);
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'N' ");
                    query.append(ignoraContratosParcelasReimplanteManual);
                    queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_EMCARENCIA));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de em carência para contratos deferidos cuja CSA optou por não preservar parcelas");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereCarencia);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");
                    query.append(comp2);
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'N' ");
                    query.append(ignoraContratosParcelasReimplanteManual);
                    queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMCARENCIA));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de em carência para contratos em estoque cuja CSA optou por não preservar parcelas");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereCarencia);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_ESTOQUE_MENSAL).append("' ");
                    query.append(comp2);
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'N' ");
                    query.append(ignoraContratosParcelasReimplanteManual);
                    queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_ESTOQUE_MENSAL, CodedValues.SAD_EMCARENCIA));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Altera o status para em carência dos contratos cuja CSA optou por não preservar parcelas");
                    query.setLength(0);
                    query.append("UPDATE ").append(tabelaCsa).append(comp1).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_EMCARENCIA).append("' ");
                    query.append(whereCarencia);
                    query.append(comp2);
                    query.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ALTERACAO_CARENCIA, "','")).append("') ");
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'N' ");
                    query.append(ignoraContratosParcelasReimplanteManual);
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    // Fim - CSA optou por não preservar parcelas.

                    // Início - CSA optou por preservar parcelas.

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de conclusão em contratos em andamento cuja CSA optou por preservar parcelas");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereConclusao);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    query.append(comp2);
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'S' ");
                    queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_CONTRATO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de conclusão sem desconto em contratos em carência cuja CSA optou por preservar parcelas");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereConclusao);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMCARENCIA).append("' AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    query.append(comp2);
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'S' ");
                    queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Altera status para concluído para contratos em andamento e em carência cuja CSA optou por preservar parcelas");
                    query.setLength(0);
                    query.append("UPDATE ").append(tabelaCsa).append(comp1).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
                    query.append(whereConclusao);
                    query.append(comp2);
                    query.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO_PRESERVA_PRD, "','")).append("') AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'S' ");
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Ocorrência de em carência em contratos em andamento cuja CSA optou por preservar parcelas");
                    query.setLength(0);
                    query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereCarencia);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    query.append(comp2);
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'S' ");
                    queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                    queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_EMCARENCIA));
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    LOG.debug("*****************************************************************");
                    LOG.debug("Altera status para em carência para contratos em andamento cuja CSA optou por preservar parcelas");
                    query.setLength(0);
                    query.append("UPDATE ").append(tabelaCsa).append(comp1).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_EMCARENCIA).append("' ");
                    query.append(whereCarencia);
                    query.append(comp2);
                    query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' AND ade.ade_prazo <= ade.ade_prd_pagas ");
                    query.append(" AND ifnull(psc.psc_vlr, :defaultPreservacao) = 'S' ");
                    LOG.trace(query.toString());
                    linhas = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + linhas);

                    // Fim - CSA optou por preservar parcelas.

                    // Se a CSA pode optar pelo reimplante e optou por não reimplantar.
                    if (csaPodeReimplantar) {
                        comp2 = " AND ifnull(psc1.psc_vlr, :defaultReimplante) = 'N' ";

                        LOG.debug("*****************************************************************");
                        LOG.debug("Ocorrência de conclusão em contratos em andamento cuja CSA optou por não reimplantar");
                        query.setLength(0);
                        query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereConclusao);
                        query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
                        query.append(comp2);
                        query.append(ignoraContratosParcelasReimplanteManual);
                        queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_CONTRATO);
                        queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                        LOG.trace(query.toString());
                        linhas = jdbc.update(query.toString(), queryParams);
                        LOG.trace("Linhas afetadas: " + linhas);

                        LOG.debug("*****************************************************************");
                        LOG.debug("Ocorrência de conclusão sem desconto em contratos deferidos, em carência e em estoque cuja CSA optou por não reimplantar");
                        query.setLength(0);
                        query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereConclusao);
                        query.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO_SEM_DESCONTO, "','")).append("') ");
                        query.append(comp2);
                        query.append(ignoraContratosParcelasReimplanteManual);
                        queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
                        queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                        LOG.trace(query.toString());
                        linhas = jdbc.update(query.toString(), queryParams);
                        LOG.trace("Linhas afetadas: " + linhas);

                        LOG.debug("*****************************************************************");
                        LOG.debug("Altera o status para concluído para os contratos cuja CSA optou por não reimplantar");
                        query.setLength(0);
                        query.append("UPDATE ").append(tabelaCsa).append(comp1).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
                        query.append(whereConclusao);
                        query.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO, "','")).append("') ");
                        query.append(comp2);
                        query.append(ignoraContratosParcelasReimplanteManual);
                        LOG.trace(query.toString());
                        linhas = jdbc.update(query.toString(), queryParams);
                        LOG.trace("Linhas afetadas: " + linhas);

                        LOG.debug("*****************************************************************");
                        LOG.debug("Ocorrência de em carência para contratos em andamento cuja CSA optou por não reimplantar");
                        query.setLength(0);
                        query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereCarencia);
                        query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
                        query.append(comp2);
                        queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                        queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_EMCARENCIA));
                        LOG.trace(query.toString());
                        linhas = jdbc.update(query.toString(), queryParams);
                        LOG.trace("Linhas afetadas: " + linhas);

                        LOG.debug("*****************************************************************");
                        LOG.debug("Ocorrência de em carência para contratos deferidos cuja CSA optou por não reimplantar");
                        query.setLength(0);
                        query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereCarencia);
                        query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");
                        query.append(comp2);
                        queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                        queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMCARENCIA));
                        LOG.trace(query.toString());
                        linhas = jdbc.update(query.toString(), queryParams);
                        LOG.trace("Linhas afetadas: " + linhas);

                        LOG.debug("*****************************************************************");
                        LOG.debug("Ocorrência de em carência para contratos em estoque cuja CSA optou por não reimplantar");
                        query.setLength(0);
                        query.append(insertOcorrencia).append(tabelaCsa).append(comp1).append(whereCarencia);
                        query.append(" AND ade.sad_codigo = '").append(CodedValues.SAD_ESTOQUE_MENSAL).append("' ");
                        query.append(comp2);
                        queryParams.addValue("tocCodigo", CodedValues.TOC_INFORMACAO);
                        queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_ESTOQUE_MENSAL, CodedValues.SAD_EMCARENCIA));
                        LOG.trace(query.toString());
                        linhas = jdbc.update(query.toString(), queryParams);
                        LOG.trace("Linhas afetadas: " + linhas);

                        LOG.debug("*****************************************************************");
                        LOG.debug("Altera o status para carência para contratos cuja CSA optou por não reimplantar");
                        query.setLength(0);
                        query.append("UPDATE ").append(tabelaCsa).append(comp1).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_EMCARENCIA).append("' ");
                        query.append(whereCarencia);
                        query.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ALTERACAO_CARENCIA, "','")).append("') ");
                        query.append(comp2);
                        LOG.trace(query.toString());
                        linhas = jdbc.update(query.toString(), queryParams);
                        LOG.trace("Linhas afetadas: " + linhas);
                    }
                }
            }

            // Conclusão de contratos que tem ade_prd_pagas maior ou igual a ade_prazo
            if (carenciaFolha == 0 && !atrasado) {
                String whereConclusaoGeral = "WHERE ade.ade_prd_pagas >= ade.ade_prazo "
                        + "AND ade.ade_carencia_final IS NULL "
                        + "AND ade.sad_codigo IN ( '" + TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO_PAGAS_MAIOR_PRAZO, "','") + "') "
                        + "AND ade.ade_int_folha = 1 "
                        + "AND ade.ade_vlr_sdo_ret IS NULL "
                        + complementoWhere;
                // Atenção: Não conclui contratos que tenham
                // Valor de saldo devedor setado, mesmo que seja zero. O controle de contratos com saldo
                // devedor é feito em outra rotina no DAO ControleSaldoDvImpRetornoDAO

                LOG.debug("*****************************************************************");
                LOG.debug("Ocorrência de conclusão para contratos que estão com pagas >= prazo ");
                query.setLength(0);
                query.append(insertOcorrencia).append(tabelaSis).append(whereConclusaoGeral);
                queryParams.addValue("tocCodigo", CodedValues.TOC_CONCLUSAO_CONTRATO);
                queryParams.addValue("ocaObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel));
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);

                LOG.debug("*****************************************************************");
                LOG.debug("Altera status para concluído dos contratos que estão com pagas >= prazo");
                query.setLength(0);
                query.append("UPDATE ").append(tabelaSis).append(" SET ade.sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
                query.append(whereConclusaoGeral);
                LOG.trace(query.toString());
                linhas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhas);
            }

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_ade_pagas");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_ade_pagas_all");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Recalcula a quantidade de parcelas pagas das consignações pelas parcelas
     * presentes nas tabelas histórica e do período, e atualiza a informação
     * na tabela tb_aut_desconto, campos ADE_PRD_PAGAS e ADE_PRD_PAGAS_TOTAL.
     * @param complementoAde
     * @param responsavel
     * @throws DAOException
     */
    @Override
    public void recalcularParcelasPagas(String complementoAde, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        int linhas = 0;
        try {
            final StringBuilder query = new StringBuilder();

            // Conta todas as parcelas pagas pela folha a partir da data de inicio do contrato.
            // Status de parcelas pagas:
            List<String> spdCodigoRecalculo = new ArrayList<>();
            spdCodigoRecalculo.add(CodedValues.SPD_LIQUIDADAFOLHA);
            spdCodigoRecalculo.add(CodedValues.SPD_LIQUIDADAMANUAL);
            spdCodigoRecalculo.add(CodedValues.SPD_REJEITADAFOLHA);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_prd_pagas_1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_tmp_prd_pagas_1 (ade_codigo VARCHAR(32) NOT NULL, prd_pagas_1 INT, prd_pagas_total_1 INT, PRIMARY KEY (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Uma tabela irá armazenar o total de pagas da tabela historica
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_prd_pagas_1 (ade_codigo, prd_pagas_1, prd_pagas_total_1) ");
            query.append(" SELECT ade.ade_codigo,");
            //DESENV-10666: foram incluídas as parcelas rejeitadas nesta query para recuperar o caso em que todas as parcelas existentes até o momento tenham sido rejeitadas
            //              (levando-se me conta sistemas com períodos agrupados). neste caso deve-se recuperar o contrato para atualizar o ade_prd_pagas para 0.
            query.append(" SUM(CASE WHEN (prd.prd_data_desconto >= ade.ade_ano_mes_ini AND prd.spd_codigo <> '").append(CodedValues.SPD_REJEITADAFOLHA).append("') THEN 1 ELSE 0 END) AS prd_pagas_1,");
            query.append(" SUM(CASE WHEN prd.spd_codigo <> '").append(CodedValues.SPD_REJEITADAFOLHA).append("' THEN 1 ELSE 0 END) AS prd_pagas_total_1");
            query.append(" FROM tb_parcela_desconto prd");
            query.append(" INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo)");
            query.append(complementoAde != null ? complementoAde : "");
            query.append(" WHERE prd.spd_codigo IN ('").append(TextHelper.join(spdCodigoRecalculo, "','")).append("')");
            query.append(" GROUP BY ade.ade_codigo");
            LOG.trace(query.toString());
            linhas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhas);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_prd_pagas_2");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_tmp_prd_pagas_2 (ade_codigo VARCHAR(32) NOT NULL, prd_pagas_2 INT, prd_pagas_total_2 INT, PRIMARY KEY (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Outra tabela irá armazenar o total de pagas da tabela do período (normalmente
            // cada contrato terá apenas uma parcela nesta tabela)
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_prd_pagas_2 (ade_codigo, prd_pagas_2, prd_pagas_total_2) ");
            query.append(" SELECT ade.ade_codigo,");
            //DESENV-10666: foram incluídas as parcelas rejeitadas nesta query para recuperar o caso em que todas as parcelas existentes até o momento tenham sido rejeitadas
            //              (levando-se em conta também sistemas com períodos agrupados). neste caso deve-se recuperar o contrato para atualizar o ade_prd_pagas para 0.
            query.append(" SUM(CASE WHEN (prd.prd_data_desconto >= ade.ade_ano_mes_ini AND prd.spd_codigo <> '").append(CodedValues.SPD_REJEITADAFOLHA).append("') THEN 1 ELSE 0 END) AS prd_pagas_2,");
            query.append(" SUM(CASE WHEN prd.spd_codigo <> '").append(CodedValues.SPD_REJEITADAFOLHA).append("' THEN 1 ELSE 0 END) AS prd_pagas_total_2");
            query.append(" FROM tb_parcela_desconto_periodo prd");
            query.append(" INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo)");
            query.append(complementoAde != null ? complementoAde : "");
            query.append(" WHERE prd.spd_codigo IN ('").append(TextHelper.join(spdCodigoRecalculo, "','")).append("')");
            query.append(" GROUP BY ade.ade_codigo");
            LOG.trace(query.toString());
            linhas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhas);

            // Cria uma tabela para reunir o somatório das duas tabelas
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_prd_pagas");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_tmp_prd_pagas (ade_codigo VARCHAR(32) NOT NULL, prd_pagas INT, prd_pagas_total INT, PRIMARY KEY (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_prd_pagas (ade_codigo, prd_pagas, prd_pagas_total) ");
            query.append(" SELECT ade_codigo, SUM(prd_pagas) as prd_pagas, SUM(prd_pagas_total) as prd_pagas_total FROM (");
            query.append(" SELECT ade_codigo, prd_pagas_1 as prd_pagas, prd_pagas_total_1 as prd_pagas_total");
            query.append(" FROM tb_tmp_prd_pagas_1");
            query.append(" UNION ALL");
            query.append(" SELECT ade_codigo, prd_pagas_2 as prd_pagas, prd_pagas_total_2 as prd_pagas_total");
            query.append(" FROM tb_tmp_prd_pagas_2");
            query.append(") AS X GROUP BY ade_codigo");
            LOG.trace(query.toString());
            linhas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhas);

            // Atualiza a tabela de contratos com o somatório das pagas
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade");
            query.append(" INNER JOIN tb_tmp_prd_pagas pg ON (pg.ade_codigo = ade.ade_codigo)");
            query.append(" SET ");
            // Somente atualiza ade_pagas se tiver algum codigo de contrato como parametro
            if (complementoAde != null && !complementoAde.equals("")) {
                query.append("ade.ade_paga = 'S', ");
            }
            query.append("ade.ade_prd_pagas = pg.prd_pagas, ");
            query.append("ade.ade_prd_pagas_total = pg.prd_pagas_total, ");
            query.append("ade.sad_codigo = (CASE WHEN ade.sad_codigo = '");
            query.append(CodedValues.SAD_DEFERIDA).append("' THEN (CASE WHEN pg.prd_pagas = 0 THEN '").append(CodedValues.SAD_DEFERIDA);
            query.append("' ELSE '").append(CodedValues.SAD_EMANDAMENTO).append("' END) ELSE ade.sad_codigo END) ");
            LOG.trace(query.toString());
            linhas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhas);

            // Apaga as tabelas desnecessárias
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_prd_pagas_1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_prd_pagas_2");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_prd_pagas");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Em caso de retorno atrasado, quando um contrato recebe retorno de uma parcela que possui
     * data base (prd_data_desconto) anterior à data inicial do contrato (ade_ano_mes_ini), deve-se
     * reduzir o prazo do contrato, pois a atualização desta parcela não será contabilizada no
     * cálculo de pagas. A inclusão de uma ocorrência de alteração irá forçar o reenvio do
     * contrato para a folha, de modo a atualizar as informações do contrato.
     * @param stat
     * @param complementoAde
     * @param responsavel
     * @throws DataAccessException
     */
    private void reduzPrazoPeloRetornoAtrasado(String complementoAde, AcessoSistema responsavel) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        int linhas = 0;
        StringBuilder query = new StringBuilder();

        query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_prd_pagas_atrasada");
        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);

        query.setLength(0);
        query.append("CREATE TEMPORARY TABLE tb_tmp_prd_pagas_atrasada (ade_codigo VARCHAR(32) NOT NULL, prd_pagas INT(11), KEY `IX_ADE` (`ade_codigo`))");
        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);

        // Cria tabela para armazenar o total de pagas em retorno atrasado
        // para cada contrato que teve parcela realizada hoje
        query.setLength(0);
        query.append("INSERT INTO tb_tmp_prd_pagas_atrasada (ade_codigo, prd_pagas) ");
        query.append("SELECT ade.ade_codigo, COUNT(*) AS prd_pagas ");
        query.append("FROM tb_parcela_desconto prd ");
        query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo) ");
        query.append(complementoAde);
        query.append("WHERE prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
        query.append("AND prd.prd_data_desconto < ade.ade_ano_mes_ini ");
        query.append("AND prd.prd_data_realizado = curdate() ");
        query.append("AND ade.ade_prazo is not null ");
        query.append("GROUP BY ade.ade_codigo ");
        LOG.trace(query.toString());
        linhas = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + linhas);

        query.setLength(0);
        query.append("SET @rownum := 0;");
        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);

        // Insere ocorrência de alteração de contratos, notificando a
        // redução da quantidade de parcelas
        String msgPrazoAlt = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.prazo", (AcessoSistema) null);
        String msgPrazoAltDe = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.prazo.de", (AcessoSistema) null);
        String msgPrazoAltPara = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.prazo.para", (AcessoSistema) null);
        query.setLength(0);
        query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
        query.append("SELECT CONCAT('L', ");
        query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
        query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
        query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
        query.append("'").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("', ade.ade_codigo, '1', NOW(), pex.pex_periodo_pos, concat('<B>").append(msgPrazoAlt).append(": (', pg.prd_pagas,')</B><BR>").append(msgPrazoAltDe).append(" ', ade.ade_prazo, ' ").append(msgPrazoAltPara).append(" ', ade.ade_prazo - pg.prd_pagas) ");
        query.append("FROM tb_aut_desconto ade ");
        query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
        query.append("INNER JOIN tb_tmp_prd_pagas_atrasada pg ON (pg.ade_codigo = ade.ade_codigo) ");
        query.append("WHERE ade.ade_prazo > pg.prd_pagas ");
        LOG.trace(query.toString());
        linhas = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + linhas);

        // Atualiza a tabela de contratos reduzindo o prazo pela quantidade de
        // parcelas pagas em retorno atrasado com a data de hoje
        query.setLength(0);
        if (!PeriodoHelper.folhaMensal(responsavel)) {
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_tmp_prd_pagas_atrasada pg ON (pg.ade_codigo = ade.ade_codigo) ");
            query.append("INNER JOIN tb_tmp_calendario_quinzenal cal1 ON (cal1.org_codigo = cnv.org_codigo and cal1.periodo = ade.ade_ano_mes_fim) ");
            query.append("INNER JOIN tb_tmp_calendario_quinzenal cal2 ON (cal2.org_codigo = cnv.org_codigo and cal2.sequencia = cal1.sequencia - pg.prd_pagas) ");
            query.append("SET ");
            query.append("ade.ade_prazo = ade.ade_prazo - pg.prd_pagas, ");
            query.append("ade.ade_ano_mes_fim = cal2.periodo ");
            query.append("WHERE ade.ade_prazo > pg.prd_pagas ");
        } else {
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_tmp_prd_pagas_atrasada pg ON (pg.ade_codigo = ade.ade_codigo) ");
            query.append("SET ");
            query.append("ade.ade_prazo = ade.ade_prazo - pg.prd_pagas, ");
            query.append("ade.ade_ano_mes_fim = date_sub(ade.ade_ano_mes_fim, interval pg.prd_pagas month) ");
            query.append("WHERE ade.ade_prazo > pg.prd_pagas ");
        }
        LOG.trace(query.toString());
        linhas = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + linhas);

        // Os demais, onde pagas >= prazo, são atualizados o pagas para que a rotina de
        // conclusão efetue a alteração de status e inclusão de ocorrencia de conclusão.
        query.setLength(0);
        query.append("UPDATE tb_aut_desconto ade ");
        query.append("INNER JOIN tb_tmp_prd_pagas_atrasada pg ON (pg.ade_codigo = ade.ade_codigo) ");
        query.append("SET ");
        query.append("ade.ade_prd_pagas = ade.ade_prazo ");
        query.append("WHERE ade.ade_prazo <= pg.prd_pagas ");
        LOG.trace(query.toString());
        linhas = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + linhas);

        // Apaga a tabela temporária
        query.setLength(0);
        query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_prd_pagas_atrasada");
        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Insere ocorrência para o relançamento das consignações que foram criticadas
     * pela folha.
     * @param adeTipoEnvio : hash onde chave é adeCodigo e valor o tipo do envio I ou A
     * @param usuCodigo    : código do usuário responsável
     * @throws DAOException
     */
    @Override
    public void insereOcorrenciaRelancamento(HashMap<String, String> adeTipoEnvio, String usuCodigo) throws DAOException {
        Connection conn = null;
        PreparedStatement preStat1 = null;
        PreparedStatement preStat2 = null;
        try {
            if (adeTipoEnvio == null || adeTipoEnvio.size() == 0) {
                return;
            }

            conn = DBHelper.makeConnection();
            Date periodo = PeriodoHelper.getInstance().getPeriodoAtual(null, AcessoSistema.getAcessoUsuarioSistema());

            String adeCodigo = null;
            String tipoEnvio = null;

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, ADE_CODIGO, OCA_DATA, OCA_PERIODO, TOC_CODIGO, USU_CODIGO, OCA_OBS) ");
            query.append("VALUES (?, ?, NOW(), ?, '").append(CodedValues.TOC_RELANCAMENTO).append("', ?, '").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.relancamento.automatico", (AcessoSistema) null)).append("')");
            preStat1 = conn.prepareStatement(query.toString());

            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" SET ");
            query.append(Columns.ADE_ANO_MES_INI).append(" = DATE_ADD(").append(Columns.ADE_ANO_MES_INI).append(", INTERVAL 1 MONTH), ");
            query.append(Columns.ADE_ANO_MES_FIM).append(" = DATE_ADD(").append(Columns.ADE_ANO_MES_FIM).append(", INTERVAL 1 MONTH) ");
            query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = ?");
            query.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" = '").append(CodedValues.SAD_DEFERIDA).append("'");
            preStat2 = conn.prepareStatement(query.toString());

            Iterator<String> it = adeTipoEnvio.keySet().iterator();
            while (it.hasNext()) {
                adeCodigo = it.next();
                tipoEnvio = adeTipoEnvio.get(adeCodigo);

                preStat1.setString(1, DBHelper.getNextId());
                preStat1.setString(2, adeCodigo);
                preStat1.setDate(3, periodo);
                preStat1.setString(4, usuCodigo);
                preStat1.executeUpdate();

                if (tipoEnvio.equals("I")) {
                    preStat2.setString(1, adeCodigo);
                    preStat2.executeUpdate();
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat1);
            DBHelper.closeStatement(preStat2);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Retira do estoque as ADE, ou por terem sido pagas no retorno atual ou por outro motivo,
     * neste caso é passado quais ADE serão atualizadas.
     * @param adeCodigos     Lista de ADE a serem atualizadas, caso se queira atualizar um grupo específico de ADE
     * @param tipoEntidade   Tipo da entidade que está fazendo a atualização: CSE, EST ou ORG.
     * @param codigoEntidade Código da entidade que está fazendo a atualização
     * @param responsavel    Usuário que está fazendo a atualização
     * @throws DAOException
     */
    @Override
    public void retiraDoEstoque(List<String> adeCodigos, String tipoEntidade, String codigoEntidade, String responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            // Criando tabela temporaria com os ade_codigos informados
            StringBuilder query = new StringBuilder();
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_retira_estoque");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_tmp_retira_estoque (ade_codigo VARCHAR(32) NOT NULL, pex_periodo_pos DATE NOT NULL, PRIMARY KEY (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retira_estoque (ade_codigo, pex_periodo_pos) ");
            query.append("SELECT ade_codigo, pex_periodo_pos ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
            if (tipoEntidade != null && codigoEntidade != null && tipoEntidade.equalsIgnoreCase("EST")) {
                query.append("INNER JOIN tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            }

            query.append("WHERE 1 = 1 ");
            if (adeCodigos != null && adeCodigos.size() > 0) {
                query.append("AND ade.ade_codigo IN (:adeCodigos) ");
                queryParams.addValue("adeCodigos", adeCodigos);
            } else {
                query.append("AND ade.ade_paga = 'S' AND ade.sad_codigo IN ('").append(CodedValues.SAD_ESTOQUE).append("', '").append(CodedValues.SAD_ESTOQUE_MENSAL).append("') ");
            }

            if (tipoEntidade != null && codigoEntidade != null) {
                if (tipoEntidade.equalsIgnoreCase("ORG")) {
                    query.append("AND cnv.org_codigo = :codigoEntidade ");
                    queryParams.addValue("codigoEntidade", codigoEntidade);
                } else if (tipoEntidade.equalsIgnoreCase("EST")) {
                    query.append("AND org.est_codigo = :codigoEntidade ");
                    queryParams.addValue("codigoEntidade", codigoEntidade);
                }
            }

            // Inclui cláusula para não retirar do estoque contratos que tenham relacionamento
            // de compulsório ainda ativo
            query.append("AND NOT EXISTS ( ");
            query.append("select 1 from tb_relacionamento_autorizacao rad ");
            query.append("inner join tb_aut_desconto adeOrigem on (rad.ade_codigo_origem = adeOrigem.ade_codigo) ");
            query.append("where rad.ade_codigo_destino = ade.ade_codigo ");
            query.append("and rad.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_COMPULSORIOS).append("' ");
            query.append("and adeOrigem.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Insere ocorrência de alteração de status
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
            query.append("SELECT CONCAT('Y', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'1', ade.ade_codigo, '1', NOW(), pex_periodo_pos, concat('").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.de", (AcessoSistema) null)).append(" ', sad_codigo,' ").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.para", (AcessoSistema) null)).append(" ").append(CodedValues.SAD_EMANDAMENTO).append("') ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_tmp_retira_estoque tmp on (ade.ade_codigo = tmp.ade_codigo) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_tmp_retira_estoque tmp on (ade.ade_codigo = tmp.ade_codigo) ");
            query.append("SET ade.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Coloca em estoque as ADE, ou por não terem sido pagas no retorno atual
     * ou por outro motivo, neste caso é passado quais ADE serão atualizadas.
     * @param adeCodigos     Lista de ADE a serem atualizadas, caso se queira atualizar um grupo específico de ADE
     * @param dataLimite     Data limite para considerar ADE já exportadas para a folha.
     * @param tipoEntidade   Tipo da entidade que está fazendo a atualização: CSE, EST ou ORG.
     * @param codigoEntidade Código da entidade que está fazendo a atualização
     * @param responsavel    Usuário que está fazendo a atualização
     * @throws DAOException
     */
    @Override
    public void colocaEmEstoque(List<String> adeCodigos, String periodo, String tipoEntidade, String codigoEntidade, String responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder tabelas = new StringBuilder();
            tabelas.append("tb_aut_desconto ade");
            tabelas.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            tabelas.append(" INNER JOIN tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            tabelas.append(" INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");

            if (tipoEntidade != null && codigoEntidade != null && !codigoEntidade.equals("")) {
                tabelas.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo");
                if (tipoEntidade.equalsIgnoreCase("ORG")) {
                    tabelas.append(" and org.org_codigo = :codigoEntidade) ");
                    queryParams.addValue("codigoEntidade", codigoEntidade);
                } else if (tipoEntidade.equalsIgnoreCase("EST")) {
                    tabelas.append(" and org.est_codigo = :codigoEntidade) ");
                    queryParams.addValue("codigoEntidade", codigoEntidade);
                } else {
                    tabelas.append(") ");
                }
            }

            StringBuilder where = new StringBuilder("WHERE 1 = 1 ");
            if (adeCodigos != null && adeCodigos.size() > 0) {
                where.append("AND ade.ade_codigo IN (:adeCodigos) ");
                queryParams.addValue("adeCodigos", adeCodigos);
            } else {
                where.append("AND ade_paga = 'N' AND sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ALTERACAO_EM_ESTOQUE, "','")).append("') ");
                if (periodo != null && !periodo.equals("")) {
                    tabelas.append("inner join tb_parcela_desconto_periodo prd on (ade.ade_codigo = prd.ade_codigo) ");
                    where.append("AND prd.spd_codigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' AND prd.prd_data_desconto = :periodo ");
                    queryParams.addValue("periodo", periodo);
                }
            }

            StringBuilder query = new StringBuilder();
            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
            query.append("SELECT CONCAT('X', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'1', ade.ade_codigo, '1', NOW(), pex.pex_periodo_pos, CONCAT('").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.de", (AcessoSistema) null)).append(" ', ade.sad_codigo, ' ").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.para", (AcessoSistema) null)).append(" ").append(CodedValues.SAD_ESTOQUE_MENSAL).append("') ");
            query.append("FROM ").append(tabelas).append(where);
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("UPDATE ").append(tabelas);
            query.append("SET sad_codigo = '").append(CodedValues.SAD_ESTOQUE_MENSAL).append("' ").append(where);
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Conclui contratos Aguardando Liquidação e atualiza a incidência de margem do contrato novo.
     * TODO: Deveria já deferir os novos contratos caso adePodeConfirmar = S.
     */
    @Override
    public void concluiAdesAguardLiquid(AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        LOG.debug("*************************************************************************************");
        LOG.debug("INÍCIO - CONCLUSÃO DE CONTRATOS AGUARDANDO FRUTO DE RENEGOCIAÇÃO");

        try {
            final StringBuilder query = new StringBuilder();

            /*
             *  Recupera o parametro de sistema que informa se a folha possui carencia
             *  para a conclusão dos contratos.
             */
            final int carenciaFolha = Integer.parseInt((String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, "0", responsavel));
            queryParams.addValue("carenciaFolha", carenciaFolha);

            query.setLength(0);
            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Insere ocorrencia de conclusão
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_periodo, oca_obs) ");
            query.append("SELECT CONCAT('V', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'15', ade_codigo, '1', NOW(), pex.pex_periodo_pos, '").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel)).append("' ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
            query.append("WHERE COALESCE(ade_prazo, 999999999) <= COALESCE(ade_prd_pagas, 0) ");
            query.append("AND sad_codigo = '11' ");
            query.append("AND ade_vlr_sdo_mov IS NULL ");
            query.append("AND ade_vlr_sdo_ret IS NULL ");
            query.append("AND ade_int_folha = 1 ");
            query.append("AND EXISTS (SELECT 1 FROM tb_periodo_exportacao pex WHERE pex.org_codigo = cnv.org_codigo AND pex.pex_periodo >= DATE_ADD(ade_ano_mes_fim, INTERVAL COALESCE(ade_carencia_final, 0) + :carenciaFolha MONTH)) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Conclui os contratos
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("SET sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
            query.append("WHERE COALESCE(ade_prazo, 999999999) <= COALESCE(ade_prd_pagas, 0) ");
            query.append("AND sad_codigo = '").append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("' ");
            query.append("AND ade_vlr_sdo_mov IS NULL ");
            query.append("AND ade_vlr_sdo_ret IS NULL ");
            query.append("AND ade_int_folha = 1 ");
            query.append("AND EXISTS (SELECT 1 FROM tb_periodo_exportacao pex WHERE pex.org_codigo = cnv.org_codigo AND pex.pex_periodo >= DATE_ADD(ade_ano_mes_fim, INTERVAL COALESCE(ade_carencia_final, 0) + :carenciaFolha MONTH)) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Acerta o ADE_INC_MARGEM dos novos contratos de compra onde o último contrato foi concluído
            // porém o mesmo não pode ser confirmado (ade_pode_confirmar = 'S') pois o usuário que incluiu
            // o contrato não possuia permissão de confirmar consignação
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_acerto_incide_margem_compra");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_acerto_incide_margem_compra (ade_codigo VARCHAR(32) NOT NULL, inc_margem SMALLINT, KEY `IX_ADE` (`ade_codigo`)) ");
            query.append("select adeDestino.ade_codigo, coalesce(pse.pse_vlr, '1') as inc_margem ");
            query.append("from tb_aut_desconto adeDestino ");
            query.append("inner join tb_verba_convenio vco on (adeDestino.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("left outer join tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo and pse.tps_codigo = '3') ");
            query.append("where adeDestino.sad_codigo = '1' ");     // CONTRATOS NA SITUAÇÃO DE AGUARD. CONFIFRMAÇÃO
            query.append("and adeDestino.ade_inc_margem = '0' ");   // QUE NÃO INCIDEM NA MARGEM
            query.append("and coalesce(pse.pse_vlr, '1') <> '0' "); // MAS DEVERIAM INCIDIR
            query.append("and not exists ( ");                      // ONDE NÃO EXISTE RELACIONAMENTO AGUARD. LIQUIDACAO
            query.append("  select 1 from tb_relacionamento_autorizacao rad ");
            query.append("  inner join tb_aut_desconto adeOrigem on (rad.ade_codigo_origem = adeOrigem.ade_codigo) ");
            query.append("  where rad.ade_codigo_destino = adeDestino.ade_codigo ");
            query.append("  and rad.tnt_codigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("', '").append(CodedValues.TNT_CONTROLE_COMPRA).append("') ");
            query.append("  and adeOrigem.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_LIQ, "','")).append("') ");
            query.append(") ");
            query.append("and exists ( ");                          // PORÉM TODOS JÁ LIQUIDADOS OU CONCLUÍDOS
            query.append("  select 1 from tb_relacionamento_autorizacao rad ");
            query.append("  inner join tb_aut_desconto adeOrigem on (rad.ade_codigo_origem = adeOrigem.ade_codigo) ");
            query.append("  where rad.ade_codigo_destino = adeDestino.ade_codigo ");
            query.append("  and rad.tnt_codigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("', '").append(CodedValues.TNT_CONTROLE_COMPRA).append("') ");
            query.append("  and adeOrigem.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_LIQUIDADO_CONCLUIDO, "','")).append("') ");
            query.append(") ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tmp_acerto_incide_margem_compra tmp on (ade.ade_codigo = tmp.ade_codigo) ");
            query.append("SET ade.ade_inc_margem = tmp.inc_margem ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }

        LOG.debug("FIM - CONCLUSÃO DE CONTRATOS AGUARDANDO FRUTO DE RENEGOCIAÇÃO");
        LOG.debug("*************************************************************************************");
    }

    /**
     * Conclui contratos que não integram folha, que tenham prazo e que a data final tenha
     * sido alcançada. Não serão concluídos pela rotina tradicional pois não integram folha
     * portanto não tem parcelas geradas, nem pagas.
     * @param orgCodigos  : os códigos dos órgãos, nulo para todos
     * @param estCodigos  : os códigos dos estabelecimentos, nulo para todos
     * @param responsavel : responsável pela operação
     * @throws DAOException
     */
    @Override
    public void concluiAdesNaoIntegramFolha(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException {
        if (ParamSist.paramEquals(CodedValues.TPC_CONCLUI_ADE_NAO_INTEGRA_FOLHA, CodedValues.TPC_SIM, responsavel)) {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            int rows = 0;

            LOG.debug("*************************************************************************************");
            LOG.debug("INÍCIO - CONCLUSÃO DE CONTRATOS QUE NÃO INTEGRAM FOLHA");

            try {
                final StringBuilder query = new StringBuilder();

                // Recupera o parametro de sistema que informa se a folha possui carencia
                // para a conclusão dos contratos.
                final int carenciaFolha = Integer.parseInt((String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, "0", responsavel));
                queryParams.addValue("carenciaFolha", carenciaFolha);

                // Cria tabela com os contratos que não integram folha e que a data final
                // não é nula e já passou, ou seja, contratos que já alcançaram a data final,
                // e que não serão concluídos pela rotina tradicional pois não integram folha.
                query.setLength(0);
                query.append("DROP TEMPORARY TABLE IF EXISTS tmp_conclusao_ade_nao_integra");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CREATE TEMPORARY TABLE tmp_conclusao_ade_nao_integra (ade_codigo VARCHAR(32) NOT NULL, pex_periodo_pos DATE NOT NULL, PRIMARY KEY (ade_codigo))");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("INSERT INTO tmp_conclusao_ade_nao_integra (ade_codigo, pex_periodo_pos) ");
                query.append("select ade.ade_codigo, pex_periodo_pos ");
                query.append("from tb_aut_desconto ade ");
                query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
                query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
                query.append("inner join tb_periodo_exportacao pex on (org.org_codigo = pex.org_codigo) ");
                query.append("where ade.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' "); // CONTRATOS NA SITUAÇÃO DE DEFERIDO,
                query.append("and ade.ade_int_folha = 0 ");                                             // QUE NÃO INTEGRAM FOLHA,
                query.append("and ade.ade_prazo is not null ");                                         // QUE TEM PRAZO DETERMINADO E QUE A DATA FINAL CHEGOU OU PASSOU
                query.append("and ade_vlr_sdo_ret is null ");                                           // QUE NÃO TEM CONTROLE DE SALDO
                query.append("and EXISTS (SELECT 1 FROM tb_periodo_exportacao pex WHERE pex.org_codigo = cnv.org_codigo AND pex.pex_periodo >= DATE_ADD(ade.ade_ano_mes_fim, INTERVAL coalesce(ade.ade_carencia_final, 0) + :carenciaFolha MONTH)) ");

                if (orgCodigos != null && orgCodigos.size() > 0) {
                    query.append("and (org.org_codigo in (:orgCodigos)) ");
                    queryParams.addValue("orgCodigos", orgCodigos);
                }
                if (estCodigos != null && estCodigos.size() > 0) {
                    query.append("and (org.est_codigo in (:estCodigos)) ");
                    queryParams.addValue("estCodigos", estCodigos);
                }
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

                query.setLength(0);
                query.append("SET @rownum := 0;");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Insere ocorrencia de conclusão sem desconto
                query.setLength(0);
                query.append("INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_periodo, oca_obs) ");
                query.append("SELECT CONCAT('J', ");
                query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
                query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
                query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
                query.append("'").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("', ade.ade_codigo, ");
                query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', NOW(), pex_periodo_pos, ");
                query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel)).append("' ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tmp_conclusao_ade_nao_integra tmp on (ade.ade_codigo = tmp.ade_codigo) ");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

                // Conclui os contratos, alterando o status para CONCLUIDO
                query.setLength(0);
                query.append("UPDATE tb_aut_desconto ");
                query.append("SET sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
                query.append("WHERE ade_codigo IN (SELECT ade_codigo FROM tmp_conclusao_ade_nao_integra)");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

                // Insere ocorrencia de conclusão sem desconto para contratos relacionados
                // pela natureza de cartão, ou seja os lançamentos de cartão serão concluídos
                // quando a reserva também for
                query.setLength(0);
                query.append("INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_periodo, oca_obs) ");
                query.append("SELECT CONCAT('J', ");
                query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
                query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
                query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
                query.append("'").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("', ade.ade_codigo, ");
                query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', NOW(), pex_periodo_pos, ");
                query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel)).append("' ");
                query.append("FROM tb_relacionamento_autorizacao rad ");
                query.append("INNER JOIN tb_aut_desconto ade on (rad.ade_codigo_destino = ade.ade_codigo) ");
                query.append("INNER JOIN tmp_conclusao_ade_nao_integra tmp on (rad.ade_codigo_origem = tmp.ade_codigo) ");
                query.append("WHERE rad.tnt_codigo = '").append(CodedValues.TNT_CARTAO).append("' ");
                query.append("AND ade.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

                // Conclui os contratos, alterando o status para CONCLUIDO
                query.setLength(0);
                query.append("UPDATE tb_relacionamento_autorizacao rad ");
                query.append("INNER JOIN tb_aut_desconto ade on (rad.ade_codigo_destino = ade.ade_codigo) ");
                query.append("INNER JOIN tmp_conclusao_ade_nao_integra tmp on (rad.ade_codigo_origem = tmp.ade_codigo) ");
                query.append("SET ade.sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
                query.append("WHERE rad.tnt_codigo = '").append(CodedValues.TNT_CARTAO).append("' ");
                query.append("AND ade.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
            }

            LOG.debug("FIM - CONCLUSÃO DE CONTRATOS QUE NÃO INTEGRAM FOLHA");
            LOG.debug("*************************************************************************************");
        }
    }

    /**
     * Conclui contratos que de servidores excluídos que a data final foi alcançada, pois como as parcelas
     * não são pagas, estes ficarão sem conclusão em serviços/sistemas que preservam parcelas.
     * @param orgCodigos  : os códigos dos órgãos, nulo para todos
     * @param estCodigos  : os códigos dos estabelecimentos, nulo para todos
     * @param responsavel : responsável pela operação
     * @throws DAOException
     */
    @Override
    public void concluiAdesServidorExcluido(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        int rows = 0;

        LOG.debug("*************************************************************************************");
        LOG.debug("INÍCIO - CONCLUSÃO DE CONTRATOS DE SERVIDORES EXCLUIDOS");

        try {
            // Consultar se há algum serviço com o parâmetro de serviço TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO
            // diferente da opção Default. Caso não exista, sair da rotina.
            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(*) FROM tb_param_svc_consignataria ");
            query.append("WHERE TPS_CODIGO = '").append(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO).append("' ");
            query.append("AND COALESCE(NULLIF(PSC_VLR, ''), '").append(CodedValues.PSC_BOOLEANO_NAO).append("') = '").append(CodedValues.PSC_BOOLEANO_SIM).append("' ");
            LOG.trace(query.toString());
            final Integer count = jdbc.queryForObject(query.toString(), queryParams, Integer.class);
            if (count == null || count == 0) {
                return;
            }

            // Recupera o parametro de sistema que informa se a folha possui carencia
            // para a conclusão dos contratos.
            final int carenciaFolha = Integer.parseInt((String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, "0", responsavel));
            queryParams.addValue("carenciaFolha", carenciaFolha);

            // Cria tabela com os contratos que a data final não é nula e já passou, ou seja,
            // contratos que já alcançaram a data final; que são de servidores excluídos, de serviços
            // que preservam parcelas, não sendo concluídos pela rotina tradicional.
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_conclusao_ade_ser_excluido");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_conclusao_ade_ser_excluido (ade_codigo VARCHAR(32) NOT NULL, pex_periodo_pos DATE NOT NULL, PRIMARY KEY (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_conclusao_ade_ser_excluido (ade_codigo, pex_periodo_pos) ");
            query.append("select ade.ade_codigo, max(pex.pex_periodo_pos) ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (org.org_codigo = pex.org_codigo) ");
            query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");

            query.append("inner join tb_param_svc_consignataria psc on (psc.svc_codigo = cnv.svc_codigo");
            query.append(" and psc.csa_codigo = cnv.csa_codigo");
            query.append(" and psc.tps_codigo = '").append(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO).append("'");
            query.append(" and coalesce(psc.psc_ativo, 1) = 1");
            query.append(") ");

            // Parâmetro TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO habilitado
            query.append("where coalesce(nullif(psc.psc_vlr, ''), '").append(CodedValues.PSC_BOOLEANO_NAO).append("') = '").append(CodedValues.PSC_BOOLEANO_SIM).append("' ");
            // Status que pode ser concluído
            query.append("and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO, "','")).append("') ");
            // Consignação que integra folha
            query.append("and ade.ade_int_folha = 1 ");
            // Não tem controle de saldo, pois tem rotina específica
            query.append("and ade.ade_vlr_sdo_ret is null ");
            // Data fim não nula
            query.append("and ade.ade_ano_mes_fim is not null ");
            // Data fim já passada
            query.append("and pex.pex_periodo >= date_add(ade.ade_ano_mes_fim, interval coalesce(ade.ade_carencia_final, 0) + :carenciaFolha month) ");
            // Servidor excluído ou falecido
            query.append("and rse.srs_codigo in ('").append(TextHelper.join(CodedValues.SRS_INATIVOS, "','")).append("') ");

            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append(" and (org.org_codigo in (:orgCodigos)) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append(" and (org.est_codigo in (:estCodigos)) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            query.append("group by ade.ade_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Insere ocorrencia de conclusão sem desconto
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_periodo, oca_obs) ");
            query.append("SELECT CONCAT('I', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("', ade.ade_codigo, ");
            query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', NOW(), pex_periodo_pos, ");
            query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido.ser.excluido", responsavel)).append("' ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tmp_conclusao_ade_ser_excluido tmp on (ade.ade_codigo = tmp.ade_codigo) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Conclui os contratos, alterando o status para CONCLUIDO
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ");
            query.append("SET sad_codigo = '").append(CodedValues.SAD_ENCERRADO).append("' ");
            query.append("WHERE ade_codigo IN (SELECT ade_codigo FROM tmp_conclusao_ade_ser_excluido)");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }

        LOG.debug("FIM - CONCLUSÃO DE CONTRATOS DE SERVIDORES EXCLUIDOS");
        LOG.debug("*************************************************************************************");
    }

    /**
     * Conclui contratos de lançamento de cartão que não foram pagos e são desconsiderados
     * do cálculo para novos lançamentos no período
     * @param orgCodigos  : os códigos dos órgãos, nulo para todos
     * @param estCodigos  : os códigos dos estabelecimentos, nulo para todos
     * @param responsavel : responsável pela operação
     * @throws DAOException
     */
    @Override
    public void concluiAdesLancamentoNaoPagos(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        int rows = 0;

        LOG.debug("*************************************************************************************");
        LOG.debug("INÍCIO - CONCLUSÃO DE LANÇAMENTOS DE CARTÃO NÃO PAGOS");

        try {
            // Consultar se há algum serviço com o parâmetro de serviço TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS
            // diferente da opção Default. Caso não exista, sair da rotina.
            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(*) FROM tb_param_svc_consignante ");
            query.append("WHERE TPS_CODIGO = '").append(CodedValues.TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS).append("' ");
            query.append("AND COALESCE(NULLIF(PSE_VLR, ''), '").append(CodedValues.PSE_BOOLEANO_NAO).append("') = '").append(CodedValues.PSE_BOOLEANO_SIM).append("' ");
            LOG.trace(query.toString());
            final Integer count = jdbc.queryForObject(query.toString(), queryParams, Integer.class);
            if (count == null || count == 0) {
                return;
            }

            // Recupera o parametro de sistema que informa se a folha possui carencia
            // para a conclusão dos contratos.
            final int carenciaFolha = Integer.parseInt((String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, "0", responsavel));
            queryParams.addValue("carenciaFolha", carenciaFolha);

            // Cria tabela com os contratos que a data final não é nula e já passou, ou seja,
            // contratos que já alcançaram a data final; que são de serviços de lançamento em
            // provisionamento de margem que são desconsiderados do cálculo para inclusão de
            // novos lançamentos
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_conclusao_ade_lancamento_nao_pago");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_conclusao_ade_lancamento_nao_pago (ade_codigo VARCHAR(32) NOT NULL, pex_periodo_pos DATE NOT NULL, PRIMARY KEY (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_conclusao_ade_lancamento_nao_pago (ade_codigo, pex_periodo_pos) ");
            query.append("select ade.ade_codigo, max(pex.pex_periodo_pos) ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (org.org_codigo = pex.org_codigo) ");

            query.append("inner join tb_param_svc_consignante pse on (pse.svc_codigo = cnv.svc_codigo");
            query.append(" and pse.tps_codigo = '").append(CodedValues.TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS).append("'");
            query.append(") ");

            // Parâmetro TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS habilitado
            query.append("where coalesce(nullif(pse.pse_vlr, ''), '").append(CodedValues.PSE_BOOLEANO_NAO).append("') = '").append(CodedValues.PSE_BOOLEANO_SIM).append("' ");
            // Status que pode ser concluído
            query.append("and ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_CONCLUSAO, "','")).append("') ");
            // Consignação que integra folha
            query.append("and ade.ade_int_folha = 1 ");
            // Não tem controle de saldo, pois tem rotina específica
            query.append("and ade.ade_vlr_sdo_ret is null ");
            // Data inicio menor ou igual ao periodo atual
            query.append("and ade.ade_ano_mes_ini <= pex.pex_periodo ");
            // Data fim não nula
            query.append("and ade.ade_ano_mes_fim is not null ");
            // Data fim já passada
            query.append("and pex.pex_periodo >= date_add(ade.ade_ano_mes_fim, interval coalesce(ade.ade_carencia_final, 0) + :carenciaFolha month) ");
            // De serviço destino de relacionamento de cartão (provisionamento de margem)
            query.append("and exists (");
            query.append(" select 1 from tb_relacionamento_servico rsv ");
            query.append(" where rsv.svc_codigo_destino = cnv.svc_codigo ");
            query.append(" and rsv.tnt_codigo = '").append(CodedValues.TNT_CARTAO).append("' ");
            query.append(") ");

            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append(" and (org.org_codigo in (:orgCodigos)) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append(" and (org.est_codigo in (:estCodigos)) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            query.append("group by ade.ade_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Insere ocorrencia de conclusão sem desconto
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_periodo, oca_obs) ");
            query.append("SELECT CONCAT('H', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("', ade.ade_codigo, ");
            query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', NOW(), pex_periodo_pos, ");
            query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido.lancamento.nao.pago", responsavel)).append("' ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tmp_conclusao_ade_lancamento_nao_pago tmp on (ade.ade_codigo = tmp.ade_codigo) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Conclui os contratos, alterando o status para CONCLUIDO
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ");
            query.append("SET sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
            query.append("WHERE ade_codigo IN (SELECT ade_codigo FROM tmp_conclusao_ade_lancamento_nao_pago)");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }

        LOG.debug("FIM - CONCLUSÃO DE LANÇAMENTOS DE CARTÃO NÃO PAGOS");
        LOG.debug("*************************************************************************************");
    }

    @Override
    public List<String> atualizarAdeValorAlteracaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            // Consultar se há algum serviço com o parâmetro de serviço TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM
            // diferente da opção Default. Caso não exista, sair da rotina.
            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(*) FROM tb_param_svc_consignante ");
            query.append("WHERE TPS_CODIGO = '").append(CodedValues.TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM).append("' ");
            query.append("AND PSE_VLR <> '").append(CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NUNCA).append("' ");
            LOG.trace(query.toString());
            final Integer count = jdbc.queryForObject(query.toString(), queryParams, Integer.class);
            if (count == null || count == 0) {
                return null;
            }

            // Caso existam serviços com atualização do valor, criar tabela temporária contendo o código
            // do registro servidor, da margem que serviço incide, a quantidade e o somatório do valor de
            // consignações ativas que o servidor possui em serviços que incidem nesta mesma margem.
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_atualizacao_ade_vlr_pela_margem");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_atualizacao_ade_vlr_pela_margem (");
            query.append("RSE_CODIGO varchar(32) NOT NULL, ");
            query.append("MAR_CODIGO smallint(6) NOT NULL, ");
            query.append("SVC_CODIGO varchar(32) NOT NULL, ");
            query.append("PSE_VLR char(1) NOT NULL, ");
            query.append("QTD_TOTAL_ADE smallint(6) NOT NULL DEFAULT 0, ");
            query.append("VLR_TOTAL_ADE decimal(13,2) NOT NULL DEFAULT 0.0, ");
            query.append("MRS_MARGEM_REST decimal(13,2) NOT NULL DEFAULT 0.0, ");
            query.append("PRIMARY KEY (RSE_CODIGO, MAR_CODIGO, SVC_CODIGO))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_atualizacao_ade_vlr_pela_margem (RSE_CODIGO, MAR_CODIGO, SVC_CODIGO, PSE_VLR, QTD_TOTAL_ADE, VLR_TOTAL_ADE, MRS_MARGEM_REST) ");
            query.append("SELECT ade.RSE_CODIGO, ade.ADE_INC_MARGEM, pse.SVC_CODIGO, pse.PSE_VLR, COUNT(DISTINCT ade.ADE_CODIGO), SUM(ade.ADE_VLR), ");

            query.append("CASE ");
            query.append(" WHEN ade.ADE_INC_MARGEM = 1 THEN rse.RSE_MARGEM_REST ");
            query.append(" WHEN ade.ADE_INC_MARGEM = 2 THEN rse.RSE_MARGEM_REST_2 ");
            query.append(" WHEN ade.ADE_INC_MARGEM = 3 THEN rse.RSE_MARGEM_REST_3 ");
            query.append(" WHEN ade.ADE_INC_MARGEM <> 0 THEN COALESCE((");
            query.append("    SELECT mrs.MRS_MARGEM_REST ");
            query.append("    FROM tb_margem_registro_servidor mrs ");
            query.append("    WHERE mrs.RSE_CODIGO = rse.RSE_CODIGO ");
            query.append("      AND mrs.MAR_CODIGO = ade.ADE_INC_MARGEM), 0) ");
            query.append(" ELSE 0 END AS MRS_MARGEM_REST ");

            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.VCO_CODIGO = ade.VCO_CODIGO) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.CNV_CODIGO = vco.CNV_CODIGO) ");
            query.append("INNER JOIN tb_param_svc_consignante pse ON (cnv.SVC_CODIGO = pse.SVC_CODIGO) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (ade.RSE_CODIGO = rse.RSE_CODIGO) ");
            query.append("WHERE pse.TPS_CODIGO = '").append(CodedValues.TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM).append("' ");
            query.append("AND pse.PSE_VLR <> '").append(CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NUNCA).append("' ");
            query.append("AND ade.SAD_CODIGO IN ('").append(CodedValues.SAD_DEFERIDA).append("', '").append(CodedValues.SAD_EMANDAMENTO).append("') ");
            query.append("AND rse.SRS_CODIGO NOT IN ('").append(CodedValues.SRS_EXCLUIDO).append("', '").append(CodedValues.SRS_FALECIDO).append("') ");

            if (entCodigos != null && entCodigos.size() > 0) {
                if (tipoEntidade.equalsIgnoreCase("EST")) {
                    query.append(" AND rse.ORG_CODIGO IN (SELECT org.ORG_CODIGO FROM tb_orgao org WHERE org.EST_CODIGO IN (:entCodigos)) ");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                    query.append(" AND rse.ORG_CODIGO IN (:entCodigos) ");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if (tipoEntidade.equalsIgnoreCase("RSE")) {
                    query.append(" AND rse.RSE_CODIGO IN (:entCodigos) ");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            }

            query.append("GROUP BY ade.RSE_CODIGO, ade.ADE_INC_MARGEM, pse.SVC_CODIGO ");

            // Só incluir na tabela temporária registros em que o servidor tenha margem positiva/negativa
            // de acordo com a opção do parâmetro TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM
            query.append("HAVING (CASE ");
            query.append(" WHEN PSE_VLR = '").append(CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_POSITIVA).append("' THEN MRS_MARGEM_REST > 0 ");
            query.append(" WHEN PSE_VLR = '").append(CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NEGATIVA).append("' THEN MRS_MARGEM_REST < 0 ");
            query.append(" WHEN PSE_VLR = '").append(CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_SEMPRE).append("' THEN MRS_MARGEM_REST <> 0 ");
            query.append("END)");

            LOG.trace(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Consolida os dados por registro servidor e margem
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_atualizacao_ade_vlr_pela_margem_consolidado");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_atualizacao_ade_vlr_pela_margem_consolidado (");
            query.append("RSE_CODIGO varchar(32) NOT NULL, ");
            query.append("MAR_CODIGO smallint(6) NOT NULL, ");
            query.append("QTD_TOTAL_ADE smallint(6) NOT NULL DEFAULT 0, ");
            query.append("VLR_TOTAL_ADE decimal(13,2) NOT NULL DEFAULT 0.0, ");
            query.append("PRIMARY KEY (RSE_CODIGO, MAR_CODIGO))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_atualizacao_ade_vlr_pela_margem_consolidado (RSE_CODIGO, MAR_CODIGO, QTD_TOTAL_ADE, VLR_TOTAL_ADE) ");
            query.append("SELECT RSE_CODIGO, MAR_CODIGO, SUM(QTD_TOTAL_ADE), SUM(VLR_TOTAL_ADE) ");
            query.append("FROM tmp_atualizacao_ade_vlr_pela_margem ");
            query.append("GROUP BY RSE_CODIGO, MAR_CODIGO");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Obtém o valor novo da consignação de acordo com o proporcional do seu valor sobre o total
            // aplicado ao valor restante da margem, conforme cálculo: ADE_VLR = ADE_VLR + ((ADE_VLR / SOMATORIO) * RSE_MARGEM_REST_X)
            query.setLength(0);
            query.append("SELECT ade.RSE_CODIGO, ade.ADE_CODIGO, ade.ADE_VLR, TRUNCATE(ade.ADE_VLR + ((ade.ADE_VLR / tmp2.VLR_TOTAL_ADE) * tmp.MRS_MARGEM_REST), 2) AS NOVO_ADE_VLR, ade.ADE_TIPO_VLR ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.VCO_CODIGO = ade.VCO_CODIGO) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.CNV_CODIGO = vco.CNV_CODIGO) ");
            query.append("INNER JOIN tmp_atualizacao_ade_vlr_pela_margem tmp ON (cnv.SVC_CODIGO = tmp.SVC_CODIGO AND ade.RSE_CODIGO = tmp.RSE_CODIGO AND ade.ADE_INC_MARGEM = tmp.MAR_CODIGO) ");
            query.append("INNER JOIN tmp_atualizacao_ade_vlr_pela_margem_consolidado tmp2 ON (ade.RSE_CODIGO = tmp2.RSE_CODIGO AND ade.ADE_INC_MARGEM = tmp2.MAR_CODIGO) ");
            query.append("WHERE ade.SAD_CODIGO IN ('").append(CodedValues.SAD_DEFERIDA).append("', '").append(CodedValues.SAD_EMANDAMENTO).append("') ");
            query.append("HAVING ADE_VLR <> NOVO_ADE_VLR");
            LOG.trace(query.toString());
            final List<Map<String, Object>> rset = jdbc.queryForList(query.toString(), queryParams);

            // Armazena os códigos distintos de registro servidor
            Set<String> rseCodigos = new HashSet<>();

            if (rset != null && !rset.isEmpty()) {
                String tocCodigo = CodedValues.TOC_ALTERACAO_CONTRATO;
                java.util.Date ocaData = DateHelper.getSystemDatetime();
                java.util.Date ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);

                for (Map<String, Object> row : rset) {
                    String rseCodigo = (String) row.get("RSE_CODIGO");
                    String adeCodigo = (String) row.get("ADE_CODIGO");
                    String adeTipoVlr = (String) row.get("ADE_TIPO_VLR");
                    BigDecimal adeVlr = (BigDecimal) row.get("ADE_VLR");
                    BigDecimal novoAdeVlr = (BigDecimal) row.get("NOVO_ADE_VLR");

                    // Ao aplicar o ajuste, limitar o valor da consignação a 0,01, caso a margem esteja negativa e seja necessário
                    // reduzir o valor da consignação, de modo que não fique negativo ou Zero, valores inválidos para a consignação.
                    if (novoAdeVlr.signum() <= 0) {
                        novoAdeVlr = new BigDecimal("0.01");
                    }

                    // Atualiza o valor da parcela: os cálculos numéricos devem ser feitos com limitação a duas
                    // casas decimais, descartando os demais dígitos, sem arredondamento.
                    novoAdeVlr = novoAdeVlr.setScale(2, RoundingMode.DOWN);

                    BigDecimal diferencaAdeVlr = novoAdeVlr.subtract(adeVlr);
                    if (diferencaAdeVlr.signum() != 0) {
                        boolean aumentouVlr = novoAdeVlr.compareTo(adeVlr) > 0;
                        String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr);

                        AutDescontoHome.updateAdeVlr(adeCodigo, novoAdeVlr);

                        // Gera ocorrência de alteração de valor da consignação, contendo o valor anterior e o novo.
                        String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.alterado.arg0.arg1.de.arg2.arg3.para.arg4.arg5", responsavel,
                                labelTipoVlr, (aumentouVlr ? NumberHelper.format(diferencaAdeVlr.abs().doubleValue(), NumberHelper.getLang()) : "(" + NumberHelper.format(diferencaAdeVlr.abs().doubleValue(), NumberHelper.getLang()) + ")"),
                                labelTipoVlr, NumberHelper.format(adeVlr.doubleValue(), NumberHelper.getLang()),
                                labelTipoVlr, NumberHelper.format(novoAdeVlr.doubleValue(), NumberHelper.getLang()));
                        OcorrenciaAutorizacaoHome.create(adeCodigo, tocCodigo, responsavel.getUsuCodigo(), ocaObs, adeVlr, novoAdeVlr, responsavel.getIpUsuario(), ocaData, ocaPeriodo, null);

                        rseCodigos.add(rseCodigo);
                    }
                }
            }

            //  Retornar a lista de registros servidores distintos que foram atualizados.
            return new ArrayList<>(rseCodigos);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void atualizaAdeUltPeriodoExportado()  throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_tmp_exportacao_ordenada tmp ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("SET ade.ade_ult_periodo_exportacao = pex.pex_periodo ");
            query.append("WHERE tmp.situacao='I' ");
            query.append("AND ade.ade_ano_mes_ini >= pex.pex_periodo ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    @Override
    public void concluiAdesSuspensasPorDataFim(String responsavelContratoSuspenso, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            int rows = 0;

            // Monta o FROM das querys.
            StringBuilder from = new StringBuilder();
            from.append(Columns.TB_AUTORIZACAO_DESCONTO);
            from.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
            from.append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO).append(") ");
            from.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
            from.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
            from.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
            from.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(") ");

            StringBuilder query = new StringBuilder();

            // Cria tabela com os contratos que a data final não é nula e já passou, ou seja,
            // contratos que já alcançaram a data final; que estão com status suspenso,
            // não sendo concluídos pela rotina tradicional.
            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_conclusao_ade_suspensa");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_conclusao_ade_suspensa(ade_codigo VARCHAR(32) NOT NULL, pex_periodo_pos DATE NOT NULL, PRIMARY KEY (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_conclusao_ade_suspensa (ade_codigo, pex_periodo_pos) ");
            query.append("select ade.ade_codigo, pex.pex_periodo ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (org.org_codigo = pex.org_codigo) ");
            query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
                        
            // Data fim já passada
            query.append("where pex.pex_periodo >= ade.ade_ano_mes_fim ").append("AND ( ");
            
            // Contrato suspenso
            if(responsavelContratoSuspenso.equals(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA)) {
                query.append("ade.sad_codigo = '").append(CodedValues.SAD_SUSPENSA).append("' ");
            } else if(responsavelContratoSuspenso.equals(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA_CSE)) {
                query.append("ade.sad_codigo = '").append(CodedValues.SAD_SUSPENSA).append("' OR ");
            }
            
            if(responsavelContratoSuspenso.equals(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSE) || responsavelContratoSuspenso.equals(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA_CSE)) {
                query.append("ade.sad_codigo = '").append(CodedValues.SAD_SUSPENSA_CSE).append("' ");
            }
            
            query.append(") AND (NOT EXISTS( ");
            query.append("  select 1 from tb_ocorrencia_autorizacao oca ");
            query.append("    inner join tb_decisao_judicial dju on dju.oca_codigo = oca.oca_codigo ");
            query.append("      where oca.ade_codigo = ade.ade_codigo) ");
            query.append(" OR (EXISTS( ");
            query.append("          select 1 from tb_ocorrencia_autorizacao oca ");
            query.append("          inner join tb_decisao_judicial dju on dju.oca_codigo = oca.oca_codigo ");
            query.append("              where oca.ade_codigo = ade.ade_codigo and dju.dju_data_revogacao is not null) ");
            query.append("      AND NOT EXISTS(");
            query.append("          select 1 from tb_ocorrencia_autorizacao oca1");
            query.append("          inner join tb_decisao_judicial dju on dju.oca_codigo = oca1.oca_codigo ");
            query.append("              where oca1.ade_codigo = ade.ade_codigo and dju.dju_data_revogacao is null))) ");
            query.append("group by ade.ade_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Insere ocorrencia de conclusão
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_periodo, oca_obs) ");
            query.append("SELECT CONCAT('I', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("', ade.ade_codigo, ");
            query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', NOW(), tmp.pex_periodo_pos, ");
            query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido.suspenso.data.fim.ultrapassada", responsavel)).append("' ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tmp_conclusao_ade_suspensa tmp on (ade.ade_codigo = tmp.ade_codigo) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Conclui os contratos
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ");
            query.append("SET sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
            query.append("WHERE ade_codigo IN (SELECT ade_codigo FROM tmp_conclusao_ade_suspensa)");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
