package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarConsignataria;
import com.zetra.econsig.webservice.soap.folha.v1.Consignataria;

/**
 * <p>Title: CadastrarConsignatariaAssembler</p>
 * <p>Description: Assembler para CadastrarConsignataria.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarConsignatariaAssembler extends BaseAssembler {

    private CadastrarConsignatariaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarConsignataria cadastrarConsignataria) {
        final ConsignatariaTransferObject csaTO = new ConsignatariaTransferObject();
        final Consignataria consignataria = cadastrarConsignataria.getConsignataria();

        csaTO.setCsaIdentificador(consignataria.getCodigo());
        csaTO.setCsaNome(consignataria.getNome());
        csaTO.setCsaEmail(getValue(consignataria.getEmail()));
        csaTO.setCsaCnpj(getValue(consignataria.getCnpj()));
        csaTO.setCsaResponsavel(getValue(consignataria.getResponsavel()));
        csaTO.setCsaLogradouro(getValue(consignataria.getLogradouro()));
        final Integer numero = getValue(consignataria.getNumero());
        if ((numero == null) || (numero == Integer.MAX_VALUE) || (numero <= 0)) {
            csaTO.setCsaNro(null);
        } else {
            csaTO.setCsaNro(numero);
        }
        csaTO.setCsaCompl(getValue(consignataria.getComplemento()));
        csaTO.setCsaBairro(getValue(consignataria.getBairro()));
        csaTO.setCsaCidade(getValue(consignataria.getCidade()));
        csaTO.setCsaUf(getValue(consignataria.getUf()));
        csaTO.setCsaCep(consignataria.getCep());
        csaTO.setCsaTel(consignataria.getTelefone());
        csaTO.setCsaFax(getValue(consignataria.getFax()));
        csaTO.setCsaNroBco(getValue(consignataria.getNumeroBanco()));
        csaTO.setCsaNroCta(getValue(consignataria.getNumeroConta()));
        csaTO.setCsaNroAge(getValue(consignataria.getNumeroAgencia()));
        csaTO.setCsaDigCta(getValue(consignataria.getDigitoConta()));
        final Short ativo = getValue(consignataria.getAtivo());
        if ((ativo == null) || (ativo == Short.MAX_VALUE) || (ativo <= 0)) {
            csaTO.setCsaAtivo(null);
        } else {
            csaTO.setCsaAtivo(ativo);
        }
        csaTO.setCsaResponsavel2(getValue(consignataria.getResponsavel2()));
        csaTO.setCsaResponsavel3(getValue(consignataria.getResponsavel3()));
        csaTO.setCsaRespCargo(getValue(consignataria.getCargoResponsavel()));
        csaTO.setCsaRespCargo2(getValue(consignataria.getCargoResponsavel2()));
        csaTO.setCsaRespCargo3(getValue(consignataria.getCargoResponsavel3()));
        csaTO.setCsaRespTelefone(getValue(consignataria.getTelefoneResponsavel()));
        csaTO.setCsaRespTelefone2(getValue(consignataria.getTelefoneResponsavel2()));
        csaTO.setCsaRespTelefone3(getValue(consignataria.getTelefoneResponsavel3()));
        csaTO.setCsaTxtContato(getValue(consignataria.getTextoContato()));
        csaTO.setCsaContato(getValue(consignataria.getContato()));
        csaTO.setCsaContatoTel(getValue(consignataria.getTelefoneContato()));
        csaTO.setCsaEndereco2(getValue(consignataria.getEndereco2()));
        csaTO.setCsaNomeAbreviado(getValue(consignataria.getNomeAbreviado()));
        csaTO.setCsaIdentificadorInterno(consignataria.getCodigoInterno());
        csaTO.setCsaDataExpiracao(getValueAsDate(consignataria.getDataExpiracao()));
        csaTO.setCsaNroContrato(getValue(consignataria.getNumeroContrato()));
        csaTO.setCsaIPAcesso(getValue(consignataria.getIpAcesso()));
        csaTO.setCsaDDNSAcesso(getValue(consignataria.getDdnsAcesso()));
        csaTO.setCsaExigeEnderecoAcesso(getValue(consignataria.getExigeEnderecoAcesso()));
        csaTO.setCsaUnidadeOrganizacional(getValue(consignataria.getUnidadeOrganizacional()));
        csaTO.setCsaNcaNatureza(getValue(consignataria.getNaturezaCodigo()));
        csaTO.setTgcIdentificador(consignataria.getGrupoCodigo());

        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(CONSIGNATARIA, csaTO);
        parametros.put(USUARIO, cadastrarConsignataria.getUsuario());
        parametros.put(SENHA, cadastrarConsignataria.getSenha());

        return parametros;
    }
}