package com.zetra.econsig.persistence.dialect;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;

import com.zetra.econsig.helper.texto.LocaleHelper;

/**
 * <p>Title: CustomOracleDialect</p>
 * <p>Description: Customização do Hibernate para Oracle.</p>
 * <p>Copyright: Copyright (c) 2013-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class CustomOracleDialect extends OracleDialect implements CustomDialect {

    private final List<CustomSqlFunction> customFuctions = new ArrayList<>();

    public CustomOracleDialect() {
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
        final BasicType<String> textType = basicTypeRegistry.resolve(StandardBasicTypes.TEXT);
        final BasicType<Date> timestampType = basicTypeRegistry.resolve(StandardBasicTypes.TIMESTAMP);

        final SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();

        // Register User Defined Functions

        // Text Manipulation Functions:
        registerFunction(functionRegistry, "format_for_comparision", stringType, "LPAD(REPLACE(?1, '-', ''), ?2, '0')");
        registerFunction(functionRegistry, "to_string", stringType, "TO_CHAR(?1)");
        registerFunction(functionRegistry, "text_to_string", textType, "TO_CHAR(?1)");
        registerFunction(functionRegistry, "concatenar", stringType, "CONCAT(?1, ?2)");
        registerFunction(functionRegistry, "substituir", stringType, "REPLACE(?1, ?2, ?3)");

        // Number Manipulation Functions:
        registerFunction(functionRegistry, "isnumeric", integerType, "CASE WHEN ?1 IS NOT NULL AND LENGTH(TRIM(TRANSLATE(?1, ' +-.0123456789', ' '))) IS NULL THEN 1 ELSE 0 END");
        registerFunction(functionRegistry, "isnumeric_ne", integerType, "CASE WHEN LENGTH(TRIM(TRANSLATE(?1, ' +-.0123456789', ' '))) IS NULL THEN 1 ELSE 0 END");
        registerFunction(functionRegistry, "to_numeric", integerType, "CAST(?1 AS NUMBER(10,0))");
        registerFunction(functionRegistry, "to_numeric_ne", integerType, "CAST(TRIM(?1) AS NUMBER(10,0))");
        registerFunction(functionRegistry, "to_decimal", bigDecimalType, "CAST(?1 AS NUMBER(?2, ?3))");
        registerFunction(functionRegistry, "to_decimal_ne", bigDecimalType, "CAST(TRIM(?1) AS NUMBER(?2, ?3))");
        registerFunction(functionRegistry, "desvio_padrao", bigDecimalType, "STDDEV(?1)");
        registerFunction(functionRegistry, "max_value", bigDecimalType, "GREATEST(?1, ?2)");
        registerFunction(functionRegistry, "min_value", bigDecimalType, "LEAST(?1, ?2)");
        registerFunction(functionRegistry, "to_short", shortType, "CAST(?1 AS NUMBER(5,0))");
        registerFunction(functionRegistry, "to_long", longType, "CAST(?1 AS NUMBER(38,0))");

        // Date Manipulation Functions:
        registerFunction(functionRegistry, "add_second", dateType, "?1 + INTERVAL '?2' SECOND");
        registerFunction(functionRegistry, "add_minute", dateType, "?1 + INTERVAL '?2' MINUTE");
        registerFunction(functionRegistry, "add_hour", dateType, "?1 + INTERVAL '?2' HOUR");
        registerFunction(functionRegistry, "add_day", dateType, "?1 + ?2 * INTERVAL '1' DAY");
        registerFunction(functionRegistry, "add_month", dateType, "ADD_MONTHS(?1, ?2)");
        registerFunction(functionRegistry, "to_days", integerType, "CEIL(CAST(?1 AS DATE) - TO_DATE('0001-01-01', 'YYYY-MM-DD'))");
        registerFunction(functionRegistry, "to_date", dateType, "TRUNC(?1)");
        registerFunction(functionRegistry, "to_datetime", dateType, "CAST(?1 AS TIMESTAMP)");
        registerFunction(functionRegistry, "to_locale_date", stringType, LocaleHelper.getDateDialectPattern());
        registerFunction(functionRegistry, "to_locale_datetime", stringType, LocaleHelper.getDateTimeDialectPattern());
        registerFunction(functionRegistry, "to_year_month", stringType, "TO_CHAR(?1, 'YYYY-MM')");
        registerFunction(functionRegistry, "to_month_year", stringType, "TO_CHAR(?1, 'MM/YYYY')");
        registerFunction(functionRegistry, "to_period", stringType, "TO_PERIOD(?1)");
        registerFunction(functionRegistry, "date_diff", integerType, "CEIL(TRUNC(?2) - TRUNC(?1))");
        registerFunction(functionRegistry, "month_diff", integerType, "FLOOR(MONTHS_BETWEEN(?2, ?1))");  // OS VALORES PASSADOS DEVEM SER DATAS
        registerFunction(functionRegistry, "format_datetime", stringType, "TO_TIMESTAMP(TO_CHAR(?1, 'YYYY-MM-DD') || ' ' || ?2, 'YYYY-MM-DD HH24:MI:SS')");
        registerFunction(functionRegistry, "data_corrente", timestampType, "SYSDATE");
        registerFunction(functionRegistry, "to_date_not_trunc", dateType, "TO_DATE(?1, 'YYYY-MM-DD')");

        // "like" Accent Insensitive e Case Insensitive
        // o 'ã' e o 'õ' produzem ? na função convert(), para estes dois casos usei o translate()
        registerFunction(functionRegistry, "like_ci_ai", booleanType, "(SELECT CASE WHEN REGEXP_LIKE(convert(translate(lower(?1),'ãõñ','aon'),'us7ascii'),convert(translate(lower(?2),'ãõñ','aon'),'us7ascii')) THEN 1 ELSE 0 END AS GOT_MATCH FROM DUAL)=1");
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

    /**
     * MYSQL *
           select
           period_diff(
               date_format('2013-06-01 01:20:30', '%Y%m'),
               date_format('2013-04-01 10:20:30', '%Y%m')
               ) as months,
           datediff('2013-06-01 01:20:30',
                    '2013-04-01 10:20:30') as days

     * ORACLE *
           select floor(months_between(
           to_date('2013-06-01 01:20:30','YYYY-MM-DD HH:MI:SS'),
           to_date('2013-04-01 10:20:30','YYYY-MM-DD HH:MI:SS'))) "Months",

           ceil(to_date('2013-06-01 01:20:30','YYYY-MM-DD HH:MI:SS') -
           to_date('2013-04-01 10:20:30','YYYY-MM-DD HH:MI:SS')) "Days"
           FROM DUAL;
     */
}
