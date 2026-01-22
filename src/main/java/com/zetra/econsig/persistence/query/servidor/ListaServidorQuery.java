package com.zetra.econsig.persistence.query.servidor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServidorQuery</p>
 * <p>Description: Listagem de Servidores</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorQuery extends HQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaServidorQuery.class);

    private AcessoSistema responsavel;
    public String tipo;
    public String codigo;
    public String estCodigo;
    public String estIdentificador;
    public String orgCodigo;
    public String orgIdentificador;
    public String rseCodigo;
    public String rseMatricula;
    public String serCPF;
    public String serNome;
    public String serSobrenome;
    public Date serDataNascimento;
    public String numerContratoBeneficio;
    public boolean count = false;
    public boolean pesquisaExata = false;
    public List<String> rseSrsCodigo;
    public List<String> listaMatricula;
    public boolean validaPermissionario = false;
    public boolean filtroVinculo = ParamSist.paramEquals(CodedValues.TPC_FILTRO_VINCULO_CONSULTA_MARGEM, CodedValues.TPC_SIM, responsavel);
    public String categoria;
    public boolean temContrato;
    public List<String> status;
    public String operacao;
    public boolean atributTemContratoNaoEspecificado = true;
    public boolean buscaBeneficiario = false;
    public String vrsCodigo;
    public boolean retornaUsuLogin;

    public ListaServidorQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public void setCriterio(TransferObject criterios) {
        responsavel = TextHelper.isNull(responsavel) ? (AcessoSistema) criterios.getAttribute("responsavel") : responsavel;
        serNome = (String) criterios.getAttribute("NOME");
        serSobrenome = (String) criterios.getAttribute("SOBRENOME");
        serDataNascimento = (Date) criterios.getAttribute("serDataNascimento");

        if (TextHelper.isNull(rseCodigo)) {
            rseCodigo = (String) criterios.getAttribute("RSE_CODIGO");
        }
        if (TextHelper.isNull(orgCodigo)) {
            orgCodigo = (String) criterios.getAttribute("ORG_CODIGO");
        }
        if (TextHelper.isNull(estCodigo)) {
            estCodigo = (String) criterios.getAttribute("EST_CODIGO");
        }


        if (!TextHelper.isNull(criterios.getAttribute("operacao")) && "Pesquisar Servidor v8_0".equalsIgnoreCase((String) criterios.getAttribute("operacao"))) {
            categoria = (String) criterios.getAttribute("categoria");
            status = (List<String>) criterios.getAttribute("status");

            if (!TextHelper.isNull(criterios.getAttribute("temContrato"))) {
                temContrato = (Boolean) criterios.getAttribute("temContrato");
                atributTemContratoNaoEspecificado = false;
            } else {
                atributTemContratoNaoEspecificado = true;
            }

        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo =
                "select count(distinct rse.rseCodigo) as total ";
        } else {
            corpo =
                "select distinct " +
                "rse.rseCodigo, " +
                "rse.rseMatricula, " +
                "rse.rseMatriculaInst, " +
                "rse.rsePrazo, " +
                "rse.rseTipo, " +
                "rse.rseDataAdmissao, " +
                "rse.rseSalario, " +
                "rse.rseProventos, " +
                "rse.rseMargem, " +
                "rse.rseMargemUsada, " +
                "rse.rseMargemRest, " +
                "rse.rseMargem2, " +
                "rse.rseMargemUsada2, " +
                "rse.rseMargemRest2, " +
                "rse.rseMargem3, " +
                "rse.rseMargemUsada3, " +
                "rse.rseMargemRest3, " +
                "rse.rseBancoSal, " +
                "rse.rseAgenciaSal, " +
                "rse.rseContaSal, " +
                "rse.rseBancoSal2, " +
                "rse.rseAgenciaSal2, " +
                "rse.rseContaSal2, " +
                "rse.rseDataFimEngajamento, " +
                "rse.rseDataLimitePermanencia, " +
                "to_locale_date(rse.rseDataFimEngajamento) as FORMATED_DATA_FIM_ENGAJAMENTO, " +
                "to_locale_date(rse.rseDataLimitePermanencia) as FORMATED_DATA_LIMITE_PERMANENCIA, " +
                "text_to_string(rse.rsePraca), " +
                "rse.rsePrazo, " +
                "rse.rseAssociado, " +
                "rse.rseClt, " +
                "rse.rseEstabilizado, " +
                "rse.rseDescontosComp, " +
                "rse.rseDescontosFacu, " +
                "rse.rseOutrosDescontos, " +
                "rse.rseDataRetorno, " +
                    "rse.srsCodigo, " +
                "rse.rsePontuacao, " +
                "srs.srsDescricao, " +
                "ser.serCodigo, " +
                "ser.serNome, " +
                "ser.serTitulacao, " +
                "ser.serPrimeiroNome, " +
                "ser.serNomeMeio, " +
                "ser.serUltimoNome, " +
                "ser.serCpf, " +
                "ser.serDataNasc, " +
                "ser.serNroIdt, " +
                "ser.serEmissorIdt, " +
                "ser.serUfIdt, " +
                "ser.serDataIdt, " +
                "ser.serCartProf, " +
                "ser.serPis, " +
                "ser.serEmail, " +
                "ser.serTel, " +
                "ser.serCelular, " +
                "org.orgCodigo, " +
                "org.orgIdentificador, " +
                "org.orgNome, " +
                "est.estCodigo, " +
                "est.estIdentificador, " +
                "est.estNome, " +
                "concatenar(concatenar(concatenar(coalesce(to_string(ser.serEnd), ''), ' - '), concatenar(coalesce(to_string(ser.serNro), ''), ' - ')), coalesce(to_string(ser.serCompl), '')) as ENDERECO, " +
                "ser.serEnd, " +
                "ser.serNro, " +
                "ser.serCompl, " +
                "ser.serBairro, " +
                "ser.serCidade, " +
                "ser.serUf, " +
                "ser.serCep, " +
                "ser.serNomeMae, " +
                "ser.serNomePai, " +
                "ser.serNacionalidade, " +
                "ser.serSexo, " +
                "ser.serEstCivil, " +
                "ser.serCidNasc, " +
                "ser.serUfNasc, " +
                "ser.serDataValidacaoEmail, " +
                "ser.serQtdFilhos, " +
                "ser.serNomeConjuge, " +
                "pos.posCodigo, " +
                "pos.posDescricao ";


            if (retornaUsuLogin) {
                corpo += ", " +
                        "usu.usuLogin ";
            }


            if (filtroVinculo) {
                corpo += ", vrs.vrsDescricao ";
                corpo += ", vrs.vrsCodigo ";
            }

            if (validaPermissionario) {
                corpo += ", prm.prmCodigo ";
            }

            if (buscaBeneficiario) {
                corpo += ", bfc.bfcCpf ";
                corpo += ", cbe.cbeNumero ";
            }
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from RegistroServidor rse");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs ");
        corpoBuilder.append(" left join rse.postoRegistroServidor pos ");
        if (retornaUsuLogin) {
            corpoBuilder.append(" left join ser.usuarioSerSet usuSer ");
            corpoBuilder.append(" left join usuSer.usuario usu ");
        }

        if (validaPermissionario) {
            corpoBuilder.append(" inner join rse.permissionarioSet prm ");
        }

        if (filtroVinculo) {
            corpoBuilder.append(" left join rse.vinculoRegistroServidor vrs ");
        }

        if (buscaBeneficiario) {
            corpoBuilder.append(" inner join ser.beneficiarioSet bfc ");
            corpoBuilder.append(" inner join bfc.contratoBeneficioSet cbe ");
        }

        if ("CSA".equalsIgnoreCase(tipo) || "COR".equalsIgnoreCase(tipo)) {
            // Se o tipo é consignatária ou correspondente, faz a ligação com o convênio
            corpoBuilder.append(" inner join org.convenioSet cnv ");

            if ("COR".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc ");
            }
        } else if ("EST".equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" inner join est.orgaoSet ORG_EST ");
        }

        corpoBuilder.append(" where 1=1 ");

        if (retornaUsuLogin) {
            if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
            } else {
                corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
            }
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (filtroVinculo && !TextHelper.isNull(vrsCodigo)) {
            corpoBuilder.append(" and vrs.vrsCodigo ").append(criaClausulaNomeada("vrsCodigo", vrsCodigo));
        }

        // Servidores excluídos não serão listados na pesquisa
        if (ParamSist.paramEquals(CodedValues.TPC_IGNORA_SERVIDORES_EXCLUIDOS, CodedValues.TPC_SIM, responsavel)) {
            corpoBuilder.append(" AND srs.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        // DESENV-20106 : Omitir servidores que não aceitaram o termo de uso quando a pesquisa é feita por CSA/COR
        corpoBuilder.append(gerarClausulaServidorComTermoUso(responsavel));

        // DESENV-23764 LICIT-10658 - PM Presidente Prudente - SP
        corpoBuilder.append(gerarClausulaRegistroServidorOcultoCsa(responsavel));

        if (!atributTemContratoNaoEspecificado && temContrato) {
            if ("CSA".equalsIgnoreCase(tipo) || "COR".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND EXISTS (select 1 from AutDesconto ade");
                corpoBuilder.append(" inner join ade.verbaConvenio vco");
                corpoBuilder.append(" where ade.registroServidor.rseCodigo = rse.rseCodigo");
                corpoBuilder.append(" and vco.convenio.cnvCodigo = cnv.cnvCodigo)");

                if ("CSA".equalsIgnoreCase(tipo)) {
                    corpoBuilder.append(" AND cnv.consignataria.csaCodigo = :codigoEntidade");
                } else if ("COR".equalsIgnoreCase(tipo)) {
                    corpoBuilder.append(" AND crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                    corpoBuilder.append(" AND crc.correspondente.corCodigo = :codigoEntidade");
                }

                if (validaPermissionario) {
                    corpoBuilder.append(" AND cnv.consignataria.csaCodigo = prm.consignataria.csaCodigo");
                }
            } else if ("EST".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND est.estCodigo = :codigoEntidade");
            } else if ("ORG".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND org.orgCodigo = :codigoEntidade");
            }
        } else if(!atributTemContratoNaoEspecificado && !temContrato) {
            if ("CSA".equalsIgnoreCase(tipo) || "COR".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND NOT EXISTS (select 1 from AutDesconto ade");
                corpoBuilder.append(" inner join ade.verbaConvenio vco");
                corpoBuilder.append(" where ade.registroServidor.rseCodigo = rse.rseCodigo");
                corpoBuilder.append(" and vco.convenio.cnvCodigo = cnv.cnvCodigo)");

                if ("CSA".equalsIgnoreCase(tipo)) {
                    corpoBuilder.append(" AND cnv.consignataria.csaCodigo = :codigoEntidade");
                } else if ("COR".equalsIgnoreCase(tipo)) {
                    corpoBuilder.append(" AND crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                    corpoBuilder.append(" AND crc.correspondente.corCodigo = :codigoEntidade");
                }

                corpoBuilder.append(" and not exists (select 1 from AutDesconto ade1 where ade1.rseCodigo = rse.rseCodigo)");

                if (validaPermissionario) {
                    corpoBuilder.append(" AND cnv.consignataria.csaCodigo = prm.consignataria.csaCodigo");
                }
            } else if ("EST".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND est.estCodigo = :codigoEntidade");
            } else if ("ORG".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND org.orgCodigo = :codigoEntidade");
            }
        } else if ("CSA".equalsIgnoreCase(tipo) || "COR".equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" AND (cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" OR EXISTS (select 1 from AutDesconto ade");
            corpoBuilder.append(" inner join ade.verbaConvenio vco");
            corpoBuilder.append(" where ade.registroServidor.rseCodigo = rse.rseCodigo");
            corpoBuilder.append(" and vco.convenio.cnvCodigo = cnv.cnvCodigo)");
            corpoBuilder.append(")");

            if ("CSA".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND cnv.consignataria.csaCodigo = :codigoEntidade");
            } else if ("COR".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpoBuilder.append(" AND crc.correspondente.corCodigo = :codigoEntidade");
            }

            if (validaPermissionario) {
                corpoBuilder.append(" AND cnv.consignataria.csaCodigo = prm.consignataria.csaCodigo");
            }
        } else if ("EST".equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" AND est.estCodigo = :codigoEntidade");
        } else if ("ORG".equalsIgnoreCase(tipo)) {
            corpoBuilder.append(" AND org.orgCodigo = :codigoEntidade");
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        } else if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" AND est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        } else if (!TextHelper.isNull(orgIdentificador)) {
            if ("EST".equalsIgnoreCase(tipo)) {
                corpoBuilder.append(" AND ORG_EST.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
            } else {
                corpoBuilder.append(" AND org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
            }
        }

        // Adiciona cláusula de matricula e cpf
        // Se esta ativado o modulo beneficio eu ignore o a verificação do CPF na tb_servidor e olho agora na tb_beneficiario
        if (buscaBeneficiario) {
            corpoBuilder.append(gerarClausulaMatriculaCpf(rseMatricula, null, !pesquisaExata));
        } else {
            corpoBuilder.append(gerarClausulaMatriculaCpf(rseMatricula, serCPF, !pesquisaExata));
        }

        if ((listaMatricula != null) && (listaMatricula.size() > 0)) {
            corpoBuilder.append(" AND rse.rseMatricula ").append(criaClausulaNomeada("listaMatricula", listaMatricula));
        }

        if ((rseSrsCodigo != null) && (rseSrsCodigo.size() > 0)) {
            corpoBuilder.append(" AND srs.srsCodigo ").append(criaClausulaNomeada("srsCodigo", rseSrsCodigo));
        }

        // Valida se o permissionário está ativo
        if (validaPermissionario) {
            corpoBuilder.append(" AND prm.prmAtivo = ").append(CodedValues.STS_ATIVO).append("");
        }

        if (!TextHelper.isNull(serNome)) {
            corpoBuilder.append(" AND coalesce(nullif(ser.serPrimeiroNome, ''), ser.serNome) ").append(criaClausulaNomeada("serPrimeiroNome", CodedValues.LIKE_MULTIPLO + serNome));
        }

        if (!TextHelper.isNull(serSobrenome)) {
            corpoBuilder.append(" AND coalesce(nullif(ser.serUltimoNome, ''), ser.serNome) ").append(criaClausulaNomeada("serSobrenome", CodedValues.LIKE_MULTIPLO + serSobrenome));
        }

        if (!TextHelper.isNull(serDataNascimento)) {
            corpoBuilder.append(" AND ser.serDataNasc ").append(criaClausulaNomeada("serDataNascimento", serDataNascimento));
        }

        if (!TextHelper.isNull(categoria)) {
            corpoBuilder.append(" AND rse.rseTipo ").append(criaClausulaNomeada("rseTipo", categoria));
        }

        if ((status != null) && (status.size() > 0)) {
            corpoBuilder.append(" AND rse.srsCodigo ").append(criaClausulaNomeada("status", status));
        }

        if (buscaBeneficiario) {
            if (!TextHelper.isNull(numerContratoBeneficio)) {
                corpoBuilder.append(" AND cbe.cbeNumero = :numerContratoBeneficio ");
            }
            if (!TextHelper.isNull(serCPF)) {
                corpoBuilder.append(" AND bfc.bfcCpf = :serCPF ");
            }
        }

        if (!count) {
            // Ordenação pelo status, para que os ativos venham na frete dos excluidos
            corpoBuilder.append(" order by rse.srsCodigo, ser.serNome");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Se esta ativado o modulo beneficio eu ignore o a verificação do CPF na tb_servidor e olho agora na tb_beneficiario
        if (buscaBeneficiario) {
            definirClausulaMatriculaCpf(rseMatricula, null, !pesquisaExata, query);
        } else {
            definirClausulaMatriculaCpf(rseMatricula, serCPF, !pesquisaExata, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if ("CSA".equalsIgnoreCase(tipo) || "COR".equalsIgnoreCase(tipo) ||
                "EST".equalsIgnoreCase(tipo) || "ORG".equalsIgnoreCase(tipo)) {
            defineValorClausulaNomeada("codigoEntidade", codigo, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        } else if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        } else if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }

        if (!TextHelper.isNull(serNome)) {
            defineValorClausulaNomeada("serPrimeiroNome", CodedValues.LIKE_MULTIPLO + serNome + CodedValues.LIKE_MULTIPLO, query);
        }

        if (!TextHelper.isNull(serSobrenome)) {
            defineValorClausulaNomeada("serSobrenome", CodedValues.LIKE_MULTIPLO + serSobrenome + CodedValues.LIKE_MULTIPLO, query);
        }

        if (!TextHelper.isNull(serDataNascimento)) {
            defineValorClausulaNomeada("serDataNascimento", DateHelper.toSQLDate(serDataNascimento), query);
        }

        if (!TextHelper.isNull(categoria)) {
            defineValorClausulaNomeada("rseTipo", categoria, query);
        }

        if ((status != null) && (status.size() > 0)) {
            defineValorClausulaNomeada("status", status, query);
        }

        if ((listaMatricula != null) && (listaMatricula.size() > 0)) {
            defineValorClausulaNomeada("listaMatricula", listaMatricula, query);
        }

        if ((rseSrsCodigo != null) && (rseSrsCodigo.size() > 0)) {
            defineValorClausulaNomeada("srsCodigo", rseSrsCodigo, query);
        }

        if (buscaBeneficiario) {
            if (!TextHelper.isNull(numerContratoBeneficio)) {
                defineValorClausulaNomeada("numerContratoBeneficio", numerContratoBeneficio, query);
            }

            if (!TextHelper.isNull(serCPF)) {
                defineValorClausulaNomeada("serCPF", serCPF, query);
            }
        }

        if (filtroVinculo && !TextHelper.isNull(vrsCodigo)) {
            defineValorClausulaNomeada("vrsCodigo", vrsCodigo, query);
        }

        if (responsavel.isCsaCor()) {
            defineValorClausulaNomeada("csaCodigoResponsavel", responsavel.getCsaCodigo(), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        String[] fields = {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_MATRICULA_INST,
                Columns.RSE_PRAZO,
                Columns.RSE_TIPO,
                Columns.RSE_DATA_ADMISSAO,
                Columns.RSE_SALARIO,
                Columns.RSE_PROVENTOS,
                Columns.RSE_MARGEM,
                Columns.RSE_MARGEM_USADA,
                Columns.RSE_MARGEM_REST,
                Columns.RSE_MARGEM_2,
                Columns.RSE_MARGEM_USADA_2,
                Columns.RSE_MARGEM_REST_2,
                Columns.RSE_MARGEM_3,
                Columns.RSE_MARGEM_USADA_3,
                Columns.RSE_MARGEM_REST_3,
                Columns.RSE_BANCO_SAL,
                Columns.RSE_AGENCIA_SAL,
                Columns.RSE_CONTA_SAL,
                Columns.RSE_BANCO_SAL_2,
                Columns.RSE_AGENCIA_SAL_2,
                Columns.RSE_CONTA_SAL_2,
                Columns.RSE_DATA_FIM_ENGAJAMENTO,
                Columns.RSE_DATA_LIMITE_PERMANENCIA,
                "FORMATED_DATA_FIM_ENGAJAMENTO",
                "FORMATED_DATA_LIMITE_PERMANENCIA",
                Columns.RSE_PRACA,
                Columns.RSE_PRAZO,
                Columns.RSE_ASSOCIADO,
                Columns.RSE_CLT,
                Columns.RSE_ESTABILIZADO,
                Columns.RSE_DESCONTOS_COMP,
                Columns.RSE_DESCONTOS_FACU,
                Columns.RSE_OUTROS_DESCONTOS,
                Columns.RSE_DATA_RETORNO,
                Columns.SRS_CODIGO,
                Columns.RSE_PONTUACAO,
                Columns.SRS_DESCRICAO,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_TITULACAO,
                Columns.SER_PRIMEIRO_NOME,
                Columns.SER_NOME_MEIO,
                Columns.SER_ULTIMO_NOME,
                Columns.SER_CPF,
                Columns.SER_DATA_NASC,
                Columns.SER_NRO_IDT,
                Columns.SER_EMISSOR_IDT,
                Columns.SER_UF_IDT,
                Columns.SER_DATA_IDT,
                Columns.SER_CART_PROF,
                Columns.SER_PIS,
                Columns.SER_EMAIL,
                Columns.SER_TEL,
                Columns.SER_CELULAR,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                "ENDERECO",
                Columns.SER_END,
                Columns.SER_NRO,
                Columns.SER_COMPL,
                Columns.SER_BAIRRO,
                Columns.SER_CIDADE,
                Columns.SER_UF,
                Columns.SER_CEP,
                Columns.SER_NOME_MAE,
                Columns.SER_NOME_PAI,
                Columns.SER_NACIONALIDADE,
                Columns.SER_SEXO,
                Columns.SER_EST_CIVIL,
                Columns.SER_CID_NASC,
                Columns.SER_UF_NASC,
                Columns.SER_DATA_VALIDACAO_EMAIL,
                Columns.SER_QTD_FILHOS,
                Columns.SER_NOME_CONJUGE,
                Columns.POS_CODIGO,
                Columns.POS_DESCRICAO
        };

        if (retornaUsuLogin) {
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 1);
            fields2[fields.length] = Columns.USU_LOGIN;
            fields = Arrays.copyOf(fields2, fields2.length);
        }

        if (filtroVinculo) {
            // Se parâmetro filtro vinculo estiver habilitado
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 2);
            fields2[fields.length] = Columns.VRS_DESCRICAO;
            fields2[fields.length + 1] =  Columns.VRS_CODIGO;
            fields = Arrays.copyOf(fields2, fields2.length);
        }

        if (validaPermissionario) {
            // Se valida permissionário, adiciona a lista de campos código do permissionário
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 1);
            fields2[fields.length] = Columns.PRM_CODIGO;
            fields = Arrays.copyOf(fields2, fields2.length);
        }

        if (buscaBeneficiario) {
            // Se tem modolo beneficio configurado
            final String[] fields2 = Arrays.copyOf(fields, fields.length + 2);
            fields2[fields.length] = Columns.BFC_CPF;
            fields2[fields.length + 1] = Columns.CBE_NUMERO;
            fields = Arrays.copyOf(fields2, fields2.length);
        }

        return fields;
    }

    public static String gerarClausulaMatriculaCpf(String rseMatricula, String serCpf, boolean likeMatricula) {
        final StringBuilder query = new StringBuilder();

        if (!TextHelper.isNull(rseMatricula)) {
            // Pega o tamanho mínimo da matrícula
            int tamanhoMatricula = 0;
            try {
                tamanhoMatricula = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, AcessoSistema.getAcessoUsuarioSistema()).toString());
            } catch (final NumberFormatException ex) {
            }

            // Parâmetros de sistema necessários
            final boolean matriculaNumerica = ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            // Verifica se a matrícula é numerica
            try {
                if (matriculaNumerica) {
                    rseMatricula = Long.valueOf(rseMatricula).toString();
                }
            } catch (final NumberFormatException ex) {
            }

            // Se a o tamanho da matrícula é maior ou igual ao tamanho minimo
            // e a consulta deve usar o like, então faz o like com o curinga no final
            if ((rseMatricula.length() >= tamanhoMatricula) && likeMatricula) {
                if (DAOFactory.isOracle()) {
                    query.append(" AND like_ci_ai(rse.rseMatricula, :rseMatricula) ");
                } else {
                    query.append(" AND rse.rseMatricula like :rseMatricula ");
                }
            } else {
                query.append(" AND rse.rseMatricula = :rseMatricula");
            }
        }


        if (!TextHelper.isNull(serCpf) && !TextHelper.isNull(rseMatricula)) {
            query.append(" AND (ser.serCpf = :serCpf OR nullif(trim(ser.serCpf), '') is null)");
        } else if (!TextHelper.isNull(serCpf)) {
            query.append(" AND ser.serCpf = :serCpf");
        }

        return query.toString();
    }

    public static String gerarClausulaNativaMatriculaCpf(String rseMatricula, String serCpf, boolean likeMatricula) {
        String clausulaMatriculaCpf = gerarClausulaMatriculaCpf(rseMatricula, serCpf, likeMatricula);

        // Substitui as referências do mapeamento hibernate para os nomes das colunas no banco de dados
        clausulaMatriculaCpf = clausulaMatriculaCpf.replaceAll("rse.rseMatricula", "rse.rse_matricula");
        return clausulaMatriculaCpf.replaceAll("ser.serCpf", "ser.ser_cpf");
    }

    public static void definirClausulaMatriculaCpf(String rseMatricula, String serCpf, boolean likeMatricula, Query<Object[]> query) throws HQueryException {
        if (!TextHelper.isNull(rseMatricula)) {
            // Pega o tamanho mínimo da matrícula
            int tamanhoMatricula = 0;
            try {
                tamanhoMatricula = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, AcessoSistema.getAcessoUsuarioSistema()).toString());
            } catch (final NumberFormatException ex) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.erro.valor.incorreto.param.sistema", AcessoSistema.getAcessoUsuarioSistema(), CodedValues.TPC_TAMANHO_MATRICULA));
            }

            // Parâmetros de sistema necessários
            final boolean matriculaNumerica = ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            // Verifica se a matrícula é numerica
            try {
                if (matriculaNumerica) {
                    rseMatricula = Long.valueOf(rseMatricula).toString();
                }
            } catch (final NumberFormatException ex) {
                throw new HQueryException("mensagem.erro.rse.matricula.invalida.arg0", AcessoSistema.getAcessoUsuarioSistema(), ex, rseMatricula);
            }

            // Se a o tamanho da matrícula é maior ou igual ao tamanho minimo
            // e a consulta deve usar o like, então faz o like com o curinga no final
            if ((rseMatricula.length() >= tamanhoMatricula) && likeMatricula) {
                if (DAOFactory.isOracle()) {
                    query.setParameter("rseMatricula", rseMatricula + ".*");
                } else {
                    query.setParameter("rseMatricula", rseMatricula + "%");
                }
            } else {
                query.setParameter("rseMatricula", rseMatricula);
            }
        }

        if (!TextHelper.isNull(serCpf)) {
            query.setParameter("serCpf", serCpf);
        }
    }

    public static String gerarClausulaServidorComTermoUso(AcessoSistema responsavel) {
        final StringBuilder query = new StringBuilder();
        if (ParamSist.paramEquals(CodedValues.TPC_OMITIR_SERVIDORES_SEM_ACEITE_TERMO_DE_USO_PARA_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor()) {
            query.append(" AND EXISTS (");
            query.append("  select 1 from ser.usuarioSerSet _usr");
            query.append("  inner join _usr.usuario _usu");
            query.append("  inner join _usu.ocorrenciaUsuarioByUsuCodigoSet _ous");
            query.append("  where _ous.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ACEITACAO_TERMO_DE_USO).append("'");
            if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel)) {
                query.append("  and _usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula) ");
            } else  {
                query.append("  and _usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula) ");
            }
            query.append(")");
        }
        return query.toString();
    }

    public static String gerarClausulaRegistroServidorOcultoCsa(AcessoSistema responsavel) {
        final StringBuilder query = new StringBuilder();
        if (responsavel.isCsaCor()) {
            query.append(" AND NOT EXISTS (SELECT 1 FROM RegistroServidorOcultoCsa roc");
            query.append(" WHERE roc.registroServidor.rseCodigo = rse.rseCodigo");
            query.append(" AND roc.consignataria.csaCodigo = :csaCodigoResponsavel");
            query.append(") ");
        }
        return query.toString();
    }
}
