-- DESENV-9204
-- @@delimiter = /

DROP PROCEDURE IF EXISTS p_carrega_calendario_beneficio /

CREATE PROCEDURE p_carrega_calendario_beneficio (IN periodoInicial DATE, IN diaCorte INT, IN quantidadePeriodo INT)
BEGIN
    DECLARE dataInicio DATETIME;
    DECLARE dataFim DATETIME;
    DECLARE totalLoop INT;

    DELETE FROM tb_calendario_beneficio_cse WHERE cbc_periodo >= periodoInicial;

    SET periodoInicial = DATE_SUB(periodoInicial, INTERVAL 1 MONTH);

    SET dataFim = STR_TO_DATE(CONCAT(YEAR(periodoInicial),'-',MONTH(periodoInicial),'-', diaCorte, ' 23:59:59') ,'%Y-%m-%d %H:%i:%s');
    SET dataInicio = DATE_ADD(dataFim, INTERVAL 1 DAY);
    SET dataInicio = STR_TO_DATE(CONCAT(YEAR(dataInicio),'-',MONTH(dataInicio),'-', DAY(dataInicio), ' 00:00:00') ,'%Y-%m-%d %H:%i:%s');
    SET dataFim = DATE_ADD(dataFim, INTERVAL 1 MONTH);

    SET periodoInicial = DATE_ADD(periodoInicial, INTERVAL 1 MONTH);

    WHILE quantidadePeriodo > 0 DO
        INSERT INTO tb_calendario_beneficio_cse
        SELECT cse_codigo, periodoInicial, diaCorte, dataInicio, dataFim FROM tb_consignante;
        SET quantidadePeriodo = quantidadePeriodo -1;
        SET dataInicio = DATE_ADD(dataInicio, INTERVAL 1 MONTH);
        SET dataFim = DATE_ADD(dataFim, INTERVAL 1 MONTH);
        SET periodoInicial = DATE_ADD(periodoInicial, INTERVAL 1 MONTH);
    END WHILE;
END /

call p_carrega_calendario_beneficio ('2016-01-01', 1, 120) /
