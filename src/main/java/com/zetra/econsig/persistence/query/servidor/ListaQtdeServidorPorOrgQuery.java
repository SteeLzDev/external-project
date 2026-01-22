package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaQtdeServidorPorOrgQuery</p>
 * <p>Description: Listagem do número de servidores por órgão</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaQtdeServidorPorOrgQuery extends HQuery {

    public AcessoSistema responsavel;

    public ListaQtdeServidorPorOrgQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select ");
        corpoBuilder.append(" org.orgCodigo as codigo, ");
        corpoBuilder.append(" upper(coalesce(org.orgNome, '"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"')) AS nome, ");
        corpoBuilder.append(" upper(coalesce(org.orgCnpj, '"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"')) AS cnpj, ");
        corpoBuilder.append(" COUNT(DISTINCT rse.rseCodigo) AS valor ");
        corpoBuilder.append(" from RegistroServidor rse");
        corpoBuilder.append(" inner join rse.orgao org");
        corpoBuilder.append(" WHERE 1=1");
        corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo").append(criaClausulaNomeada("srsAtivo", CodedValues.SRS_ATIVOS));
        corpoBuilder.append(" AND exists (");
        corpoBuilder.append(" select 1");

        if(responsavel.isCseSupOrg()) {
            corpoBuilder.append(" from Orgao org");
        }

        if(responsavel.isCsaCor()) {
            corpoBuilder.append(" from Convenio cnv");
            corpoBuilder.append(" inner join cnv.consignataria csa");
            corpoBuilder.append(" inner join cnv.servico svc");
        }

        if(responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc");
        }

        if(responsavel.isCsaCor()) {
            corpoBuilder.append(" WHERE rse.orgao.orgCodigo = cnv.orgao.orgCodigo");
        }

        if(responsavel.isCseSupOrg()) {
            corpoBuilder.append(" WHERE rse.orgao.orgCodigo = org.orgCodigo");
        }


        if(responsavel.isCsa()) {
            corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidade ");
        }

        if(responsavel.isOrg()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                corpoBuilder.append(" AND org.estabelecimento.estCodigo = :codigoEntidade");
            } else {
                corpoBuilder.append(" AND org.orgCodigo = :codigoEntidade");
            }
        }

        if(responsavel.isCor()) {
            if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidade ");
            } else {
                corpoBuilder.append(" AND csa.csaCodigo = :codigoEntidadePai ");
                corpoBuilder.append(" AND crc.correspondente.corCodigo = :codigoEntidade ");
                corpoBuilder.append(" AND crc.convenio.statusConvenio.scvCodigo = " + CodedValues.SCV_ATIVO);
            }
        }

        if(responsavel.isCsaCor()) {
            corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '" + CodedValues.SCV_ATIVO).append("'");
        }

        corpoBuilder.append(" )");
        corpoBuilder.append(" GROUP BY org.orgNome, org.orgCodigo, org.orgCnpj ");
        corpoBuilder.append(" ORDER BY org.orgNome ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

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

        defineValorClausulaNomeada("srsAtivo", CodedValues.SRS_ATIVOS, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
        		"codigo",
                "nome",
                "cnpj",
                "valor"
        };
    }

}
