package com.zetra.econsig.persistence.query.notificacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ListaNotificacoesInativasQuery</p>
 * <p>Description: Lista notificações não enviadas de um tipo dado a um usuário com dada anterior à dada pelo filtro.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaNotificacoesInativasQuery extends HQuery {

    public String ndiCodigo;
    public String tnoCodigo;
    public String funCodigo;
    public String usuCodigoDestinatario;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ndi.ndiCodigo ");
        corpoBuilder.append("from NotificacaoDispositivo ndi where 1=1 ");
        corpoBuilder.append(" and ndi.funcao.funCodigo ").append(criaClausulaNomeada("funCodigo", funCodigo));
        corpoBuilder.append(" and ndi.usuarioDestinatario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigoDestinatario));
        corpoBuilder.append(" and ndi.tipoNotificacao.tnoCodigo ").append(criaClausulaNomeada("tnoCodigo", tnoCodigo));
        corpoBuilder.append(" and ndi.ndiDataEnvio is NULL ");

        corpoBuilder.append(" and ndi.ndiData <= (");
        corpoBuilder.append(" select ndi2.ndiData ");
        corpoBuilder.append(" from NotificacaoDispositivo ndi2 where ndi2.ndiCodigo ").append(criaClausulaNomeada("ndiCodigo", ndiCodigo));
        corpoBuilder.append(" )");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("ndiCodigo", ndiCodigo, query);
        defineValorClausulaNomeada("tnoCodigo", tnoCodigo, query);
        defineValorClausulaNomeada("funCodigo", funCodigo, query);
        defineValorClausulaNomeada("usuCodigo", usuCodigoDestinatario, query);

        return query;
    }
}
