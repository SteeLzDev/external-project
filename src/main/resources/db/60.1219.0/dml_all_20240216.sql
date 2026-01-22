-- DESENV-20989
INSERT INTO tb_tipo_dado_adicional (TDA_CODIGO, TEN_CODIGO, TDA_DESCRICAO, TDA_EXPORTA, TDA_SUP_CONSULTA, TDA_CSE_CONSULTA, TDA_CSA_CONSULTA, TDA_SER_CONSULTA, TDA_SUP_ALTERA, TDA_CSE_ALTERA, TDA_CSA_ALTERA, TDA_SER_ALTERA, TDA_DOMINIO, tda_ordenacao)
VALUES ('90', '19', 'Taxa', 'N', 'S', 'S', 'S', 'S', 'S', 'N', 'S', 'N', 'MONETARIO', 101);

INSERT INTO tb_tipo_dado_adicional (TDA_CODIGO, TEN_CODIGO, TDA_DESCRICAO, TDA_EXPORTA, TDA_SUP_CONSULTA, TDA_CSE_CONSULTA, TDA_CSA_CONSULTA, TDA_SER_CONSULTA, TDA_SUP_ALTERA, TDA_CSE_ALTERA, TDA_CSA_ALTERA, TDA_SER_ALTERA, TDA_DOMINIO, tda_ordenacao)
VALUES ('91', '19', 'Valor creditado', 'N', 'S', 'S', 'S', 'S', 'S', 'N', 'S', 'N', 'MONETARIO', 100);

UPDATE tb_dados_autorizacao_desconto SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_dados_autorizacao_desconto SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE ht_ocorrencia_dados_ade SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE ht_ocorrencia_dados_ade SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_consignataria_permite_tda SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_consignataria_permite_tda SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_dados_consignante SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_dados_consignante SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_dados_consignataria SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_dados_consignataria SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_dados_correspondente SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_dados_correspondente SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_dados_estabelecimento SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_dados_estabelecimento SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_dados_orgao SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_dados_orgao SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_dados_servidor SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_dados_servidor SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_funcao_editavel_tda SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_funcao_editavel_tda SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_ocorrencia_dados_ade SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_ocorrencia_dados_ade SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_ocorrencia_dados_servidor SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_ocorrencia_dados_servidor SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

UPDATE tb_servico_permite_tda SET TDA_CODIGO = '90' WHERE TDA_CODIGO = 'salarypay_taxa';
UPDATE tb_servico_permite_tda SET TDA_CODIGO = '91' WHERE TDA_CODIGO = 'salarypay_valor_creditado';

DELETE FROM tb_tipo_dado_adicional WHERE TDA_CODIGO = 'salarypay_taxa';
DELETE FROM tb_tipo_dado_adicional WHERE TDA_CODIGO = 'salarypay_valor_creditado';

