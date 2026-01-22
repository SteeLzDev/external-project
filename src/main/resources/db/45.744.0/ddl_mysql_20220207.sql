/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/02/2022 10:37:35                          */
/*==============================================================*/


alter table ht_registro_servidor
   add RSE_MEDIA_MARGEM decimal(13,2);

alter table ht_registro_servidor
   add RSE_MEDIA_MARGEM_2 decimal(13,2);

alter table ht_registro_servidor
   add RSE_MEDIA_MARGEM_3 decimal(13,2);

alter table tb_margem_registro_servidor
   add MRS_MEDIA_MARGEM decimal(13,2);

alter table tb_registro_servidor
   add RSE_MEDIA_MARGEM decimal(13,2);

alter table tb_registro_servidor
   add RSE_MEDIA_MARGEM_2 decimal(13,2);

alter table tb_registro_servidor
   add RSE_MEDIA_MARGEM_3 decimal(13,2);

