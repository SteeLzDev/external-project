-- DESENV-10489

INSERT INTO tb_tipo_entidade (TEN_CODIGO, TEN_DESCRICAO, TEN_CAMPO_ENT_00, TEN_CAMPO_ENT_01) VALUES ('110', 'Dados Adicionais de Servidor', 'SER_CODIGO', 'TDA_CODIGO');

UPDATE tb_tipo_ocorrencia SET TOC_DESCRICAO = 'Criação de dados adicionais'   WHERE TOC_CODIGO = '122';
UPDATE tb_tipo_ocorrencia SET TOC_DESCRICAO = 'Alteração de dados adicionais' WHERE TOC_CODIGO = '123';
UPDATE tb_tipo_ocorrencia SET TOC_DESCRICAO = 'Exclusão de dados adicionais'  WHERE TOC_CODIGO = '124';
