package com.zetra.econsig.persistence.query.funcao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarOperacoesSensiviesQuery</p>
 * <p>Description: Lista os operações na fila de autorização de acordo com papel</p>
 * <p>Copyright: Copyright (c) 2002-2003</p>
 * <p>Company: ZetraSoft</p>
  * $Author: igor.lucas $
 * $Revision: 26246 $
 * $Date: 2019-02-14 09:27:49 -0200 (qui, 14 fev 2019) $
 */
public class ListarOperacoesSensiviesQuery extends HQuery {

    public String entidadeCodigo;
    public AcessoSistema responsavel;

    public boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (!count) {
            corpoBuilder.append("SELECT ");
            corpoBuilder.append("usu.usuNome, ");
            corpoBuilder.append("usu.usuLogin, ");
            corpoBuilder.append("onc.oncIpAcesso, ");
            corpoBuilder.append("onc.oncData, ");
            corpoBuilder.append("fun.funCodigo, ");
            corpoBuilder.append("fun.funDescricao, ");
            corpoBuilder.append("onc.oncCodigo, ");
            corpoBuilder.append("CASE "
                    + "        WHEN onc.rseCodigo IS NOT NULL THEN CONCAT(rse.rseMatricula, ' - ', ser.serNome)"
                    + "        ELSE ''"
                    + "    END AS SERVIDOR ");
        } else {
            corpoBuilder.append("select count(*)");
        }

        corpoBuilder.append(" FROM OperacaoNaoConfirmada onc");
        corpoBuilder.append(" INNER JOIN onc.acessoRecurso acr");
        corpoBuilder.append(" INNER JOIN acr.funcao fun");
        corpoBuilder.append(" INNER JOIN onc.usuario usu");
        corpoBuilder.append(" LEFT JOIN onc.registroServidor rse");
        corpoBuilder.append(" LEFT JOIN rse.servidor ser");

        List<String> usuCodigoAprovador = new ArrayList<>();
        usuCodigoAprovador.add(CodedValues.NOT_EQUAL_KEY);
        usuCodigoAprovador.add(responsavel.getUsuCodigo());

        corpoBuilder.append(" WHERE usu.usuCodigo ").append(criaClausulaNomeada("usuCodigoAprovador",usuCodigoAprovador));

        String tipoEntidade = responsavel.getTipoEntidade();
        List<String> entidadeCodigos = null;

        if (tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP)) {
            corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioSup usup WHERE usup.usuCodigo = usu.usuCodigo))");

            entidadeCodigo = null;
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
            corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioCse ucse WHERE ucse.usuCodigo = usu.usuCodigo)");
            corpoBuilder.append(" OR EXISTS (SELECT 1 FROM UsuarioOrg uorg WHERE uorg.usuCodigo = usu.usuCodigo))");

            entidadeCodigo = null;
        } else  if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioOrg uorg WHERE uorg.usuCodigo = usu.usuCodigo AND uorg.orgCodigo ");
            corpoBuilder.append(criaClausulaNomeada("entidadeCodigo",entidadeCodigo)).append(")");

            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                entidadeCodigos = new ArrayList<>();
                entidadeCodigos.add(CodedValues.NOT_EQUAL_KEY);
                entidadeCodigos.add(entidadeCodigo);

                corpoBuilder.append(" OR EXISTS (SELECT 1 FROM UsuarioOrg uorgOutro INNER JOIN uorgOutro.orgao org INNER JOIN org.estabelecimento est WHERE uorgOutro.usuCodigo = usu.usuCodigo AND est.estCodigo = :codigoEntidadePai");
                corpoBuilder.append(" AND uorgOutro.orgCodigo ").append(criaClausulaNomeada("entidadeCodigos", entidadeCodigos));
                corpoBuilder.append("))");
            } else {
                corpoBuilder.append(")");
            }
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
            corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioCsa ucsa WHERE ucsa.usuCodigo = usu.usuCodigo  AND ucsa.csaCodigo ");
            corpoBuilder.append(criaClausulaNomeada("entidadeCodigo",entidadeCodigo)).append(")");
            corpoBuilder.append(" OR EXISTS (SELECT 1 FROM UsuarioCor ucor INNER JOIN ucor.correspondente cor INNER JOIN cor.consignataria csa ");
            corpoBuilder.append("WHERE ucor.usuCodigo = usu.usuCodigo AND csa.csaCodigo ").append(criaClausulaNomeada("entidadeCodigo",entidadeCodigo)).append("))");
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR)) {
            corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioCor ucor WHERE ucor.usuCodigo = usu.usuCodigo  AND ucor.corCodigo ");
            corpoBuilder.append(criaClausulaNomeada("entidadeCodigo",entidadeCodigo)).append(")");

            if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                entidadeCodigos = new ArrayList<>();
                entidadeCodigos.add(CodedValues.NOT_EQUAL_KEY);
                entidadeCodigos.add(entidadeCodigo);

                corpoBuilder.append(" OR EXISTS (SELECT 1 FROM UsuarioCor ucorOutro INNER JOIN ucorOutro.correspondente cor INNER JOIN cor.consignataria csa WHERE ucorOutro.usuCodigo = usu.usuCodigo AND csa.csaCodigo = :codigoEntidadePai");
                corpoBuilder.append(" AND ucorOutro.corCodigo ").append(criaClausulaNomeada("entidadeCodigos",entidadeCodigos));
                corpoBuilder.append("))");
            } else {
                corpoBuilder.append(")");
            }
        }

        corpoBuilder.append(" ORDER BY onc.oncData");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("usuCodigoAprovador", usuCodigoAprovador, query);

        if (!TextHelper.isNull(entidadeCodigo)) {
            defineValorClausulaNomeada("entidadeCodigo", entidadeCodigo, query);
        }

        if (entidadeCodigos != null) {
            defineValorClausulaNomeada("entidadeCodigos", entidadeCodigos, query);
        }
        if ((tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG) && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) ||
            (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR) && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) {
            defineValorClausulaNomeada("codigoEntidadePai", responsavel.getCodigoEntidadePai(), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_NOME,
                Columns.USU_LOGIN,
                Columns.ONC_IP_ACESSO,
                Columns.ONC_DATA,
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.ONC_CODIGO,
                "SERVIDOR"
        };
    }

}
