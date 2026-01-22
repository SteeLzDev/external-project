package com.zetra.econsig.persistence.query.usuario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemUsuarioCsaCorQuery</p>
 * <p>Description: Retornar usuarios de CSA/COR que possuem um CPF informado ou possuem CPF igual a de algum servidor nao excluido.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUsuarioCsaCorQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;
    public String usuCodigo;
    public String usuCpf;
    public boolean validaServidor = false;
    public boolean mesmaEntidade = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT usu.usuCodigo, usu.usuCpf ");
        corpoBuilder.append("FROM Usuario usu ");
        corpoBuilder.append("LEFT OUTER JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT OUTER JOIN usu.usuarioCorSet usuarioCor ");

        corpoBuilder.append("WHERE 1=1 ");
        corpoBuilder.append("AND (usuarioCsa.usuCodigo IS NOT NULL ");
        corpoBuilder.append("  OR usuarioCor.usuCodigo IS NOT NULL ");
        corpoBuilder.append(")" );

        if (!TextHelper.isNull(usuCpf)) {
            corpoBuilder.append(" AND usu.usuCpf ").append(criaClausulaNomeada("usuCpf", usuCpf));
        }
        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" AND usu.usuCodigo <> :usuCodigo");
        }
        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade)) {
            final String operador = mesmaEntidade ? "=" : "<>";
            if (tipoEntidade.equals("CSA")) {
                corpoBuilder.append(" AND (usuarioCsa.csaCodigo IS NULL OR usuarioCsa.csaCodigo ").append(operador).append(" :codigoEntidade)");
            } else if (tipoEntidade.equals("COR")) {
                corpoBuilder.append(" AND (usuarioCor.corCodigo IS NULL OR usuarioCor.corCodigo ").append(operador).append(" :codigoEntidade)");
            }
        }

        if (validaServidor) {
            // CPF do usuario deve ser igual ao de algum servidor com matricula nao excluida
            corpoBuilder.append(" AND exists ( select 1 from Servidor ser ");
            corpoBuilder.append("              inner join ser.registroServidorSet rse ");
            corpoBuilder.append("              where ser.serCpf = usu.usuCpf ");
            corpoBuilder.append("              AND rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("'))");
        }

        List<Object> status = new ArrayList<>();
        status.add(CodedValues.STU_ATIVO);
        corpoBuilder.append(" AND usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(usuCpf)) {
            defineValorClausulaNomeada("usuCpf", usuCpf, query);
        }
        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }
        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade)) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        defineValorClausulaNomeada("status", status, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_CPF
        };
    }
}