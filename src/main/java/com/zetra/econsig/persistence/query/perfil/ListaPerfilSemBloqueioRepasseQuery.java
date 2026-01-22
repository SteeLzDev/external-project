package com.zetra.econsig.persistence.query.perfil;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPerfilSemBloqueioRepasseQuery</p>
 * <p>Description:Lista os perfis que podem ser utilizados para criação de novo usuário, não retornado
 * aqueles perfis que possuem bloqueio de repasse de alguma função do papel de origem
 * para o papel de destino. </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPerfilSemBloqueioRepasseQuery extends HQuery {
    public String tipoEntidade;
    public String codigoEntidade;
    public String usuCodigoEdt;
    public String papCodigoOrigem;
    public String papCodigoDestino;
    public AcessoSistema responsavel;

    /**
     * Lista os perfis que podem ser utilizados para criação de novo usuário, não retornado
     * aqueles perfis que possuem bloqueio de repasse de alguma função do papel de origem
     * para o papel de destino.
     */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tabelaEntidade = null;
        String campoEntidade = null;
        String campoStatus = null;

        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE)) { // CONSIGNANTE
            tabelaEntidade = "per.perfilCseSet pce";
            campoEntidade = "pce.cseCodigo";
            campoStatus = "pce.pceAtivo";

        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSA)) { // CONSIGNATARIA
            tabelaEntidade = "per.perfilCsaSet pca";
            campoEntidade = "pca.csaCodigo";
            campoStatus = "pca.pcaAtivo";

        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) { // ORGAO
            tabelaEntidade = "per.perfilOrgSet por";
            campoEntidade = "por.orgCodigo";
            campoStatus = "por.porAtivo";

        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_COR)) { // CORRESPONDENTE
            tabelaEntidade = "per.perfilCorSet pco";
            campoEntidade = "pco.corCodigo";
            campoStatus = "pco.pcoAtivo";

        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) { // SUPORTE
            tabelaEntidade = "per.perfilSupSet psu";
            campoEntidade = "psu.cseCodigo";
            campoStatus = "psu.psuAtivo";

        } else {
            throw new HQueryException("mensagem.erro.sistema.tipo.entidade.invalido", (AcessoSistema) null);
        }

        StringBuilder corpoBuilder = new StringBuilder("select per.perCodigo, per.perDescricao");
        corpoBuilder.append(" from Perfil per");
        corpoBuilder.append(" inner join ").append(tabelaEntidade);

        corpoBuilder.append(" where ").append(campoStatus).append(" = 1");

        if (!TextHelper.isNull(codigoEntidade)) {
            corpoBuilder.append(" AND ").append(campoEntidade).append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        }

        corpoBuilder.append(" AND (NOT EXISTS (SELECT 1 FROM ").append("Funcao fun");
        corpoBuilder.append(" INNER JOIN fun.funcaoPerfilSet per1");
        corpoBuilder.append(" INNER JOIN fun.bloqueioRepasseFuncaoSet brf");
        corpoBuilder.append(" WHERE per.perCodigo = per1.perCodigo AND ");
        corpoBuilder.append("brf.papelDestino.papCodigo ").append(criaClausulaNomeada("papCodigoDestino", papCodigoDestino)).append(" AND ");
        corpoBuilder.append("brf.papelOrigem.papCodigo ").append(criaClausulaNomeada("papCodigoOrigem", papCodigoOrigem)).append(")");

        if (!TextHelper.isNull(usuCodigoEdt)) {
            corpoBuilder.append(" OR EXISTS (SELECT 1 FROM ").append("PerfilUsuario upe");
            corpoBuilder.append(" WHERE per.perCodigo = upe.perfil.perCodigo").append(" AND ");
            corpoBuilder.append("upe.usuCodigo").append(criaClausulaNomeada("usuCodigoEdt", usuCodigoEdt)).append(")");
        }
        corpoBuilder.append(")");

        if (responsavel == null || !responsavel.isSup()) {
            corpoBuilder.append(" and COALESCE(per.perVisivel, 'S') <> 'N'");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(codigoEntidade)) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        if (!TextHelper.isNull(papCodigoDestino)) {
            defineValorClausulaNomeada("papCodigoDestino", papCodigoDestino, query);
        }

        if (!TextHelper.isNull(papCodigoOrigem)) {
            defineValorClausulaNomeada("papCodigoOrigem", papCodigoOrigem, query);
        }

        if (!TextHelper.isNull(usuCodigoEdt)) {
            defineValorClausulaNomeada("usuCodigoEdt", usuCodigoEdt, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.PER_CODIGO, Columns.PER_DESCRICAO };
    }
}
