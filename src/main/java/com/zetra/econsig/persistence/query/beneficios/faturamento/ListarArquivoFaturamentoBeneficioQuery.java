package com.zetra.econsig.persistence.query.beneficios.faturamento;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FaturamentoBeneficioQuery</p>
 * <p>Description: Query para faturamento de beneficios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ListarArquivoFaturamentoBeneficioQuery extends HQuery {

	public String fatCodigo;
	public String rseMatricula;
	public String cbeNumero;
	public String bfcCpf;
	public Integer afbCodigo;

	public boolean contador = false;

	@Override
	protected Query<Object[]> preparar(Session session) throws HQueryException {
		StringBuilder corpo = new StringBuilder();

		corpo.append("select ");
		if (!contador) {
			corpo.append("fat.fatPeriodo, ");
			corpo.append("afb.afbCodigo, ");
			corpo.append("afb.rseMatricula, ");
			corpo.append("afb.cbeNumero, ");
			corpo.append("afb.bfcCpf, ");
			corpo.append("tla.tlaCodigo, ");
			corpo.append("tla.tlaDescricao, ");
			corpo.append("ben.benDescricao, ");
			corpo.append("csa.csaNome, ");
			corpo.append("afb.afbValorSubsidio, ");
			corpo.append("afb.afbValorRealizado, ");
			corpo.append("afb.afbValorNaoRealizado, ");
			corpo.append("afb.afbValorTotal ");
		} else {
			corpo.append("count(afb.afbCodigo) as TOTAL ");
		}

		corpo.append("from ArquivoFaturamentoBen afb ");
		corpo.append("inner join afb.tipoLancamento tla ");
		corpo.append("inner join afb.faturamentoBeneficio fat ");
		corpo.append("inner join afb.autDesconto ade ");
		corpo.append("inner join ade.contratoBeneficio cbe ");
		corpo.append("inner join cbe.beneficio ben ");
		corpo.append("inner join ben.consignataria csa ");
		corpo.append("where 1=1 ");

		if (!TextHelper.isNull(fatCodigo)) {
			corpo.append("and fat.fatCodigo ").append(criaClausulaNomeada("fatCodigo", fatCodigo));
		}

		if (!TextHelper.isNull(rseMatricula)) {
			corpo.append("and afb.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
		}

		if (!TextHelper.isNull(cbeNumero)) {
			corpo.append("and afb.cbeNumero ").append(criaClausulaNomeada("cbeNumero", cbeNumero));
		}

		if (!TextHelper.isNull(bfcCpf)) {
			corpo.append("and afb.bfcCpf ").append(criaClausulaNomeada("bfcCpf", bfcCpf));
		}

		if (afbCodigo != null) {
			corpo.append("and afb.afbCodigo ").append(criaClausulaNomeada("afbCodigo", afbCodigo));
		}

		Query<Object[]> query = instanciarQuery(session, corpo.toString());

		if (!TextHelper.isNull(fatCodigo)) {
			defineValorClausulaNomeada("fatCodigo", fatCodigo, query);
		}

		if (!TextHelper.isNull(rseMatricula)) {
			defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
		}

		if (!TextHelper.isNull(cbeNumero)) {
			defineValorClausulaNomeada("cbeNumero", cbeNumero, query);
		}

		if (!TextHelper.isNull(bfcCpf)) {
			defineValorClausulaNomeada("bfcCpf", bfcCpf, query);
		}

		if (afbCodigo != null) {
			defineValorClausulaNomeada("afbCodigo", afbCodigo, query);
		}

		if (!contador) {

			if (firstResult != null && firstResult != -1) {
				query.setFirstResult(firstResult);
			}

			if (maxResults != null && maxResults != -1) {
				query.setMaxResults(maxResults);
			}

		}

		return query;

	}

	@Override
	protected String[] getFields() {
		if (!contador) {
			return new String[] {
					Columns.FAT_PERIODO,
					Columns.AFB_CODIGO,
					Columns.AFB_RSE_MATRICULA,
					Columns.CBE_NUMERO,
					Columns.BFC_CPF,
					Columns.TLA_CODIGO,
					Columns.TLA_DESCRICAO,
					Columns.BEN_DESCRICAO,
					Columns.CSA_NOME,
					Columns.AFB_VALOR_SUBSIDIO,
					Columns.AFB_VALOR_REALIZADO,
					Columns.AFB_VALOR_NAO_REALIZADO,
					Columns.AFB_VALOR_TOTAL
			};
		} else {
			return new String [] {"TOTAL"};
		}
	}

}