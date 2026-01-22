package com.zetra.econsig.persistence.query.mensagem;

import org.apache.http.util.TextUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaModeloEmailQuery</p>
 * <p>Description: Listagem de modelo de email.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaModeloEmailQuery extends HQuery {

    public String memCodigo;

    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder("select mem.memCodigo, mem.memTitulo, mem.memTexto from ModeloEmail mem where 1=1 ");

        String _memCodigo = memCodigo;
        if (responsavel != null && responsavel.getCodigoEntidade() != null) {
            if (_memCodigo.charAt(3) != '.') {
                _memCodigo = responsavel.getTipoEntidade().toLowerCase() + "." + (TextHelper.isNull(_memCodigo) ? "" : _memCodigo);
            }
        }
        if (!TextUtils.isEmpty(_memCodigo)) {
            corpo.append(" and mem.memCodigo = :memCodigo");
        }
        corpo.append(" order by mem.memCodigo");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextUtils.isEmpty(_memCodigo)) {
            query.setParameter("memCodigo", _memCodigo);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MEM_CODIGO,
                Columns.MEM_TITULO,
                Columns.MEM_TEXTO
        };
    }
}
