package com.zetra.econsig.values;

/**
 * <p>Title: MotivoDependenciaEnum </p>
 * <p>Description: Enumeração de motivoDependencia.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public enum MotivoDependenciaEnum {

    ESTUDANTE("1"),
    INVALIDEZ("2"),
    ESTUDANTEINVALIDEZ("3"),
    CONJUGE("4"),
    COMPANHEIROCOMFILHO("5"),
    COMPANHEIROCOMFILHOUNIAO("6"),
    FILHOENTEADO21("7"),
    FILHOENTEADO("8"),
    FILHOENTEADOUNIVERSITARIOTECNICO24("9"),
    FILHOENTEADOUNIVERSITARIOTECNICO2GRAU("10"),
    FILHOENTEADOQUALQUERIDADEINCAPACITADOFISICAOUMENTALMENTE("11"),
    IRMAONETOBISNETOSEMARRIMOGUARDAJUDICIAL21("12"),
    IRMAONETOBISNETOSEMARRIMOGUARDAJUDICIAL("13"),
    IRMAONETOBISNETOSEMARRIMO24AINDACURSANDOSUPERIOR("14"),
    IRMAONETOBISNETOSEMARRIMOGUARDAJUDICIALUNIVERSITARIO("15"),
    IRMAONETOBISNETOSEMARRIMOGUARDAJUDICIALINCAPACITADO("16"),
    PAISAVOSBISAVOS("17"),
    MENORPOBRE21("18"),
    MENORPOBRETUTOR("19"),
    INCAPAZ("20"),
    EXCONJUGEPENSAO("21"),
    EXCONJUGE("22");

    public String mdeCodigo;

    private MotivoDependenciaEnum(String codigo) {
        mdeCodigo = codigo;
    }

    public String getCodigo() {
        return mdeCodigo;
    }

    public boolean equals(String codigo) {
        return mdeCodigo.equals(codigo);
    }
}
