package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusConvenioQuery</p>
 * <p>Description: lista serviços e seus status por entidade</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaStatusConvenioQuery extends HQuery {
    public boolean count = false;

    public String csaCodigo;
    public String orgCodigo;
    public String scvCodigo;
    public String svcIdentificador;
    public String svcDescricao;
    // Verifica se o convênio possui contratos e ignora o status do convênio.
    public boolean verificaConvenioPossuiContratos = Boolean.FALSE;
    public String temAde;
    public boolean filtroCampoSvcRelatorioCsa = Boolean.FALSE;


    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(csaCodigo) && TextHelper.isNull(orgCodigo)) {
            throw new HQueryException("mensagem.erroInternoSistema", (AcessoSistema) null);
        }

        String corpo = "";

        if (!count) {
            if (!TextHelper.isNull(csaCodigo) && !TextHelper.isNull(orgCodigo)) {
                corpo = "select servico.svcCodigo, " +
                        "servico.svcIdentificador, " +
                        "servico.svcDescricao, " +
                        "coalesce(convenio.statusConvenio.scvCodigo,'2') as status, " +
                        "case when exists (select 1 from AutDesconto ade1 inner join ade1.verbaConvenio vco join vco.convenio cnv "
                        + " where cnv.statusConvenio.scvCodigo = '1' and cnv.consignataria.csaCodigo " + criaClausulaNomeada("csaCodigo", csaCodigo)
                        + " and servico.svcCodigo = cnv.svcCodigo "
                        + " and ade1.sadCodigo not in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','") + "')) then 'S' else 'N' end as temADE ";
            } else if (!TextHelper.isNull(csaCodigo)) {
                corpo = "select servico.svcCodigo, " +
                        "servico.svcIdentificador, " +
                        "servico.svcDescricao, " +
                        "case when count(convenio.orgao.orgCodigo) = 0 then '2' " +
                        "when count(convenio.orgao.orgCodigo) = (select count(*) from Orgao orgao) then '1' " +
                        "else '0' end as status, " +
                        "case when exists (select 1 from AutDesconto ade1 inner join ade1.verbaConvenio vco join vco.convenio cnv "
                        + " where cnv.statusConvenio.scvCodigo = '1' and cnv.consignataria.csaCodigo " + criaClausulaNomeada("csaCodigo", csaCodigo)
                        + " and servico.svcCodigo = cnv.svcCodigo "
                        + " and ade1.sadCodigo not in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','") + "')) then 'S' else 'N' end as temADE ";
            } else if (!TextHelper.isNull(orgCodigo)) {
                corpo = "select servico.svcCodigo, " +
                        "servico.svcIdentificador, " +
                        "servico.svcDescricao, " +
                        "case when count(convenio.consignataria.csaCodigo) = 0 then '2' " +
                        "when count(convenio.consignataria.csaCodigo) = (select count(*) from Consignataria csa) then '1' " +
                        "else '0' end as status ";
            }
        } else {
            if (!TextHelper.isNull(csaCodigo) && !TextHelper.isNull(orgCodigo)) {
                corpo = "select coalesce(convenio.statusConvenio.scvCodigo, '2') as status ";
            } else if (!TextHelper.isNull(csaCodigo)) {
                corpo = "select case when count(convenio.orgao.orgCodigo) = 0 then '2' " +
                        "when count(convenio.orgao.orgCodigo) = (select count(*) from Orgao orgao) then '1' " +
                        "else '0' end as status ";
            } else if (!TextHelper.isNull(orgCodigo)) {
                corpo = "select case when count(convenio.consignataria.csaCodigo) = 0 then '2' " +
                        "when count(convenio.consignataria.csaCodigo) = (select count(*) from Consignataria csa) then '1' " +
                        "else '0' end as status ";
            }
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (!TextHelper.isNull(csaCodigo) && !TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" from Servico servico ");
            corpoBuilder.append(" left outer join servico.convenioSet convenio ");
            corpoBuilder.append(" with (convenio.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            corpoBuilder.append(" and convenio.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(")");

        } else if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" from Servico servico ");
            corpoBuilder.append(" left outer join servico.convenioSet convenio ");
            corpoBuilder.append(" with (");
            if (!verificaConvenioPossuiContratos) {
                corpoBuilder.append(" convenio.statusConvenio.scvCodigo = '1' and");
            }
            corpoBuilder.append(" convenio.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(")");

        } else if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" from Servico servico ");
            corpoBuilder.append(" left outer join servico.convenioSet convenio ");
            corpoBuilder.append(" with (");
            if (!verificaConvenioPossuiContratos) {
                corpoBuilder.append(" convenio.statusConvenio.scvCodigo = '1' and");
            }
            corpoBuilder.append(" convenio.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo)).append(")");
        }

        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("servico.svcIdentificador", "svcIdentificador", svcIdentificador));
        }

        if (!TextHelper.isNull(svcDescricao)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("servico.svcDescricao", "svcDescricao", svcDescricao));
        }

        if (verificaConvenioPossuiContratos) {
            corpoBuilder.append(" and exists (select 1 from AutDesconto ade ");
            corpoBuilder.append(" inner join ade.verbaConvenio vco2");
            corpoBuilder.append(" inner join vco2.convenio cnv2");
            corpoBuilder.append(" where cnv2.cnvCodigo = convenio.cnvCodigo) ");
        }

        if (!TextHelper.isNull(temAde)) {
            corpoBuilder.append(" and case when exists (select 1 from AutDesconto ade1 inner join ade1.verbaConvenio vco join vco.convenio cnv ");
            corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '1' and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            corpoBuilder.append(" and servico.svcCodigo = cnv.svcCodigo ");
            corpoBuilder.append(" and ade1.sadCodigo not in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')) then 'S' else 'N' end = :temAde ");
        }

        if(filtroCampoSvcRelatorioCsa && !TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and convenio.consignataria.csaCodigo IS NOT NULL ");

        }

        if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" group by servico.svcCodigo, servico.svcIdentificador, servico.svcDescricao");
        }

        if (!TextHelper.isNull(scvCodigo)) {
            if (!TextHelper.isNull(csaCodigo) && !TextHelper.isNull(orgCodigo)) {
                corpoBuilder.append(" and (coalesce(convenio.statusConvenio.scvCodigo, '2')) ");
            } else if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" having (case when count(convenio.orgao.orgCodigo) = 0 then '2' ");
                corpoBuilder.append(" when count(convenio.orgao.orgCodigo) = (select count(*) from Orgao orgao) then '1' ");
                corpoBuilder.append(" else '0' end) ");
            } else if (!TextHelper.isNull(orgCodigo)) {
                corpoBuilder.append(" having (case when count(convenio.consignataria.csaCodigo) = 0 then '2' ");
                corpoBuilder.append(" when count(convenio.consignataria.csaCodigo) = (select count(*) from Consignataria csa) then '1' ");
                corpoBuilder.append(" else '0' end) ");
            }
            corpoBuilder.append(criaClausulaNomeada("scvCodigo", scvCodigo));
        }

        if (!count) {
            corpoBuilder.append(" order by 4,3");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(scvCodigo)) {
            defineValorClausulaNomeada("scvCodigo", scvCodigo, query);
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }

        if (!TextHelper.isNull(svcDescricao)) {
            defineValorClausulaNomeada("svcDescricao", svcDescricao, query);
        }

        if(!TextHelper.isNull(temAde)) {
            defineValorClausulaNomeada("temAde", temAde, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if (!count && TextHelper.isNull(csaCodigo) && !TextHelper.isNull(orgCodigo)) {
            return new String[] {
                    Columns.SVC_CODIGO,
                    Columns.SVC_IDENTIFICADOR,
                    Columns.SVC_DESCRICAO,
                    "STATUS"
            };
        } else if (!count && !TextHelper.isNull(csaCodigo)) {
            return new String[] {
                    Columns.SVC_CODIGO,
                    Columns.SVC_IDENTIFICADOR,
                    Columns.SVC_DESCRICAO,
                    "STATUS",
                    "TEMADE"
            };
        } else {
            return new String[] {
                    "STATUS"
            };
        }
    }
}
