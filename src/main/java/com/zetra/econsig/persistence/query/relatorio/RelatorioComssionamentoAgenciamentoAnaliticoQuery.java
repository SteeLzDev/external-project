package com.zetra.econsig.persistence.query.relatorio;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioAgenciamentoAnalitico</p>
 * <p>Description: Classe de query para Relatorio de Comissionamento e Agenciamento Analítico </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author:$
 * $Revision$
 * $Date:$
 */

public class RelatorioComssionamentoAgenciamentoAnaliticoQuery extends ReportHQuery {

	private static final int QTDE_DIAS_INI_FIM_VIGENCIA_NOVO_CBE = 45;
	public String periodo;
	public String csaCogido;
	public List<String> orgaos;
	public Boolean agenciamento = false;
	public String percentualAgenciamento;
	public String nseCodigo;
	public String benCodigo;

	@Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute("periodo");
        csaCogido = (String) criterio.getAttribute("csaCogido");
        orgaos = (List<String>) criterio.getAttribute("orgao");
        agenciamento = (Boolean) criterio.getAttribute("agenciamento");
        percentualAgenciamento = (String) criterio.getAttribute("percentual");
        nseCodigo = (String) criterio.getAttribute("nseCodigo");
        benCodigo = (String) criterio.getAttribute("benCodigo");
    }

	@Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
		List<String> tntCodigos = CodedValues.TNT_BENEFICIO_MENSALIDADE;

		StringBuilder corpo = new StringBuilder();

		corpo.append("select ");
		corpo.append("cse.cseNome as CONTRATANTE, ");
		corpo.append("ben.benCodigoContrato as NUMERO_AUXILIAR, ");
		corpo.append("tib.tibDescricao as TIPO_CLIENTE, ");
		corpo.append("cbe.cbeNumero as CODIGO_CLIENTE, ");
		corpo.append("'' as CODIGO_CLIENTE_ANTIGO, "); // Não existe esse dado no módulo de benefícios
		corpo.append("bfc.bfcNome as NOME_CLIENTE, ");
		corpo.append("ben.benDescricao as PRODUTO, ");
		corpo.append("cbe.cbeDataInicioVigencia as DATA_VIGENCIA, ");
		corpo.append("cbe.cbeDataCancelamento as DATA_CANCELAMENTO_CLIENTE, ");
		corpo.append("bfc.bfcDataNascimento as DATA_NASC_CLIENTE, ");
		corpo.append("bfc.bfcDataNascimento as FAIXA_ETARIA, ");
		corpo.append("cbe.cbeValorTotal as VALOR_MODULO, ");
		corpo.append("round((cbe.cbeValorTotal / 100) * :percentualAgenciamento, 2) as VALOR_APURADO ");

		corpo.append("from ArquivoFaturamentoBen afb ");
		corpo.append("inner join afb.faturamentoBeneficio fat ");
		corpo.append("inner join afb.autDesconto ade ");
		corpo.append("inner join ade.registroServidor rse ");
		corpo.append("inner join ade.verbaConvenio vco ");
		corpo.append("inner join vco.convenio cnv ");
		corpo.append("inner join cnv.consignataria csa ");
		corpo.append("inner join cnv.orgao org ");
		corpo.append("inner join cnv.servico svc ");
		corpo.append("inner join ade.tipoLancamento tla ");
		corpo.append("inner join ade.contratoBeneficio cbe ");
		corpo.append("inner join cbe.beneficio ben ");
		corpo.append("inner join cbe.beneficiario bfc ");
		corpo.append("inner join bfc.tipoBeneficiario tib ");
		corpo.append("inner join org.estabelecimento est ");
		corpo.append("inner join est.consignante cse ");

		corpo.append("where fat.fatPeriodo ").append(criaClausulaNomeada("periodo", periodo));
		corpo.append("and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));

		if (!TextHelper.isNull(csaCogido)) {
			corpo.append("and csa.csaCodigo ").append(criaClausulaNomeada("csaCogido", csaCogido));
		}

		if (orgaos != null && !orgaos.isEmpty()) {
			corpo.append("and org.orgCodigo ").append(criaClausulaNomeada("orgao", orgaos));
		}

        if (!TextHelper.isNull(benCodigo)) {
            corpo.append("and ben.benCodigo ").append(criaClausulaNomeada("benCodigo", benCodigo));
        }

		corpo.append("and tla.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigos", tntCodigos));

        if (agenciamento) {

            corpo.append("and ade.adeAnoMesIni ").append(criaClausulaNomeada("periodo", periodo));

            corpo.append("and ( ");
            corpo.append(" not exists ( ");
            corpo.append("select 1  ");
            corpo.append("from rse.autDescontoSet ade1 ");
            corpo.append("inner join ade1.tipoLancamento tla1 ");
            corpo.append("inner join ade1.contratoBeneficio cbe1 ");
            corpo.append("inner join cbe1.beneficio ben1 ");
            corpo.append("inner join cbe1.beneficiario bfc1 ");
            corpo.append("where 1 = 1 ");
            corpo.append("and tla1.tipoNatureza.tntCodigo = tla.tipoNatureza.tntCodigo ");
            corpo.append("and ade1.adeCodigo <> ade.adeCodigo ");
            corpo.append("and bfc1.bfcCodigo = bfc.bfcCodigo ");
            corpo.append("and ben1.consignataria.csaCodigo = ben.consignataria.csaCodigo ");
            corpo.append("and ade1.adeAnoMesIni <> :periodo ");
            corpo.append(") ");

            corpo.append("or ");

            corpo.append("exists ( ");
            corpo.append("select  1 ");
            corpo.append("from rse.autDescontoSet ade1 ");
            corpo.append("inner join ade1.contratoBeneficio cbe1 ");
            corpo.append("inner join ade1.tipoLancamento tla1 ");
            corpo.append("inner join cbe1.beneficio ben1 ");
            corpo.append("inner join cbe1.beneficiario bfc1 ");
            corpo.append("where 1 = 1 ");
            corpo.append("and tla1.tipoNatureza.tntCodigo = tla.tipoNatureza.tntCodigo ");
            corpo.append("and ade1.adeCodigo <> ade.adeCodigo ");
            corpo.append("and bfc1.bfcCodigo = bfc.bfcCodigo ");
            corpo.append("and ben1.consignataria.csaCodigo = ben.consignataria.csaCodigo ");
            corpo.append("and ade1.adeAnoMesIni <> :periodo ");
            corpo.append("group by bfc1.bfcCodigo ");
            corpo.append("having min(date_diff(cbe.cbeDataInclusao, cbe1.cbeDataFimVigencia)) >=").append(QTDE_DIAS_INI_FIM_VIGENCIA_NOVO_CBE);
            corpo.append(") ");
            corpo.append(") ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

		defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
		defineValorClausulaNomeada("percentualAgenciamento", new BigDecimal(percentualAgenciamento), query);
		defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
		defineValorClausulaNomeada("tntCodigos", tntCodigos, query);

		if (!TextHelper.isNull(csaCogido)) {
			defineValorClausulaNomeada("csaCogido", csaCogido, query);
		}

		if (orgaos != null && !orgaos.isEmpty()) {
			defineValorClausulaNomeada("orgao", orgaos, query);
		}

        if (!TextHelper.isNull(benCodigo)) {
            defineValorClausulaNomeada("benCodigo", benCodigo, query);
        }

        return query;
	}

	@Override
    protected String[] getFields() {
        return new String[] {
        		"CONTRATANTE",
        		"NUMERO_AUXILIAR",
        		"TIPO_CLIENTE",
        		"CODIGO_CLIENTE",
        		"CODIGO_CLIENTE_ANTIGO",
        		"NOME_CLIENTE",
        		"PRODUTO",
        		"DATA_VIGENCIA",
        		"DATA_CANCELAMENTO_CLIENTE",
        		"DATA_NASC_CLIENTE",
        		"FAIXA_ETARIA",
        		"VALOR_MODULO",
        		"VALOR_APURADO"
        };
    }
}
