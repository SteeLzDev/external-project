-- DESENV-21185
-- MYSQL
-- ORACLE
update tb_aut_desconto adeDestino
set adeDestino.ade_ano_mes_ini_ref = (
  select adeOrigem.ade_ano_mes_ini
  from tb_aut_desconto adeOrigem
  inner join tb_relacionamento_autorizacao rad on (rad.ade_codigo_origem = adeOrigem.ade_codigo)
  where rad.tnt_codigo = '21'
    and rad.ade_codigo_destino = adeDestino.ade_codigo
)
where exists (
  select 1
  from tb_relacionamento_autorizacao rad
  where rad.tnt_codigo = '21'
    and rad.ade_codigo_destino = adeDestino.ade_codigo

);

