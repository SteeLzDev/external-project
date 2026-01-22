package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarEstabelecimento;
import com.zetra.econsig.webservice.soap.folha.v1.Estabelecimento;

/**
 * <p>Title: CadastrarEstabelecimentoAssembler</p>
 * <p>Description: Assembler para CadastrarEstabelecimento.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarEstabelecimentoAssembler extends BaseAssembler {

    private CadastrarEstabelecimentoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarEstabelecimento cadastrarEstabelecimento) {
        final EstabelecimentoTransferObject estTO = new EstabelecimentoTransferObject();
        final Estabelecimento estabelecimento = cadastrarEstabelecimento.getEstabelecimento();

        final Short ativo = getValue(estabelecimento.getAtivo());
        if ((ativo == null) || (ativo == Short.MAX_VALUE) || (ativo <= 0)) {
            estTO.setEstAtivo(null);
        } else {
            estTO.setEstAtivo(ativo);
        }
        estTO.setEstBairro(getValue(estabelecimento.getBairro()));
        estTO.setEstCep(getValue(estabelecimento.getCep()));
        estTO.setEstCidade(getValue(estabelecimento.getCidade()));
        estTO.setEstCnpj(estabelecimento.getCnpj());
        estTO.setEstCompl(getValue(estabelecimento.getComplemento()));
        estTO.setEstEmail(getValue(estabelecimento.getEmail()));
        estTO.setEstFax(getValue(estabelecimento.getFax()));
        estTO.setEstLogradouro(getValue(estabelecimento.getLogradouro()));
        estTO.setEstNome(estabelecimento.getNome());
        final Integer numero = getValue(estabelecimento.getNumero());
        if ((numero == null) || (numero == Integer.MAX_VALUE) || (numero <= 0)) {
            estTO.setEstNro(null);
        } else {
            estTO.setEstNro(numero);
        }
        estTO.setEstRespCargo(getValue(estabelecimento.getCargoResponsavel()));
        estTO.setEstRespCargo2(getValue(estabelecimento.getCargoResponsavel2()));
        estTO.setEstRespCargo3(getValue(estabelecimento.getCargoResponsavel3()));
        estTO.setEstResponsavel(getValue(estabelecimento.getResponsavel()));
        estTO.setEstResponsavel2(getValue(estabelecimento.getResponsavel2()));
        estTO.setEstResponsavel3(getValue(estabelecimento.getResponsavel3()));
        estTO.setEstRespTelefone(getValue(estabelecimento.getTelefoneResponsavel()));
        estTO.setEstRespTelefone2(getValue(estabelecimento.getTelefoneResponsavel2()));
        estTO.setEstRespTelefone3(getValue(estabelecimento.getTelefoneResponsavel3()));
        estTO.setEstTel(getValue(estabelecimento.getTelefone()));
        estTO.setEstUf(getValue(estabelecimento.getUf()));
        estTO.setEstIdentificador(estabelecimento.getCodigo());

        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(ESTABELECIMENTO, estTO);
        parametros.put(USUARIO, cadastrarEstabelecimento.getUsuario());
        parametros.put(SENHA, cadastrarEstabelecimento.getSenha());

        return parametros;
    }
}