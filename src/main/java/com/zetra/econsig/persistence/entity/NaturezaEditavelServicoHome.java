package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

public class NaturezaEditavelServicoHome extends AbstractEntityHome {

    public static NaturezaEditavelNse findByPrimaryKey(NaturezaEditavelNseId id) throws FindException {
        NaturezaEditavelNse naturezaEditavelNse = new NaturezaEditavelNse();
        naturezaEditavelNse.setId(id);
        return find(naturezaEditavelNse, id);
    }

    public static NaturezaEditavelNse create(NaturezaEditavelNseId id, TipoNatureza tipoNatureza, NaturezaServico naturezaServico) throws CreateException {
        NaturezaEditavelNse naturezaEditavelNse = new NaturezaEditavelNse();
        naturezaEditavelNse.setId(id);
        naturezaEditavelNse.setTipoNatureza(tipoNatureza);
        naturezaEditavelNse.setNaturezaServico(naturezaServico);

        create(naturezaEditavelNse);

        return naturezaEditavelNse;
    }
}
