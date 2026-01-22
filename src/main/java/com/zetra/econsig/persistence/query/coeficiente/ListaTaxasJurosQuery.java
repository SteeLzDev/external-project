package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ListaTaxasJurosQuery</p>
 * <p>Description: Listagem de Taxas de Juros</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTaxasJurosQuery extends HNativeQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaTaxasJurosQuery.class);

    private String periodo;
    private String csaCodigo;
    private String svcCodigo;
    private Integer prazo;
    private boolean ativo;
    private boolean ordenaPeloPrazo = false;
    private boolean taxaJuroCompartilhada = false;
    private boolean orderByCftDataFimVig = false;
    private AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute("PERIODO");
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        svcCodigo = (String) criterio.getAttribute(Columns.SVC_CODIGO);
        prazo = (Integer) criterio.getAttribute("PRAZO");
        ativo = criterio.getAttribute("ATIVO") != null ? ((Boolean) criterio.getAttribute("ATIVO")) : false;
        ordenaPeloPrazo = criterio.getAttribute("ORDENA_PELO_PRAZO") != null ? ((Boolean) criterio.getAttribute("ORDENA_PELO_PRAZO")) : false;
        responsavel = (AcessoSistema) criterio.getAttribute(AcessoSistema.SESSION_ATTR_NAME);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoQuery = new StringBuilder();

        if (!TextHelper.isNull(periodo)) {
            corpoQuery.append(getCorpoQueryListaTaxas(Columns.TB_COEFICIENTE_ATIVO));
            corpoQuery.append(" UNION ");
            corpoQuery.append(getCorpoQueryListaTaxas(Columns.TB_COEFICIENTE));
        } else {
            corpoQuery.append(getCorpoQueryListaTaxas(Columns.TB_COEFICIENTE_ATIVO));
        }

        corpoQuery.append(" ORDER BY ");

        //DESENV-17250: para taxas criadas com data fim de vingência definidas por parâmetro de serviço, ordernar por esta data em ordem decrescente.
        if (TextHelper.isNull(periodo) && orderByCftDataFimVig) {
            corpoQuery.append("campo_data_fim_vigencia").append(" DESC,");
        }

        if (!ordenaPeloPrazo) {
            corpoQuery.append(" (CASE WHEN (COALESCE(").append(Columns.getColumnName(Columns.CFT_VLR)).append(", 1000.00) = 0) THEN 999.00 ELSE COALESCE(").append(Columns.getColumnName(Columns.CFT_VLR)).append(", 1000.00) END) * 1.00");
        } else {
            corpoQuery.append(Columns.getColumnName(Columns.PRZ_VLR));
        }

        // Define os valores para os parâmetros nomeados
        final Query<Object[]> query = instanciarQuery(session, corpoQuery.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (prazo != null) {
            defineValorClausulaNomeada("prazo", prazo.shortValue(), query);
        }
        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }

        return query;
    }

    /**
     * Monta o corpo da query de acordo com qual tabela de taxas utilizar.
     * @param tabelaTaxas
     * @return
     * @throws HQueryException
     */
    private String getCorpoQueryListaTaxas(String tabelaTaxas) throws HQueryException {
        String campoCftCodigo = Columns.CFT_CODIGO;
        String campoCftDia = Columns.CFT_DIA;
        String campoCftVlr = Columns.CFT_VLR;
        String campoCftDataIniVig = Columns.CFT_DATA_INI_VIG;
        String campoCftDataFimVig = Columns.CFT_DATA_FIM_VIG;
        String campoCftDataCadastro = Columns.CFT_DATA_CADASTRO;
        String campoCftVlrRef = Columns.CFT_VLR_REF;
        String campoCftVlrMinimo = Columns.CFT_VLR_MINIMO;
        String campoCftPrzCsaCodigo = Columns.CFT_PRZ_CSA_CODIGO;        

        if (Columns.TB_COEFICIENTE_ATIVO.equals(tabelaTaxas)) {
            campoCftCodigo = Columns.CFA_CODIGO;
            campoCftDia = Columns.CFA_DIA;
            campoCftVlr = Columns.CFA_VLR;
            campoCftDataIniVig = Columns.CFA_DATA_INI_VIG;
            campoCftDataFimVig = Columns.CFA_DATA_FIM_VIG;
            campoCftDataCadastro = Columns.CFA_DATA_CADASTRO;
            campoCftVlrRef = Columns.CFA_VLR_REF;
            campoCftVlrMinimo = Columns.CFA_VLR_MINIMO;
            campoCftPrzCsaCodigo = Columns.CFA_PRZ_CSA_CODIGO;            
        }

        final StringBuilder corpo = new StringBuilder("SELECT ").append(campoCftCodigo).append(", ").append(campoCftPrzCsaCodigo).append(", ").append(campoCftDia).append(", ").append(campoCftVlr).append(", ").append(campoCftDataIniVig).append(" as campo_data_fim_vigencia, ").append(campoCftDataFimVig).append(", ").append(campoCftDataCadastro).append(", ").append(campoCftVlrRef).append(", ").append(campoCftVlrMinimo).append(", ").append(Columns.PRZ_VLR);

        if (TextHelper.isNull(csaCodigo)) {
            corpo.append(", ").append(Columns.CSA_CODIGO).append(", ").append(Columns.CSA_NOME).append(", ").append(Columns.CSA_NOME_ABREV).append(", ").append(Columns.CSA_IDENTIFICADOR);
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo.toString());

        corpoBuilder.append(" FROM ").append(tabelaTaxas);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_PRAZO_CONSIGNATARIA);
        corpoBuilder.append("   ON (").append(campoCftPrzCsaCodigo).append(" = ").append(Columns.PZC_CODIGO).append(")");
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_PRAZO);
        corpoBuilder.append("   ON (").append(Columns.PRZ_CODIGO).append(" = ").append(Columns.PZC_PRZ_CODIGO).append(")");
        if (taxaJuroCompartilhada) {
            corpoBuilder.append(" INNER JOIN ").append(Columns.TB_RELACIONAMENTO_SERVICO);
            corpoBuilder.append("  ON (").append(Columns.PRZ_SVC_CODIGO).append(" = ").append(Columns.RSV_SVC_CODIGO_ORIGEM).append(")");
        }
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA);
        corpoBuilder.append("   ON (").append(Columns.CSA_CODIGO).append(" = ").append(Columns.PZC_CSA_CODIGO).append(")");
        corpoBuilder.append(" WHERE ").append(Columns.PZC_ATIVO).append(" = '").append(CodedValues.STS_ATIVO).append("'");
        corpoBuilder.append("   AND ").append(Columns.PRZ_ATIVO).append(" = '").append(CodedValues.STS_ATIVO).append("'");

        if (taxaJuroCompartilhada) {
            corpoBuilder.append("  AND ").append(Columns.RSV_TNT_CODIGO).append(" = '").append(CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS).append("'");
        }

        if (!TextHelper.isNull(svcCodigo)) {
            if (!taxaJuroCompartilhada) {
                corpoBuilder.append(" AND ").append(Columns.PRZ_SVC_CODIGO).append(criaClausulaNomeada("svcCodigo", svcCodigo));
            } else {
                corpoBuilder.append(" AND ").append(Columns.RSV_SVC_CODIGO_DESTINO).append(criaClausulaNomeada("svcCodigo", svcCodigo));
            }
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND ").append(Columns.CSA_CODIGO).append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (prazo != null) {
            corpoBuilder.append(" AND ").append(Columns.PRZ_VLR).append(criaClausulaNomeada("prazo", prazo));
        }

        if (!TextHelper.isNull(periodo)) {
            // Buscando as taxas de juros que estavam ativas ou que estarão ativas em uma certa data.
            // É utilizado no ranking de taxas onde o gestor pode ver o que estava ativo no passado
            corpoBuilder.append(" AND ").append("to_days(").append(campoCftDataIniVig).append(") <= to_days(:periodo)");
            corpoBuilder.append(" AND (").append("to_days(").append(campoCftDataFimVig).append(") >= to_days(:periodo) OR ").append(campoCftDataFimVig).append(" IS NULL)");
        } else if (ativo) {
            // Buscando as taxas de juros que estão ativas no momento
            corpoBuilder.append(" AND ").append("to_days(").append(campoCftDataIniVig).append(") <= to_days(data_corrente())");
            corpoBuilder.append(" AND (").append("to_days(").append(campoCftDataFimVig).append(") >= to_days(data_corrente()) OR ").append(campoCftDataFimVig).append(" IS NULL)");
        } else {
            // Buscando as taxas de juros que estão sendo editadas
            try {
                //DESENV-17250: para serviços que tenham o parâmetro de serviço consignante TPS_DIAS_VIGENCIA_CET preenchido, as novas taxas
                //              não serão inicializadas com a data fim de vigência nula.
                final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                if (paramSvcCse.getTpsDiasVigenciaCet() == null) {
                    corpoBuilder.append(" AND ").append(campoCftDataFimVig).append(" IS NULL");
                } else {
                    corpoBuilder.append(" AND ").append("to_days(").append(campoCftDataIniVig).append(") >= to_days(data_corrente())");
                    orderByCftDataFimVig = true;
                }
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new HQueryException(ex);
            }
        }

        return corpoBuilder.toString();
    }

    public boolean isTaxaJuroCompartilhada() {
        return taxaJuroCompartilhada;
    }

    public void setTaxaJuroCompartilhada(boolean taxaJuroCompartilhada) {
        this.taxaJuroCompartilhada = taxaJuroCompartilhada;
    }

    @Override
    protected String[] getFields() {
        if (TextHelper.isNull(csaCodigo)) {
            return new String[] {
                    Columns.CFT_CODIGO,
                    Columns.CFT_PRZ_CSA_CODIGO,
                    Columns.CFT_DIA,
                    Columns.CFT_VLR,
                    Columns.CFT_DATA_INI_VIG,
                    Columns.CFT_DATA_FIM_VIG,
                    Columns.CFT_DATA_CADASTRO,
                    Columns.CFT_VLR_REF,
                    Columns.CFT_VLR_MINIMO,
                    Columns.PRZ_VLR,
                    Columns.CSA_CODIGO,
                    Columns.CSA_NOME,
                    Columns.CSA_NOME_ABREV,
                    Columns.CSA_IDENTIFICADOR
            };
        } else {
            return new String[] {
                    Columns.CFT_CODIGO,
                    Columns.CFT_PRZ_CSA_CODIGO,
                    Columns.CFT_DIA,
                    Columns.CFT_VLR,
                    Columns.CFT_DATA_INI_VIG,
                    Columns.CFT_DATA_FIM_VIG,
                    Columns.CFT_DATA_CADASTRO,
                    Columns.CFT_VLR_REF,
                    Columns.CFT_VLR_MINIMO,
                    Columns.PRZ_VLR
            };
        }
    }
}
