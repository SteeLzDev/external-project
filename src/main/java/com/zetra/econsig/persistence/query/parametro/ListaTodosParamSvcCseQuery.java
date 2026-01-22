package com.zetra.econsig.persistence.query.parametro;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTodosParamSvcCseSupQuery</p>
 * <p>Description: Listagem de todos os parâmetros de serviço de consignante, mesmo que
 * só exista o tipo de parâmetro cadastrado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTodosParamSvcCseQuery extends HQuery {

    public String svcCodigo;
    public String responsavelAltera;
    public List<String> tpsCodigos;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "tps.tpsCodigo, " +
                "pse.pseVlr, " +
                "pse.pseVlrRef ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from TipoParamSvc tps");
        corpoBuilder.append(" left outer join tps.paramSvcConsignanteSet pse WITH ");
        corpoBuilder.append(" pse.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" where 1=1 ");

        if (responsavelAltera != null) {
            if (responsavel.isCse()) {
                if (responsavelAltera.equals("S")) {
                    corpoBuilder.append(" AND (tps.tpsCseAltera IS NULL OR tps.tpsCseAltera = 'S')");
                } else {
                    corpoBuilder.append(" AND tps.tpsCseAltera ").append(criaClausulaNomeada("responsavelAltera", responsavelAltera));
                }
            } else if (responsavel.isSup()) {
                if (responsavelAltera.equals("S")) {
                    corpoBuilder.append(" AND (tps.tpsSupAltera IS NULL OR tps.tpsSupAltera = 'S')");
                } else {
                    corpoBuilder.append(" AND tps.tpsSupAltera ").append(criaClausulaNomeada("responsavelAltera", responsavelAltera));
                }
            }
        }
        if (tpsCodigos != null && tpsCodigos.size() > 0) {
            corpoBuilder.append(" AND tps.tpsCodigo ").append(criaClausulaNomeada("tpsCodigos", tpsCodigos));
        }


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        if (responsavelAltera != null && !responsavelAltera.equals("S")) {
            defineValorClausulaNomeada("responsavelAltera", responsavelAltera, query);
        }
        if (tpsCodigos != null && tpsCodigos.size() > 0) {
            defineValorClausulaNomeada("tpsCodigos", tpsCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPS_CODIGO,
                Columns.PSE_VLR,
                Columns.PSE_VLR_REF
        };
    }
}
