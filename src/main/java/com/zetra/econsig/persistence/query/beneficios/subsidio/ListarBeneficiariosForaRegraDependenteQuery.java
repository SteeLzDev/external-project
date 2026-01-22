package com.zetra.econsig.persistence.query.beneficios.subsidio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.GrauParentescoEnum;
import com.zetra.econsig.values.MotivoDependenciaEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;

/**
 * <p>Title: ListarBeneficiariosCalculoSubsidioQuery</p>
 * <p>Description: Lista beneficiários fora da Regra Dependente</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficiariosForaRegraDependenteQuery extends HQuery {
    public String serCodigo;
    public List<String> tntCodigos;
    public List<String> scbCodigos;
    public Character subsidioConcedido;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append("bfc.bfcDataNascimento, ");
        corpoBuilder.append("bfc.motivoDependencia.mdeCodigo, ");
        corpoBuilder.append("bfc.bfcNome, ");
        corpoBuilder.append("bfc.bfcCpf, ");
        corpoBuilder.append("csa.csaNome, ");
        corpoBuilder.append("ben.benDescricao, ");
        corpoBuilder.append("ser.serEmail, ");
        corpoBuilder.append("cbe.cbeCodigo, ");
        corpoBuilder.append("pse262.pseVlr, ");
        corpoBuilder.append("pse263.pseVlr  ");

        corpoBuilder.append("FROM ContratoBeneficio cbe ");
        corpoBuilder.append("INNER JOIN cbe.beneficio ben ");
        corpoBuilder.append("INNER JOIN cbe.beneficiario bfc ");
        corpoBuilder.append("INNER JOIN cbe.autDescontoSet ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN cnv.orgao org ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN ade.tipoLancamento tla ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN tla.tipoNatureza tnt ");
        corpoBuilder.append("INNER JOIN svc.paramSvcConsignanteSet pse256 WITH pse256.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TEM_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse262 WITH pse262.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO).append("' ");
        corpoBuilder.append("LEFT  JOIN svc.paramSvcConsignanteSet pse263 WITH pse263.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO).append("' ");

        corpoBuilder.append("WHERE 1 = 1 ");

        // Beneficiários do grupo familiar
        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append("AND bfc.servidor.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo)).append(" ");
        }

        corpoBuilder.append("AND bfc.tipoBeneficiario.tibCodigo = :dependente ");

        // Fora da regra do dependente
        corpoBuilder.append("AND (bfc.motivoDependencia.mdeCodigo != :motivoInvalidez or bfc.motivoDependencia.mdeCodigo != :motivoEstudanteInvalidez or bfc.motivoDependencia.mdeCodigo is null) ");
        corpoBuilder.append("AND (bfc.bfcSubsidioConcedido != :subConcedido or bfc.bfcSubsidioConcedido is null) ");
        corpoBuilder.append("AND cbe.statusContratoBeneficio.scbCodigo not in (:scbCodigos) ");
        corpoBuilder.append("AND (bfc.grauParentesco.grpCodigo != :grauParentesco or bfc.grauParentesco.grpCodigo is null) ");

        // De serviços que possuem subsídio
        corpoBuilder.append("AND pse256.pseVlr = '1' ");

        // De natureza de relacionamento da mensalidade de benefício
        corpoBuilder.append("AND tla.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigos)).append(" ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tntCodigo", tntCodigos, query);

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        defineValorClausulaNomeada("dependente", TipoBeneficiarioEnum.DEPENDENTE.tibCodigo, query);
        defineValorClausulaNomeada("motivoInvalidez", MotivoDependenciaEnum.INVALIDEZ.mdeCodigo, query);
        defineValorClausulaNomeada("motivoEstudanteInvalidez", MotivoDependenciaEnum.ESTUDANTEINVALIDEZ.mdeCodigo, query);
        defineValorClausulaNomeada("subConcedido", subsidioConcedido.toString(), query);
        defineValorClausulaNomeada("scbCodigos", scbCodigos, query);
        defineValorClausulaNomeada("grauParentesco", GrauParentescoEnum.CONJUGE.getCodigo(), query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BFC_DATA_NASCIMENTO,
                Columns.MDE_CODIGO,
                Columns.BFC_NOME,
                Columns.BFC_CPF,
                Columns.CSA_NOME,
                Columns.BEN_DESCRICAO,
                Columns.SER_EMAIL,
                Columns.CBE_CODIGO,
                Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO,
                Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO,
        };
    }
}
