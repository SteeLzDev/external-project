package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.COPIAR_CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO_COPIAR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarOrgao;
import com.zetra.econsig.webservice.soap.folha.v1.Orgao;

/**
 * <p>Title: CadastrarOrgaoAssembler</p>
 * <p>Description: Assembler para CadastrarOrgao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarOrgaoAssembler extends BaseAssembler {

    private CadastrarOrgaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarOrgao cadastrarOrgao) {
        final OrgaoTransferObject orgTO = new OrgaoTransferObject();
        final Orgao orgao = cadastrarOrgao.getOrgao();

        final Short ativo = getValue(orgao.getAtivo());
        if ((ativo == null) || (ativo == Short.MAX_VALUE) || (ativo <= 0)) {
            orgTO.setOrgAtivo(null);
        } else {
            orgTO.setOrgAtivo(ativo);
        }
        orgTO.setOrgBairro(getValue(orgao.getBairro()));
        orgTO.setOrgCep(getValue(orgao.getCep()));
        orgTO.setOrgCidade(getValue(orgao.getCidade()));
        orgTO.setOrgCnpj(getValue(orgao.getCnpj()));
        orgTO.setOrgCompl(getValue(orgao.getComplemento()));
        orgTO.setOrgDDNSAcesso(getValue(orgao.getDdnsAcesso()));
        final Integer diaRepasse = getValue(orgao.getDiaRepasse());
        if ((diaRepasse == null) || (diaRepasse == Integer.MAX_VALUE) || (diaRepasse <= 0)) {
            orgTO.setOrgDiaRepasse(null);
        } else {
            orgTO.setOrgDiaRepasse(diaRepasse);
        }
        orgTO.setOrgEmail(getValue(orgao.getEmail()));
        orgTO.setOrgFax(getValue(orgao.getFax()));
        orgTO.setOrgIdentificador(orgao.getCodigo());
        orgTO.setOrgIPAcesso(getValue(orgao.getIpAcesso()));
        orgTO.setOrgLogradouro(getValue(orgao.getLogradouro()));
        orgTO.setOrgNome(orgao.getNome());
        orgTO.setOrgNomeAbrev(getValue(orgao.getNomeAbreviado()));
        final Integer numero = getValue(orgao.getNumero());
        if ((numero == null) || (numero == Integer.MAX_VALUE) || (numero <= 0)) {
            orgTO.setOrgNro(null);
        } else {
            orgTO.setOrgNro(numero);
        }
        orgTO.setOrgRespCargo(getValue(orgao.getCargoResponsavel()));
        orgTO.setOrgRespCargo2(getValue(orgao.getCargoResponsavel2()));
        orgTO.setOrgRespCargo3(getValue(orgao.getCargoResponsavel3()));
        orgTO.setOrgResponsavel(getValue(orgao.getResponsavel()));
        orgTO.setOrgResponsavel2(getValue(orgao.getResponsavel2()));
        orgTO.setOrgResponsavel3(getValue(orgao.getResponsavel3()));
        orgTO.setOrgRespTelefone(getValue(orgao.getTelefoneResponsavel()));
        orgTO.setOrgRespTelefone2(getValue(orgao.getTelefoneResponsavel2()));
        orgTO.setOrgRespTelefone3(getValue(orgao.getTelefoneResponsavel3()));
        orgTO.setOrgTel(getValue(orgao.getTelefone()));
        orgTO.setOrgUf(getValue(orgao.getUf()));
        orgTO.setEstCodigo(orgao.getCodigoEstabelecimento());

        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(COPIAR_CONVENIO, cadastrarOrgao.getCopiarConvenios());
        parametros.put(ORGAO_COPIAR, cadastrarOrgao.getCodigoOrgaoaCopiar());
        parametros.put(ORGAO, orgTO);
        parametros.put(USUARIO, cadastrarOrgao.getUsuario());
        parametros.put(SENHA, cadastrarOrgao.getSenha());

        return parametros;
    }
}