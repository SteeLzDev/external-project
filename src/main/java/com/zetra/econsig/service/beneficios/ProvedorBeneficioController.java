package com.zetra.econsig.service.beneficios;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ProvedorBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ProvedorBeneficioController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ProvedorBeneficioController {

    public TransferObject buscarProvedorBeneficioPorProCodigo (String proCodigo) throws ProvedorBeneficioControllerException;

    public List<TransferObject> listarProvedorBeneficioEmPerimetro (Float latitude, Float longitude, Float raioMax, List<String> nseCodigos, String chaveBuscaBen, AcessoSistema responsavel) throws ProvedorBeneficioControllerException;

    public List<TransferObject> buscarProvedorBeneficioPorCsaCodigoAgrupa(String csaCodigo) throws ProvedorBeneficioControllerException;

}
