package com.zetra.econsig.persistence.query.seguranca;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarOperacoesLiberacaoMargemParaBloqueioQuery</p>
 * <p>Description: Listar dados das operações de liberação de margem para realizar notificações e/ou bloqueios de segurança dos envolvidos</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarOperacoesLiberacaoMargemParaBloqueioQuery extends HQuery {

    public String usuCodigo;
    public String csaCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select olm.olmCodigo ");
        corpoBuilder.append(", usu.usuCodigo ");
        corpoBuilder.append(", usu.usuNome ");
        corpoBuilder.append(", usu.usuLogin ");
        corpoBuilder.append(", usu.statusLogin.stuCodigo ");
        corpoBuilder.append(", ser.serNome ");
        corpoBuilder.append(", ser.serCpf ");
        corpoBuilder.append(", rse.rseCodigo ");
        corpoBuilder.append(", rse.rseMatricula ");
        corpoBuilder.append(", rse.statusRegistroServidor.srsCodigo ");
        if (responsavel.isCsa()) {
            corpoBuilder.append(", csa.csaNome ");
            corpoBuilder.append(", '" + AcessoSistema.ENTIDADE_CSA + "' as tipoEntidade ");
        } else if (responsavel.isCor()) {
            corpoBuilder.append(", cor.corNome ");
            corpoBuilder.append(", '" + AcessoSistema.ENTIDADE_COR + "' as tipoEntidade ");
        } else if (responsavel.isOrg()) {
            corpoBuilder.append(", org.orgNome ");
            corpoBuilder.append(", '" + AcessoSistema.ENTIDADE_ORG + "' as tipoEntidade ");
        } else {
            corpoBuilder.append(", '' as nomeEntidade ");
            corpoBuilder.append(", '' as tipoEntidade ");
        }
        corpoBuilder.append("from OperacaoLiberaMargem olm ");
        corpoBuilder.append("inner join olm.usuario usu ");
        corpoBuilder.append("inner join olm.registroServidor rse ");
        corpoBuilder.append("inner join rse.servidor ser ");

        if (responsavel.isCsa()) {
            corpoBuilder.append("inner join usu.usuarioCsaSet usuarioCsa ");
            corpoBuilder.append("inner join usuarioCsa.consignataria csa ");
        } else if (responsavel.isCor()) {
            corpoBuilder.append("inner join usu.usuarioCorSet usuarioCor ");
            corpoBuilder.append("inner join usuarioCor.correspondente cor ");
        } else if (responsavel.isOrg()) {
            corpoBuilder.append("inner join usu.usuarioOrgSet usuarioOrg ");
            corpoBuilder.append("inner join usuarioOrg.orgao org ");
        }

        corpoBuilder.append("where olm.olmBloqueio = 'N' ");
        corpoBuilder.append("and olm.olmConfirmada = 'S' ");

        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and olm.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OLM_CODIGO,
                Columns.USU_CODIGO,
                Columns.USU_NOME,
                Columns.USU_LOGIN,
                Columns.STU_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SRS_CODIGO,
                "NOME_ENTIDADE",
                "TIPO_ENTIDADE"
        };
    }

}
