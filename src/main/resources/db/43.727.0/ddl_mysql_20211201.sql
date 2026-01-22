/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     02/12/2021 11:15:39                          */
/*==============================================================*/


alter table tb_convenio
   add CNV_COD_VERBA_FOLHA varchar(40);

/*==============================================================*/
/* Index: IDX_COD_VERBA_FOLHA                                   */
/*==============================================================*/
create index IDX_COD_VERBA_FOLHA on tb_convenio
(
   CNV_COD_VERBA_FOLHA
);

