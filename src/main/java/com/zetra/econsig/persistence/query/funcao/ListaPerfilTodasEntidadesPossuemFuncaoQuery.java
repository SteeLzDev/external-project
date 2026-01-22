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
 * <p>Description: Retorna todos os perfis de todas as entidades que possuem a função passada por parâmetro.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPerfilTodasEntidadesPossuemFuncaoQuery extends HNativeQuery {
    public String funCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(funCodigo)) {
            throw new HQueryException ("mensagem.erro.informe.permissao", (AcessoSistema) null);
        }

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select fpe.fun_codigo, fpe.per_codigo,");

        corpoBuilder.append(" case");
        corpoBuilder.append(" when pce.cse_codigo is not null then '1'");
        corpoBuilder.append(" when por.org_codigo is not null then '3'");
        corpoBuilder.append(" when pca.csa_codigo is not null then '2'");
        corpoBuilder.append(" when pco.cor_codigo is not null then '4'");
        corpoBuilder.append(" when psu.cse_codigo is not null then '7'");
        corpoBuilder.append(" end AS PAPEL,");

        corpoBuilder.append(" case");
        corpoBuilder.append(" when pce.cse_codigo is not null then pce.cse_codigo");
        corpoBuilder.append(" when por.org_codigo is not null then por.org_codigo");
        corpoBuilder.append(" when pca.csa_codigo is not null then pca.csa_codigo");
        corpoBuilder.append(" when pco.cor_codigo is not null then pco.cor_codigo");
        corpoBuilder.append(" when psu.cse_codigo is not null then psu.cse_codigo");
        corpoBuilder.append(" end AS CODIGO_ENTIDADE,");

        corpoBuilder.append(" case");
        corpoBuilder.append(" when pce.cse_codigo is not null then 'CSE'");
        corpoBuilder.append(" when por.org_codigo is not null then 'ORG'");
        corpoBuilder.append(" when pca.csa_codigo is not null then 'CSA'");
        corpoBuilder.append(" when pco.cor_codigo is not null then 'COR'");
        corpoBuilder.append(" when psu.cse_codigo is not null then 'SUP'");
        corpoBuilder.append(" end AS TIPO_ENTIDADE");

        corpoBuilder.append(" from tb_funcao_perfil fpe");
        corpoBuilder.append(" left outer join tb_perfil_cse pce on (pce.per_codigo = fpe.per_codigo)");
        corpoBuilder.append(" left outer join tb_perfil_org por on (por.per_codigo = fpe.per_codigo)");
        corpoBuilder.append(" left outer join tb_perfil_csa pca on (pca.per_codigo = fpe.per_codigo)");
        corpoBuilder.append(" left outer join tb_perfil_cor pco on (pco.per_codigo = fpe.per_codigo)");
        corpoBuilder.append(" left outer join tb_perfil_sup psu on (psu.per_codigo = fpe.per_codigo)");
        corpoBuilder.append(" where fun_codigo ").append(criaClausulaNomeada("funCodigo", funCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("funCodigo", funCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.PER_CODIGO,
                "PAPEL",
                "CODIGO_ENTIDADE",
                "TIPO_ENTIDADE"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
