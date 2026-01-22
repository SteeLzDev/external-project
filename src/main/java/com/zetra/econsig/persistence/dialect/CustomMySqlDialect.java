package com.zetra.econsig.persistence.dialect;

import static org.hibernate.type.SqlTypes.CHAR;
import static org.hibernate.type.SqlTypes.VARCHAR;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.sql.internal.CapacityDependentDdlType;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

import com.zetra.econsig.helper.texto.LocaleHelper;

/**
 * <p>Title: CustomMySqlDialect</p>
 * <p>Description: Customização do Hibernate para Sql Server.</p>
 * <p>Copyright: Copyright (c) 2008-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class CustomMySqlDialect extends MySQLDialect implements CustomDialect {

    private final List<CustomSqlFunction> customFuctions = new ArrayList<>();

    public CustomMySqlDialect() {
    }

    /**
     *
     * O hibernate só permite que as funcoes sejam criadas com nomes em caixa baixa (MINUSCULO).
     */
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);

        final BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
        final BasicType<BigDecimal> bigDecimalType = basicTypeRegistry.resolve(StandardBasicTypes.BIG_DECIMAL);
        final BasicType<Boolean> booleanType = basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN);
        final BasicType<Date> dateType = basicTypeRegistry.resolve(StandardBasicTypes.DATE);
        final BasicType<Integer> integerType = basicTypeRegistry.resolve(StandardBasicTypes.INTEGER);
        final BasicType<Long> longType = basicTypeRegistry.resolve(StandardBasicTypes.LONG);
        final BasicType<Short> shortType = basicTypeRegistry.resolve(StandardBasicTypes.SHORT);
        final BasicType<String> stringType = basicTypeRegistry.resolve(StandardBasicTypes.STRING);

        final SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();

        // Register User Defined Functions

        // Text Manipulation Functions:
        registerFunction(functionRegistry, "format_for_comparision", stringType, "lpad(replace(?1, '-', ''), ?2, '0')");
        registerFunction(functionRegistry, "to_string", stringType, "cast(?1 as char)");
        registerFunction(functionRegistry, "text_to_string", stringType, "cast(?1 as char)");
        registerFunction(functionRegistry, "concatenar", stringType, "concat(?1, ?2)");
        registerFunction(functionRegistry, "substituir", stringType, "replace(?1, ?2, ?3)");
        //registerFunction(functionRegistry, "trim", stringType, "trim(?1)");

        // Number Manipulation Functions:
        registerFunction(functionRegistry, "isnumeric", integerType, "?1 REGEXP '^([+-]{0,1}[0-9]*(([.]{1}[0-9]*)|([.]{0,1}[0-9]+)))$'");
        registerFunction(functionRegistry, "isnumeric_ne", integerType, "COALESCE(?1, '') REGEXP '^([+-]{0,1}[0-9]*(([.]{1}[0-9]*)|([.]{0,1}[0-9]+))){0,1}$'");
        registerFunction(functionRegistry, "to_numeric", integerType, "cast(?1 as signed integer)");
        registerFunction(functionRegistry, "to_numeric_ne", integerType, "cast(nullif(?1, '') as signed integer)");
        registerFunction(functionRegistry, "to_decimal", bigDecimalType, "cast(?1 as decimal(?2, ?3))");
        registerFunction(functionRegistry, "to_decimal_ne", bigDecimalType, "cast(nullif(?1, '') as decimal(?2, ?3))");
        registerFunction(functionRegistry, "desvio_padrao", bigDecimalType, "std(?1)");
        registerFunction(functionRegistry, "max_value", bigDecimalType, "greatest(?1, ?2)");
        registerFunction(functionRegistry, "min_value", bigDecimalType, "least(?1, ?2)");
        registerFunction(functionRegistry, "to_short", shortType, "cast(?1 as signed integer)");
        registerFunction(functionRegistry, "to_long", longType, "cast(?1 as unsigned integer)");

        // Date Manipulation Functions:
        registerFunction(functionRegistry, "add_second", dateType, "date_add(?1, interval ?2 second)");
        registerFunction(functionRegistry, "add_minute", dateType, "date_add(?1, interval ?2 minute)");
        registerFunction(functionRegistry, "add_hour", dateType, "date_add(?1, interval ?2 hour)");
        registerFunction(functionRegistry, "add_day", dateType, "date_add(?1, interval ?2 day)");
        registerFunction(functionRegistry, "add_month", dateType, "date_add(?1, interval ?2 month)");
        registerFunction(functionRegistry, "to_days", integerType, "to_days(?1)");
        registerFunction(functionRegistry, "to_date", dateType, "cast(?1 as date)");
        registerFunction(functionRegistry, "to_datetime", dateType, "cast(?1 as datetime)");
        registerFunction(functionRegistry, "to_locale_date", stringType, LocaleHelper.getDateDialectPattern());
        registerFunction(functionRegistry, "to_locale_datetime", stringType, LocaleHelper.getDateTimeDialectPattern());
        registerFunction(functionRegistry, "to_year_month", stringType, "date_format(?1, '%Y-%m')");
        registerFunction(functionRegistry, "to_month_year", stringType, "date_format(?1, '%m/%Y')");
        registerFunction(functionRegistry, "to_period", stringType, "to_period(?1)");
        registerFunction(functionRegistry, "date_diff", integerType, "datediff(?1, ?2)");
        registerFunction(functionRegistry, "month_diff", integerType, "period_diff(date_format(?2, '%Y%m'), date_format(?1, '%Y%m'))");
        registerFunction(functionRegistry, "format_datetime", stringType, "date_format(?1, concat('%Y-%m-%d ', ?2))");
        registerFunction(functionRegistry, "data_corrente", dateType, "curdate()");
        registerFunction(functionRegistry, "to_date_not_trunc", dateType, "STR_TO_DATE(?1, '%Y-%m-%d')");

        // "like" Accent Insensitive e Case Insensitive (O AND 1 é necessário pois será comparado com o 1 usado na query)
        registerFunction(functionRegistry, "like_ci_ai", booleanType, "?1 like ?2");
    }

    @Override
    protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.registerColumnTypes( typeContributions, serviceRegistry );
        final DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();

        // Faz mapa de colunas text, mapeadas para LONGVARCHAR (que não possui mapeamento
        // no dialeto MySQL) para o tipo String
        /** //registerColumnTypes(Types.LONGVARCHAR, StandardBasicTypes.STRING.getName()); */
        ddlTypeRegistry.addDescriptor( new DdlTypeImpl(Types.LONGVARCHAR, columnType( VARCHAR ), "char", this ) );

        // Registra o tipo char(1) como sendo um tipo character
        /** //registerColumnType(Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName());  */
        // Registra o tipo char(32) como sendo um tipo string, pois por default será um char(1)
        /** //registerHibernateType(Types.CHAR, 32, StandardBasicTypes.STRING.getName()); */
        ddlTypeRegistry.addDescriptor(CapacityDependentDdlType.builder(CHAR, columnType( CHAR ), "char", this)
                                                              .withTypeCapacity(1, "char($l)" )
                                                              .withTypeCapacity(32, columnType( VARCHAR ) ).build());
    }


    @Override
    public void registerFunction(SqmFunctionRegistry functionRegistry, String name, BasicType<?> returnType, String template) {
        functionRegistry.registerPattern(name, template, returnType);
        customFuctions.add(new CustomSqlFunction(name, returnType, template));
    }

    @Override
    public List<CustomSqlFunction> getCustomFunctions() {
        return customFuctions;
    }
}
