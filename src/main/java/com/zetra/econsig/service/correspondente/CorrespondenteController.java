package com.zetra.econsig.service.correspondente;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CorrespondenteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.persistence.entity.EnderecoCorrespondente;
import com.zetra.econsig.persistence.entity.TipoEndereco;

/**
 * <p>Title: ConsignatariaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CorrespondenteController {


    public int countEnderecoCorrespondenteByCorCodigo(String corCodigo, String csaCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException;

    public List<TransferObject> lstEnderecoCorrespondenteByCorCodigo(String corCodigo, String csaCodigo, int count, int offset, AcessoSistema responsavel) throws CorrespondenteControllerException;

    public EnderecoCorrespondente findEnderecoCorrespondenteByPKCorCodigo(String ecrCodigo, String corCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException;

    public List<TipoEndereco> listAllTipoEndereco(AcessoSistema responsavel) throws CorrespondenteControllerException;

    public EnderecoCorrespondente createEnderecoCorrespondente(String corCodigo, String tieCodigo, String ecrLogradouro, String ecrNumero, String ecrComplemento, String ecrBairro, String ecrMunicipio, String ecrUf, String ecrCep, BigDecimal ecrLatitude, BigDecimal ecrLongitude, AcessoSistema responsavel) throws CorrespondenteControllerException;

    public EnderecoCorrespondente updateEnderecoCorrespondente(String ecrCodigo, String corCodigo, String tieCodigo, String ecrLogradouro, String ecrNumero, String ecrComplemento, String ecrBairro, String ecrMunicipio, String ecrUf, String ecrCep, BigDecimal ecrLatitude, BigDecimal ecrLongitude, AcessoSistema responsavel) throws CorrespondenteControllerException;

    public EnderecoCorrespondente removeEnderecoCorrespondente(String ecrCodigo, String corCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException;

    public void createOcorrenciaCorrespondente(String corCodigo, String tocCodigo, String ocrObs, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public Correspondente findCorrespondenteByPrimaryKey(String corCodigo, AcessoSistema responsavel) throws CorrespondenteControllerException;

}
