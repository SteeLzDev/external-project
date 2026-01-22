package com.zetra.econsig.persistence.query.mensagem;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMensagensQuery</p>
 * <p>Description: Lista de mensagens por entidade</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMensagemQuery extends HQuery {
    private final String MENSAGEM_FIELDS = "mensagem.menCodigo, " +
                                           "mensagem.usuario.usuCodigo, " +
                                           "mensagem.usuario.usuLogin, " +
                                           "mensagem.menTitulo, " +
                                           "mensagem.menTexto, " +
                                           "mensagem.menData, " +
                                           "mensagem.menSequencia, " +
                                           "mensagem.menExibeCse, " +
                                           "mensagem.menExibeCsa, " +
                                           "mensagem.menExibeOrg, " +
                                           "mensagem.menExibeCor, " +
                                           "mensagem.menExibeSer, " +
                                           "mensagem.menExibeSup, " +
                                           "mensagem.menHtml, " +
                                           "mensagem.menExigeLeitura, " +
                                           "mensagem.menPermiteLerDepois, " +
                                           "mensagem.menNotificarCseLeitura, " +
                                           "mensagem.menBloqCsaSemLeitura, " +
                                           "mensagem.funcao.funCodigo," +
                                           "mensagem.menLidaIndividualmente, " +
                                           "mensagem.menPublica, " +
                                           "mensagem.menPushNotificationSer";

    public boolean count = false;

    public String menExibeCsa;

    public String menExibeCor;

    public String menExibeCse;

    public String menExibeOrg;

    public String menExibeSer;

    public String menExibeSup;

    public List<String> menCodigo;

    public String menTitulo;

    public Boolean exibeMenPublica;

    public List<String> csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select " + MENSAGEM_FIELDS;
            if (!TextHelper.isNull(menExibeCsa) || !TextHelper.isNull(menExibeCor)) {
                corpo += ", consignataria.csaCodigo";
                corpo += ", COALESCE(consignataria.csaNomeAbrev, consignataria.csaNome, '" + ApplicationResourcesHelper.getMessage("rotulo.mensagem.todas.consignatarias", (AcessoSistema) null) + "') as CSA_NOME";
            }
        } else {
            corpo = "select count(*) as total ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Mensagem mensagem ");

        if ((!TextHelper.isNull(menExibeCsa) && (TextHelper.isNull(csaCodigo)))) {
            corpoBuilder.append(" left outer join mensagem.mensagemCsaSet menCsa");
            corpoBuilder.append(" left outer join menCsa.consignataria consignataria");
        } else if (!TextHelper.isNull(menExibeCsa) && !TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" left outer join mensagem.mensagemCsaSet menCsa with menCsa.csaCodigo in (:csaCodigo)");
            corpoBuilder.append(" left outer join menCsa.consignataria consignataria");
        } else if (!TextHelper.isNull(menExibeCor)) {
            corpoBuilder.append(" left outer join mensagem.mensagemCsaSet menCsa");
            corpoBuilder.append(" left outer join menCsa.consignataria consignataria");

            corpoBuilder.append(" left outer join consignataria.correspondenteSet correspondente");
        }

        corpoBuilder.append(" WHERE 1=1 ");

        if ((menCodigo != null) && !menCodigo.isEmpty()) {
            corpoBuilder.append(" and mensagem.menCodigo ").append(criaClausulaNomeada("menCodigo", menCodigo));
        }

        if (menExibeCse != null) {
            corpoBuilder.append(" and mensagem.menExibeCse = 'S'");
        }

        if (menExibeCsa != null) {
            corpoBuilder.append(" and mensagem.menExibeCsa = 'S'");
            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" or consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            } else if (!"".equals(menExibeCsa)) {
                corpoBuilder.append(" and (").append(criaClausulaNomeada("consignataria.csaNome", "csaNome", menExibeCsa));
                corpoBuilder.append(" or ").append(criaClausulaNomeada("consignataria.csaIdentificador", "menExibeCsa", menExibeCsa));
                corpoBuilder.append(" or consignataria.csaCodigo is null)");
            }
        }

        if (menExibeCor != null) {
            corpoBuilder.append(" and mensagem.menExibeCor = 'S'");

            if (!"".equals(menExibeCor)) {
                corpoBuilder.append(" and (").append(criaClausulaNomeada("consignataria.csaNome", "csaNome", menExibeCor));
                corpoBuilder.append(" or ").append(criaClausulaNomeada("correspondente.corNome", "corNome", menExibeCor));
                corpoBuilder.append(" or ").append(criaClausulaNomeada("consignataria.csaIdentificador", "menExibeCor", menExibeCor));
                corpoBuilder.append(" or consignataria.csaCodigo is null)");
            }
        }

        if (menExibeOrg != null) {
            corpoBuilder.append(" and mensagem.menExibeOrg = 'S'");
        }

        if (menExibeSer != null) {
            corpoBuilder.append(" and mensagem.menExibeSer = 'S'");
        }

        if (menExibeSup != null) {
            corpoBuilder.append(" and mensagem.menExibeSup = 'S'");
        }

        if (!TextHelper.isNull(menTitulo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("mensagem.menTitulo", "menTitulo", menTitulo));
        }

        if ((exibeMenPublica != null) && exibeMenPublica) {
            corpoBuilder.append(" and mensagem.menPublica = 'S'");
        }

        if (!count) {
            corpoBuilder.append(" order by mensagem.menSequencia desc, mensagem.menData desc");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if ((menCodigo != null) && !menCodigo.isEmpty()) {
            defineValorClausulaNomeada("menCodigo", menCodigo, query);
        }

        if (!TextHelper.isNull(menExibeCsa) && TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaNome", menExibeCsa, query);
            defineValorClausulaNomeada("menExibeCsa", menExibeCsa, query);
        }

        if (!TextHelper.isNull(menExibeCsa) && !TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(menExibeCor)) {
            defineValorClausulaNomeada("csaNome", menExibeCor, query);
            defineValorClausulaNomeada("corNome", menExibeCor, query);
            defineValorClausulaNomeada("menExibeCor", menExibeCor, query);
        }

        if (!TextHelper.isNull(menTitulo)) {
            defineValorClausulaNomeada("menTitulo", menTitulo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if (!TextHelper.isNull(menExibeCsa)) {
            return new String[] {
                                  Columns.MEN_CODIGO,
                                  Columns.MEN_USU_CODIGO,
                                  Columns.USU_LOGIN,
                                  Columns.MEN_TITULO,
                                  Columns.MEN_TEXTO,
                                  Columns.MEN_DATA,
                                  Columns.MEN_SEQUENCIA,
                                  Columns.MEN_EXIBE_CSE,
                                  Columns.MEN_EXIBE_CSA,
                                  Columns.MEN_EXIBE_ORG,
                                  Columns.MEN_EXIBE_COR,
                                  Columns.MEN_EXIBE_SER,
                                  Columns.MEN_EXIBE_SUP,
                                  Columns.MEN_HTML,
                                  Columns.MEN_EXIGE_LEITURA,
                                  Columns.MEN_PERMITE_LER_DEPOIS,
                                  Columns.MEN_NOTIFICAR_CSE_LEITURA,
                                  Columns.MEN_BLOQ_CSA_SEM_LEITURA,
                                  Columns.MEN_FUN_CODIGO,
                                  Columns.MEN_LIDA_INDIVIDUALMENTE,
                                  Columns.MEN_PUBLICA,
                                  Columns.MEN_PUSH_NOTIFICATION_SER,
                                  Columns.CSA_CODIGO,
                                  "CSA_NOME"
            };

        } else {
            return new String[] {
                                  Columns.MEN_CODIGO,
                                  Columns.MEN_USU_CODIGO,
                                  Columns.USU_LOGIN,
                                  Columns.MEN_TITULO,
                                  Columns.MEN_TEXTO,
                                  Columns.MEN_DATA,
                                  Columns.MEN_SEQUENCIA,
                                  Columns.MEN_EXIBE_CSE,
                                  Columns.MEN_EXIBE_CSA,
                                  Columns.MEN_EXIBE_ORG,
                                  Columns.MEN_EXIBE_COR,
                                  Columns.MEN_EXIBE_SER,
                                  Columns.MEN_EXIBE_SUP,
                                  Columns.MEN_HTML,
                                  Columns.MEN_EXIGE_LEITURA,
                                  Columns.MEN_PERMITE_LER_DEPOIS,
                                  Columns.MEN_NOTIFICAR_CSE_LEITURA,
                                  Columns.MEN_BLOQ_CSA_SEM_LEITURA,
                                  Columns.MEN_FUN_CODIGO,
                                  Columns.MEN_LIDA_INDIVIDUALMENTE,
                                  Columns.MEN_PUBLICA,
                                  Columns.MEN_PUSH_NOTIFICATION_SER
            };

        }
    }
}
