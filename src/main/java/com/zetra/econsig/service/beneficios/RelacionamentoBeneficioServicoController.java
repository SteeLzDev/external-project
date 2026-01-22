package com.zetra.econsig.service.beneficios;


import java.util.List;

import com.zetra.econsig.exception.RelacionamentoBeneficioServicoControllerException;
import com.zetra.econsig.persistence.entity.BeneficioServico;

/**
 * <p>Title: RelacionamentoBeneficioServicoController</p>
 * <p>Description: Controller para Relacionamento de Benefício e Serviço</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RelacionamentoBeneficioServicoController {
    public List<BeneficioServico> findByBenCodigoTibCodigo(String benCodigo, String tibCodigo) throws RelacionamentoBeneficioServicoControllerException;
}
