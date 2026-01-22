package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemConvenioOutroSvcTransfQuery</p>
 * <p>Description: Retorna os dados para a transferência de contratos,
 * entre serviços de mesma natureza, relacionados pela natureza
 * TNT_PERMITE_TRANSFERENCIA_ENTRE_SERVICOS.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConvenioOutroSvcTransfQuery extends HNativeQuery {

    // Código do órgão e registro servidor do novo servidor
    public String orgCodigo;
    public String rseCodigo;
    // Código do contrato a ser transferido
    public String adeCodigo;
    // Se TRUE, muda ordenação para dar prioridade a cnv/svc sem bloqueios
    public boolean bloqTransfSerBloqCnvSvc;

    @Override
    public void setCriterios(TransferObject criterio) {
        orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
        rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
        adeCodigo = (String) criterio.getAttribute(Columns.ADE_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        // Dados necessários para a rotina de transferência
        corpoBuilder.append("select ");
        corpoBuilder.append(" ade.ade_codigo as ADE_CODIGO,");
        corpoBuilder.append(" ade.ade_numero as ADE_NUMERO,");
        corpoBuilder.append(" cnv.csa_codigo as CSA_CODIGO,");
        corpoBuilder.append(" cnv.svc_codigo as SVC_CODIGO,");
        corpoBuilder.append(" vco.vco_codigo as VCO_CODIGO,");
        corpoBuilder.append(" vco2.vco_codigo as VCO_CODIGO_NOVO,");
        corpoBuilder.append(" cnv2.cnv_cod_verba as VERBA_NOVO,");

        // Dados necessários para ordenação
        corpoBuilder.append(" cnv2.scv_codigo,");
        corpoBuilder.append(" svc2.svc_identificador");

        // Inicia pelos dados do contrato a ser transferido
        corpoBuilder.append(" from tb_aut_desconto ade");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
        corpoBuilder.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");
        corpoBuilder.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo)");

        // Localiza verba convênio de serviços que possuam a mesma natureza
        corpoBuilder.append(" left outer join tb_servico svc2 on (svc2.nse_codigo = svc.nse_codigo)");
        corpoBuilder.append(" left outer join tb_convenio cnv2");
        corpoBuilder.append(" on (cnv2.csa_codigo = cnv.csa_codigo");
        corpoBuilder.append(" and cnv2.svc_codigo = svc2.svc_codigo");
        corpoBuilder.append(" and cnv2.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(")");
        corpoBuilder.append(" left outer join tb_verba_convenio vco2 on (cnv2.cnv_codigo = vco2.cnv_codigo)");
        corpoBuilder.append(" left outer join tb_aut_desconto ade2 on (vco2.vco_codigo = ade2.vco_codigo");
        corpoBuilder.append(" and ade2.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and ade2.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");
        corpoBuilder.append(" )");

        // Faz join com bloqueio de convênio/serviço/natureza por servidor para alterar ordenação
        if (bloqTransfSerBloqCnvSvc) {
            corpoBuilder.append(" left outer join tb_param_convenio_registro_ser pcr on (pcr.cnv_codigo = cnv2.cnv_codigo");
            corpoBuilder.append(" and pcr.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
            corpoBuilder.append(" and pcr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("'");
            corpoBuilder.append(" )");
            corpoBuilder.append(" left outer join tb_param_servico_registro_ser psr on (psr.svc_codigo = svc2.svc_codigo");
            corpoBuilder.append(" and psr.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
            corpoBuilder.append(" and psr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO).append("'");
            corpoBuilder.append(" )");
            corpoBuilder.append(" left outer join tb_natureza_servico nse on (svc2.nse_codigo = nse.nse_codigo)");
            corpoBuilder.append(" left outer join tb_param_nse_registro_ser pnr on (pnr.nse_codigo = nse.nse_codigo");
            corpoBuilder.append(" and pnr.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
            corpoBuilder.append(" and pnr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO).append("'");
            corpoBuilder.append(" )");
        }

        // Cláusula obrigatória do contrato sendo transferido
        corpoBuilder.append(" where ade.ade_codigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        // É o mesmo serviço, ou é outro serviço de mesma natureza que possui relacionamento
        // que libera transferência entre eles.
        corpoBuilder.append(" and (svc.svc_codigo = svc2.svc_codigo");
        corpoBuilder.append(" or exists (select 1 from tb_relacionamento_servico rsv ");
        corpoBuilder.append(" where rsv.svc_codigo_origem = svc.svc_codigo");
        corpoBuilder.append(" and rsv.svc_codigo_destino = svc2.svc_codigo");
        corpoBuilder.append(" and rsv.tnt_codigo = '").append(CodedValues.TNT_PERMITE_TRANSFERENCIA_ENTRE_SERVICOS).append("')");
        corpoBuilder.append(" )");

        // Realiza agrupamento já que o left em ade2 pode retornar múltiplos contratos
        corpoBuilder.append(" group by ade.ade_codigo, ade.ade_numero, cnv.csa_codigo, cnv.svc_codigo, vco.vco_codigo, vco2.vco_codigo, cnv2.scv_codigo, svc2.svc_identificador, cnv2.cnv_cod_verba");

        // Se permite transferência para outro serviço de mesma natureza, ordenada os possíveis candidatos
        // de forma que o primeiro convênio ativo, com a menor quantidade de contratos ativos, e que possua
        // o contrato mais próximo do término seja utilizado
        corpoBuilder.append(" order by");
        corpoBuilder.append(" case cnv2.scv_codigo");
        corpoBuilder.append("   when '").append(CodedValues.SCV_ATIVO).append("' then 1");
        corpoBuilder.append("   when '").append(CodedValues.SCV_INATIVO).append("' then 2");
        corpoBuilder.append("   else 9");
        corpoBuilder.append(" end asc,");

        // Se bloqueia transferência para convênio/serviço/natureza com bloqueio no servidor, muda a ordenação
        // para dar prioridade aos convênios/serviços/natureza que possuem a maior quantidade de contratos
        // permitidas cadastrada (caso não exista, será 999).
        if (bloqTransfSerBloqCnvSvc) {
            corpoBuilder.append(" max(coalesce(to_numeric_ne(pcr.pcr_vlr), 999)) + max(coalesce(to_numeric_ne(psr.psr_vlr), 999))  + max(coalesce(to_numeric_ne(pnr.pnr_vlr), 999)) desc,");
        }

        corpoBuilder.append(" count(ade2.ade_codigo) asc,");
        corpoBuilder.append(" min(ade2.ade_ano_mes_fim) asc,");
        corpoBuilder.append(" svc2.svc_identificador asc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);

        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "ADE_CODIGO",
                "ADE_NUMERO",
                "CSA_CODIGO",
                "SVC_CODIGO",
                "VCO_CODIGO",
                "VCO_CODIGO_NOVO",
                "VERBA_NOVO"
        };
    }
}
