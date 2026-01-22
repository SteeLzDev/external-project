-- DESENV-9389

UPDATE tb_status_contrato_beneficio SET SCB_DESCRICAO = 'Solicitado' WHERE SCB_CODIGO = '1';

INSERT INTO tb_status_contrato_beneficio (SCB_CODIGO, SCB_DESCRICAO) VALUES ('1', 'Solicitado');
INSERT INTO tb_status_contrato_beneficio (SCB_CODIGO, SCB_DESCRICAO) VALUES ('2', 'Aguard. Inclusão Operadora');
INSERT INTO tb_status_contrato_beneficio (SCB_CODIGO, SCB_DESCRICAO) VALUES ('3', 'Ativo');
INSERT INTO tb_status_contrato_beneficio (SCB_CODIGO, SCB_DESCRICAO) VALUES ('4', 'Cancelamento Solicitado');
INSERT INTO tb_status_contrato_beneficio (SCB_CODIGO, SCB_DESCRICAO) VALUES ('5', 'Aguard. Exclusão Operadora');
INSERT INTO tb_status_contrato_beneficio (SCB_CODIGO, SCB_DESCRICAO) VALUES ('6', 'Cancelado');

update tb_contrato_beneficio cbe inner join tb_aut_desconto ade on cbe.CBE_CODIGO = ade.CBE_CODIGO inner join tb_tipo_lancamento tla on tla.TLA_CODIGO = ade.TLA_CODIGO inner join tb_tipo_natureza tnt on tnt.tnt_codigo = tla.TNT_CODIGO inner join tb_status_autorizacao_desconto sad on sad.sad_codigo = ade.sad_codigo set cbe.SCB_CODIGO = '3' where tnt.tnt_codigo in ('25','26') and sad.sad_codigo in ('4','5','11','15');
update tb_contrato_beneficio cbe inner join tb_aut_desconto ade on cbe.CBE_CODIGO = ade.CBE_CODIGO inner join tb_tipo_lancamento tla on tla.TLA_CODIGO = ade.TLA_CODIGO inner join tb_tipo_natureza tnt on tnt.tnt_codigo = tla.TNT_CODIGO inner join tb_status_autorizacao_desconto sad on sad.sad_codigo = ade.sad_codigo set cbe.SCB_CODIGO = '6' where tnt.tnt_codigo in ('25','26') and sad.sad_codigo in ('7','8','9');
update tb_contrato_beneficio cbe inner join tb_aut_desconto ade on cbe.CBE_CODIGO = ade.CBE_CODIGO inner join tb_tipo_lancamento tla on tla.TLA_CODIGO = ade.TLA_CODIGO inner join tb_tipo_natureza tnt on tnt.tnt_codigo = tla.TNT_CODIGO inner join tb_status_autorizacao_desconto sad on sad.sad_codigo = ade.sad_codigo set cbe.SCB_CODIGO = '1' where tnt.tnt_codigo in ('25','26') and sad.sad_codigo = '1';
