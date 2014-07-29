function Resource(model, category, rsr)
{
  this.category = category;
  this.model = model;
  this.rsr = rsr;
  this.path;
  this.raphael_object;
  this.current_value;
  this.possible_values;
  this.possible_attributes;
  this.current_attribute;
  this.bbox;
  this.info_window;

  this.print = function(){
    transformation = TransformationHandler.getTransformation(this);
    this.path = PathHandler.getPath(this);
    this.raphael_object = this.rsr.path(Raphael.transformPath(this.path, transformation));
    update_attribute(this);
    this.raphael_object.attr(this.current_attribute);
    this.raphael_object.click(getClickHandler(this));
    update_bbox(this);
  }

  this.reprint = function(){
    this.bbox.remove();
    this.raphael_object.remove();
    this.print();
  }

  function getClickHandler(resource) {
    return function() {
        var new_value = get_new_value(resource)
        $.post(get_url(resource),
          {id: resource.model.id, value: new_value},
          get_callback_function(resource),
          "json"
        );
      };
  }

  function get_callback_function(resource){
    if(resource.category == "actor"){
      return function(data){
        reprint_actors(data.updated_actors);
        show_updated_rules(data.updated_rules);
      };
    }else{
      return function(data){
        reprint_sensors(data.updated_sensors);
        reprint_actors(data.updated_actors);
        show_updated_rules(data.updated_rules);
      }; 
    }
  }

  function get_url(resource){
    if(resource.category == "actor"){
      return '/smart_home/change_actor';
    } else{
      return '/smart_home/change_sensor';
    }  
  }

  function get_new_value(resource){
    for (var i = 0, j = resource.possible_values.length; i < j; i++)
    {
      if(resource.possible_values[i] == resource.current_value){
        index_of_new_value = (i < resource.possible_values.length) ? i+1 : 0;
        return resource.possible_values[index_of_new_value];
      }
    }
  }

  function update_attribute(resource){
    resource.current_attribute = resource.possible_attributes[String(resource.current_value)];
  }

  function update_bbox(resource){
    resource.bbox = get_bbox(resource.raphael_object);
    resource.bbox.attr({fill: "white", "fill-opacity": "0", "opacity": "0"});
    resource.bbox.click(getClickHandler(resource));
    resource.raphael_object.toFront();
    set_hover(resource);
  }

  function get_bbox(path){
    var bbox = path.getBBox(false);
    var top_left_corner = "" + String(Math.ceil(bbox.x)) + " " + String(Math.ceil(bbox.y))
    var top_right_corner = "" + String(Math.ceil(bbox.x + bbox.width)) + " " + String(Math.ceil(bbox.y))
    var bottom_left_corner = "" + String(Math.ceil(bbox.x)) + " " + String(Math.ceil(bbox.y + bbox.height))
    var bottom_right_corner = "" + String(Math.ceil(bbox.x2)) + " " + String(Math.ceil(bbox.y2))
    return this.rsr.path(  "M"+ top_left_corner + 
                      "L"+ top_right_corner +
                      "L" + bottom_right_corner +
                      "L"+ bottom_left_corner + 
                      "L" + top_left_corner
                    );
  }

  function set_hover(resource){
    resource.raphael_object.hover( get_mouse_in_function(resource),
      get_mouse_out_function(resource)
    );
    resource.bbox.hover(get_mouse_in_function(resource),
      get_mouse_out_function(resource)
    );
  }

  function get_mouse_in_function(resource){
    return function(){
      resource.info_window = InfoWindow.print(resource);
      hover_attr = {};
      $.extend(hover_attr,resource.current_attribute);
      hover_attr["stroke"] = "#000";
      resource.raphael_object.attr(hover_attr);
    };
  }

  function get_mouse_out_function(resource){
    return function () {
        resource.info_window["text"].remove();
        resource.info_window["info_window"].remove();
        update_attribute(resource);
        resource.raphael_object.attr(resource.current_attribute);
    };
  }

}