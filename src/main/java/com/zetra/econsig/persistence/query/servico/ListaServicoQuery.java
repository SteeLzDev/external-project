package com.zetra.econsig.persistence.query.servico;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoQuery</p>
 * <p>Description: Listagem de Serviços</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoQuery extends HQuery {
    public Short svcAtivo;

    public String svcIdentificador;

    public String svcDescricao;

    public String svcCodigo;

    public String tgsCodigo;

    public String nseCodigo;

    public List<String> svcCodigos;

    public List<String> marCodigos;

    public boolean count = false;

    public boolean orderByList = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo = "select count(*) as total ";
        } else {
            corpo = "select servico.svcCodigo, " +
                    "   servico.svcIdentificador, " +
                    "   servico.svcDescricao, " +
                    "   servico.svcPrioridade, " +
                    "   servico.svcAtivo, " +
                    "   servico.svcObs, " +
                    "   servico.naturezaServico.nseCodigo, " +
                    "   nse.nseDescricao ";
        }
        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico servico ");
        corpoBuilder.append("left outer join servico.naturezaServico nse ");
        corpoBuilder.append("where 1=1 ");

        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("servico.svcIdentificador", "svcIdentificador", svcIdentificador));
        }

        if ((svcCodigos != null) && (!svcCodigos.isEmpty())) {
            corpoBuilder.append(" and servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        } else if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (!TextHelper.isNull(svcDescricao)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("servico.svcDescricao", "svcDescricao", svcDescricao));
        }

        if (!TextHelper.isNull(tgsCodigo)) {
            corpoBuilder.append(" and servico.tipoGrupoSvc.tgsCodigo ").append(criaClausulaNomeada("tipoGrupoSvc", tgsCodigo));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and servico.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        if (svcAtivo != null) {
            corpoBuilder.append(" and servico.svcAtivo ").append(criaClausulaNomeada("svcAtivo", svcAtivo));
        }

        if ((marCodigos != null) && !marCodigos.isEmpty()) {
            corpoBuilder.append(" AND EXISTS (SELECT 1 FROM ParamSvcConsignante pse");
            corpoBuilder.append(" WHERE pse.servico.svcCodigo = servico.svcCodigo");
            corpoBuilder.append(" AND pse.tpsCodigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("'");
            corpoBuilder.append(" AND pse.pseVlr ").append(criaClausulaNomeada("marCodigos", marCodigos)).append(")");
        }

        if (!count) {

            if (orderByList && ((svcCodigos != null) && (!svcCodigos.isEmpty()))) {
                corpoBuilder.append(" order by CASE WHEN ").append("servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
                corpoBuilder.append(" THEN 0 ELSE 1 END ");
                corpoBuilder.append(", servico.svcDescricao asc");
            } else {
                corpoBuilder.append(" order by servico.svcDescricao");
            }
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }

        if (!TextHelper.isNull(svcDescricao)) {
            defineValorClausulaNomeada("svcDescricao", svcDescricao, query);
        }

        if ((svcCodigos != null) && (!svcCodigos.isEmpty())) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        } else if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(tgsCodigo)) {
            defineValorClausulaNomeada("tipoGrupoSvc", tgsCodigo, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (svcAtivo != null) {
            defineValorClausulaNomeada("svcAtivo", svcAtivo, query);
        }

        if ((marCodigos != null) && !marCodigos.isEmpty()) {
            defineValorClausulaNomeada("marCodigos", marCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                              Columns.SVC_CODIGO,
                              Columns.SVC_IDENTIFICADOR,
                              Columns.SVC_DESCRICAO,
                              Columns.SVC_PRIORIDADE,
                              Columns.SVC_ATIVO,
                              Columns.SVC_OBS,
                              Columns.NSE_CODIGO,
                              Columns.NSE_DESCRICAO
        };
    }
}
