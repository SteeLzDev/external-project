package com.zetra.econsig.persistence.query.seguranca;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarUsuariosLiberacaoMargemParaBloqueioQuery</p>
 * <p>Description: Listar usuários de CSA e COR para realizar bloqueios de segurança devido ao limite excedido de operações de liberação de margem</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarUsuariosLiberacaoMargemParaBloqueioQuery extends HNativeQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select usu.usu_codigo ");
        corpoBuilder.append(", usu.usu_nome ");
        corpoBuilder.append(", usu.usu_login ");
        corpoBuilder.append("from tb_usuario usu ");
        corpoBuilder.append("inner join tb_usuario_csa usuarioCsa on (usu.usu_codigo = usuarioCsa.usu_codigo) ");
        corpoBuilder.append("where usuarioCsa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append("and usu.stu_codigo not in ('").append(TextHelper.joinWithEscapeSql(CodedValues.STU_CODIGOS_INATIVOS, "' , '")).append("')");

        corpoBuilder.append("union ");

        corpoBuilder.append("select usu.usu_codigo ");
        corpoBuilder.append(", usu.usu_nome ");
        corpoBuilder.append(", usu.usu_login ");
        corpoBuilder.append("from tb_usuario usu ");
        corpoBuilder.append("inner join tb_usuario_cor usuarioCor on (usu.usu_codigo = usuarioCor.usu_codigo) ");
        corpoBuilder.append("inner join tb_correspondente cor on (usuarioCor.cor_codigo = cor.cor_codigo) ");
        corpoBuilder.append("where cor.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append("and usu.stu_codigo not in ('").append(TextHelper.joinWithEscapeSql(CodedValues.STU_CODIGOS_INATIVOS, "' , '")).append("')");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_NOME,
                Columns.USU_LOGIN
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

}
