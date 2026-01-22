package com.zetra.econsig.persistence.dao.generic;

import java.math.BigDecimal;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericAutorizacaoDAO</p>
 * <p>Description: Implementacao Genérica do DAO de autorização. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericAutorizacaoDAO implements AutorizacaoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericAutorizacaoDAO.class);

    protected boolean retAtrasadoSomaAparcela = false;

    /**
     * Atualiza os valores do contrato referentes a folha de acordo com a importação do
     * retorno da folha
     * @param adeCodigo
     * @param adeVlr
     * @param adeAnoMesIni
     * @param adeAnoMesFim
     * @throws DAOException
     * @todo Este método pode ser feito via EJB
     */
    @Override
    public void atualizaValorFolha(String adeCodigo, BigDecimal adeVlrFolha, String adePrazoFolha, String adeAnoMesIniFolha, String adeAnoMesFimFolha) throws DAOException {
        if (adeCodigo == null || adeCodigo.equals("")) {
            throw new DAOException("mensagem.erro.atualizar.valor.folha.codigo.invalido", (AcessoSistema) null);
        }


        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" SET ");
            String complemento = "";

            if (!TextHelper.isNull(adeAnoMesFimFolha)) {
                query.append(Columns.ADE_ANO_MES_FIM_FOLHA).append(" = '").append(adeAnoMesFimFolha).append("'");
                complemento = MySqlDAOFactory.SEPARADOR;
            }
            if (!TextHelper.isNull(adeAnoMesIniFolha)) {
                query.append(complemento).append(Columns.ADE_ANO_MES_INI_FOLHA).append(" = '").append(adeAnoMesIniFolha).append("'");
                complemento = MySqlDAOFactory.SEPARADOR;
            }
            if (!TextHelper.isNull(adePrazoFolha)) {
                query.append(complemento).append(Columns.ADE_PRAZO_FOLHA).append(" = ").append(adePrazoFolha);
                complemento = MySqlDAOFactory.SEPARADOR;
            }

            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) && adeVlrFolha != null) {
                query.append(complemento).append(Columns.ADE_VLR_FOLHA).append(" = COALESCE(").append(Columns.ADE_VLR_FOLHA).append(", 0) + ").append(NumberHelper.format(adeVlrFolha.doubleValue(), "en"));
            } else if (retAtrasadoSomaAparcela && adeVlrFolha != null) {
                query.append(complemento).append(Columns.ADE_VLR_FOLHA).append(" = CASE WHEN ").append(Columns.ADE_VLR).append(" <> ").append(NumberHelper.format(adeVlrFolha.doubleValue(), "en"));
                query.append(" THEN COALESCE(").append(Columns.ADE_VLR_FOLHA).append(", 0) + ").append(NumberHelper.format(adeVlrFolha.doubleValue(), "en"));
                query.append(" ELSE ").append(NumberHelper.format(adeVlrFolha.doubleValue(), "en")).append(" END ");
            } else {
                query.append(complemento).append(Columns.ADE_VLR_FOLHA).append(" = ").append((adeVlrFolha != null ? NumberHelper.format(adeVlrFolha.doubleValue(), "en") : "null"));
            }

            query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = '").append(adeCodigo).append("'");


            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * reinicia campo de motivo de não exportação (mne_codigo) de todos os registros de contratos
     * @param adeCodigo
     * @param adeVlr
     * @param adeAnoMesIni
     * @param adeAnoMesFim
     * @throws DAOException
     * @todo Este método pode ser feito via EJB
     */
    @Override
    public void limpaMotivoNaoExportacao(AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" SET ");
            query.append(Columns.ADE_MNE_CODIGO).append(" = NULL");
            query.append(" WHERE ").append(Columns.ADE_MNE_CODIGO).append(" IS NOT NULL");

            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o campo de carência final para os sistemas que fazem reimplante de capital devido (tpc_codigo = 488)
     * @param carenciaFolha
     * @param responsavel
     * @throws DAOException
     */
    protected void atualizaCarenciaFinal(int carenciaFolha, String complementoAde, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            StringBuilder query = new StringBuilder();

            // DESENV-19257 - Remove a carência final para todas as consignações não encerradas que tem carência final definida
            query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" SET ").append(Columns.ADE_CARENCIA_FINAL).append(" = NULL");
            query.append(" WHERE ").append(Columns.ADE_CARENCIA_FINAL).append(" IS NOT NULL");
            query.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" NOT IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");

            // DESENV-22377 - considerar apenas consignações de natureza empréstimo e SalaryPay
            query.append(" AND EXISTS(");
            query.append(" SELECT 1 FROM ").append(Columns.TB_VERBA_CONVENIO);
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON ").append(Columns.CNV_CODIGO).append(" = ").append(Columns.VCO_CNV_CODIGO);
            query.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" ON ").append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO);
            query.append(" WHERE ").append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO);
            query.append(" AND ").append(Columns.SVC_NSE_CODIGO).append(" IN ('").append(CodedValues.NSE_EMPRESTIMO).append("', '").append(CodedValues.NSE_SALARYPAY).append("') ");
            query.append(")");

            // E o serviço não tem regra específica para carência de conclusão
            query.append(" AND NOT EXISTS (SELECT 1 FROM ").append(Columns.TB_VERBA_CONVENIO);
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
            query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(") ");
            query.append(" WHERE ").append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO);
            query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '").append(CodedValues.TPS_CARENCIA_FINAL).append("'");
            query.append(" AND ").append(Columns.PSE_VLR).append(" <> ''");
            query.append(")");

            if (!TextHelper.isNull(complementoAde)) {
                query.append(" AND ").append(Columns.ADE_CODIGO).append(" IN (SELECT tmp.ADE_CODIGO FROM tb_tmp_ade_pagas tmp)");
            }
            LOG.trace(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Define a carência final para as consignações que tiveram parcela rejeitada ou paga a menor no período. Parcelas sem
            // retorno são consideradas como rejeitadas visto que a conclusão de contratos é executada antes da rejeição das parcelas sem retorno
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" SET ").append(Columns.ADE_CARENCIA_FINAL).append(" = ").append(carenciaFolha);
            query.append(" WHERE ").append(Columns.ADE_SAD_CODIGO).append(" NOT IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");

            // DESENV-22377 - considerar apenas consignações de natureza empréstimo e SalaryPay
            query.append(" AND EXISTS (");
            query.append(" SELECT 1 FROM ").append(Columns.TB_VERBA_CONVENIO);
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON ").append(Columns.CNV_CODIGO).append(" = ").append(Columns.VCO_CNV_CODIGO);
            query.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" ON ").append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO);
            query.append(" WHERE ").append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO);
            query.append(" AND ").append(Columns.SVC_NSE_CODIGO).append(" IN ('").append(CodedValues.NSE_EMPRESTIMO).append("', '").append(CodedValues.NSE_SALARYPAY).append("') ");
            query.append(")");

            // DESENV-18631 - considerar parcela rejeitada somente se pagas < prazo
            // DESENV-18305 - Adicionamos a tabela tb_parcela_desconto para caso algum contrato não tenha parcela no período processado
            query.append(" AND ((COALESCE(").append(Columns.ADE_PRD_PAGAS).append(", 0) < COALESCE(").append(Columns.ADE_PRAZO).append(", 999) ");
            query.append("   AND (EXISTS (");
            query.append("     SELECT 1 FROM ").append(Columns.TB_PARCELA_DESCONTO_PERIODO);
            query.append("     WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.PDP_ADE_CODIGO);
            query.append("     AND ").append(Columns.PDP_SPD_CODIGO).append(" IN ('").append(CodedValues.SPD_REJEITADAFOLHA).append("','").append(CodedValues.SPD_SEM_RETORNO).append("')");
            query.append("   )");
            query.append("   OR EXISTS (");
            query.append("     SELECT 1 FROM ").append(Columns.TB_PARCELA_DESCONTO);
            query.append("     WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.PRD_ADE_CODIGO);
            query.append("     AND ").append(Columns.PRD_SPD_CODIGO).append(" IN ('").append(CodedValues.SPD_REJEITADAFOLHA).append("','").append(CodedValues.SPD_SEM_RETORNO).append("')");
            query.append("   )");
            query.append(" ))");
            query.append(" OR EXISTS (");
            query.append("   SELECT 1 FROM ").append(Columns.TB_PARCELA_DESCONTO_PERIODO);
            query.append("   WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.PDP_ADE_CODIGO);
            query.append("   AND ").append(Columns.PDP_VLR_PREVISTO).append(" > ").append(Columns.PDP_VLR_REALIZADO);

            //DESENV-20731 - Parâmetro de sistema que deve configurar se a regra de carência para conclusão com saldo em aberto (parâmetro de Sistema 488)
            //só deve considerar como saldo em aberto as parcelas pagas pelo retorno (SPD_CODIGO = 6), não considerando as pagas de forma manual (SPD_CODIGO = 7).
            if (ParamSist.paramEquals(CodedValues.TPC_VERIFICA_CARENCIA_CONCLUSAO_APENAS_COM_SDV_PARCELAS_PAGAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                query.append("   AND ").append(Columns.PDP_SPD_CODIGO).append(" IN ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("')");    
            }else {
                query.append("   AND ").append(Columns.PDP_SPD_CODIGO).append(" IN ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("')");
            }
            
            query.append(" )");
            query.append(" OR EXISTS (");
            query.append("   SELECT 1 FROM ").append(Columns.TB_PARCELA_DESCONTO);
            query.append("   WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.PRD_ADE_CODIGO);
            query.append("   AND ").append(Columns.PRD_VLR_PREVISTO).append(" > ").append(Columns.PRD_VLR_REALIZADO);
            
            //DESENV-20731 - Parâmetro de sistema que deve configurar se a regra de carência para conclusão com saldo em aberto (parâmetro de Sistema 488)
            //só deve considerar como saldo em aberto as parcelas pagas pelo retorno (SPD_CODIGO = 6), não considerando as pagas de forma manual (SPD_CODIGO = 7).
            if (ParamSist.paramEquals(CodedValues.TPC_VERIFICA_CARENCIA_CONCLUSAO_APENAS_COM_SDV_PARCELAS_PAGAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                query.append("   AND ").append(Columns.PRD_SPD_CODIGO).append(" IN ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("')");    
            }else {
                query.append("   AND ").append(Columns.PRD_SPD_CODIGO).append(" IN ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("')");
            }
            
            query.append(" )");
            query.append(")");

            if (!TextHelper.isNull(complementoAde)) {
                query.append(" AND ").append(Columns.ADE_CODIGO).append(" IN (SELECT tmp.ADE_CODIGO FROM tb_tmp_ade_pagas tmp)");
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * opção que define se no retorno atrasado o valor realizado deste arquivo deve ser somado ao realizado da tabela de histórico de parcela.
     */
    @Override
    public void setRetAtrasadoSomaAparcela(boolean somaParcela) throws DAOException {
        retAtrasadoSomaAparcela = somaParcela;
    }
}
