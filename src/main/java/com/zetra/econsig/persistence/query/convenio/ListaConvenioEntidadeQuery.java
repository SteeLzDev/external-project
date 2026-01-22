package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioEntidadeQuery</p>
 * <p>Description: Listagem de serviços distintos de uma entidade baseados nos convênios
 * ativos que a mesma possui no sistema. Caso a entidade seja CSA/COR, retorna também
 * o código de verba associado a este serviço. Caso a operação seja RENEGOCIAR/COMPRAR
 * lista apenas os serviços aptos a estas operações</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioEntidadeQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;
    public String operacao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;

        if ((tipoEntidade != null) &&
                (tipoEntidade.equalsIgnoreCase("CSA") || tipoEntidade.equalsIgnoreCase("COR"))) {
            corpo =
                "select distinct " +
                "svc.svcCodigo, " +
                "svc.svcIdentificador, " +
                "svc.svcDescricao, " +
                "nse.nseCodigo, " +
                "nse.nseDescricao, " +
                "cnv.cnvCodigo, " +
                "cnv.cnvCodVerba, " +
                "cnv.orgCodigo ";
        } else {
            corpo =
                "select distinct " +
                "svc.svcCodigo, " +
                "svc.svcIdentificador, " +
                "svc.svcDescricao, " +
                "nse.nseCodigo, " +
                "nse.nseDescricao ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join svc.naturezaServico nse ");

        // Se entidade de correspondente, faz join com tabela de convênio por correspondente
        if ((tipoEntidade != null) && tipoEntidade.equalsIgnoreCase("COR")) {
            corpoBuilder.append("inner join cnv.correspondenteConvenioSet crc ");
        }

        // Se operação de renegociação ou compra, seleciona apenas os serviços aptos
        if ((operacao != null) &&
                (operacao.equalsIgnoreCase("renegociar") || operacao.equalsIgnoreCase("comprar"))) {
            corpoBuilder.append("left outer join svc.paramSvcConsignanteSet pse WITH ");
            corpoBuilder.append("pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PERMITE_RENEGOCIACAO).append("'");
        }

        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append("");

        if ((operacao != null) && operacao.equalsIgnoreCase("leilao")) {
            // Somente serviços de natureza empréstimo e com prazos e taxas ativas e vigentes
            corpoBuilder.append(" and nse.nseCodigo = '").append(CodedValues.NSE_EMPRESTIMO).append("'");
            corpoBuilder.append(" and exists (");
            corpoBuilder.append("   select 1 from svc.prazoSet prz ");
            corpoBuilder.append("   inner join prz.prazoConsignatariaSet prc ");
            corpoBuilder.append("   inner join prc.coeficienteAtivoSet cft ");
            corpoBuilder.append("   where prc.consignataria.csaCodigo = cnv.consignataria.csaCodigo ");
            corpoBuilder.append("     and coalesce(prz.przAtivo, 1) = 1 ");
            corpoBuilder.append("     and coalesce(prc.przCsaAtivo, 1) = 1 ");
            corpoBuilder.append("     and (cft.cftDataFimVig is null or cft.cftDataFimVig > current_date()) ");
            corpoBuilder.append(")");
        }

        if (ParamSist.paramEquals(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // Se tem compartilhamento de cadastro de taxas, retorna somente os serviços que não possuem
            // relacionamento de compartilhamento como destino do relacionamento
            if ((operacao != null) && (operacao.equalsIgnoreCase("reservar") || operacao.equalsIgnoreCase("renegociar") || operacao.equalsIgnoreCase("comprar"))) {
                corpoBuilder.append(" and not exists (");
                corpoBuilder.append("   select 1 from svc.relacionamentoServicoByDestinoSet rsv ");
                corpoBuilder.append("   where rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS).append("' ");
                corpoBuilder.append("     and rsv.svcCodigoOrigem <> rsv.svcCodigoDestino ");
                corpoBuilder.append(")");
            }
        }

        if (tipoEntidade != null) {
            if (tipoEntidade.equalsIgnoreCase("ORG")) {
                corpoBuilder.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else if (tipoEntidade.equalsIgnoreCase("CSA")) {
                corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else if (tipoEntidade.equalsIgnoreCase("COR")) {
                corpoBuilder.append(" and crc.correspondente.corCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
                corpoBuilder.append(" and crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            }
        }

        if ((operacao != null) &&
                (operacao.equalsIgnoreCase("renegociar") || operacao.equalsIgnoreCase("comprar"))) {
            corpoBuilder.append(" and (pse.pseVlr IS NULL OR pse.pseVlr = '1')");
        }

        corpoBuilder.append(" order by svc.svcDescricao");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Define o parâmetro com o código da entidade (desnecessário no caso de CSE)
        if ((tipoEntidade != null) &&
                (tipoEntidade.equalsIgnoreCase("ORG")|| tipoEntidade.equalsIgnoreCase("CSA") || tipoEntidade.equalsIgnoreCase("COR"))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if ((tipoEntidade != null) &&
                (tipoEntidade.equalsIgnoreCase("CSA") || tipoEntidade.equalsIgnoreCase("COR"))) {
            return new String[] {
                    Columns.SVC_CODIGO,
                    Columns.SVC_IDENTIFICADOR,
                    Columns.SVC_DESCRICAO,
                    Columns.NSE_CODIGO,
                    Columns.NSE_DESCRICAO,
                    Columns.CNV_CODIGO,
                    Columns.CNV_COD_VERBA,
                    Columns.ORG_CODIGO
            };
        } else {
            return new String[] {
                    Columns.SVC_CODIGO,
                    Columns.SVC_IDENTIFICADOR,
                    Columns.SVC_DESCRICAO,
                    Columns.NSE_CODIGO,
                    Columns.NSE_DESCRICAO
            };
        }
    }
}
