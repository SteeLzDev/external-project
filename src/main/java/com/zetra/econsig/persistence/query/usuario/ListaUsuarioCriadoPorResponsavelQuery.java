package com.zetra.econsig.persistence.query.usuario;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuarioCriadoPorResponsavelQuery</p>
 * <p>Description: Lista os usuários que foram criados pelo(s) usuário(s) responsável(is) pela criação dos mesmos.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuarioCriadoPorResponsavelQuery  extends HQuery {

    public List<String> responsaveis = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tocCodigo = CodedValues.TOC_INCLUSAO_USUARIO;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append("case ");
        corpoBuilder.append("when responsavel.statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO).append("' then coalesce(nullif(concat(responsavel.usuTipoBloq, '(*)'), ''), responsavel.usuLogin) ");
        corpoBuilder.append("else responsavel.usuLogin end AS RESPONSAVEL, ");
        corpoBuilder.append("usuario.usuCodigo as USU_CODIGO, usuario.usuNome as USU_NOME, ");
        corpoBuilder.append("case ");
        corpoBuilder.append("when statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO).append("' then coalesce(nullif(concat(usuario.usuTipoBloq, '(*)'), ''), usuario.usuLogin) ");
        corpoBuilder.append("else usuario.usuLogin end AS USU_LOGIN, ");
        corpoBuilder.append("statusLogin.stuCodigo as STU_CODIGO, statusLogin.stuDescricao as STU_DESCRICAO, ");
        corpoBuilder.append("usuario.usuDataUltAcesso as USU_DATA_ULT_ACESSO, ous.ousData as OUS_DATA, ousExclusao.ousData as DATA_EXCLUSAO, ");
        corpoBuilder.append("case ");
        corpoBuilder.append("when consignante.cseCodigo is not null then consignante.cseNome ");
        corpoBuilder.append("when orgao.orgCodigo is not null then orgao.orgNome ");
        corpoBuilder.append("when consignataria.csaCodigo is not null then consignataria.csaNome ");
        corpoBuilder.append("when correspondente.corCodigo is not null then correspondente.corNome ");
        corpoBuilder.append("else '' end as ENTIDADE, ");
        corpoBuilder.append("case ");
        corpoBuilder.append("when consignante.cseCodigo is not null then 'CSE' ");
        corpoBuilder.append("when orgao.orgCodigo is not null then 'ORG' ");
        corpoBuilder.append("when consignataria.csaCodigo is not null then 'CSA' ");
        corpoBuilder.append("when correspondente.corCodigo is not null then 'COR' ");
        corpoBuilder.append("else 'SER' end as TIPO_ENTIDADE ");
        corpoBuilder.append("from OcorrenciaUsuario ous ");
        corpoBuilder.append("inner join ous.usuarioByUsuCodigo usuario ");
        corpoBuilder.append("inner join ous.usuarioByOusUsuCodigo responsavel ");
        corpoBuilder.append("inner join usuario.statusLogin statusLogin ");
        corpoBuilder.append("left outer join usuario.ocorrenciaUsuarioByUsuCodigoSet ousExclusao ");
        corpoBuilder.append("with ousExclusao.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_EXCLUSAO_USUARIO).append("' ");
        corpoBuilder.append("left outer join usuario.usuarioCseSet usuarioCse ");
        corpoBuilder.append("left outer join usuarioCse.consignante consignante ");
        corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("left outer join usuarioCsa.consignataria consignataria ");
        corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
        corpoBuilder.append("left outer join usuarioCor.correspondente correspondente ");
        corpoBuilder.append("left outer join usuario.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("left outer join usuarioOrg.orgao orgao ");
        corpoBuilder.append("left outer join usuario.usuarioSerSet usuarioSer ");
        corpoBuilder.append("left outer join usuarioSer.servidor servidor ");

        corpoBuilder.append(" where 1 = 1 ");
        corpoBuilder.append(" and ous.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        if (responsaveis != null && !responsaveis.isEmpty()) {
            corpoBuilder.append(" and responsavel.usuCodigo ").append(criaClausulaNomeada("responsaveis", responsaveis));
        }

        corpoBuilder.append(" order by responsavel.usuLogin, ");
        corpoBuilder.append("case ");
        corpoBuilder.append("when consignante.cseCodigo is not null then consignante.cseNome ");
        corpoBuilder.append("when orgao.orgCodigo is not null then orgao.orgNome ");
        corpoBuilder.append("when consignataria.csaCodigo is not null then consignataria.csaNome ");
        corpoBuilder.append("when correspondente.corCodigo is not null then correspondente.corNome ");
        corpoBuilder.append("else '' end, ");
        corpoBuilder.append("statusLogin.stuCodigo, ");
        corpoBuilder.append("usuario.usuLogin ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        if (responsaveis != null && !responsaveis.isEmpty()) {
            defineValorClausulaNomeada("responsaveis", responsaveis, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "RESPONSAVEL",
                Columns.USU_CODIGO,
                Columns.USU_NOME,
                Columns.USU_LOGIN,
                Columns.STU_CODIGO,
                Columns.STU_DESCRICAO,
                Columns.USU_DATA_ULT_ACESSO,
                Columns.OUS_DATA,
                "DATA_EXCLUSAO",
                "ENTIDADE",
                "TIPO_ENTIDADE"
        };
    }
}
