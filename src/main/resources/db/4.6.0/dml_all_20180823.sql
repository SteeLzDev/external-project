-- DESENV-9393

INSERT INTO tb_nacionalidade (NAC_CODIGO, NAC_DESCRICAO) VALUES ('1', 'Brasileiro');
INSERT INTO tb_nacionalidade (NAC_CODIGO, NAC_DESCRICAO) VALUES ('2', 'Estrangeiro');
UPDATE tb_beneficiario SET NAC_CODIGO = '1';
