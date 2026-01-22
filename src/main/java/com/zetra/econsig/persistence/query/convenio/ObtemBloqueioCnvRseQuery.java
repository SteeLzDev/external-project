package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemBloqueioCnvRseQuery</p>
 * <p>Description: Conta os convênios bloqueados e desbloqueados para um servidor</p>
 * <p>Copyright: Copyright (c) 2006-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class ObtemBloqueioCnvRseQuery extends HQuery {
    public String rseCodigo;
    public String csaCodigo;
    public String svcCodigo;
    public Boolean inativosSomenteComBloqueio;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "SELECT CASE (coalesce(paramCnvRse.pcrVlr, ''))"
                           + " WHEN '' THEN 'D'"
                           + " ELSE 'B' END, "
                           + " COUNT(*) AS TOTAL";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" left outer join cnv.paramConvenioRegistroSerSet paramCnvRse ");
        corpoBuilder.append(" with paramCnvRse.registroServidor.rseCodigo = :rseCodigo");
        corpoBuilder.append(" and paramCnvRse.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("'");
        corpoBuilder.append(" WHERE cnv.orgao.orgCodigo = (select rse.orgao.orgCodigo from RegistroServidor rse where rse.rseCodigo = :rseCodigo) ");

        if ((inativosSomenteComBloqueio != null) && inativosSomenteComBloqueio ) {
            corpoBuilder.append(" and ( cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" or paramCnvRse.pcrVlr is not null ) ");
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        corpoBuilder.append(" group by CASE (coalesce(paramCnvRse.pcrVlr, '')) WHEN '' THEN 'D' ELSE 'B' END");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                        "TIPO",
                        "TOTAL"
                   };

    }

}
