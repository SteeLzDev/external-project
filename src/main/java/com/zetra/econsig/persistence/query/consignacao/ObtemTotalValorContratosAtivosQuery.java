package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalValorContratosAtivosQuery</p>
 * <p>Description: Totaliza o valor dos contratos ativos de acordo
 * com os demais parâmetros informados</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalValorContratosAtivosQuery extends HQuery {

    public AcessoSistema responsavel;

    public ObtemTotalValorContratosAtivosQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "SELECT " + "sum(ade.adeVlr)";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from AutDesconto ade, Convenio cnv ");
        corpoBuilder.append(" inner join cnv.orgao org");

        if (responsavel.isCsaCor()) {
            corpoBuilder.append(" inner join cnv.consignataria csa");
        }
        if (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc");
        }

        corpoBuilder.append(" inner join ade.registroServidor rse");

        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            corpoBuilder.append(" inner join org.estabelecimento est");
        }

        corpoBuilder.append(" inner join ade.verbaConvenio vco");

        corpoBuilder.append(" WHERE rse.orgao.orgCodigo = org.orgCodigo");
        corpoBuilder.append(" AND vco.convenio.cnvCodigo = cnv.cnvCodigo ");
        corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo").append(criaClausulaNomeada("srsAtivo", CodedValues.SRS_ATIVOS));


        if (responsavel.isCsa()) {
            corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidade ");
        }

        if (responsavel.isOrg()) {
            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                corpoBuilder.append(" AND est.estCodigo = :codigoEntidade ");
            } else {
                corpoBuilder.append(" AND org.orgCodigo = :codigoEntidade ");
            }
        }

        if (responsavel.isCor()) {
            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidade ");
            } else {
                corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidadePai ");
                corpoBuilder.append(" AND crc.corCodigo = :codigoEntidade ");
                corpoBuilder.append(" AND ade.correspondente.corCodigo = crc.correspondente.corCodigo");
                corpoBuilder.append(" AND crc.convenio.statusConvenio.scvCodigo= '" + CodedValues.SCV_ATIVO + "'");
            }
        }

        corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '" + CodedValues.SCV_ATIVO + "'");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo").append(criaClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS));


        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS, query);
        defineValorClausulaNomeada("srsAtivo", CodedValues.SRS_ATIVOS, query);

        if (responsavel.isCsa()) {
            defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidade(), query);
        }

        if (responsavel.isOrg()) {
            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidadePai(), query);
            } else {
                defineValorClausulaNomeada("codigoEntidade", responsavel.getCodigoEntidade(), query);
            }
        }

        if (responsavel.isCor()) {
            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
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
        return new String[] { "total" };
    }

}
