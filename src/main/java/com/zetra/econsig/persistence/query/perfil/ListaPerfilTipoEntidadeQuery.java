package com.zetra.econsig.persistence.query.perfil;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPerfilTipoEntidadeQuery</p>
 * <p>Description:Lista os perfis da entidade selecionada </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPerfilTipoEntidadeQuery extends HQuery {
    public String tipoEntidade;
    public String codigoEntidade;
    public Short pceAtivo;
    public Short pcaAtivo;
    public Short porAtivo;
    public Short pcoAtivo;
    public Short psuAtivo;
    public String perDescricao;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String corpo = "select per.perCodigo, per.perDescricao, per.papel.papCodigo, per.perEntAltera, per.perDataExpiracao, per.perIpAcesso, per.perDdnsAcesso";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipoEntidade)) { // CONSIGNANTE
            corpoBuilder.append(", pce.pceAtivo as STATUS");
            corpoBuilder.append(" from Perfil per ");
            corpoBuilder.append(" inner join per.perfilCseSet pce ");
            corpoBuilder.append(" where 1 = 1 ");

            if (!TextHelper.isNull(codigoEntidade)) {
                corpoBuilder.append(" and pce.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }

        } else if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipoEntidade)) { // CONSIGNATARIA
            corpoBuilder.append(", pca.pcaAtivo as STATUS");
            corpoBuilder.append(" from Perfil per ");
            corpoBuilder.append(" inner join per.perfilCsaSet pca ");
            corpoBuilder.append(" where 1 = 1 ");

            if (!TextHelper.isNull(codigoEntidade)) {
                corpoBuilder.append(" and pca.csaCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }

        } else if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade)) { // ORGAO
            corpoBuilder.append(", por.porAtivo as STATUS");
            corpoBuilder.append(" from Perfil per ");
            corpoBuilder.append(" inner join per.perfilOrgSet por ");
            corpoBuilder.append(" where 1 = 1 ");

            if (!TextHelper.isNull(codigoEntidade)) {
                corpoBuilder.append(" and por.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }

        } else if (AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipoEntidade)) { // CORRESPONDENTE
            corpoBuilder.append(", pco.pcoAtivo as STATUS");
            corpoBuilder.append(" from Perfil per ");
            corpoBuilder.append(" inner join per.perfilCorSet pco ");
            corpoBuilder.append(" where 1 = 1 ");

            if (!TextHelper.isNull(codigoEntidade)) {
                corpoBuilder.append(" and pco.corCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }

        } else if (AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipoEntidade)) { // SUPORTE
            corpoBuilder.append(", psu.psuAtivo as STATUS");
            corpoBuilder.append(" from Perfil per ");
            corpoBuilder.append(" inner join per.perfilSupSet psu ");
            corpoBuilder.append(" where 1 = 1 ");

            if (!TextHelper.isNull(codigoEntidade)) {
                corpoBuilder.append(" and psu.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }

        } else {
            throw new HQueryException("mensagem.erro.sistema.tipo.entidade.invalido", (AcessoSistema) null);
        }

        if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(pceAtivo)) {
            corpoBuilder.append(" AND pce.pceAtivo ").append(criaClausulaNomeada("pceAtivo", pceAtivo));
        }

        if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(pcaAtivo)) {
            corpoBuilder.append(" AND pca.pcaAtivo ").append(criaClausulaNomeada("pcaAtivo", pcaAtivo));
        }

        if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(porAtivo)) {
            corpoBuilder.append(" AND por.porAtivo ").append(criaClausulaNomeada("porAtivo", porAtivo));
        }

        if (AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(pcoAtivo)) {
            corpoBuilder.append(" AND pco.pcoAtivo ").append(criaClausulaNomeada("pcoAtivo", pcoAtivo));
        }

        if (AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(psuAtivo)) {
            corpoBuilder.append(" AND psu.psuAtivo ").append(criaClausulaNomeada("psuAtivo", psuAtivo));
        }

        if (!TextHelper.isNull(perDescricao)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("per.perDescricao", "perDescricao", perDescricao));
        }

        if ((responsavel == null) || !responsavel.isSup()) {
            corpoBuilder.append(" and COALESCE(per.perVisivel, 'S') <> 'N'");
        }

        corpoBuilder.append(" ORDER BY per.perDescricao ASC ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(codigoEntidade)) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(pceAtivo)) {
            defineValorClausulaNomeada("pceAtivo", pceAtivo, query);
        }

        if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(pcaAtivo)) {
            defineValorClausulaNomeada("pcaAtivo", pcaAtivo, query);
        }

        if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(porAtivo)) {
            defineValorClausulaNomeada("porAtivo", porAtivo, query);
        }

        if (AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(pcoAtivo)) {
            defineValorClausulaNomeada("pcoAtivo", pcoAtivo, query);
        }

        if (AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipoEntidade) && !TextHelper.isNull(psuAtivo)) {
            defineValorClausulaNomeada("psuAtivo", psuAtivo, query);
        }

        if (!TextHelper.isNull(perDescricao)) {
            defineValorClausulaNomeada("perDescricao", perDescricao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PER_CODIGO,
                Columns.PER_DESCRICAO,
                Columns.PER_PAP_CODIGO,
                Columns.PER_ENT_ALTERA,
                Columns.PER_DATA_EXPIRACAO,
                Columns.PER_IP_ACESSO,
                Columns.PER_DDNS_ACESSO,
                "STATUS"
        };
    }
}
