/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     18/05/2023 13:48:17                          */
/*==============================================================*/


/*==============================================================*/
/* Index: arquivo_movimento_idx0                                */
/*==============================================================*/
create index arquivo_movimento_idx0 on tb_arquivo_movimento (
   ade_numero asc
);

/*==============================================================*/
/* Index: arquivo_movimento_idx1                                */
/*==============================================================*/
create index arquivo_movimento_idx1 on tb_arquivo_movimento (
   pex_periodo asc,
   rse_matricula asc,
   cnv_cod_verba asc
);

/*==============================================================*/
/* Index: arquivo_movimento_idx2                                */
/*==============================================================*/
create index arquivo_movimento_idx2 on tb_arquivo_movimento (
   pex_periodo asc,
   ser_cpf asc,
   cnv_cod_verba asc
);

