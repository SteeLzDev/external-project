package com.zetra.econsig.persistence.query.dashboardprocessamento;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;

/**
 * <p>Title: ObtemMediaMargemProcessadaDashboardQuery</p>
 * <p>Description: Listagem da média de margem dos servidores que já tiveram os blocos de margem processados
 * para exibir no dashboard de processamento da folha.</p>
 * <p>Copyright: Copyright (c) 2013-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas, Leonel Martins
 */
public class ObtemMediaMargemProcessadaDashboardQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT AVG(hmr.hmrMargemAntes) ");
        corpoBuilder.append(", AVG(hmr.hmrMargemDepois) ");
        corpoBuilder.append("FROM BlocoProcessamento bpr ");
        corpoBuilder.append("INNER JOIN bpr.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.historicoMargemRseSet hmr ");

        corpoBuilder.append(" WHERE bpr.tipoBlocoProcessamento.tbpCodigo = '").append(TipoBlocoProcessamentoEnum.MARGEM.getCodigo()).append("'");
        corpoBuilder.append(" AND bpr.statusBlocoProcessamento.sbpCodigo = '").append(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo()).append("'");
        corpoBuilder.append(" AND hmr.hmrOperacao = '").append(OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN.getCodigo()).append("'");
        corpoBuilder.append(" AND hmr.margem.marCodigo = ").append(CodedValues.INCIDE_MARGEM_SIM);
        corpoBuilder.append(" AND rse.rseDataCarga >= bpr.bprDataProcessamento");
        corpoBuilder.append(" AND hmr.hmrData >= rse.rseDataCarga");

        if ((codigoEntidade != null) && (tipoEntidade != null) && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            corpoBuilder.append(" AND bpr.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        } else if ((codigoEntidade != null) && (tipoEntidade != null) && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append(" AND bpr.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if ((codigoEntidade != null) && (tipoEntidade != null) && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HMR_MARGEM_ANTES,
                Columns.HMR_MARGEM_DEPOIS
        };
    }
}
