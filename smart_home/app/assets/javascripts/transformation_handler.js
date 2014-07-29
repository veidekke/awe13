led_transformations = { '1': "t100,230", '2': "t430,500",
                        '3': "t180,500", '3': "t180,500",
                        '4': "t375,230", '5': "t220,280" };

music_transformations = { '1': "t10, 130", '2': "t385, 415",
                          '3': "t10, 415", '4': "t260, 130" };

volume_transformations = {  '1': "t40,130", '2': "t415,415",
                            '3': "t40, 415", '4': "t290,130" };

heating_transformations = { '1': "r90 t100,80", '2': "t425,570",
                            '3': "t250,570", '4': "r90 t180,-450" };

var TransformationHandler = {
    getTransformation: function(actor){
      switch(actor.model.resource_type){
        case "led":
          return led_transformations[String(actor.model.room_id)];
        case "lock":
          return "t220,110";
        case "daytime":
          return "t450, 30 s2.5";
        case "music":
          return music_transformations[String(actor.model.room_id)];
        case "volume":
          return volume_transformations[String(actor.model.room_id)];
        case "heating":
          return heating_transformations[String(actor.model.room_id)];
      }
  }   
};