/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     18/05/2023 13:39:40                          */
/*==============================================================*/


/*==============================================================*/
/* Index: ARQUIVO_MOVIMENTO_IDX0                                */
/*==============================================================*/
create index ARQUIVO_MOVIMENTO_IDX0 on tb_arquivo_movimento
(
   ADE_NUMERO
);

/*==============================================================*/
/* Index: ARQUIVO_MOVIMENTO_IDX1                                */
/*==============================================================*/
create index ARQUIVO_MOVIMENTO_IDX1 on tb_arquivo_movimento
(
   PEX_PERIODO,
   RSE_MATRICULA,
   CNV_COD_VERBA
);

/*==============================================================*/
/* Index: ARQUIVO_MOVIMENTO_IDX2                                */
/*==============================================================*/
create index ARQUIVO_MOVIMENTO_IDX2 on tb_arquivo_movimento
(
   PEX_PERIODO,
   SER_CPF,
   CNV_COD_VERBA
);

