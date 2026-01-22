package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaEmailConsignatariasNotificacaoLeilaoQuery</p>
 * <p>Description: Lista os e-mails para notificação de novo leilão.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaEmailConsignatariasNotificacaoLeilaoQuery  extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT DISTINCT psc.pscVlr ");
        corpoBuilder.append("FROM ParamSvcConsignataria psc ");
        corpoBuilder.append("WHERE psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO).append("' ");
        corpoBuilder.append("AND NULLIF(TRIM(psc.pscVlr), '') IS NOT NULL ");

        // Parâmetro de CSA/SVC associado a convênios ativos de serviços de natureza empréstimo
        corpoBuilder.append("AND EXISTS (");
        corpoBuilder.append(" SELECT 1 FROM Convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" WHERE cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" AND svc.naturezaServico.nseCodigo = '").append(CodedValues.NSE_EMPRESTIMO).append("'");
        corpoBuilder.append(" AND psc.servico.svcCodigo = svc.svcCodigo");
        corpoBuilder.append(" AND psc.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(")");

        // Consignatárias que possuem usuários ou perfis com permissão de FUN_INFORMAR_PROPOSTAS_LEILAO
        // PerfilCsa
        corpoBuilder.append("AND (EXISTS (");
        corpoBuilder.append(" SELECT 1 FROM psc.consignataria csa");
        corpoBuilder.append(" INNER JOIN csa.perfilCsaSet pca");
        corpoBuilder.append(" INNER JOIN pca.perfil per");
        corpoBuilder.append(" INNER JOIN per.funcaoSet fun");
        corpoBuilder.append(" WHERE fun.funCodigo = '").append(CodedValues.FUN_INFORMAR_PROPOSTAS_LEILAO).append("'");
        corpoBuilder.append(" AND pca.pcaAtivo = ").append(CodedValues.STS_ATIVO).append("");
        corpoBuilder.append(") ");
        // PerfilCor
        corpoBuilder.append("OR EXISTS (");
        corpoBuilder.append(" SELECT 1 FROM psc.consignataria csa");
        corpoBuilder.append(" INNER JOIN csa.correspondenteSet cor");
        corpoBuilder.append(" INNER JOIN cor.perfilCorSet pco");
        corpoBuilder.append(" INNER JOIN pco.perfil per");
        corpoBuilder.append(" INNER JOIN per.funcaoSet fun");
        corpoBuilder.append(" WHERE fun.funCodigo = '").append(CodedValues.FUN_INFORMAR_PROPOSTAS_LEILAO).append("'");
        corpoBuilder.append(" AND pco.pcoAtivo = ").append(CodedValues.STS_ATIVO).append("");
        corpoBuilder.append(") ");
        // UsuarioCsa
        corpoBuilder.append("OR EXISTS (");
        corpoBuilder.append(" SELECT 1 FROM psc.consignataria csa");
        corpoBuilder.append(" INNER JOIN csa.usuarioCsaSet uca");
        corpoBuilder.append(" INNER JOIN uca.usuario usu");
        corpoBuilder.append(" INNER JOIN uca.funcaoSet fun");
        corpoBuilder.append(" WHERE fun.funCodigo = '").append(CodedValues.FUN_INFORMAR_PROPOSTAS_LEILAO).append("'");
        corpoBuilder.append(" AND usu.statusLogin.stuCodigo = '").append(CodedValues.STU_ATIVO).append("'");
        corpoBuilder.append(") ");
        // UsuarioCor
        corpoBuilder.append("OR EXISTS (");
        corpoBuilder.append(" SELECT 1 FROM psc.consignataria csa");
        corpoBuilder.append(" INNER JOIN csa.correspondenteSet cor");
        corpoBuilder.append(" INNER JOIN cor.usuarioCorSet uco");
        corpoBuilder.append(" INNER JOIN uco.usuario usu");
        corpoBuilder.append(" INNER JOIN uco.funcaoPerfilCorSet fun");
        corpoBuilder.append(" WHERE fun.funcao.funCodigo = '").append(CodedValues.FUN_INFORMAR_PROPOSTAS_LEILAO).append("'");
        corpoBuilder.append(" AND usu.statusLogin.stuCodigo = '").append(CodedValues.STU_ATIVO).append("'");
        corpoBuilder.append("))");

        return instanciarQuery(session, corpoBuilder.toString());
    }
}
