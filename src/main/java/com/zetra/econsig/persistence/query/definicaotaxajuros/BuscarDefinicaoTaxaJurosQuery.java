package com.zetra.econsig.persistence.query.definicaotaxajuros;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: BuscarDefinicaoTaxaJurosQuery</p>
 * <p>Description: Listagem de definição de taxa de juros</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio.goncalves $
 * $Revision: 20570 $
 * $Date: 2016-09-20 20:11:04 -0300 (Ter, 20 set 2016) $
 */
public class BuscarDefinicaoTaxaJurosQuery extends HQuery {

	public Integer idade;
	public Integer tempServico;
	public BigDecimal salario;
	public BigDecimal margem;
	public BigDecimal valorTotal;
	public BigDecimal valorContrato;
	public Integer prazo;
    public List<String> funCodigos;

    public String rseCodigo;
	public String csaCodigo;
	public String orgCodigo;
	public String svcCodigo;

    public int offset = -1;
    public int maxResults = -1;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Se o simulador agrupa os serviços de natureza EMPRESTIMO e a simulação é para um servidor
        final boolean simuladorAgrupadoPorNaturezaServico = ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())
                && (rseCodigo != null)
                && (csaCodigo == null);

        // Na simulação de renegociação o prazo é passado como zero para listar o ranking com todos, porém no cadastro da definição não aceita zero,
        // assim precisamos setar como 1 para listar alguma definição se existir.
        if (prazo != null && prazo == 0) {
            prazo = 1;
        }

        StringBuilder corpo = new StringBuilder();

        corpo.append(" select dtj.dtjCodigo, ");
        corpo.append(" dtj.dtjTaxaJuros, ");
        corpo.append(" dtj.dtjTaxaJurosMinima, ");
        corpo.append(" csa.csaCodigo, ");
        corpo.append(" csa.csaNome, ");
        corpo.append(" csa.csaNomeAbrev, ");
        corpo.append(" csa.csaIdentificador, ");
        corpo.append(" svc.svcCodigo, ");
        corpo.append(" svc.svcDescricao, ");
        corpo.append(" svc.svcIdentificador, ");
        corpo.append(" ").append(prazo).append(", ");
        corpo.append(" dtj.dtjDataVigenciaIni, ");
        corpo.append(" dtj.dtjDataVigenciaFim, ");
        corpo.append(" dtj.funCodigo, ");
        corpo.append(" cast(coalesce(psc.pscVlr, '").append(CodedValues.CSA_NAO_PROMOVIDA).append("') as int), ");
        corpo.append(" dtj.dtjFaixaPrazoIni, ");
        corpo.append(" dtj.dtjFaixaPrazoFim ");

        corpo.append(" from DefinicaoTaxaJuros dtj ");
        corpo.append(" inner join dtj.consignataria csa ");
        corpo.append(" inner join dtj.servico svc ");
        corpo.append(" left outer join svc.paramSvcConsignatariaSet psc with psc.consignataria.csaCodigo = csa.csaCodigo and psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_RELEVANCIA_CSA_RANKING).append("'");


        if (!simuladorAgrupadoPorNaturezaServico) {
            corpo.append(" where svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        } else {
            corpo.append(" where svc.naturezaServico.nseCodigo = (");
            corpo.append(" select svcOrigem.naturezaServico.nseCodigo from Servico svcOrigem");
            corpo.append(" where svcOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
            corpo.append(")");
        }

        if (csaCodigo != null) {
        	corpo.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        corpo.append(" AND csa.csaAtivo = ").append(CodedValues.STS_ATIVO);

        if (orgCodigo != null) {
            corpo.append(" and (dtj.orgao is null or dtj.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(") ");
        }

        if (idade != null) {
        	corpo.append(" and (dtj.dtjFaixaEtariaIni is null or dtj.dtjFaixaEtariaIni <= :idade ) ");
        	corpo.append(" and (dtj.dtjFaixaEtariaFim is null or dtj.dtjFaixaEtariaFim >= :idade ) ");
        }

        if (funCodigos != null){
            corpo.append(" and (dtj.funCodigo is null or dtj.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos) + ") ");
        }

        if (tempServico != null) {
        	corpo.append(" and (dtj.dtjFaixaTempServicoIni is null or dtj.dtjFaixaTempServicoIni <= :tempServico ) ");
        	corpo.append(" and (dtj.dtjFaixaTempServicoFim is null or dtj.dtjFaixaTempServicoFim >= :tempServico ) ");
        }

        if (salario != null) {
        	corpo.append(" and (dtj.dtjFaixaSalarioIni is null or dtj.dtjFaixaSalarioIni <= :salario ) ");
        	corpo.append(" and (dtj.dtjFaixaSalarioFim is null or dtj.dtjFaixaSalarioFim >= :salario ) ");
        }

        if (margem != null) {
        	corpo.append(" and (dtj.dtjFaixaMargemIni is null or dtj.dtjFaixaMargemIni <= :margem ) ");
        	corpo.append(" and (dtj.dtjFaixaMargemFim is null or dtj.dtjFaixaMargemFim >= :margem ) ");
        }

        if (valorTotal != null) {
        	corpo.append(" and (dtj.dtjFaixaValorTotalIni is null or dtj.dtjFaixaValorTotalIni <= :valorTotal ) ");
        	corpo.append(" and (dtj.dtjFaixaValorTotalFim is null or dtj.dtjFaixaValorTotalFim >= :valorTotal ) ");
        }

        if (valorContrato != null) {
        	corpo.append(" and (dtj.dtjFaixaValorContratoIni is null or dtj.dtjFaixaValorContratoIni <= :valorContrato ) ");
        	corpo.append(" and (dtj.dtjFaixaValorContratoFim is null or dtj.dtjFaixaValorContratoFim >= :valorContrato ) ");
        }

        if (prazo != null) {
        	corpo.append(" and (dtj.dtjFaixaPrazoIni is null or dtj.dtjFaixaPrazoIni <= :prazo ) ");
        	corpo.append(" and (dtj.dtjFaixaPrazoFim is null or dtj.dtjFaixaPrazoFim >= :prazo ) ");
        }

        corpo.append(" and (dtj.dtjDataVigenciaIni <= current_timestamp() ) ");
        corpo.append(" and (dtj.dtjDataVigenciaFim is null ) ");

        corpo.append(" and exists (select 1 from Convenio cnv where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpo.append(" and cnv.consignataria.csaCodigo = csa.csaCodigo");
        corpo.append(" and cnv.servico.svcCodigo = svc.svcCodigo");

        if (orgCodigo != null) {
            corpo.append(" and (cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(")");
        } else {
            corpo.append(" and (cnv.orgao.orgCodigo = dtj.orgao.orgCodigo or dtj.orgao.orgCodigo is null)");

        }
        corpo.append(")");

        corpo.append(" and exists (select 1 from PrazoConsignataria przCsa inner join przCsa.prazo prz where przCsa.przCsaAtivo = ").append(CodedValues.STS_ATIVO).append("");
        corpo.append(" and przCsa.consignataria.csaCodigo = csa.csaCodigo");
        corpo.append(" and prz.servico.svcCodigo = svc.svcCodigo");
        corpo.append(" and prz.przVlr ").append(criaClausulaNomeada("prazo", prazo));
        corpo.append(")");
    	corpo.append(" order by dtj.dtjTaxaJuros asc ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        if (csaCodigo != null) {
        	defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (orgCodigo != null) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (idade != null) {
        	defineValorClausulaNomeada("idade", idade.shortValue(), query);
        }

        if (funCodigos != null){
            defineValorClausulaNomeada("funCodigos", funCodigos, query);
        }

        if (tempServico != null) {
        	defineValorClausulaNomeada("tempServico", tempServico.shortValue(), query);
        }

        if (salario != null) {
        	defineValorClausulaNomeada("salario", salario, query);
        }

        if (margem != null) {
        	defineValorClausulaNomeada("margem", margem, query);
        }

        if (valorTotal != null) {
        	defineValorClausulaNomeada("valorTotal", valorTotal, query);
        }

        if (valorContrato != null) {
        	defineValorClausulaNomeada("valorContrato", valorContrato, query);
        }

        if (prazo != null) {
        	defineValorClausulaNomeada("prazo", prazo.shortValue(), query);
        }

        if (offset != -1) {
            query.setFirstResult(offset);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.DTJ_CODIGO,
                Columns.CFT_VLR,
                Columns.CFT_VLR_MINIMO,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_IDENTIFICADOR,
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.SVC_IDENTIFICADOR,
                Columns.PRZ_VLR,
                Columns.DTJ_DATA_VIGENCIA_INI,
                Columns.DTJ_DATA_VIGENCIA_FIM,
                Columns.DTJ_FUN_CODIGO,
                "RELEVANCIA",
                Columns.DTJ_FAIXA_PRAZO_INI,
                Columns.DTJ_FAIXA_PRAZO_FIM
        };
    }
}
