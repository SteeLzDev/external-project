package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.TipoEnderecoEnum;

/**
 * <p>Title: RelatorioContratosBeneficiosQuery</p>
 * <p>Description: Query usada para gerar relatório de contratos de benefícios</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioContratosBeneficiosQuery extends ReportHQuery {
    public String dataIni;
    public String dataFim;
    public List<String> status;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("dataIni");
        dataFim = (String) criterio.getAttribute("dataFim");
        status = (List<String>) criterio.getAttribute("status");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT scb.scbDescricao as scb_descricao, ");
        corpoBuilder.append("cse.cseNome as cse_nome, ");
        corpoBuilder.append("bfc.bfcNome as bfc_nome, ");
        corpoBuilder.append("bfc.bfcSexo as bfc_sexo, ");
        corpoBuilder.append("to_locale_date(bfc.bfcDataNascimento) as bfc_data_nascimento, ");
        corpoBuilder.append("bfc.bfcCpf as bfc_cpf, ");
        corpoBuilder.append("bfc.bfcNomeMae as bfc_nome_mae, ");
        corpoBuilder.append("bfc.bfcEstadoCivil as bfc_estado_civil, ");
        corpoBuilder.append("coalesce(grp.grpDescricao,'') as grp_descricao, ");
        corpoBuilder.append("rse.rseMatricula as rse_matricula, ");
        corpoBuilder.append("to_locale_datetime(rse.rseDataAdmissao) as rse_data_admissao, ");
        corpoBuilder.append("ens.ensCep as ens_cep, ");
        corpoBuilder.append("ens.ensLogradouro as ens_logradouro, ");
        corpoBuilder.append("ens.ensNumero as ens_numero, ");
        corpoBuilder.append("ens.ensComplemento as ens_complemento, ");
        corpoBuilder.append("ens.ensBairro as ens_bairro, ");
        corpoBuilder.append("ens.ensMunicipio as ens_municipio, ");
        corpoBuilder.append("ens.ensUf as ens_uf, ");
        corpoBuilder.append("ben.benCodigoPlano as ben_codigo_plano, ");
        corpoBuilder.append("ben.benDescricao as ben_descricao, ");
        corpoBuilder.append("ser.serNome as ser_nome, ");
        corpoBuilder.append("to_locale_datetime(cbe.cbeDataInclusao) as cbe_data_inclusao, ");
        corpoBuilder.append("to_locale_datetime(cbe.cbeDataInicioVigencia) as cbe_data_inicio_vigencia, ");
        corpoBuilder.append("case when toc.tocCodigo = '" + CodedValues.TOC_INCLUSAO_CONTRATO_BENEFICIO.toString() + "' then 'I' when toc.tocCodigo IN ('" + CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO.toString() + "', '" + CodedValues.TOC_RETIFICACAO_MOTIVO_OPERACAO + "', '" + CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO.toString() + "') THEN 'A' WHEN toc.tocCodigo = '" + CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO.toString() + "' then 'E' else 'N/A' end as toc_descricao, ");
        corpoBuilder.append("case when scb.scbCodigo = '" + StatusContratoBeneficioEnum.CANCELADO.getCodigo() + "' and toc.tocCodigo = '" + CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO.toString() + "' then coalesce(concat(tmo.tmoCodigo,' - ',tmo.tmoDescricao),'') else '' end as tmo_descricao, ");
        corpoBuilder.append("case when scb.scbCodigo = '" + StatusContratoBeneficioEnum.CANCELADO.getCodigo() + "' and toc.tocCodigo = '" + CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO.toString() + "' and cbe.cbeDataCancelamento is not null then to_locale_datetime(cbe.cbeDataCancelamento) else '' end as data_cancelamento ");
        corpoBuilder.append("from ContratoBeneficio cbe ");
        corpoBuilder.append("inner join cbe.autDescontoSet ade ");
        corpoBuilder.append("inner join ade.registroServidor rse ");
        corpoBuilder.append("inner join rse.servidor ser ");
        corpoBuilder.append("inner join cbe.beneficio ben ");
        corpoBuilder.append("inner join cbe.beneficiario bfc ");
        corpoBuilder.append("inner join rse.orgao org ");
        corpoBuilder.append("inner join org.estabelecimento est ");
        corpoBuilder.append("inner join est.consignante cse ");
        corpoBuilder.append("inner join ade.tipoLancamento tla ");
        corpoBuilder.append("inner join tla.tipoNatureza tnt ");
        corpoBuilder.append("inner join cbe.ocorrenciaCttBeneficioSet ocb ");
        corpoBuilder.append("inner join ocb.tipoOcorrencia toc ");
        corpoBuilder.append("inner join cbe.statusContratoBeneficio scb ");
        corpoBuilder.append("left outer join ser.enderecoServidorSet ens with ens.tipoEndereco.tieCodigo = '").append(TipoEnderecoEnum.COBRANCA.getCodigo()).append("' ");
        corpoBuilder.append("left outer join ocb.tipoMotivoOperacao tmo ");
        corpoBuilder.append("left outer join bfc.grauParentesco grp ");

        corpoBuilder.append("WHERE ocb.ocbData between :dataIni and :dataFim ");
        corpoBuilder.append("AND tnt.tntCodigo IN :tntCodigo ");

        if (!TextHelper.isNull(status) && !status.isEmpty()) {
            corpoBuilder.append("AND scb.scbCodigo").append(criaClausulaNomeada("status", status));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        defineValorClausulaNomeada("tntCodigo", CodedValues.TNT_BENEFICIO_MENSALIDADE, query);

        if (!TextHelper.isNull(status) && !status.isEmpty()) {
            defineValorClausulaNomeada("status", status, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SCB_DESCRICAO,
                Columns.CSE_NOME,
                Columns.BFC_NOME,
                Columns.BFC_SEXO,
                Columns.BFC_DATA_NASCIMENTO,
                Columns.BFC_CPF,
                Columns.BFC_NOME_MAE,
                Columns.BFC_ESTADO_CIVIL,
                Columns.GRP_DESCRICAO,
                Columns.RSE_MATRICULA,
                Columns.RSE_DATA_ADMISSAO,
                Columns.ENS_CEP,
                Columns.ENS_LOGRADOURO,
                Columns.ENS_NUMERO,
                Columns.ENS_COMPLEMENTO,
                Columns.ENS_BAIRRO,
                Columns.ENS_MUNICIPIO,
                Columns.ENS_UF,
                Columns.BEN_CODIGO_PLANO,
                Columns.BEN_DESCRICAO,
                Columns.SER_NOME,
                Columns.CBE_DATA_INCLUSAO,
                Columns.CBE_DATA_INICIO_VIGENCIA,
                Columns.TMO_DESCRICAO,
                Columns.TOC_DESCRICAO,
                Columns.CBE_DATA_CANCELAMENTO
        };
    }

}
