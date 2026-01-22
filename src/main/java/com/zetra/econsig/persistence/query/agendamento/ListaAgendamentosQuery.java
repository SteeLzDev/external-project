package com.zetra.econsig.persistence.query.agendamento;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAgendamentosQuery</p>
 * <p>Description: Listagem de agendamentos.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAgendamentosQuery extends HQuery {

    private final List<String> agdCodigos;
    private final List<String> sagCodigos;
    private final List<String> tagCodigos;
    private final String classe;
    private final String usuCodigo;
    private final String tipoEntidade;
    private final String codigoEntidade;
    private final String relCodigo;
    public boolean count;

    public ListaAgendamentosQuery(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, String classe,
            String tipoEntidade, String codigoEntidade, String usuCodigo, String relCodigo) {
        this.agdCodigos = agdCodigos;
        this.sagCodigos = sagCodigos;
        this.tagCodigos = tagCodigos;
        this.classe = classe;
        this.usuCodigo = usuCodigo;
        this.tipoEntidade = tipoEntidade;
        this.codigoEntidade = codigoEntidade;
        this.relCodigo = relCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select ");

        if (!count) {
            corpo.append("agd.agdCodigo, ");
            corpo.append("agd.agdDescricao, ");
            corpo.append("agd.agdDataCadastro, ");
            corpo.append("agd.agdDataPrevista, ");
            corpo.append("agd.agdJavaClassName, ");
            corpo.append("agd.statusAgendamento.sagCodigo, ");
            corpo.append("agd.statusAgendamento.sagDescricao, ");
            corpo.append("agd.tipoAgendamento.tagCodigo, ");
            corpo.append("agd.tipoAgendamento.tagDescricao, ");
            corpo.append("agd.usuario.usuCodigo, ");
            corpo.append("agd.usuario.usuLogin ");
        } else {
            corpo.append("count(*) as total ");
        }

        corpo.append("from Agendamento agd ");
        corpo.append("inner join agd.usuario usuario ");

        if (!TextHelper.isNull(tipoEntidade)) {
            if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
                corpo.append("inner join usuario.usuarioCseSet usuarioCse ");
                corpo.append("inner join usuarioCse.consignante consignante ");
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                corpo.append("inner join usuario.usuarioCsaSet usuarioCsa ");
                corpo.append("inner join usuarioCsa.consignataria consignataria ");
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR)) {
                corpo.append("inner join usuario.usuarioCorSet usuarioCor ");
                corpo.append("inner join usuarioCor.correspondente correspondente ");
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
                corpo.append("inner join usuario.usuarioOrgSet usuarioOrg ");
                corpo.append("inner join usuarioOrg.orgao orgao ");
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP)) {
                corpo.append("inner join usuario.usuarioSupSet usuarioSup ");
                corpo.append("inner join usuarioSup.consignante consignante ");
            }
        }

        corpo.append("where 1 = 1 ");

        if (!TextHelper.isNull(codigoEntidade)) {
            if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
                corpo.append(" and usuarioCse.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                corpo.append(" and usuarioCsa.csaCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR)) {
                corpo.append(" and usuarioCor.corCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
                corpo.append(" and usuarioOrg.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP)) {
                corpo.append(" and usuarioSup.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        if (agdCodigos != null && !agdCodigos.isEmpty()) {
            corpo.append(" and agd.agdCodigo ").append(criaClausulaNomeada("agdCodigos", agdCodigos));
        }

        if (sagCodigos != null && !sagCodigos.isEmpty()) {
            corpo.append(" and agd.statusAgendamento.sagCodigo ").append(criaClausulaNomeada("sagCodigos", sagCodigos));
        }

        if (tagCodigos != null && !tagCodigos.isEmpty()) {
            corpo.append(" and agd.tipoAgendamento.tagCodigo ").append(criaClausulaNomeada("tagCodigos", tagCodigos));
        }

        if (!TextHelper.isNull(classe)) {
            corpo.append(" and agd.agdJavaClassName ").append(criaClausulaNomeada("classe", classe));
        }

        if (!TextHelper.isNull(usuCodigo)) {
            corpo.append(" and agd.usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }

        if (!TextHelper.isNull(relCodigo)) {
            corpo.append(" and agd.relatorio.relCodigo ").append(criaClausulaNomeada("relCodigo", relCodigo));
        }

        if (!count) {
            corpo.append(" order by agd.agdDataCadastro desc, agd.agdDataPrevista desc ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(codigoEntidade)) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        if (agdCodigos != null && !agdCodigos.isEmpty()) {
            defineValorClausulaNomeada("agdCodigos", agdCodigos, query);
        }

        if (sagCodigos != null && !sagCodigos.isEmpty()) {
            defineValorClausulaNomeada("sagCodigos", sagCodigos, query);
        }

        if (tagCodigos != null && !tagCodigos.isEmpty()) {
            defineValorClausulaNomeada("tagCodigos", tagCodigos, query);
        }

        if (!TextHelper.isNull(classe)) {
            defineValorClausulaNomeada("classe", classe, query);
        }

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(relCodigo)) {
            defineValorClausulaNomeada("relCodigo", relCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.AGD_CODIGO,
                Columns.AGD_DESCRICAO,
                Columns.AGD_DATA_CADASTRO,
                Columns.AGD_DATA_PREVISTA,
                Columns.AGD_JAVA_CLASS_NAME,
                Columns.SAG_CODIGO,
                Columns.SAG_DESCRICAO,
                Columns.TAG_CODIGO,
                Columns.TAG_DESCRICAO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN
        };
    }
}
