package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.CRS_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PRS_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA_SAL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA_SAL_2;
import static com.zetra.econsig.webservice.CamposAPI.RSE_ASSOCIADO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO_SAL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO_SAL_2;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BASE_CALCULO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CATEGORIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CLT;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA_SAL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA_SAL_2;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_CTC;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_FIM_ENGAJAMENTO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_LIMITE_PERMANENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DESCONTOS_COMP;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DESCONTOS_FACU;
import static com.zetra.econsig.webservice.CamposAPI.RSE_ESTABILIZADO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA_INST;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MUNICIPIO_LOTACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_OBS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_OUTROS_DESCONTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRACA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PROVENTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.SBO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CART_PROF;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.SER_DDD_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_DDD_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMISSOR_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_CONJUGE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MAE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MEIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_PAI;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_PIS;
import static com.zetra.econsig.webservice.CamposAPI.SER_PRIMEIRO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_TITULACAO;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_ULTIMO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.UNI_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VRS_IDENTIFICADOR;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v4.CadastrarServidor;

/**
 * <p>Title: CadastrarServidorAssembler</p>
 * <p>Description: Assembler para CadastrarServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarServidorAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CadastrarServidorAssembler.class);

    private CadastrarServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarServidor cadastrarServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, cadastrarServidor.getUsuario());
        parametros.put(SENHA, cadastrarServidor.getSenha());
        parametros.put(CONVENIO, getValue(cadastrarServidor.getConvenio()));
        parametros.put(CLIENTE, getValue(cadastrarServidor.getCliente()));

        parametros.put(SER_CPF, getValue(cadastrarServidor.getCpf()));
        parametros.put(SER_NOME_PAI, getValue(cadastrarServidor.getNomePai()));
        parametros.put(SER_NOME_MAE, getValue(cadastrarServidor.getNomeMae()));
        parametros.put(SER_DATA_NASCIMENTO, getValueAsDate(cadastrarServidor.getDataNascimento()));
        parametros.put(SER_NOME, getValue(cadastrarServidor.getNome()));
        parametros.put(SER_TITULACAO, getValue(cadastrarServidor.getTitulacao()));
        parametros.put(SER_PRIMEIRO_NOME, getValue(cadastrarServidor.getPrimeiroNome()));
        parametros.put(SER_NOME_MEIO, getValue(cadastrarServidor.getNomeMeio()));
        parametros.put(SER_ULTIMO_NOME, getValue(cadastrarServidor.getUltimoNome()));
        parametros.put(SER_NACIONALIDADE, getValue(cadastrarServidor.getNacionalidade()));
        parametros.put(SER_SEXO, getValue(cadastrarServidor.getSexo()));
        parametros.put(SER_EST_CIVIL, getValue(cadastrarServidor.getEstadoCivil()));
        parametros.put(SER_NRO_IDT, getValue(cadastrarServidor.getIdentidade()));
        parametros.put(SER_EMISSOR_IDT, getValue(cadastrarServidor.getEmissorIdentidade()));
        parametros.put(SER_UF_IDT, getValue(cadastrarServidor.getUfIdentidade()));

        parametros.put(SER_CART_PROF, getValue(cadastrarServidor.getNumCarteiraTrabalho()));
        parametros.put(SER_PIS, getValue(cadastrarServidor.getNumPis()));
        parametros.put(SER_END, getValue(cadastrarServidor.getLogradouro()));
        parametros.put(SER_COMPL, getValue(cadastrarServidor.getComplemento()));
        parametros.put(SER_BAIRRO, getValue(cadastrarServidor.getBairro()));
        parametros.put(SER_CIDADE, getValue(cadastrarServidor.getCidade()));
        parametros.put(SER_UF, getValue(cadastrarServidor.getUf()));
        parametros.put(SER_CEP, getValue(cadastrarServidor.getCep()));

        parametros.put(SER_DDD_TEL, getValue(cadastrarServidor.getDddTelefone()));
        parametros.put(SER_TEL, getValue(cadastrarServidor.getTelefone()));
        parametros.put(SER_DDD_CELULAR, getValue(cadastrarServidor.getDddCelular()));
        parametros.put(SER_CELULAR, getValue(cadastrarServidor.getCelular()));
        parametros.put(SER_NRO, getValue(cadastrarServidor.getNro()));
        parametros.put(SER_EMAIL, getValue(cadastrarServidor.getEmail()));
        parametros.put(SER_DATA_IDT, getValueAsDate(cadastrarServidor.getDataIdentidade()));

        //SRS_CODIGO`) REFERENCES `tb_status_registro_servidor`
        parametros.put(SITUACAO_SERVIDOR, getValue(cadastrarServidor.getSituacao()));

        // (vrs_codigo`) REFERENCES `tb_vinculo_registro_servidor`
        parametros.put(VRS_IDENTIFICADOR, getValue(cadastrarServidor.getVinculoCodigo()));

        //`crs_codigo`) REFERENCES `tb_cargo_registro_servidor`
        parametros.put(CRS_IDENTIFICADOR, getValue(cadastrarServidor.getCargoCodigo()));

        // prs_codigo`) REFERENCES `tb_padrao_registro_servidor
        parametros.put(PRS_IDENTIFICADOR, getValue(cadastrarServidor.getPadraoCodigo()));

        //`sbo_codigo`) REFERENCES `tb_sub_orgao`
        parametros.put(SBO_IDENTIFICADOR, getValue(cadastrarServidor.getSubOrgaoCodigo()));

        //`uni_codigo`) REFERENCES `tb_unidade` (`UNI_CODIGO`
        parametros.put(UNI_IDENTIFICADOR, getValue(cadastrarServidor.getUnidadeCodigo()));

        parametros.put(EST_IDENTIFICADOR, getValue(cadastrarServidor.getEstabelecimentoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, getValue(cadastrarServidor.getOrgaoCodigo()));

        parametros.put(RSE_CATEGORIA, getValue(cadastrarServidor.getCategoria()));
        parametros.put(RSE_CLT, getValue(cadastrarServidor.getClt()));
        parametros.put(RSE_ESTABILIZADO, getValue(cadastrarServidor.getEstabilizado()));
        parametros.put(RSE_MUNICIPIO_LOTACAO, getValue(cadastrarServidor.getMunicipioLotacao()));
        parametros.put(RSE_PRAZO, getValue(cadastrarServidor.getPrazoServidor()));
        parametros.put(RSE_DATA_ADMISSAO, getValueAsDate(cadastrarServidor.getDataAdmissao()));
        parametros.put(RSE_BANCO_SAL, getValue(cadastrarServidor.getBanco()));
        parametros.put(RSE_AGENCIA_SAL, getValue(cadastrarServidor.getAgencia()));
        parametros.put(RSE_CONTA_SAL, getValue(cadastrarServidor.getConta()));

        parametros.put(RSE_BANCO_SAL_2, getValue(cadastrarServidor.getBancoAlternativo()));
        parametros.put(RSE_AGENCIA_SAL_2, getValue(cadastrarServidor.getAgenciaAlternativa()));
        parametros.put(RSE_CONTA_SAL_2, getValue(cadastrarServidor.getContaAlternativa()));
        parametros.put(RSE_DATA_FIM_ENGAJAMENTO, getValueAsDate(cadastrarServidor.getDataFimEngajamento()));
        parametros.put(RSE_DATA_LIMITE_PERMANENCIA, getValueAsDate(cadastrarServidor.getDataLimitePermanencia()));
        parametros.put(RSE_OBS, getValue(cadastrarServidor.getObservacao()));
        parametros.put(RSE_PRACA, getValue(cadastrarServidor.getPraca()));
        parametros.put(RSE_MATRICULA, getValue(cadastrarServidor.getMatricula()));
        parametros.put(RSE_SALARIO, getValue(cadastrarServidor.getSalario()));
        parametros.put(RSE_PROVENTOS, getValue(cadastrarServidor.getProventos()));

        parametros.put(RSE_DESCONTOS_COMP, getValue(cadastrarServidor.getDescontosCompulsorios()));
        parametros.put(RSE_DESCONTOS_FACU, getValue(cadastrarServidor.getDescontosFacultativos()));
        parametros.put(RSE_OUTROS_DESCONTOS, getValue(cadastrarServidor.getOutrosDescontos()));
        parametros.put(RSE_ASSOCIADO, getValue(cadastrarServidor.getAssociado()));
        parametros.put(RSE_BASE_CALCULO, getValue(cadastrarServidor.getBaseCalculo()));
        parametros.put(RSE_MATRICULA_INST, getValue(cadastrarServidor.getMatriculaInstitucional()));
        parametros.put(SER_UF_NASC, getValue(cadastrarServidor.getUfNascimento()));
        parametros.put(SER_CID_NASC, getValue(cadastrarServidor.getCidadeNascimento()));
        parametros.put(SER_NOME_CONJUGE, getValue(cadastrarServidor.getNomeConjuge()));
        parametros.put(RSE_DATA_CTC, getValueAsDate(cadastrarServidor.getDataContracheque()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v8.CadastrarServidor cadastrarServidor) {
        final com.zetra.econsig.webservice.soap.operacional.v4.CadastrarServidor cadastrarServidorV4 = new com.zetra.econsig.webservice.soap.operacional.v4.CadastrarServidor();
        try {
            final com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory factoryV4 = new com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory();
            final com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor situacaoServidorV4 = new com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor();
            if ((cadastrarServidor.getSituacao() != null) && (cadastrarServidor.getSituacao().getValue() != null)) {
                BeanUtils.copyProperties(situacaoServidorV4, cadastrarServidor.getSituacao().getValue());
            }
            BeanUtils.copyProperties(cadastrarServidorV4, cadastrarServidor);
            cadastrarServidorV4.setSituacao(factoryV4.createCadastrarServidorSituacao(situacaoServidorV4));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(cadastrarServidorV4);
    }
}
