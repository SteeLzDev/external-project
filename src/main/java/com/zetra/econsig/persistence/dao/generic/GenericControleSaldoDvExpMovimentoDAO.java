package com.zetra.econsig.persistence.dao.generic;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.ControleSaldoDvExpMovimentoDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericControleSaldoDvExpMovimentoDAO</p>
 * <p>Description: Implementacao Genérica do DAO de controle de saldo no movimento. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericControleSaldoDvExpMovimentoDAO extends GenericControleSaldoDevedorDAO implements ControleSaldoDvExpMovimentoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericControleSaldoDvExpMovimentoDAO.class);

    /***************************************************************************/
    /**
     * Retorna true caso exista algum contrato com correção de saldo devedor
     * e o coeficiente utilizado para a correção não esteja presente. Retorna
     * false, caso todos coeficientes estejam ok, ou se não existem contratos
     * que possuem correção de saldo devedor.
     * @return boolean
     * @throws DAOException
     */
    @Override
    public boolean coeficientesCorrecaoAusentes() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
        query.append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
        query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
        query.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(")");

        // Parâmetro de serviço TPS_POSSUI_CORRECAO_SALDO_DEVEDOR
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" tpsPossuiCorrecao ON (");
        query.append(Columns.CNV_SVC_CODIGO).append(" = tpsPossuiCorrecao.SVC_CODIGO)");
        // Parâmetro de serviço TPS_FORMA_CALCULO_CORRECAO_SALDO_DV
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" tpsFormaCorrecao ON (");
        query.append(Columns.CNV_SVC_CODIGO).append(" = tpsFormaCorrecao.SVC_CODIGO)");

        query.append(" LEFT OUTER JOIN ").append(Columns.TB_COEFICIENTE_CORRECAO).append(" ON (");
        query.append(Columns.CCR_TCC_CODIGO).append(" = tpsFormaCorrecao.PSE_VLR)");

        // Serviços que possuem correção de saldo devedor
        query.append(" WHERE tpsPossuiCorrecao.TPS_CODIGO = '");
        query.append(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");
        query.append(" AND tpsPossuiCorrecao.PSE_VLR IS NOT NULL ");
        query.append(" AND tpsPossuiCorrecao.PSE_VLR <> '");
        query.append(CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");

        // Parâmetro de serviço TPS_FORMA_CALCULO_CORRECAO_SALDO_DV
        query.append(" AND tpsFormaCorrecao.TPS_CODIGO = '");
        query.append(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV).append("'");

        // Status do contrato
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Contratos que não foram feitos no período de exportação
        query.append(" AND ").append(Columns.ADE_ANO_MES_INI).append(" < ");
        query.append(Columns.PEX_DATA_INI);

        // Restringe para os que não tem o coeficiente cadastrado
        query.append(" AND ").append(Columns.CCR_TCC_CODIGO).append(" IS NULL");

        LOG.trace(query.toString());

        // Se existir algum registro retorna true indicando que os coeficientes não estão ok
        final int total = Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);
        return (total > 0);
    }

    /***************************************************************************/
    /**
     * Realiza a correção do saldo devedor para os serviços que possuem
     * esta funcionalidade configurada no parâmetro de serviço.
     * @throws DAOException
     */
    @Override
    public void corrigirSaldoDevedor() throws DAOException {
        try {
            List<String> servicosCorrecao = pesquisarServicosCorrecaoSaldoDevedor(null);
            if (servicosCorrecao != null && servicosCorrecao.size() > 0) {
                // Remove as ocorrências de exportação do período atual
                removerOcorrenciasCorrecaoDoPeriodo();

                // Cria tabela temporária para armazenar dados da correção
                criarTabelaTemporariaCorrecaoSdv();

                // Calcula a correção do saldo para os contratos que tem correção
                // sobre o valor total do saldo devedor
                calcularCorrecaoTotalSaldoDevedor(servicosCorrecao);

                // Calcula a correção do saldo para os contratos que tem correção
                // sobre o saldo das parcelas já enviadas para a folha
                calcularCorrecaoSaldoParcelas(servicosCorrecao);

                // Realiza a correção primeiramente para os serviços que possuem
                // correção no próprio serviço
                corrigirSdvProprioServico();

                // Realiza a correção dos demais serviços (que possuem correção
                // em outro serviço)
                corrigirSdvEmOutroServico();

                // Insere ocorrências de correção de saldo devedor
                criarOcorrenciasCorrecao();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    protected abstract void removerOcorrenciasCorrecaoDoPeriodo() throws DataAccessException;
    protected abstract void criarTabelaTemporariaCorrecaoSdv() throws DataAccessException;
    protected abstract void calcularCorrecaoTotalSaldoDevedor(List<String> servicos) throws DataAccessException;
    protected abstract void calcularCorrecaoSaldoParcelas(List<String> servicos) throws DataAccessException;
    protected abstract void corrigirSdvProprioServico() throws DataAccessException;
    protected abstract void corrigirSdvEmOutroServico() throws DataAccessException;
    protected abstract void criarOcorrenciasCorrecao() throws DataAccessException;


    /***************************************************************************/
    /**
     * Ajusta o ADE_VLR de acordo com o novo saldo devedor. Para os serviços que
     * possuem controle de valor máximo de desconto, o mínimo entre o saldo
     * devedor e o teto deve ser armazenado no adeVlr
     * @throws DAOException
     */
    @Override
    public void ajustarAdeValor() throws DAOException {
        try {
            List<String> servicosControleSaldo = pesquisarServicosControleSaldoDevedor();
            if (servicosControleSaldo != null && servicosControleSaldo.size() > 0) {
                ajustarAdeValorSemControleTeto(servicosControleSaldo);

                ajustarAdeValorComTetoDeParcela(servicosControleSaldo);

                ajustarAdeValorComTetoPeloCargo(servicosControleSaldo);

                criarOcorrenciasAlteracaoAdeVlr(servicosControleSaldo);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    protected abstract void ajustarAdeValorSemControleTeto(List<String> servicosControleSaldo) throws DataAccessException;
    protected abstract void ajustarAdeValorComTetoDeParcela(List<String> servicosControleSaldo) throws DataAccessException;
    protected abstract void ajustarAdeValorComTetoPeloCargo(List<String> servicosControleSaldo) throws DataAccessException;
    protected abstract void criarOcorrenciasAlteracaoAdeVlr(List<String> servicosControleSaldo) throws DataAccessException;
}
