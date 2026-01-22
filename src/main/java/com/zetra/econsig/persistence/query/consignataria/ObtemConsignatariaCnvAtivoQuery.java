package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemConsignatariaCnvAtivoQuery</p>
 * <p>Description: Listagem de Consignatárias ligadas a um convênio ativos</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConsignatariaCnvAtivoQuery extends HQuery {
    public String svcCodigo;
    public String orgCodigo;
    public boolean csaDeveSerAtiva = false;
    public boolean listagemReserva = false;
    AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String corpo = "select distinct " +
                "coalesce(nullif(trim(consignataria.csaNomeAbrev), ''), consignataria.csaNome) as " + Columns.getColumnName(Columns.CSA_NOME_ABREV) + "," +
                "consignataria.csaCodigo," +
                "consignataria.csaNome," +
                "consignataria.csaIdentificador," +
                "consignataria.csaCnpj," +
                "consignataria.csaIdentificadorInterno";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio convenio ");
        corpoBuilder.append(" inner join convenio.consignataria consignataria ");
        corpoBuilder.append(" WHERE convenio.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");

        if (csaDeveSerAtiva) {
            corpoBuilder.append(" and consignataria.csaAtivo = ").append(CodedValues.STS_ATIVO).append("");
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and convenio.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and convenio.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (listagemReserva) {
            // Que não possui parâmetro de consignatária impedindo a exibição na listagem
           corpoBuilder.append(" and not exists (");
           corpoBuilder.append("   select 1 from consignataria.paramSvcConsignatariaSet psc");
           corpoBuilder.append("   where psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO).append("'");
           corpoBuilder.append("     and psc.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
           corpoBuilder.append("     and psc.pscVlr = '").append(CodedValues.TPA_NAO).append("'");
           corpoBuilder.append(")");
       }

        corpoBuilder.append(" order by coalesce(nullif(trim(consignataria.csaNomeAbrev), ''), consignataria.csaNome)");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.getColumnName(Columns.CSA_NOME_ABREV),
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_CNPJ,
                Columns.CSA_IDENTIFICADOR_INTERNO
        };
    }
}
