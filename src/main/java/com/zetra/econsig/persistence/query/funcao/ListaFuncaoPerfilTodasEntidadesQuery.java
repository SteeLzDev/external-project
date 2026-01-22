package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncaoPerfilTodasEntidadesQuery</p>
 * <p>Description: Retorna todos os perfis personalizados que possuem a função passada por parâmetro.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncaoPerfilTodasEntidadesQuery extends HNativeQuery {
    public String funCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(funCodigo)) {
            throw new HQueryException ("mensagem.erro.informe.permissao", (AcessoSistema) null);
        }

        StringBuilder where = new StringBuilder();
        where.append(" where fun_codigo ").append(criaClausulaNomeada("funCodigo", funCodigo)).append(" ");

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select cse_codigo AS CODIGO_ENTIDADE, 'CSE' AS TIPO_ENTIDADE, '1' AS PAPEL, usu_codigo AS USU_CODIGO, fun_codigo AS FUN_CODIGO");
        corpoBuilder.append(" from tb_funcao_perfil_cse ").append(where).append(" union");
        corpoBuilder.append(" select org_codigo AS CODIGO_ENTIDADE, 'ORG' AS TIPO_ENTIDADE, '3' AS PAPEL, usu_codigo AS USU_CODIGO, fun_codigo AS FUN_CODIGO");
        corpoBuilder.append(" from tb_funcao_perfil_org ").append(where).append(" union");
        corpoBuilder.append(" select csa_codigo AS CODIGO_ENTIDADE, 'CSA' AS TIPO_ENTIDADE, '2' AS PAPEL, usu_codigo AS USU_CODIGO, fun_codigo AS FUN_CODIGO");
        corpoBuilder.append(" from tb_funcao_perfil_csa ").append(where).append(" union");
        corpoBuilder.append(" select cor_codigo AS CODIGO_ENTIDADE, 'COR' AS TIPO_ENTIDADE, '4' AS PAPEL, usu_codigo AS USU_CODIGO, fun_codigo AS FUN_CODIGO");
        corpoBuilder.append(" from tb_funcao_perfil_cor ").append(where).append(" union");
        corpoBuilder.append(" select cse_codigo AS CODIGO_ENTIDADE, 'SUP' AS TIPO_ENTIDADE, '7' AS PAPEL, usu_codigo AS USU_CODIGO, fun_codigo AS FUN_CODIGO");
        corpoBuilder.append(" from tb_funcao_perfil_sup ").append(where);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("funCodigo", funCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CODIGO_ENTIDADE",
                "TIPO_ENTIDADE",
                "PAPEL",
                Columns.USU_CODIGO,
                Columns.FUN_CODIGO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
