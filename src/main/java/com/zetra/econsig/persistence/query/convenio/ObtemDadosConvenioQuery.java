package com.zetra.econsig.persistence.query.convenio;

import java.util.Arrays;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemDadosConvenioQuery</p>
 * <p>Description: Retorna os status e descrição das entidades de um convênio</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemDadosConvenioQuery extends HQuery {

    public String cnvCodigo;
    public String corCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                "cnv.cnvCodVerba, " +
                "cnv.statusConvenio.scvCodigo, " +
                "svc.svcDescricao, " +
                "svc.svcAtivo, " +
                "csa.csaNome, " +
                "csa.csaAtivo, " +
                "csa.csaPermiteIncluirAde, " +
                "org.orgNome, " +
                "org.orgAtivo, " +
                "est.estNome, " +
                "est.estAtivo, " +
                "cse.cseNome, " +
                "cse.cseAtivo";

        if (!TextHelper.isNull(corCodigo)) {
            corpo += ", cor.corNome" +
                     ", cor.corAtivo" +
                     ", crc.statusConvenio.scvCodigo";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join est.consignante cse ");

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" inner join csa.correspondenteSet cor ");
            corpoBuilder.append(" inner join cor.correspondenteConvenioSet crc ");
        }

        corpoBuilder.append(" WHERE cnv.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" and cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            corpoBuilder.append(" and crc.convenio.cnvCodigo = cnv.cnvCodigo");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        String[] fields = new String [] {
                Columns.CNV_COD_VERBA,
                Columns.CNV_SCV_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.SVC_ATIVO,
                Columns.CSA_NOME,
                Columns.CSA_ATIVO,
                Columns.CSA_PERMITE_INCLUIR_ADE,
                Columns.ORG_NOME,
                Columns.ORG_ATIVO,
                Columns.EST_NOME,
                Columns.EST_ATIVO,
                Columns.CSE_NOME,
                Columns.CSE_ATIVO
        };

        if (!TextHelper.isNull(corCodigo)) {
            // Adiciona a lista de campos o nome e o status do correspondente
            String[] fields2 = Arrays.copyOf(fields, fields.length + 3);
            fields2[fields.length] = Columns.COR_NOME;
            fields2[fields.length + 1] = Columns.COR_ATIVO;
            fields2[fields.length + 2] = Columns.CRC_SCV_CODIGO;
            return fields2;
        } else {
            return fields;
        }
    }
}
