var InfoWindow = {
    print: function(actor){
      var info_window = actor.rsr.rect((10-actor.model.last_user.length *2), 20, (100+actor.model.last_user.length*4), 60);
      info_window.attr({fill: "white"})
      var text = actor.rsr.text(50,50, "Type: "+ actor.model.resource_type + "\n" +
                                                  "Value: " + actor.current_value + "\n" + 
                                                  "Last User: " + actor.model.last_user);
      var center_of_actor = get_center_of_bbox(actor.raphael_object.getBBox());
      var center_of_info_window = get_center_of_bbox(info_window.getBBox());
      
      var x_difference = center_of_actor.x - center_of_info_window.x;
      var y_difference = center_of_actor.y - center_of_info_window.y;

      var window_shift_x = 0;
      var text_shift_x = 0;
      if(center_of_actor.x > 200){
        window_shift_x = -80;
        text_shift_x = -68;
      } else {
        window_shift_x = 80;
        text_shift_x = 90;
      }
      
      info_window.transform("t" + (x_difference+window_shift_x) + "," + y_difference);
      text.transform("t" + (x_difference+text_shift_x) + "," + y_difference);
      // translate 100, 100, rotate 45°, translate -100, 0
      //el.transform("t100,100r45t-100,0");

      return { "info_window" : info_window, "text" : text };
  }
};