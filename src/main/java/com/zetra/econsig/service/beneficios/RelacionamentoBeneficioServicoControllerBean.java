package com.zetra.econsig.service.beneficios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RelacionamentoBeneficioServicoControllerException;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.persistence.entity.RelacionamentoBeneficioServicoHome;

/**
 * <p>Title: RelacionamentoBeneficioServicoControllerBean</p>
 * <p>Description: Controller Bean para Relacionamento de Benefício e Serviço</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class RelacionamentoBeneficioServicoControllerBean implements RelacionamentoBeneficioServicoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelacionamentoBeneficioServicoControllerBean.class);

    @Override
    public List<BeneficioServico> findByBenCodigoTibCodigo(String benCodigo, String tibCodigo) throws RelacionamentoBeneficioServicoControllerException {
        try {
            return RelacionamentoBeneficioServicoHome.findByBenCodigoTibCodigo(benCodigo, tibCodigo);
        } catch (FindException e) {
           LOG.error(e.getCause(), e);
           throw new RelacionamentoBeneficioServicoControllerException(e);
        }
    }
}
