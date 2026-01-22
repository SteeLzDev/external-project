package com.zetra.econsig.persistence.query.servidor;

import java.util.Arrays;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemServidorQuery</p>
 * <p>Description: Retorna os dados de um servidor informado por parâmetro</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemServidorQuery extends HQuery {

    public String rseCodigo;
    public String serCodigo;
    public String vrsCodigo;

    // Só retorna campos de margem, caso este flag esteja habilitado
    public boolean retornaMargem;

    public boolean retornaUsuLogin;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(rseCodigo) && TextHelper.isNull(serCodigo)) {
            throw new HQueryException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema());
        }

        String corpo = "select " +
                        "est.consignante.cseCodigo, " +
                        "est.estCodigo, " +
                        "est.estIdentificador, " +
                        "est.estNome, " +
                        "org.orgCodigo, " +
                        "org.orgIdentificador, " +
                        "org.orgNome, " +
                        "org.orgCnpj, " +
                        "rse.rseCodigo, " +
                        "rse.rseMatricula, " +
                        "rse.rseMatriculaInst, " +
                        "rse.rsePrazo, " +
                        "rse.rseTipo, " +
                        "rse.rseBancoSal, " +
                        "rse.rseAgenciaSal, " +
                        "rse.rseAgenciaDvSal, " +
                        "rse.rseContaSal, " +
                        "rse.rseContaDvSal, " +
                        "rse.rseBancoSal2, " +
                        "rse.rseAgenciaSal2, " +
                        "rse.rseAgenciaDvSal2, " +
                        "rse.rseContaSal2, " +
                        "rse.rseContaDvSal2, " +
                        "rse.rseDataAdmissao, " +
                        "rse.rseSalario, " +
                        "rse.rseProventos, " +
                        "rse.rseAssociado, " +
                        "rse.rseClt, " +
                        "rse.rseParamQtdAdeDefault, " +
                        "rse.rseDataFimEngajamento, " +
                        "rse.rseDataLimitePermanencia, " +
                        "rse.rseEstabilizado, " +
                        "rse.rseDataCarga, " +
                        "rse.rseDataCtc, " +
                        "rse.rseDataAlteracao, " +
                        "rse.rseBaseCalculo," +
                        "rse.rseObs," +
                        "rse.rseMunicipioLotacao," +
                        "rse.rsePraca," +
                        "rse.rseDescontosComp," +
                        "rse.rseDescontosFacu," +
                        "rse.rseOutrosDescontos," +
                        "rse.rseDataSaida, " +
                        "rse.rseDataUltSalario, " +
                        "rse.rsePedidoDemissao, " +
                        "rse.rseDataRetorno, " +
                        "rse.rseMotivoBloqueio, " +
                        "rse.rseMotivoFaltaMargem, " +
                        "rse.rseMargem, " +
                        "rse.rseMargemUsada, " +
                        "rse.rseMargemRest, " +
                        "rse.rseMargem2, " +
                        "rse.rseMargemUsada2, " +
                        "rse.rseMargemRest2, " +
                        "rse.rseMargem3, " +
                        "rse.rseMargemUsada3, " +
                        "rse.rseMargemRest3, " +
                        "ser.serCodigo, " +
                        "ser.serNome, " +
                        "ser.serPrimeiroNome, " +
                        "ser.serUltimoNome, " +
                        "ser.serCpf, " +
                        "ser.serDataNasc, " +
                        "ser.serCep, " +
                        "ser.serNroIdt, " +
                        "ser.serEmissorIdt, " +
                        "ser.serUfIdt, " +
                        "ser.serDataIdt, " +
                        "ser.serEnd, " +
                        "ser.serBairro, " +
                        "ser.serCidade, " +
                        "concatenar(concatenar(concatenar(coalesce(to_string(ser.serEnd), ''), ' - '), concatenar(coalesce(to_string(ser.serNro), ''), ' - ')), coalesce(to_string(ser.serCompl), '')) as ENDERECO, " +
                        "ser.serNomeMae, " +
                        "ser.serNomePai, " +
                        "ser.serNomeConjuge, " +
                        "ser.serTel, " +
                        "ser.serCelular, " +
                        "ser.serEmail, " +
                        "ser.serSexo, " +
                        "ser.serEstCivil, " +
                        "ser.serQtdFilhos, " +
                        "ser.nivelEscolaridade.nesCodigo, " +
                        "ser.tipoHabitacao.thaCodigo, " +
                        "ser.serNacionalidade, " +
                        "ser.serCartProf, " +
                        "ser.serPis, " +
                        "ser.serEnd, " +
                        "ser.serNro, " +
                        "ser.serCompl, " +
                        "ser.serUf, " +
                        "ser.serCidNasc, " +
                        "ser.serUfNasc, " +
                        "rse.vinculoRegistroServidor.vrsCodigo, " +
                        "srs.srsCodigo, " +
                        "srs.srsDescricao, " +
                        "sbo.sboCodigo, " +
                        "sbo.sboDescricao, " +
                        "sbo.sboIdentificador, " +
                        "uni.uniCodigo, " +
                        "uni.uniDescricao, " +
                        "uni.uniIdentificador, " +
                        "crs.crsCodigo, " +
                        "crs.crsDescricao, " +
                        "crs.crsIdentificador, " +
                        "crs.crsVlrDescMax, " +
                        "prs.prsCodigo, " +
                        "prs.prsIdentificador, " +
                        "prs.prsDescricao, " +
                        "pos.posCodigo, " +
                        "pos.posDescricao, " +
                        "trs.trsDescricao, " +
                        "vrs.vrsDescricao, " +
                        "cap.capDescricao, " +
                        "cap.capCodigo ";

        if (retornaUsuLogin) {
            corpo += ", " +
                    "usu.usuLogin ";
        }

        if (retornaMargem) {
            corpo += ", "
                   + "rse.rseMargem, "
                   + "rse.rseMargemRest, "
                   + "rse.rseMargemUsada, "
                   + "rse.rseMargem2, "
                   + "rse.rseMargemRest2, "
                   + "rse.rseMargemUsada2, "
                   + "rse.rseMargem3, "
                   + "rse.rseMargemRest3, "
                   + "rse.rseMargemUsada3";

        }


        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.subOrgao sbo");
        corpoBuilder.append(" LEFT OUTER JOIN rse.unidade uni");
        corpoBuilder.append(" LEFT OUTER JOIN rse.cargoRegistroServidor crs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.padraoRegistroServidor prs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.postoRegistroServidor pos");
        corpoBuilder.append(" LEFT OUTER JOIN rse.tipoRegistroServidor trs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.capacidadeRegistroSer cap");
        corpoBuilder.append(" LEFT OUTER JOIN rse.vinculoRegistroServidor vrs");
        if (retornaUsuLogin) {
            corpoBuilder.append(" LEFT OUTER JOIN ser.usuarioSerSet usuSer ");
            corpoBuilder.append(" LEFT OUTER JOIN usuSer.usuario usu ");
        }


        corpoBuilder.append(" WHERE 1 = 1");
        if (retornaUsuLogin) {
            if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
            } else {
                corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
            }
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" and rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" and ser.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        if (!TextHelper.isNull(vrsCodigo)) {
            corpoBuilder.append(" and vrs.vrsCodigo ").append(criaClausulaNomeada("vrsCodigo", vrsCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (!TextHelper.isNull(vrsCodigo)) {
            defineValorClausulaNomeada("vrsCodigo", vrsCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        String[] fields = new String[] {
                Columns.EST_CSE_CODIGO,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.ORG_CNPJ,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_MATRICULA_INST,
                Columns.RSE_PRAZO,
                Columns.RSE_TIPO,
                Columns.RSE_BANCO_SAL,
                Columns.RSE_AGENCIA_SAL,
                Columns.RSE_AGENCIA_DV_SAL,
                Columns.RSE_CONTA_SAL,
                Columns.RSE_CONTA_DV_SAL,
                Columns.RSE_BANCO_SAL_2,
                Columns.RSE_AGENCIA_SAL_2,
                Columns.RSE_AGENCIA_DV_SAL_2,
                Columns.RSE_CONTA_SAL_2,
                Columns.RSE_CONTA_DV_SAL_2,
                Columns.RSE_DATA_ADMISSAO,
                Columns.RSE_SALARIO,
                Columns.RSE_PROVENTOS,
                Columns.RSE_ASSOCIADO,
                Columns.RSE_CLT,
                Columns.RSE_PARAM_QTD_ADE_DEFAULT,
                Columns.RSE_DATA_FIM_ENGAJAMENTO,
                Columns.RSE_DATA_LIMITE_PERMANENCIA,
                Columns.RSE_ESTABILIZADO,
                Columns.RSE_DATA_CARGA,
                Columns.RSE_DATA_CTC,
                Columns.RSE_DATA_ALTERACAO,
                Columns.RSE_BASE_CALCULO,
                Columns.RSE_OBS,
                Columns.RSE_MUNICIPIO_LOTACAO,
                Columns.RSE_PRACA,
                Columns.RSE_DESCONTOS_COMP,
                Columns.RSE_DESCONTOS_FACU,
                Columns.RSE_OUTROS_DESCONTOS,
                Columns.RSE_DATA_SAIDA,
                Columns.RSE_DATA_ULT_SALARIO,
                Columns.RSE_PEDIDO_DEMISSAO,
                Columns.RSE_DATA_RETORNO,
                Columns.RSE_MOTIVO_BLOQUEIO,
                Columns.RSE_MOTIVO_FALTA_MARGEM,
                Columns.RSE_MARGEM,
                Columns.RSE_MARGEM_USADA,
                Columns.RSE_MARGEM_REST,
                Columns.RSE_MARGEM_2,
                Columns.RSE_MARGEM_USADA_2,
                Columns.RSE_MARGEM_REST_2,
                Columns.RSE_MARGEM_3,
                Columns.RSE_MARGEM_USADA_3,
                Columns.RSE_MARGEM_REST_3,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_PRIMEIRO_NOME,
                Columns.SER_ULTIMO_NOME,
                Columns.SER_CPF,
                Columns.SER_DATA_NASC,
                Columns.SER_CEP,
                Columns.SER_NRO_IDT,
                Columns.SER_EMISSOR_IDT,
                Columns.SER_UF_IDT,
                Columns.SER_DATA_IDT,
                Columns.SER_END,
                Columns.SER_BAIRRO,
                Columns.SER_CIDADE,
                "ENDERECO",
                Columns.SER_NOME_MAE,
                Columns.SER_NOME_PAI,
                Columns.SER_NOME_CONJUGE,
                Columns.SER_TEL,
                Columns.SER_CELULAR,
                Columns.SER_EMAIL,
                Columns.SER_SEXO,
                Columns.SER_EST_CIVIL,
                Columns.SER_QTD_FILHOS,
                Columns.SER_NES_CODIGO,
                Columns.SER_THA_CODIGO,
                Columns.SER_NACIONALIDADE,
                Columns.SER_CART_PROF,
                Columns.SER_PIS,
                Columns.SER_END,
                Columns.SER_NRO,
                Columns.SER_COMPL,
                Columns.SER_UF,
                Columns.SER_CID_NASC,
                Columns.SER_UF_NASC,
                Columns.RSE_VRS_CODIGO,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.SBO_CODIGO,
                Columns.SBO_DESCRICAO,
                Columns.SBO_IDENTIFICADOR,
                Columns.UNI_CODIGO,
                Columns.UNI_DESCRICAO,
                Columns.UNI_IDENTIFICADOR,
                Columns.CRS_CODIGO,
                Columns.CRS_DESCRICAO,
                Columns.CRS_IDENTIFICADOR,
                Columns.CRS_VLR_DESC_MAX,
                Columns.PRS_CODIGO,
                Columns.PRS_IDENTIFICADOR,
                Columns.PRS_DESCRICAO,
                Columns.POS_CODIGO,
                Columns.POS_DESCRICAO,
                Columns.TRS_DESCRICAO,
                Columns.VRS_DESCRICAO,
                Columns.CAP_DESCRICAO,
                Columns.CAP_CODIGO
        };

        if (retornaUsuLogin) {
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 1);
            fields2[fields.length] = Columns.USU_LOGIN;
            fields = Arrays.copyOf(fields2, fields2.length);
        }

        if (retornaMargem) {
            String[] fields2 = Arrays.copyOf(fields, fields.length + 9);
            fields2[fields.length + 0] = Columns.RSE_MARGEM;
            fields2[fields.length + 1] = Columns.RSE_MARGEM_REST;
            fields2[fields.length + 2] = Columns.RSE_MARGEM_USADA;
            fields2[fields.length + 3] = Columns.RSE_MARGEM_2;
            fields2[fields.length + 4] = Columns.RSE_MARGEM_REST_2;
            fields2[fields.length + 5] = Columns.RSE_MARGEM_USADA_2;
            fields2[fields.length + 6] = Columns.RSE_MARGEM_3;
            fields2[fields.length + 7] = Columns.RSE_MARGEM_REST_3;
            fields2[fields.length + 8] = Columns.RSE_MARGEM_USADA_3;
            fields = Arrays.copyOf(fields2, fields2.length);
        }

        return fields;
    }
}