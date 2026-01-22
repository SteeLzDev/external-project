package com.zetra.econsig.persistence.query.mensagem;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaEmailsEnvioMensagemQuery</p>
 * <p>Description: Listagem de emails cadastrados de entidades ativas para envio de mensagem.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaEmailsEnvioMensagemQuery extends HQuery {

    public String menCodigo;
    public String papCodigo;
    public List<String> csaCodigos;
    public boolean incluirBloqueadas = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder("SELECT ");

        if (papCodigo.equals(CodedValues.PAP_CONSIGNANTE)) {
            corpoBuilder.append("cse.cseCodigo AS CODIGO_ENTIDADE, cse.cseEmail as EMAIL");
        } else if (papCodigo.equals(CodedValues.PAP_CONSIGNATARIA)) {
            corpoBuilder.append("csa.csaCodigo AS CODIGO_ENTIDADE, csa.csaEmail as EMAIL");
        } else if (papCodigo.equals(CodedValues.PAP_CORRESPONDENTE)) {
            corpoBuilder.append("cor.corCodigo AS CODIGO_ENTIDADE, cor.corEmail as EMAIL");
        } else if (papCodigo.equals(CodedValues.PAP_ORGAO)) {
            corpoBuilder.append("org.orgCodigo AS CODIGO_ENTIDADE, org.orgEmail as EMAIL");
        } else if (papCodigo.equals(CodedValues.PAP_SERVIDOR)) {
            corpoBuilder.append("ser.serCodigo AS CODIGO_ENTIDADE, ser.serEmail as EMAIL");
        } else if (papCodigo.equals(CodedValues.PAP_SUPORTE)) {
            corpoBuilder.append("usu.usuCodigo AS CODIGO_ENTIDADE, usu.usuEmail as EMAIL");
        }

        corpoBuilder.append(" FROM");
        if (papCodigo.equals(CodedValues.PAP_CONSIGNANTE)) {
            corpoBuilder.append(" Consignante cse");
            corpoBuilder.append(" WHERE cse.cseAtivo ").append(criaClausulaNomeada("ativo", CodedValues.STS_ATIVO));
            corpoBuilder.append(" AND NULLIF(TRIM(cse.cseEmail), '') IS NOT NULL");
        } else if (papCodigo.equals(CodedValues.PAP_CONSIGNATARIA)) {
            corpoBuilder.append(" Consignataria csa");
            if (!TextHelper.isNull(menCodigo)) {
                corpoBuilder.append(" INNER JOIN csa.mensagemCsaSet menCsa");
                corpoBuilder.append(" INNER JOIN menCsa.mensagem men");
                corpoBuilder.append(" WITH men.menCodigo ").append(criaClausulaNomeada("menCodigo", menCodigo));
            }
            if (!incluirBloqueadas) {
                corpoBuilder.append(" WHERE csa.csaAtivo ").append(criaClausulaNomeada("ativo", CodedValues.STS_ATIVO));
            } else {
                corpoBuilder.append(" WHERE 1=1 ");
            }
            corpoBuilder.append(" AND NULLIF(TRIM(csa.csaEmail), '') IS NOT NULL");
            if (csaCodigos != null && !csaCodigos.isEmpty()) {
                corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos));
            }
        } else if (papCodigo.equals(CodedValues.PAP_CORRESPONDENTE)) {
            corpoBuilder.append(" Correspondente cor");
            corpoBuilder.append(" INNER JOIN cor.consignataria csa ");
            corpoBuilder.append(" WHERE cor.corAtivo ").append(criaClausulaNomeada("ativo", CodedValues.STS_ATIVO));
            corpoBuilder.append(" AND csa.csaAtivo ").append(criaClausulaNomeada("ativo", CodedValues.STS_ATIVO));
            corpoBuilder.append(" AND NULLIF(TRIM(cor.corEmail), '') IS NOT NULL");

            if (csaCodigos != null && !csaCodigos.isEmpty()) {
                corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos));
            }
        } else if (papCodigo.equals(CodedValues.PAP_ORGAO)) {
            corpoBuilder.append(" Orgao org");
            corpoBuilder.append(" WHERE org.orgAtivo ").append(criaClausulaNomeada("ativo", CodedValues.STS_ATIVO));
            corpoBuilder.append(" AND NULLIF(TRIM(org.orgEmail), '') IS NOT NULL");
        } else if (papCodigo.equals(CodedValues.PAP_SERVIDOR)) {
            corpoBuilder.append(" RegistroServidor rse");
            corpoBuilder.append(" INNER JOIN rse.servidor ser");
            corpoBuilder.append(" WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("ativo", CodedValues.SRS_ATIVO));
            corpoBuilder.append(" AND NULLIF(TRIM(ser.serEmail), '') IS NOT NULL");
        } else if (papCodigo.equals(CodedValues.PAP_SUPORTE)) {
            corpoBuilder.append(" Usuario usu ");
            corpoBuilder.append(" INNER JOIN usu.usuarioSupSet sup");
            corpoBuilder.append(" WHERE usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("ativo", CodedValues.STU_ATIVO));
            corpoBuilder.append(" AND NULLIF(TRIM(usu.usuEmail), '') IS NOT NULL");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (papCodigo.equals(CodedValues.PAP_CONSIGNATARIA) || papCodigo.equals(CodedValues.PAP_CORRESPONDENTE)) {
            if (csaCodigos != null && !csaCodigos.isEmpty()) {
                defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
            }
        }

        if (papCodigo.equals(CodedValues.PAP_CONSIGNANTE)) {
            defineValorClausulaNomeada("ativo", CodedValues.STS_ATIVO, query);
        } else if (papCodigo.equals(CodedValues.PAP_CONSIGNATARIA)) {
            if (!incluirBloqueadas) {
                defineValorClausulaNomeada("ativo", CodedValues.STS_ATIVO, query);
            }
            if (!TextHelper.isNull(menCodigo)) {
                defineValorClausulaNomeada("menCodigo", menCodigo, query);
            }
        } else if (papCodigo.equals(CodedValues.PAP_CORRESPONDENTE)) {
            defineValorClausulaNomeada("ativo", CodedValues.STS_ATIVO, query);
        } else if (papCodigo.equals(CodedValues.PAP_ORGAO)) {
            defineValorClausulaNomeada("ativo", CodedValues.STS_ATIVO, query);
        } else if (papCodigo.equals(CodedValues.PAP_SERVIDOR)) {
            defineValorClausulaNomeada("ativo", CodedValues.SRS_ATIVO, query);
        } else if (papCodigo.equals(CodedValues.PAP_SUPORTE)) {
            defineValorClausulaNomeada("ativo", CodedValues.STU_ATIVO, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {"CODIGO_ENTIDADE", "EMAIL"};
    }
}
