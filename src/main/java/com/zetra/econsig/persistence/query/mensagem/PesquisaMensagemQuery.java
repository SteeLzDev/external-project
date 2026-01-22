package com.zetra.econsig.persistence.query.mensagem;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PesquisaMensagemQuery</p>
 * <p>Description: Retorna mensagens a serem visualizadas na pg inicial</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PesquisaMensagemQuery extends HQuery {

    public boolean count = false;
    public AcessoSistema responsavel;
    public String menExigeLeitura;
    public Date menDataMinima;
    public boolean naoConfirmadas = false;
    public Integer qtdeMesExpirarMsg;
    public boolean menLida = false;

    private static final String MENSAGEM_FIELDS = "mensagem.menCodigo, " +
            "mensagem.usuario.usuCodigo, " +
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
            "mensagem.funcao.funCodigo, " +
            "mensagem.menLidaIndividualmente, " +
            "mensagem.menPushNotificationSer, " +
            "mensagemArq.arqCodigo";

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoQuery = new StringBuilder();

        if (count) {
            corpoQuery.append("SELECT COUNT(*)");
            corpoQuery.append(" FROM Mensagem mensagem");
        } else {
            corpoQuery.append("SELECT ").append(MENSAGEM_FIELDS);
            if(menLida){
                corpoQuery.append(", CASE WHEN EXISTS(");
                corpoQuery.append(" SELECT 1 FROM LeituraMensagemUsuario lmu");
                corpoQuery.append(" WHERE lmu.menCodigo = mensagem.menCodigo ");
                corpoQuery.append(" AND lmu.usuCodigo = :usuCodigo");
                corpoQuery.append(" ) THEN 'S' ELSE 'N' END AS lida");
            }
            corpoQuery.append(" FROM Mensagem mensagem");
            corpoQuery.append(" LEFT OUTER JOIN mensagem.arquivoMensagemSet mensagemArq");
        }
        corpoQuery.append(" WHERE 1=1");
        if (responsavel.isCse()) {
            corpoQuery.append(" AND mensagem.menExibeCse = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isOrg()) {
            corpoQuery.append(" AND mensagem.menExibeOrg = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isCsa()) {
            corpoQuery.append(" AND mensagem.menExibeCsa = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isCor()) {
            corpoQuery.append(" AND mensagem.menExibeCor = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isSer()) {
            corpoQuery.append(" AND mensagem.menExibeSer = '").append(CodedValues.TPC_SIM).append("'");
        } else if (responsavel.isSup()) {
            corpoQuery.append(" AND mensagem.menExibeSup = '").append(CodedValues.TPC_SIM).append("'");
        }

        if (responsavel.isCsaCor()) {
            corpoQuery.append(" AND (EXISTS (");
            corpoQuery.append("    SELECT 1 FROM mensagem.mensagemCsaSet menCsa");
            corpoQuery.append("    WHERE menCsa.csaCodigo = :csaCodigo");
            corpoQuery.append(" ) OR NOT EXISTS (SELECT 1 FROM mensagem.mensagemCsaSet menCsa)");
            corpoQuery.append(")");
        }

        if (!TextHelper.isNull(menExigeLeitura)) {
            corpoQuery.append(" AND mensagem.menExigeLeitura = '").append(CodedValues.TPC_SIM).append("'");
        }

        if (menDataMinima != null) {
            // Somente mensagens posteriores ao cadastro do usuario especificado
            corpoQuery.append(" AND mensagem.menData > :menDataMinima");
        }

        if (naoConfirmadas) {
            corpoQuery.append(" AND NOT EXISTS ( SELECT 1 FROM LeituraMensagemUsuario lmu ");
            corpoQuery.append(" WHERE lmu.menCodigo = mensagem.menCodigo ");
            corpoQuery.append(" AND lmu.usuCodigo = :usuCodigo ");
            corpoQuery.append(" ) ");

            // Verifica se existe alguma função associada à mensagem e se o usuário ou perfil relacionado ao usuário possui a função
            corpoQuery.append(" AND ( ");
            corpoQuery.append(" EXISTS ( ");
            corpoQuery.append(" SELECT 1 FROM FuncaoPerfilCsa fca ");
            corpoQuery.append(" WHERE fca.funCodigo = mensagem.funcao.funCodigo ");
            corpoQuery.append(" AND fca.usuCodigo = :usuCodigo ");
            corpoQuery.append(" ) ");
            corpoQuery.append(" OR EXISTS ( ");
            corpoQuery.append(" SELECT 1 ");
            corpoQuery.append(" FROM PerfilUsuario perfilUsu ");
            corpoQuery.append(" INNER JOIN perfilUsu.usuario usu ");
            corpoQuery.append(" INNER JOIN perfilUsu.perfil perfil ");
            corpoQuery.append(" INNER JOIN perfil.funcaoSet fun ");
            corpoQuery.append(" WHERE fun.funCodigo = mensagem.funcao.funCodigo ");
            corpoQuery.append(" AND usu.usuCodigo = :usuCodigo ");
            corpoQuery.append(" ) ");
            corpoQuery.append(" OR mensagem.funcao.funCodigo IS NULL ");
            corpoQuery.append(" ) ");
        }

        if (qtdeMesExpirarMsg != null && qtdeMesExpirarMsg > 0) {
            corpoQuery.append(" AND (month_diff(mensagem.menData, current_date()) <= :qtdeMesExpirarMsg OR mensagem.menBloqCsaSemLeitura = 'S')");
        }

        if (!count) {
            corpoQuery.append(" ORDER BY mensagem.menSequencia DESC, ");
            corpoQuery.append("mensagem.menData DESC");
        }

        Query<Object[]> query = instanciarQuery(session, corpoQuery.toString());

        if (menDataMinima != null) {
            defineValorClausulaNomeada("menDataMinima", menDataMinima, query);
        }

        if (qtdeMesExpirarMsg != null && qtdeMesExpirarMsg > 0) {
            defineValorClausulaNomeada("qtdeMesExpirarMsg", qtdeMesExpirarMsg, query);
        }

        if (naoConfirmadas || menLida) {
            defineValorClausulaNomeada("usuCodigo", responsavel.getUsuCodigo(), query);
        }

        if (responsavel.isCsaCor()) {
            defineValorClausulaNomeada("csaCodigo", responsavel.getCsaCodigo(), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if(menLida){
            return new String [] {
                    Columns.MEN_CODIGO,
                    Columns.MEN_USU_CODIGO,
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
                    Columns.MEN_PUSH_NOTIFICATION_SER,
                    Columns.ARQ_CODIGO,
                    "lida"
            };
        } else {
            return new String [] {
                    Columns.MEN_CODIGO,
                    Columns.MEN_USU_CODIGO,
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
                    Columns.MEN_PUSH_NOTIFICATION_SER,
                    Columns.ARQ_CODIGO
            };
        }
    }
}
