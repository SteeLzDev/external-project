-- DESENV-19580
UPDATE tb_tipo_filtro_relatorio
SET TFR_RECURSO = '/relatorios/campos_relatorio/campo_periodo_data_ocorrencia.jsp'
WHERE TFR_CODIGO = 'campo_data_ocorrencia';

