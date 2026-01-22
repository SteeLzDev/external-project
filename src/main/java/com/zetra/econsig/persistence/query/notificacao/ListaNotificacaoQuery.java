package com.zetra.econsig.persistence.query.notificacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaNotificacaoQuery</p>
 * <p>Description: Retorna uma lista das notficações mais recentes do agrupamento função/destinatário,
 *                 para os filtros dados.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaNotificacaoQuery extends HQuery {

    public String tnoCodigo;
    public String funCodigo;
    public boolean ndiAtivo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "max(ndi.ndiCodigo), " +
                       "max(ndi.ndiTexto), " +
                       "ndi.funcao.funCodigo, " +
                       "ndi.usuarioDestinatario.usuCodigo, " +
                       "ndi.tipoNotificacao.tnoCodigo, " +
                       "max(ndi.ndiData) ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from NotificacaoDispositivo ndi ");
        corpoBuilder.append(" where ndi.ndiDataEnvio is NULL");
        corpoBuilder.append(" and ndi.funcao.funCodigo ").append(criaClausulaNomeada("funCodigo", funCodigo));
        corpoBuilder.append(" and ndi.tipoNotificacao.tnoCodigo ").append(criaClausulaNomeada("tnoCodigo", tnoCodigo));

        if (ndiAtivo) {
            corpoBuilder.append(" and ndi.ndiAtivo = ").append(CodedValues.NDI_ATIVO).append(" ");
        }

        corpoBuilder.append(" and ndi.ndiData = (");
        corpoBuilder.append(" select max(ndi2.ndiData) ");
        corpoBuilder.append(" from NotificacaoDispositivo ndi2 ");
        corpoBuilder.append(" where ndi.usuarioDestinatario.usuCodigo = ndi2.usuarioDestinatario.usuCodigo ");
        corpoBuilder.append("   and ndi.tipoNotificacao.tnoCodigo = ndi2.tipoNotificacao.tnoCodigo ");
        corpoBuilder.append("   and ndi.funcao.funCodigo = ndi2.funcao.funCodigo ");
        corpoBuilder.append(")");

        corpoBuilder.append(" and exists (");
        corpoBuilder.append(" select 1 ");
        corpoBuilder.append(" from UsuarioChaveDispositivo ucd ");
        corpoBuilder.append(" where ucd.usuario.usuCodigo = ndi.usuarioDestinatario.usuCodigo ");
        corpoBuilder.append("   and ucd.ucdToken is not null ");
        corpoBuilder.append(")");

        corpoBuilder.append(" group by ndi.usuarioDestinatario.usuCodigo, ndi.tipoNotificacao.tnoCodigo, ndi.funcao.funCodigo ");
        corpoBuilder.append(" order by ndi.ndiData");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tnoCodigo", tnoCodigo, query);
        defineValorClausulaNomeada("funCodigo", funCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.NDI_CODIGO,
                Columns.NDI_TEXTO,
                Columns.NDI_FUN_CODIGO,
                Columns.NDI_USU_CODIGO_DESTINATARIO,
                Columns.NDI_TNO_CODIGO,
                Columns.NDI_DATA
        };
    }
}
