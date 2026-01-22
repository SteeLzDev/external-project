-- DESENV-21185
-- MYSQL
update tb_relacionamento_autorizacao rad
inner join tb_aut_desconto adeOrigem on (rad.ade_codigo_origem = adeOrigem.ade_codigo)
inner join tb_aut_desconto adeDestino on (rad.ade_codigo_destino = adeDestino.ade_codigo)
set adeDestino.ade_ano_mes_ini_ref = adeOrigem.ade_ano_mes_ini
where rad.tnt_codigo = '21';

