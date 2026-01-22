package com.zetra.econsig.persistence.query.margem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSomaMargensPrincipais</p>
 * <p>Description: Listagem da soma das margens da RegistroServidor</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSomaMargensPrincipaisQuery extends HQuery {

    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "SELECT "
                     + "sum(rse.rseMargemRest),"
                     + "sum(rse.rseMargemRest2),"
                     + "sum(rse.rseMargemRest3)";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from RegistroServidor rse ");
        corpoBuilder.append(" WHERE rse.statusRegistroServidor.srsCodigo").append(criaClausulaNomeada("srsAtivo", CodedValues.SRS_ATIVOS));
        corpoBuilder.append(" and rse.orgao.orgCodigo in (");
        corpoBuilder.append(" select cnv.orgao.orgCodigo");
        corpoBuilder.append(" from Convenio cnv");
        corpoBuilder.append(" inner join cnv.orgao org");

        if(responsavel.isCsaCor()) {
            corpoBuilder.append(" inner join cnv.consignataria csa");
        }

        corpoBuilder.append(" inner join cnv.servico svc");

        if(responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc");
        }

        if(responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            corpoBuilder.append(" inner join org.estabelecimento est");
        }

        corpoBuilder.append(" WHERE 1=1");

        if(responsavel.isCsa()) {
            corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidade ");
        }

        if(responsavel.isOrg()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                corpoBuilder.append(" AND est.estCodigo = :codigoEntidade ");
            } else {
                corpoBuilder.append(" AND org.orgCodigo = :codigoEntidade ");
            }
        }

        if(responsavel.isCor()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidade ");
            } else {
                corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidadePai ");
                corpoBuilder.append(" AND crc.corCodigo = :codigoEntidade ");
                corpoBuilder.append(" AND crc.convenio.statusConvenio.scvCodigo = " + CodedValues.SCV_ATIVO);
            }
        }

        corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '" + CodedValues.SCV_ATIVO + "'");
        corpoBuilder.append(" AND svc.svcAtivo = 1)");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("srsAtivo", CodedValues.SRS_ATIVOS, query);

        if(responsavel.isCsa()) {
            defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidade(), query);
        }

        if(responsavel.isOrg()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidadePai(), query);
            } else {
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidade(), query);
            }
        }

        if(responsavel.isCor()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidadePai(), query);
            } else {
                defineValorClausulaNomeada("codigoEntidadePai", responsavel.getCodigoEntidadePai(), query);
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidade(), query);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_MARGEM_REST,
                Columns.RSE_MARGEM_REST_2,
                Columns.RSE_MARGEM_REST_3
        };
    }
}
