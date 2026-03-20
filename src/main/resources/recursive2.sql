"with recursive r (     " +
"          path,   " +
"          parent_id,   " +
"          folder_id , " +
"          folder_order " +
"      ) as ( " +
"select " +
"	array[cast(psf1.folder_name as varchar)], " +
"	psf1.parent_id, " +
"	array[psf1.folder_id], " +
"	array[psf1.folder_order] " +
"from " +
"	dis_personal_space_folders psf1 " +
"where " +
"	psf1.profile_id = :profileId " +
"union all " +
"select " +
"	array[psf2.folder_name ]|| r.path, " +
"	psf2.parent_id, " +
"	array[psf2.folder_id] || r.folder_id, " +
"	array[psf2.folder_order] || r.folder_order " +
"from " +
"	dis_personal_space_folders psf2 " +
"inner join r on " +
"	psf2.folder_id = r.parent_id    " +
"      )     " +
"      select " +
"	 	array_to_string(path,'%!%') as folderNames, " + 
"	 	array_to_string(folder_id,'%!%') as folderIds " +
"from " +
"	r " +
"where " +
"	parent_id is null " +
"	and not exists ( " +
"	select " +
"		* " +
"	from " +
"		dis_personal_space_folders psf3 " +
"	where " +
"		psf3.parent_id = r.folder_id[array_upper(r.folder_id, 1)]) " +
"order by " +
"	r.folder_order[1]" 