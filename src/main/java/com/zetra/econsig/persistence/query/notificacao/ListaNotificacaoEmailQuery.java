package com.zetra.econsig.persistence.query.notificacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaNotificacaoQuery</p>
 * <p>Description: Retorna uma lista das notficações para os filtros dados.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaNotificacaoEmailQuery extends HQuery {

    private String nemCodigo;
    private String tnoCodigo;
    private String funCodigo;
    private String usuCodigo;
    private Object nemDataEnvio;

    public ListaNotificacaoEmailQuery(TransferObject criterio) {
        super();
        if (criterio != null) {
            if (!TextHelper.isNull(criterio.getAttribute(Columns.NEM_CODIGO))) {
                nemCodigo = (String) criterio.getAttribute(Columns.NEM_CODIGO);
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.NEM_TNO_CODIGO))) {
                tnoCodigo = (String) criterio.getAttribute(Columns.NEM_TNO_CODIGO);
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.NEM_FUN_CODIGO))) {
                funCodigo = (String) criterio.getAttribute(Columns.NEM_FUN_CODIGO);
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.NEM_USU_CODIGO))) {
                funCodigo = (String) criterio.getAttribute(Columns.NEM_USU_CODIGO);
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.NEM_DATA_ENVIO))) {
                nemDataEnvio = criterio.getAttribute(Columns.NEM_DATA_ENVIO);
            }
        }
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "nem.nemCodigo, " +
                       "nem.funcao.funCodigo, " +
                       "nem.usuario.usuCodigo, " +
                       "nem.tipoNotificacao.tnoCodigo, " +
                       "nem.nemTitulo, " +
                       "nem.nemTexto, " +
                       "nem.nemData, " +
                       "nem.nemDataEnvio, " +
                       "text_to_string(nem.nemDestinatario) ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from NotificacaoEmail nem ");
        corpoBuilder.append(" where 1 = 1");

        if (!TextHelper.isNull(nemCodigo)) {
            corpoBuilder.append(" and nem.nemCodigo ").append(criaClausulaNomeada("nemCodigo", nemCodigo));
        }

        if (!TextHelper.isNull(tnoCodigo)) {
            corpoBuilder.append(" and nem.tipoNotificacao.tnoCodigo ").append(criaClausulaNomeada("tnoCodigo", tnoCodigo));
        }

        if (!TextHelper.isNull(funCodigo)) {
            corpoBuilder.append(" and nem.funcao.funCodigo ").append(criaClausulaNomeada("funCodigo", funCodigo));
        }

        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" and nem.usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }

        if (!TextHelper.isNull(nemDataEnvio) && nemDataEnvio.equals(CodedValues.IS_NULL_KEY)) {
            corpoBuilder.append(" and nem.nemDataEnvio IS NULL ");
        } else if (!TextHelper.isNull(nemDataEnvio) && nemDataEnvio.equals(CodedValues.IS_NOT_NULL_KEY)) {
            corpoBuilder.append(" and nem.nemDataEnvio IS NOT NULL ");
        } else if (!TextHelper.isNull(nemDataEnvio)) {
            corpoBuilder.append(" and nem.nemDataEnvio ").append(criaClausulaNomeada("nemDataEnvio", nemDataEnvio));
        }

        corpoBuilder.append(" order by nem.tipoNotificacao.tnoCodigo, text_to_string(nem.nemDestinatario) asc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(nemCodigo)) {
            defineValorClausulaNomeada("nemCodigo", nemCodigo, query);
        }

        if (!TextHelper.isNull(tnoCodigo)) {
            defineValorClausulaNomeada("tnoCodigo", tnoCodigo, query);
        }

        if(!TextHelper.isNull(funCodigo)) {
            defineValorClausulaNomeada("funCodigo", funCodigo, query);
        }

        if(!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(nemDataEnvio) && !nemDataEnvio.equals(CodedValues.IS_NULL_KEY) && !nemDataEnvio.equals(CodedValues.IS_NOT_NULL_KEY)) {
            corpoBuilder.append(" and nem.nemDataEnvio ").append(criaClausulaNomeada("nemDataEnvio", nemDataEnvio));
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.NEM_CODIGO,
                Columns.NEM_FUN_CODIGO,
                Columns.NEM_USU_CODIGO,
                Columns.NEM_TNO_CODIGO,
                Columns.NEM_TITULO,
                Columns.NEM_TEXTO,
                Columns.NEM_DATA,
                Columns.NEM_DATA_ENVIO,
                Columns.NEM_DESTINATARIO
        };
    }

}
