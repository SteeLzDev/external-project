package com.zetra.econsig.persistence.query.faq;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFaqQuery</p>
 * <p>Description: Lista de faq's por entidade</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFaqQuery extends HQuery {
    private final String FAQ_FIELDS = "faq.faqCodigo, " +
                                     "faq.usuario.usuCodigo, " +
                                     "faq.faqTitulo1, " +
                                     "faq.faqTitulo2, " +
                                     "faq.faqTexto, " +
                                     "faq.faqData, " +
                                     "faq.faqSequencia, " +
                                     "faq.faqExibeCse, " +
                                     "faq.faqExibeCsa, " +
                                     "faq.faqExibeOrg, " +
                                     "faq.faqExibeCor, " +
                                     "faq.faqExibeSer, " +
                                     "faq.faqHtml, " +
                                     "faq.faqExibeSup, " +
                                     "faq.faqExibeMobile, " +
                                     "faq.cafCodigo, " +
                                     "caf.cafDescricao ";

    public boolean count = false;

    public String faqExibeCsa;
    public String faqExibeCor;
    public String faqExibeCse;
    public String faqExibeOrg;
    public String faqExibeSer;
    public String faqExibeSup;
    public String faqTitulo1;
    public String faqTitulo2;
    public String faqExibeMobile;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select " + FAQ_FIELDS;
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Faq faq ");
        corpoBuilder.append(" left outer join faq.categoriaFaq caf ");
        corpoBuilder.append(" WHERE 1=1 ");

        if (faqExibeCse != null) {
            corpoBuilder.append(" and faq.faqExibeCse = '").append(CodedValues.TPC_SIM).append("'");
        }
        if (faqExibeCsa != null) {
            corpoBuilder.append(" and faq.faqExibeCsa = '").append(CodedValues.TPC_SIM).append("'");
        }
        if (faqExibeCor != null) {
            corpoBuilder.append(" and faq.faqExibeCor = '").append(CodedValues.TPC_SIM).append("'");
        }
        if (faqExibeOrg != null) {
            corpoBuilder.append(" and faq.faqExibeOrg = '").append(CodedValues.TPC_SIM).append("'");
        }
        if (faqExibeSer != null) {
            corpoBuilder.append(" and faq.faqExibeSer = '").append(CodedValues.TPC_SIM).append("'");
        }
        if (faqExibeSup != null) {
            corpoBuilder.append(" and faq.faqExibeSup = '").append(CodedValues.TPC_SIM).append("'");
        }
        if (faqExibeMobile != null) {
            corpoBuilder.append(" and faq.faqExibeMobile = :faqExibeMobile ");
        }
        if (!TextHelper.isNull(faqTitulo1)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("faq.faqTitulo1", "faqTitulo1", faqTitulo1));
        }
        if (!TextHelper.isNull(faqTitulo2)) {
            corpoBuilder.append(" and faq.faqTitulo2 ").append(criaClausulaNomeada("faqTitulo2", faqTitulo2));
        }

        if (!count) {
            corpoBuilder.append(" order by faq.cafCodigo, faq.faqSequencia DESC, faq.faqData DESC");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(faqTitulo1)) {
            defineValorClausulaNomeada("faqTitulo1", faqTitulo1, query);
        }
        if (!TextHelper.isNull(faqTitulo2)) {
            defineValorClausulaNomeada("faqTitulo2", faqTitulo2, query);
        }
        if (!TextHelper.isNull(faqExibeMobile)) {
            defineValorClausulaNomeada("faqExibeMobile", faqExibeMobile, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.FAQ_CODIGO,
                Columns.FAQ_USU_CODIGO,
                Columns.FAQ_TITULO_1,
                Columns.FAQ_TITULO_2,
                Columns.FAQ_TEXTO,
                Columns.FAQ_DATA,
                Columns.FAQ_SEQUENCIA,
                Columns.FAQ_EXIBE_CSE,
                Columns.FAQ_EXIBE_CSA,
                Columns.FAQ_EXIBE_ORG,
                Columns.FAQ_EXIBE_COR,
                Columns.FAQ_EXIBE_SER,
                Columns.FAQ_HTML,
                Columns.FAQ_EXIBE_SUP,
                Columns.FAQ_EXIBE_MOBILE,
                Columns.FAQ_CAF_CODIGO,
                Columns.CAF_DESCRICAO
        };
    }
}
