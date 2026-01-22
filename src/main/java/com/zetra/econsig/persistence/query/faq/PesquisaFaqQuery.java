package com.zetra.econsig.persistence.query.faq;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PesquisaFaqQuery</p>
 * <p>Description: Retorna faq's a serem visualizadas na pg inicial</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PesquisaFaqQuery extends HQuery {

    public boolean count = false;
    public AcessoSistema responsavel;
    public String pesquisa;
    public String usuCodigo;

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
    								"faq.faqExibeSup ";

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        if (count) {
            corpo.append("SELECT COUNT(faq.faqCodigo)");
        } else {
            corpo.append("SELECT ").append(FAQ_FIELDS);
        }

        if (!TextHelper.isNull(usuCodigo)) {
        	corpo.append(", avf.avfCodigo ");
        	corpo.append(", avf.avfNota ");
        	corpo.append(", avf.avfComentario ");
        } else {
        	corpo.append(", '' as avfCodigo ");
        	corpo.append(", '' as avfNota ");
        	corpo.append(", '' as avfComentario ");
        }

        corpo.append(" FROM Faq faq ");

        if (!TextHelper.isNull(usuCodigo)) {
        	corpo.append(" LEFT OUTER JOIN faq.avaliacaoFaqSet avf WITH avf.usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }

        corpo.append(" WHERE 1=1");

        if (!TextHelper.isNull(usuCodigo)) {
        	corpo.append(" AND (avf.faq.faqCodigo IS NULL ");
        	corpo.append(" OR (avf.faq.faqCodigo, avf.usuario.usuCodigo, avf.avfData) ");
        	corpo.append(" IN (SELECT avf2.faq.faqCodigo, avf2.usuario.usuCodigo, MAX(avf2.avfData) ");
        	corpo.append(" FROM AvaliacaoFaq avf2 ");
        	corpo.append(" WHERE avf2.usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        	corpo.append(" GROUP BY avf2.faq.faqCodigo, avf2.usuario.usuCodigo ");
        	corpo.append(" )) ");
        }

        if (!TextHelper.isNull(pesquisa)) {
        	corpo.append(" AND (faq.faqTitulo1 LIKE :pesquisa OR faq.faqTitulo2 LIKE :pesquisa OR faq.faqTexto LIKE :pesquisa) ");
        }

        if (responsavel.isCse()) {
            corpo.append(" AND (faq.faqExibeCse = '").append(CodedValues.TPC_SIM).append("')");
        }
        if (responsavel.isOrg()) {
            corpo.append(" AND (faq.faqExibeOrg = '").append(CodedValues.TPC_SIM).append("')");
        }
        if (responsavel.isCsa()) {
        	corpo.append(" AND (faq.faqExibeCsa = '").append(CodedValues.TPC_SIM).append("')");
        }
        if (responsavel.isCor()) {
        	corpo.append(" AND (faq.faqExibeCor = '").append(CodedValues.TPC_SIM).append("')");
        }
        if (responsavel.isSer()) {
            corpo.append(" AND (faq.faqExibeSer = '").append(CodedValues.TPC_SIM).append("')");
        }
        if (responsavel.isSup()) {
            corpo.append(" AND (faq.faqExibeSup = '").append(CodedValues.TPC_SIM).append("')");
        }

        corpo.append(" AND (faq.faqExibeMobile = '").append(CodedValues.TPC_NAO).append("')");

        if (!count) {
            corpo.append(" ORDER BY faq.faqSequencia DESC, faq.faqData DESC");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }
        if (!TextHelper.isNull(pesquisa)) {
            defineValorClausulaNomeada("pesquisa", "%" + pesquisa + "%", query);
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
                Columns.AVF_CODIGO,
                Columns.AVF_NOTA,
                Columns.AVF_COMENTARIO
        };
    }
}
