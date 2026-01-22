package com.zetra.econsig.persistence.query.servidor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

/**
 * <p>Title: ListaServidorCadastroQuery</p>
 * <p>Description: Retornar informações para perguntas no cadastro de usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorCadastroQuery extends HQuery {

    public boolean count = false;
    public List<TransferObject> excecoes = null;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<Object> nomesMae = null;
        List<Object> nomesPai = null;
        List<Object> nomesConjuge = null;
        List<Object> enderecos = null;
        List<Object> bairros = null;
        List<Object> cidades = null;
        List<Object> ceps = null;
        List<Object> cidadesNascimento = null;
        List<Object> telefones = null;
        List<Object> celulares = null;
        List<Object> numerosIdentidade = null;
        List<Object> numerosCartProf = null;
        List<Object> numerosPis = null;
        List<Object> categorias = null;
        List<Object> municipiosLotacao = null;

        List<Object> complemento = null;
        List<Object> serCodigo = null;
        List<Object> rseDataLimitePermanencia = null;
        List<Object> rseDataFimEngajamento = null;


        StringBuilder corpoBuilder = new StringBuilder(" select ");

        if (!count) {
            corpoBuilder.append("ser.serCodigo, ");
            corpoBuilder.append("ser.serEnd, ");
            corpoBuilder.append("ser.serNro, ");
            corpoBuilder.append("ser.serBairro, ");
            corpoBuilder.append("ser.serCidade, ");
            corpoBuilder.append("ser.serCep, ");
            corpoBuilder.append("ser.serDataNasc, ");
            corpoBuilder.append("ser.serCidNasc, ");
            corpoBuilder.append("ser.serNomeMae, ");
            corpoBuilder.append("ser.serNomePai, ");
            corpoBuilder.append("ser.serNomeConjuge, ");
            corpoBuilder.append("ser.serTel, ");
            corpoBuilder.append("ser.serCelular, ");
            corpoBuilder.append("ser.serDataIdt, ");
            corpoBuilder.append("ser.serNroIdt, ");
            corpoBuilder.append("ser.serCartProf, ");
            corpoBuilder.append("ser.serPis, ");

            corpoBuilder.append("ser.serCompl, ");
            corpoBuilder.append("ser.serNacionalidade, ");
            corpoBuilder.append("ser.serUf, ");
            corpoBuilder.append("ser.serUfIdt, ");
            corpoBuilder.append("ser.serUfNasc, ");
            corpoBuilder.append("ser.serEmissorIdt, ");
            corpoBuilder.append("ser.serEstCivil, ");

            corpoBuilder.append("rse.rseDataAdmissao, ");
            corpoBuilder.append("rse.rseTipo, ");
            corpoBuilder.append("rse.rseMunicipioLotacao, ");
            corpoBuilder.append("rse.rseAgenciaSal, ");
            corpoBuilder.append("rse.rseContaSal, ");

            corpoBuilder.append("rse.rseBancoSal, ");
            corpoBuilder.append("rse.rseBancoSal2, ");
            corpoBuilder.append("rse.rseAgenciaSal2, ");
            corpoBuilder.append("rse.rseContaSal2, ");
            corpoBuilder.append("rse.rseDataFimEngajamento, ");
            corpoBuilder.append("rse.rseDataLimitePermanencia, ");
            corpoBuilder.append("to_locale_date(rse.rseDataFimEngajamento) as FORMATED_DATA_FIM_ENGAJAMENTO,");
            corpoBuilder.append("to_locale_date(rse.rseDataLimitePermanencia) as FORMATED_DATA_LIMITE_PERMANENCIA,");
            corpoBuilder.append("text_to_string(rse.rsePraca), ");
            corpoBuilder.append("rse.rsePrazo, ");
            corpoBuilder.append("rse.rseAssociado, ");
            corpoBuilder.append("rse.rseClt, ");
            corpoBuilder.append("rse.rseEstabilizado ");

        } else {
            corpoBuilder.append("count(ser.serCodigo) ");
        }

        corpoBuilder.append(" from Servidor ser");
        corpoBuilder.append(" inner join ser.registroServidorSet rse");

        corpoBuilder.append(" where 1 = 1");

        serCodigo = getListaExcecoes(Columns.SER_CODIGO);
        corpoBuilder.append(" and ser.serCodigo not in (:serCodigo)");

        try {
            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_MAE, responsavel)) {
                // Verifica se o nome da mãe foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serNomeMae), '') is not null");
                // Adiciona exceção
                nomesMae = getListaExcecoes(Columns.SER_NOME_MAE);
                if (!nomesMae.isEmpty()) {
                    corpoBuilder.append(" and ser.serNomeMae not in (:nomesMae)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_PAI, responsavel)) {
                // Verifica se o nome do pai foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serNomePai), '') is not null");
                // Adiciona exceção
                nomesPai = getListaExcecoes(Columns.SER_NOME_PAI);
                if (!nomesPai.isEmpty()) {
                    corpoBuilder.append(" and ser.serNomePai not in (:nomesPai)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_CONJUGE, responsavel)) {
                // Verifica se o nome do conjuge foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serNomeConjuge), '') is not null");
                // Adiciona exceção
                nomesConjuge = getListaExcecoes(Columns.SER_NOME_CONJUGE);
                if (!nomesConjuge.isEmpty()) {
                    corpoBuilder.append(" and ser.serNomeConjuge not in (:nomesConjuge)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_NASC, responsavel) ||
                    ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_NASC, responsavel) ||
                    ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_NASC, responsavel)) {
                // Verifica se a data de nascimento foi preenchida
                corpoBuilder.append(" and ser.serDataNasc is not null");
                // Campo de input não adiciona exceção
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CIDADE_NASC, responsavel)) {
                // Verifica se a cidade de nascimento foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serCidNasc), '') is not null");
                // Adiciona exceção
                cidadesNascimento = getListaExcecoes(Columns.SER_CID_NASC);
                if (!cidadesNascimento.isEmpty()) {
                    corpoBuilder.append(" and ser.serCidNasc not in (:cidadesNascimento)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ENDERECO, responsavel)) {
                // Verifica se o endereço foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serEnd, ''), '') is not null");
                // Adiciona exceção
                enderecos = getListaExcecoes(Columns.SER_END);
                if (!enderecos.isEmpty()) {
                    corpoBuilder.append(" and ser.serEnd not in (:enderecos)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_ENDERECO, responsavel)) {
                // Verifica se o número do endereço foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serNro, ''), '') is not null");
                // Campo de input não adiciona exceção
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BAIRRO, responsavel)) {
                // Verifica se o bairro foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serBairro), '') is not null");
                // Adiciona exceção
                bairros = getListaExcecoes(Columns.SER_BAIRRO);
                if (!bairros.isEmpty()) {
                    corpoBuilder.append(" and ser.serBairro not in (:bairros)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CIDADE, responsavel)) {
                // Verifica se a cidade foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serCidade), '') is not null");
                // Adiciona exceção
                cidades = getListaExcecoes(Columns.SER_CIDADE);
                if (!cidades.isEmpty()) {
                    corpoBuilder.append(" and ser.serCidade not in (:cidades)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CEP, responsavel)) {
                // Verifica se a cep foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serCep), '') is not null");
                // Adiciona exceção
                ceps = getListaExcecoes(Columns.SER_CEP);
                if (!ceps.isEmpty()) {
                    corpoBuilder.append(" and ser.serCep not in (:ceps)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_TELEFONE, responsavel)) {
                // Verifica se o telefone foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serTel), '') is not null");
                // Adiciona exceção
                telefones = getListaExcecoes(Columns.SER_TEL);
                if (!telefones.isEmpty()) {
                    corpoBuilder.append(" and ser.serTel not in (:telefones)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CELULAR, responsavel)) {
                // Verifica se o telefone foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serCelular), '') is not null");
                // Adiciona exceção
                celulares = getListaExcecoes(Columns.SER_CELULAR);
                if (!celulares.isEmpty()) {
                    corpoBuilder.append(" and ser.serCelular not in (:celulares)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_IDT, responsavel) ||
                    ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_IDT, responsavel) ||
                    ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_IDT, responsavel)) {
                // Verifica se a data de emissão do RG foi preenchida
                corpoBuilder.append(" and ser.serDataIdt is not null");
                // Campo de input não adiciona exceção
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_IDENTIDADE, responsavel)) {
                // Verifica se o número de identidade foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serNroIdt), '') is not null");
                // Adiciona exceção
                numerosIdentidade = getListaExcecoes(Columns.SER_NRO_IDT);
                if (!numerosIdentidade.isEmpty()) {
                    corpoBuilder.append(" and ser.serNroIdt not in (:numerosIdentidade)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_CART_TRABALHO, responsavel)) {
                // Verifica se o número da carteira de trabalho foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serCartProf), '') is not null");
                // Adiciona exceção
                numerosCartProf = getListaExcecoes(Columns.SER_CART_PROF);
                if (!numerosCartProf.isEmpty()) {
                    corpoBuilder.append(" and ser.serCartProf not in (:numerosCartProf)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_PIS, responsavel)) {
                // Verifica se o número do PIS foi preenchido
                corpoBuilder.append(" and nullif(trim(ser.serPis), '') is not null");
                // Adiciona exceção
                numerosPis = getListaExcecoes(Columns.SER_PIS);
                if (!numerosPis.isEmpty()) {
                    corpoBuilder.append(" and ser.serPis not in (:numerosPis)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CATEGORIA, responsavel)) {
                // Verifica se o número do PIS foi preenchido
                corpoBuilder.append(" and nullif(trim(rse.rseTipo), '') is not null");
                // Adiciona exceção
                categorias = getListaExcecoes(Columns.RSE_TIPO);
                if (!categorias.isEmpty()) {
                    corpoBuilder.append(" and rse.rseTipo not in (:categorias)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_ADMISSAO, responsavel) ||
                    ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_ADMISSAO, responsavel) ||
                    ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_ADMISSAO, responsavel)) {
                // Verifica se a data de admissão foi preenchida
                corpoBuilder.append(" and rse.rseDataAdmissao is not null");
                // Campo de input não adiciona exceção
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_AGENCIA_SALARIO, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseAgenciaSal), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CONTA_SALARIO, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseContaSal), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_EST_CIVIL, responsavel)) {
                corpoBuilder.append(" and nullif(trim(ser.serEstCivil), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MUNICIPIO_LOTACAO, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseMunicipioLotacao), '') is not null");
                // Adiciona exceção
                municipiosLotacao = getListaExcecoes(Columns.RSE_MUNICIPIO_LOTACAO);
                if (!municipiosLotacao.isEmpty()) {
                    corpoBuilder.append(" and rse.rseMunicipioLotacao not in (:municipiosLotacao)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_COMPL, responsavel)) {
                corpoBuilder.append(" and nullif(trim(ser.serCompl), '') is not null");
                // Adiciona exceção
                complemento = getListaExcecoes(Columns.SER_COMPL);
                if (!complemento.isEmpty()) {
                    corpoBuilder.append(" and ser.serCompl not in (:serCompl)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NACIONALIDADE, responsavel)) {
                corpoBuilder.append(" and nullif(trim(ser.serNacionalidade), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_EMISSOR_IDT, responsavel)) {
                corpoBuilder.append(" and nullif(trim(ser.serEmissorIdt), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF, responsavel)) {
                corpoBuilder.append(" and nullif(trim(ser.serUf), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BANCO_SAL, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseBancoSal), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BANCO_SAL_2, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseBancoSal2), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_AGENCIA_SALARIO2, responsavel)) {
                // Verifica se o nome da mãe foi preenchido
                corpoBuilder.append(" and nullif(trim(rse.rseAgenciaSal2), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CONTA_SALARIO2, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseContaSal2), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ASSOCIADO, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseAssociado), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CLT, responsavel)) {
                // Verifica se o nome da mãe foi preenchido
                corpoBuilder.append(" and nullif(trim(rse.rseClt), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ESTABILIZADO, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseEstabilizado), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF_IDT, responsavel)) {
                corpoBuilder.append(" and nullif(trim(ser.serUfIdt), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF_NASC, responsavel)) {
                corpoBuilder.append(" and nullif(trim(ser.serUfNasc), '') is not null");
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DATA_LIMITE_PERMANENCIA, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseDataLimitePermanencia), '') is not null");
                // Adiciona exceção
                rseDataLimitePermanencia = getListaExcecoes(Columns.RSE_DATA_LIMITE_PERMANENCIA);
                if (!rseDataLimitePermanencia.isEmpty()) {
                    corpoBuilder.append(" and rse.rseDataLimitePermanencia not in (:rseDataLimitePermanencia)");
                }
            }

            if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DATA_FIM_ENGAJAMENTO, responsavel)) {
                corpoBuilder.append(" and nullif(trim(rse.rseDataFimEngajamento), '') is not null");
                // Adiciona exceção
                rseDataFimEngajamento = getListaExcecoes(Columns.RSE_DATA_FIM_ENGAJAMENTO);
                if (!rseDataFimEngajamento.isEmpty()) {
                    corpoBuilder.append(" and rse.rseDataFimEngajamento not in (:rseDataFimEngajamento)");
                }
            }

        } catch (ZetraException ex) {
            throw new HQueryException(ex);
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (nomesMae != null && !nomesMae.isEmpty()) {
            defineValorClausulaNomeada("nomesMae", nomesMae, query);
        }
        if (nomesPai != null && !nomesPai.isEmpty()) {
            defineValorClausulaNomeada("nomesPai", nomesPai, query);
        }
        if (nomesConjuge != null && !nomesConjuge.isEmpty()) {
            defineValorClausulaNomeada("nomesConjuge", nomesConjuge, query);
        }
        if (enderecos != null && !enderecos.isEmpty()) {
            defineValorClausulaNomeada("enderecos", enderecos, query);
        }
        if (bairros != null && !bairros.isEmpty()) {
            defineValorClausulaNomeada("bairros", bairros, query);
        }
        if (cidades != null && !cidades.isEmpty()) {
            defineValorClausulaNomeada("cidades", cidades, query);
        }
        if (ceps != null && !ceps.isEmpty()) {
            defineValorClausulaNomeada("ceps", ceps, query);
        }
        if (cidadesNascimento != null && !cidadesNascimento.isEmpty()) {
            defineValorClausulaNomeada("cidadesNascimento", cidadesNascimento, query);
        }
        if (telefones != null && !telefones.isEmpty()) {
            defineValorClausulaNomeada("telefones", telefones, query);
        }
        if (celulares != null && !celulares.isEmpty()) {
            defineValorClausulaNomeada("celulares", celulares, query);
        }
        if (numerosIdentidade != null && !numerosIdentidade.isEmpty()) {
            defineValorClausulaNomeada("numerosIdentidade", numerosIdentidade, query);
        }
        if (numerosCartProf != null && !numerosCartProf.isEmpty()) {
            defineValorClausulaNomeada("numerosCartProf", numerosCartProf, query);
        }
        if (numerosPis != null && !numerosPis.isEmpty()) {
            defineValorClausulaNomeada("numerosPis", numerosPis, query);
        }
        if (categorias != null && !categorias.isEmpty()) {
            defineValorClausulaNomeada("categorias", categorias, query);
        }
        if (complemento != null && !complemento.isEmpty()) {
            defineValorClausulaNomeada("serCompl", complemento, query);
        }
        if (serCodigo != null && !serCodigo.isEmpty()) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }
        if (rseDataLimitePermanencia != null && !rseDataLimitePermanencia.isEmpty()) {
            defineValorClausulaNomeada("rseDataLimitePermanencia", rseDataLimitePermanencia, query);
        }
        if (rseDataFimEngajamento != null && !rseDataFimEngajamento.isEmpty()) {
            defineValorClausulaNomeada("rseDataFimEngajamento", rseDataFimEngajamento, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
       return new String[] {
                Columns.SER_CODIGO,
                Columns.SER_END,
                Columns.SER_NRO,
                Columns.SER_BAIRRO,
                Columns.SER_CIDADE,
                Columns.SER_CEP,
                Columns.SER_DATA_NASC,
                Columns.SER_CID_NASC,
                Columns.SER_NOME_MAE,
                Columns.SER_NOME_PAI,
                Columns.SER_NOME_CONJUGE,
                Columns.SER_TEL,
                Columns.SER_CELULAR,
                Columns.SER_DATA_IDT,
                Columns.SER_NRO_IDT,
                Columns.SER_CART_PROF,
                Columns.SER_PIS,
                Columns.SER_COMPL,
                Columns.SER_NACIONALIDADE,
                Columns.SER_UF,
                Columns.SER_UF_IDT,
                Columns.SER_UF_NASC,
                Columns.SER_EMISSOR_IDT,
                Columns.SER_EST_CIVIL,
                Columns.RSE_DATA_ADMISSAO,
                Columns.RSE_TIPO,
                Columns.RSE_MUNICIPIO_LOTACAO,
                Columns.RSE_AGENCIA_SAL,
                Columns.RSE_CONTA_SAL,
                Columns.RSE_BANCO_SAL,
                Columns.RSE_CONTA_SAL_2,
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
                Columns.RSE_ESTABILIZADO
        };
    }

    private List<Object> getListaExcecoes(String nomeCampo) {
        List<Object> listaExcecoes = new ArrayList<>();
        if (excecoes != null) {
            for (TransferObject excecao : excecoes) {
                if (!TextHelper.isNull(excecao.getAttribute(nomeCampo))) {
                    listaExcecoes.add(excecao.getAttribute(nomeCampo));
                }
            }
        }
        return listaExcecoes;
    }
}