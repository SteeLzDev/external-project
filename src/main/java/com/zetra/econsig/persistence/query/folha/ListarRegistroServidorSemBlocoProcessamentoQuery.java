package com.zetra.econsig.persistence.query.folha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;

/**
 * <p>Title: ObtemTotalParcelasPeriodoProcessamentoQuery</p>
 * <p>Description: Retorna o total de parcelas do periodo de processamento.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarRegistroServidorSemBlocoProcessamentoQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        // Lista registros servidores
        corpoBuilder.append("SELECT rse.rseCodigo ");
        corpoBuilder.append("FROM RegistroServidor rse ");

        if (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            corpoBuilder.append("INNER JOIN rse.orgao org ");
        }

        // Não excluídos
        corpoBuilder.append("WHERE rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.join(CodedValues.SRS_INATIVOS, "','")).append("') ");

        // Da entidade que realiza o processamento
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND rse.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        // Que não possuem bloco de processamento de margem
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM rse.blocoProcessamentoSet bpr WHERE bpr.tipoBlocoProcessamento.tbpCodigo = '").append(TipoBlocoProcessamentoEnum.MARGEM.getCodigo()).append("') ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }
}
