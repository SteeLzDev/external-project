package com.zetra.econsig.persistence.query.retorno;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemConsignatariaParaRetornoQuery</p>
 * <p>Description: Listagem de Consignatárias para um determinado código de verba,
 * identificadores de CSA/ORG/EST/SVC, matrícula / índice para separação do arquivo
 * de retorno entre as entidades</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConsignatariaParaRetornoQuery extends HQuery {
    public String csaIdentificador;
    public String orgIdentificador;
    public String estIdentificador;
    public String svcIdentificador;
    public String cnvCodVerba;
    public String cnvCodVerbaRef;
    public String cnvCodVerbaFerias;
    public String rseMatricula;
    public String adeNumero;
    public String adeIndice;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select csa.csaCodigo";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (!TextHelper.isNull(rseMatricula)) {
            // Se a matrícula foi informada, então pesquisa pelos contratos no sistema
            corpoBuilder.append(" from AutDesconto ade");
            corpoBuilder.append(" inner join ade.registroServidor rse");
            corpoBuilder.append(" inner join ade.verbaConvenio vco");
            corpoBuilder.append(" inner join vco.convenio cnv");

        } else {
            // Se não passou matrícula, busca apenas pela verba + identificadores
            corpoBuilder.append(" from Convenio cnv");
        }

        corpoBuilder.append(" inner join cnv.consignataria csa");
        corpoBuilder.append(" inner join cnv.servico svc");
        corpoBuilder.append(" inner join cnv.orgao org");
        corpoBuilder.append(" inner join org.estabelecimento est");
        corpoBuilder.append(" where 1=1");

        if (!TextHelper.isNull(csaIdentificador)) {
            corpoBuilder.append(" and csa.csaIdentificador ").append(criaClausulaNomeada("csaIdentificador", csaIdentificador));
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }
        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }
        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" and svc.svcIdentificador ").append(criaClausulaNomeada("svcIdentificador", svcIdentificador));
        }
        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" and cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
        }
        if (!TextHelper.isNull(cnvCodVerbaRef)) {
            corpoBuilder.append(" and cnv.cnvCodVerbaRef ").append(criaClausulaNomeada("cnvCodVerbaRef", cnvCodVerbaRef));
        }
        if (!TextHelper.isNull(cnvCodVerbaFerias)) {
            corpoBuilder.append(" and cnv.cnvCodVerbaFerias ").append(criaClausulaNomeada("cnvCodVerbaFerias", cnvCodVerbaFerias));
        }
        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }
        if (!TextHelper.isNull(adeNumero)) {
            corpoBuilder.append(" and ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }
        if (!TextHelper.isNull(adeIndice)) {
            corpoBuilder.append(" and ade.adeIndice ").append(criaClausulaNomeada("adeIndice", adeIndice));
        }
        corpoBuilder.append(" group by csa.csaCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaIdentificador)) {
            defineValorClausulaNomeada("csaIdentificador", csaIdentificador, query);
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }
        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }
        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }
        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }
        if (!TextHelper.isNull(cnvCodVerbaRef)) {
            defineValorClausulaNomeada("cnvCodVerbaRef", cnvCodVerbaRef, query);
        }
        if (!TextHelper.isNull(cnvCodVerbaFerias)) {
            defineValorClausulaNomeada("cnvCodVerbaFerias", cnvCodVerbaFerias, query);
        }
        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }
        if (!TextHelper.isNull(adeNumero)) {
            defineValorClausulaNomeada("adeNumero", Long.valueOf(adeNumero), query);
        }
        if (!TextHelper.isNull(adeIndice)) {
            defineValorClausulaNomeada("adeIndice", adeIndice, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO
        };
    }
}
