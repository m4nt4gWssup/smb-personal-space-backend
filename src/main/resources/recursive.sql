 WITH RECURSIVE r ( 
         lnk_source_id, 
         lnk_dest_id, 
         lnk_order_id, 
         item_id, 
         path 
     ) AS ( 
         SELECT  
             lt1.lnk_source_id, 
             lt1.lnk_dest_id, 
             cast(lt1.lnk_order_id as varchar), 
             lt1.lnk_source_id as item_id, 
             ( 
                 SELECT 
                     cast(itm_title as varchar) 
                 FROM 
                     item_type_tab 
                 WHERE 
                     itm_id = lt1.lnk_source_id 
              ) AS path 
         FROM 
             item_link_tab lt1 
         WHERE 
             ((:itemId > 0 
                 AND lt1.lnk_source_id = :itemId) OR 
             (lt1.lnk_source_id IN ( 
                        SELECT 
                                item_id  
                            FROM 
                                dis_trm_tab 
                            WHERE  
                                role_ids LIKE '%!' || :codeId || '!%') 
                 AND :itemId < 0)) AND lt1.lnk_link_type = 'son-of' 
         UNION ALL 
         SELECT 
             lt2.lnk_source_id, 
             lt2.lnk_dest_id, 
             lt2.lnk_order_id ||  ',' || r.lnk_order_id, 
             r.item_id, 
             ( 
                 SELECT 
                     itm_title 
                 FROM 
                     item_type_tab 
                 WHERE 
                     itm_id = lt2.lnk_source_id 
             ) || '%!%' || r.path 
         FROM 
                  item_link_tab lt2 
             INNER JOIN r ON lt2.lnk_source_id = r.lnk_dest_id 
         WHERE 
             lt2.lnk_link_type = 'son-of' 
     ) 
     SELECT 
         path AS itemPath, 
         item_id as itemId, 
         lnk_order_id AS itemOrder 
     FROM 
         r 
     WHERE 
         lnk_dest_id = 0 
     ORDER BY lnk_order_id 