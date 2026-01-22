-- DESENV-16142
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES('campo_data_ocorrencia', 'Data da Ocorrência', '/relatorios/campos_relatorio/campo_periodo_data_inclusao.jsp', 'N');

UPDATE tb_relatorio_filtro
SET TFR_CODIGO = 'campo_data_ocorrencia'
WHERE REL_CODIGO='sint_ocorrencia_autorizacao' AND TFR_CODIGO='campo_data_inicio_fim';

UPDATE tb_relatorio_filtro
SET TFR_CODIGO = 'campo_data_ocorrencia'
WHERE REL_CODIGO='ocorrencia_autorizacao' AND TFR_CODIGO='campo_data_inicio_fim';