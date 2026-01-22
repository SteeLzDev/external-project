package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncoesAuditaveisPapelQuery</p>
 * <p>Description: Lista as funções permitidas ao responsável que podem ser auditadas para </p>
 *                 a entidade destino </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesAuditaveisPapelQuery extends HQuery {
    public String codigoEntidade;
    public String usuCodigoResponsavel;
    public String papCodigoDestino;
    public String papCodigoOrigem;
    public String perCodigoResponsavel;
    public String tipo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(tipo)) {
            throw new HQueryException("mensagem.erro.informe.tipo.entidade.recuperar.funcoes.auditaveis", (AcessoSistema) null);
        }

        String corpo =
                "select " +
                        "fun.funCodigo, " +
                        "fun.funDescricao, " +
                        "grf.grfCodigo ," +
                        "grf.grfDescricao, " +
                        "case when funAudit.funCodigo is not null then '1' " +
                        "else '0' end as CHECKED";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Funcao fun");
        corpoBuilder.append(" inner join fun.papelFuncaoSet pf1");
        corpoBuilder.append(" inner join fun.papelFuncaoSet pf2");
        corpoBuilder.append(" inner join fun.grupoFuncao grf");

        if (!TextHelper.isNull(perCodigoResponsavel)) {
            corpoBuilder.append(" inner join fun.funcaoPerfilSet per");
        } else {
            if (TextHelper.isNull(papCodigoOrigem)) {
                throw new HQueryException("mensagem.erro.informe.usu.papel.recuperar.funcoes.auditaveis", (AcessoSistema) null);
            }

            if (papCodigoOrigem.equals(CodedValues.PAP_CONSIGNANTE)) {
                corpoBuilder.append(" inner join fun.funcaoPerfilCseSet funcaoPerfilEntOrigem ");
            } else if (papCodigoOrigem.equals(CodedValues.PAP_CONSIGNATARIA)) {
                corpoBuilder.append(" inner join fun.funcaoPerfilCsaSet funcaoPerfilEntOrigem ");
            } else if (papCodigoOrigem.equals(CodedValues.PAP_CORRESPONDENTE)) {
                corpoBuilder.append(" inner join fun.funcaoPerfilCorSet funcaoPerfilEntOrigem ");
            } else if (papCodigoOrigem.equals(CodedValues.PAP_ORGAO)) {
                corpoBuilder.append(" inner join fun.funcaoPerfilOrgSet funcaoPerfilEntOrigem ");
            } else if (papCodigoOrigem.equals(CodedValues.PAP_SUPORTE)) {
                corpoBuilder.append(" inner join fun.funcaoPerfilSupSet funcaoPerfilEntOrigem ");
            }
        }

        if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) {
            corpoBuilder.append(" left outer join fun.funcaoAuditavelCseSet funAudit with (funAudit.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade)).append(")");
        } else if (tipo.equals(AcessoSistema.ENTIDADE_CSA)) {
            corpoBuilder.append(" left outer join fun.funcaoAuditavelCsaSet funAudit with (funAudit.csaCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade)).append(")");
        } else if (tipo.equals(AcessoSistema.ENTIDADE_COR)) {
            corpoBuilder.append(" left outer join fun.funcaoAuditavelCorSet funAudit with (funAudit.corCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade)).append(")");
        } else if (tipo.equals(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append(" left outer join fun.funcaoAuditavelOrgSet funAudit with (funAudit.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade)).append(")");
        }else if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
            corpoBuilder.append(" left outer join fun.funcaoAuditavelSupSet funAudit with (funAudit.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade)).append(")");
        }

        corpoBuilder.append(" where fun.funAuditavel = 'S' ");

        if (!TextHelper.isNull(papCodigoDestino)) {
            corpoBuilder.append(" AND pf1.papCodigo ").append(criaClausulaNomeada("papCodigoDestino", papCodigoDestino));
        }

        if (!TextHelper.isNull(papCodigoOrigem)) {
            corpoBuilder.append(" AND pf2.papCodigo ").append(criaClausulaNomeada("papCodigoOrigem", papCodigoOrigem));
        }

        if (!TextHelper.isNull(perCodigoResponsavel)) {
            corpoBuilder.append(" AND per.perCodigo ").append(criaClausulaNomeada("perCodigoResponsavel", perCodigoResponsavel));
        } else if (!TextHelper.isNull(usuCodigoResponsavel)) {
            corpoBuilder.append(" AND funcaoPerfilEntOrigem.usuCodigo ").append(criaClausulaNomeada("usuCodigoResponsavel", usuCodigoResponsavel));
        }

        corpoBuilder.append(" ORDER BY cast(grf.grfCodigo as int), fun.funDescricao");

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

        if (!TextHelper.isNull(perCodigoResponsavel)) {
            defineValorClausulaNomeada("perCodigoResponsavel", perCodigoResponsavel, query);
        } else if (!TextHelper.isNull(usuCodigoResponsavel)) {
            defineValorClausulaNomeada("usuCodigoResponsavel", usuCodigoResponsavel, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.FUN_GRF_CODIGO,
                Columns.GRF_DESCRICAO,
                "CHECKED"
        };
    }

}
