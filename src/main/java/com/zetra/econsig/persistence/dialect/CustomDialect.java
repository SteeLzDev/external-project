package com.zetra.econsig.persistence.dialect;

import java.util.List;

import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicType;

/**
 * <p>Title: CustomSqlFunction</p>
 * <p>Description: Mapeamento de funções customizadas para funções nativas do SGBD.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public interface CustomDialect {

    public void registerFunction(SqmFunctionRegistry functionRegistry, String name, BasicType<?> returnType, String template);

    public List<CustomSqlFunction> getCustomFunctions();
}
