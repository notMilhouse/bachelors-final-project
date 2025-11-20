classDiagram
direction BT
class profile {
   varchar(100) name
   varchar(255) email
   varchar(255) password
   uuid id
}
class profile_embedding {
   uuid profile_id
   vector(128) embedding
   uuid id
}
class profile_measurement {
   uuid profile_id
   numeric(5,2) weight_value
   timestamp with time zone measured_at
   timestamp with time zone created_at
   timestamp with time zone recorded_at
   uuid id
}

profile_embedding  -->  profile : profile_id:id
profile_measurement  -->  profile : profile_id:id
