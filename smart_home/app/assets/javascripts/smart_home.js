var move, start, up, reprint_actors;
var path_light, attr_geraete;
var lock_open_path, lock_closed_path;
var music_path, volume_lvl_0_path, volume_lvl_1_path, volume_lvl_2_path, volume_lvl_3_path;
var heating_path, ambient_temperature_path, sun_path;
var hallway_path;
var rsr;
var resources;

jQuery(function() {
  var aktoren, attr_zimmer, background, current, i, id;
  var sensoren, state, zimmer, _fn, _results;
  
  husband_path = "M21.021,16.349c-0.611-1.104-1.359-1.998-2.109-2.623c-0.875,0.641-1.941,1.031-3.103,1.031c-1.164,0-2.231-0.391-3.105-1.031c-0.75,0.625-1.498,1.519-2.111,2.623c-1.422,2.563-1.578,5.192-0.35,5.874c0.55,0.307,1.127,0.078,1.723-0.496c-0.105,0.582-0.166,1.213-0.166,1.873c0,2.932,1.139,5.307,2.543,5.307c0.846,0,1.265-0.865,1.466-2.189c0.201,1.324,0.62,2.189,1.463,2.189c1.406,0,2.545-2.375,2.545-5.307c0-0.66-0.061-1.291-0.168-1.873c0.598,0.574,1.174,0.803,1.725,0.496C22.602,21.541,22.443,18.912,21.021,16.349zM15.808,13.757c2.362,0,4.278-1.916,4.278-4.279s-1.916-4.279-4.278-4.279c-2.363,0-4.28,1.916-4.28,4.279S13.445,13.757,15.808,13.757z";
  wife_path = "M21.022,16.349c-0.611-1.104-1.359-1.998-2.109-2.623c-0.875,0.641-1.941,1.031-3.104,1.031c-1.164,0-2.231-0.391-3.105-1.031c-0.75,0.625-1.498,1.519-2.111,2.623c-1.422,2.563-1.579,5.192-0.351,5.874c0.55,0.307,1.127,0.078,1.723-0.496c-0.105,0.582-0.167,1.213-0.167,1.873c0,2.932,1.139,5.307,2.543,5.307c0.846,0,1.265-0.865,1.466-2.189c0.201,1.324,0.62,2.189,1.464,2.189c1.406,0,2.545-2.375,2.545-5.307c0-0.66-0.061-1.291-0.168-1.873c0.598,0.574,1.174,0.803,1.725,0.496C22.603,21.541,22.444,18.912,21.022,16.349zM15.808,13.757c2.363,0,4.279-1.916,4.279-4.279s-1.916-4.279-4.279-4.279c-2.363,0-4.28,1.916-4.28,4.279S13.445,13.757,15.808,13.757zM18.731,4.974c1.235,0.455,0.492-0.725,0.492-1.531s0.762-1.792-0.492-1.391c-1.316,0.422-2.383,0.654-2.383,1.461S17.415,4.489,18.731,4.974zM15.816,4.4c0.782,0,0.345-0.396,0.345-0.884c0-0.488,0.438-0.883-0.345-0.883s-0.374,0.396-0.374,0.883C15.442,4.005,15.034,4.4,15.816,4.4zM12.884,4.974c1.316-0.484,2.383-0.654,2.383-1.461S14.2,2.474,12.884,2.052c-1.254-0.402-0.492,0.584-0.492,1.391S11.648,5.428,12.884,4.974z";
  hallway_path = "M200,0 L200,225 L0,225 L0,275 L500,275 L500,225 L250,225 L250,0 L200,0";
  ambient_temperature_path = "M17.5,19.508V8.626h-3.999v10.881c-1.404,0.727-2.375,2.178-2.375,3.869c0,2.416,1.959,4.375,4.375,4.375s4.375-1.959,4.375-4.375C19.876,21.686,18.905,20.234,17.5,19.508zM20.5,5.249c0-2.757-2.244-5-5.001-5s-4.998,2.244-4.998,5v12.726c-1.497,1.373-2.376,3.314-2.376,5.4c0,4.066,3.31,7.377,7.376,7.377s7.374-3.311,7.374-7.377c0-2.086-0.878-4.029-2.375-5.402V5.249zM20.875,23.377c0,2.963-2.41,5.373-5.375,5.373c-2.962,0-5.373-2.41-5.373-5.373c0-1.795,0.896-3.443,2.376-4.438V5.251c0-1.654,1.343-3,2.997-3s3,1.345,3,3v13.688C19.979,19.934,20.875,21.582,20.875,23.377zM22.084,8.626l4.5,2.598V6.029L22.084,8.626z";
  sun_path = "M15.502,7.504c-4.35,0-7.873,3.523-7.873,7.873c0,4.347,3.523,7.872,7.873,7.872c4.346,0,7.871-3.525,7.871-7.872C23.374,11.027,19.85,7.504,15.502,7.504zM15.502,21.25c-3.244-0.008-5.866-2.63-5.874-5.872c0.007-3.243,2.63-5.866,5.874-5.874c3.242,0.008,5.864,2.631,5.871,5.874C21.366,18.62,18.744,21.242,15.502,21.25zM15.502,6.977c0.553,0,1-0.448,1-1.001V1.125c-0.002-0.553-0.448-1-1-1c-0.553,0-1.001,0.449-1,1.002v4.85C14.502,6.528,14.949,6.977,15.502,6.977zM18.715,7.615c0.125,0.053,0.255,0.076,0.382,0.077c0.394,0,0.765-0.233,0.925-0.618l1.856-4.483c0.21-0.511-0.031-1.095-0.541-1.306c-0.511-0.211-1.096,0.031-1.308,0.541L18.174,6.31C17.963,6.82,18.205,7.405,18.715,7.615zM21.44,9.436c0.195,0.194,0.451,0.293,0.707,0.293s0.512-0.098,0.707-0.293l3.43-3.433c0.391-0.39,0.39-1.023,0-1.415c-0.392-0.39-1.025-0.39-1.415,0.002L21.44,8.021C21.049,8.412,21.049,9.045,21.44,9.436zM23.263,12.16c0.158,0.385,0.531,0.617,0.923,0.617c0.127,0,0.257-0.025,0.383-0.078l4.48-1.857c0.511-0.211,0.753-0.797,0.541-1.307s-0.796-0.752-1.307-0.54l-4.481,1.857C23.292,11.064,23.051,11.65,23.263,12.16zM29.752,14.371l-4.851,0.001c-0.552,0-1,0.448-0.998,1.001c0,0.553,0.447,0.999,0.998,0.999l4.852-0.002c0.553,0,0.999-0.449,0.999-1C30.752,14.817,30.304,14.369,29.752,14.371zM29.054,19.899l-4.482-1.854c-0.512-0.212-1.097,0.03-1.307,0.541c-0.211,0.511,0.031,1.096,0.541,1.308l4.482,1.854c0.126,0.051,0.256,0.075,0.383,0.075c0.393,0,0.765-0.232,0.925-0.617C29.806,20.695,29.563,20.109,29.054,19.899zM22.86,21.312c-0.391-0.391-1.023-0.391-1.414,0.001c-0.391,0.39-0.39,1.022,0,1.413l3.434,3.429c0.195,0.195,0.45,0.293,0.706,0.293s0.513-0.098,0.708-0.293c0.391-0.392,0.389-1.025,0-1.415L22.86,21.312zM20.029,23.675c-0.211-0.511-0.796-0.752-1.307-0.541c-0.51,0.212-0.752,0.797-0.54,1.308l1.86,4.48c0.159,0.385,0.531,0.617,0.925,0.617c0.128,0,0.258-0.024,0.383-0.076c0.511-0.211,0.752-0.797,0.54-1.309L20.029,23.675zM15.512,23.778c-0.553,0-1,0.448-1,1l0.004,4.851c0,0.553,0.449,0.999,1,0.999c0.553,0,1-0.448,0.998-1l-0.003-4.852C16.511,24.226,16.062,23.777,15.512,23.778zM12.296,23.142c-0.51-0.21-1.094,0.031-1.306,0.543l-1.852,4.483c-0.21,0.511,0.033,1.096,0.543,1.307c0.125,0.052,0.254,0.076,0.382,0.076c0.392,0,0.765-0.234,0.924-0.619l1.853-4.485C13.051,23.937,12.807,23.353,12.296,23.142zM9.57,21.325c-0.392-0.391-1.025-0.389-1.415,0.002L4.729,24.76c-0.391,0.392-0.389,1.023,0.002,1.415c0.195,0.194,0.45,0.292,0.706,0.292c0.257,0,0.513-0.098,0.708-0.293l3.427-3.434C9.961,22.349,9.961,21.716,9.57,21.325zM7.746,18.604c-0.213-0.509-0.797-0.751-1.307-0.54L1.96,19.925c-0.511,0.212-0.752,0.798-0.54,1.308c0.16,0.385,0.531,0.616,0.924,0.616c0.127,0,0.258-0.024,0.383-0.076l4.479-1.861C7.715,19.698,7.957,19.113,7.746,18.604zM7.1,15.392c0-0.553-0.447-0.999-1-0.999l-4.851,0.006c-0.553,0-1.001,0.448-0.999,1.001c0.001,0.551,0.449,1,1,0.998l4.852-0.006C6.654,16.392,7.102,15.942,7.1,15.392zM1.944,10.869l4.485,1.85c0.125,0.053,0.254,0.076,0.381,0.076c0.393,0,0.766-0.232,0.925-0.618c0.212-0.511-0.032-1.097-0.544-1.306L2.708,9.021c-0.511-0.21-1.095,0.032-1.306,0.542C1.19,10.074,1.435,10.657,1.944,10.869zM8.137,9.451c0.195,0.193,0.449,0.291,0.705,0.291s0.513-0.098,0.709-0.295c0.391-0.389,0.389-1.023-0.004-1.414L6.113,4.609C5.723,4.219,5.088,4.221,4.699,4.612c-0.391,0.39-0.389,1.024,0.002,1.414L8.137,9.451zM10.964,7.084c0.16,0.384,0.532,0.615,0.923,0.615c0.128,0,0.258-0.025,0.384-0.077c0.51-0.212,0.753-0.798,0.54-1.307l-1.864-4.479c-0.212-0.51-0.798-0.751-1.308-0.539C9.129,1.51,8.888,2.096,9.1,2.605L10.964,7.084z";
  moon_path = "M19.632 19.632c-4.539 4.538-11.414 5.309-16.752 2.32 0.616 1.098 1.386 2.131 2.32 3.066 5.472 5.472 14.344 5.472 19.816 0 5.472-5.474 5.472-14.346 0-19.818-0.934-0.934-1.966-1.704-3.066-2.32 2.99 5.338 2.218 12.213-2.318 16.752z";
  
  rsr = Raphael(0, 100, "570", "630");
  resources = {};

  attr_geraete = {
    fill: "white",
    stroke: "#666"
  };
  
  attr_zimmer = {
    fill: "white",
    stroke: "#666",
    "stroke-width": 1,
    "stroke-linejoin": "round"
  };
  
  background = rsr.rect(0, 0, 570, 630).attr({
    fill: "#aaa"
  });

  //kueche
  rooms[0].path = rsr.rect(10, 120, 200, 225).attr(attr_zimmer);
  rsr.text(120,140, rooms[0].name);
  //bad
  rooms[1].path = rsr.rect(385, 395, 125, 225).attr(attr_zimmer);
  rsr.text(450,415, rooms[1].name);
  //wohnzimmer
  rooms[2].path = rsr.rect(10, 395, 375, 225).attr(attr_zimmer);
  rsr.text(200,415, rooms[2].name);
  //schlafzimmer
  rooms[3].path = rsr.rect(260, 120, 250, 225).attr(attr_zimmer);
  rsr.text(385,140, rooms[3].name);
  //flur
  rooms[4].path = rsr.path(Raphael.transformPath(hallway_path,"t10,120")).attr(attr_zimmer);
  rsr.text(235,160, rooms[4].name);

  init_resources();
  //aktoren.temperature_draussen = rsr.path(Raphael.transformPath(ambient_temperature_path,"t375, 30 s2")).attr(attr_geraete);

  people[0].path = rsr.path(Raphael.transformPath(husband_path,"t30, 30 s2.5")).attr(attr_geraete);
  people[1].path = rsr.path(Raphael.transformPath(wife_path,"t80, 30 s2.5")).attr(attr_geraete);
  people[2].path = rsr.path(Raphael.transformPath(husband_path,"t130, 42 s1.5")).attr(attr_geraete);
  

  rsr.set(people[0].path).drag(move, drag_husband, up_husband);
  rsr.set(people[1].path).drag(move, drag_wife, up_wife);
  rsr.set(people[2].path).drag(move, drag_kid, up_kid);
  
});

init_resources = function(){
  init_actors();
  init_sensors();
}

init_actors = function(){
  actors_tmp = actors;
  actors = new Array();
  var i = 0;
  while (i < actors_tmp.length){
    model = actors_tmp[i];
    actors[i] = new Resource(model, "actor", rsr);
    set_properties(actors[i], model);
    actors[i].print();
    i++;
  }
}

init_sensors = function(){
  sensors_tmp = sensors;
  sensors = new Array();
  var i = 0;
  while (i < sensors_tmp.length){
    model = sensors_tmp[i]
    sensors[i] = new Resource(model, "sensor", rsr)
    set_properties(sensors[i], model);
    sensors[i].print();
    i++;
  }
}

show_updated_rules = function(updated_rules){
  if(updated_rules.length > 0){
    var alert_msg = "Updated Rules: \n";
    for (var i = 0, j = updated_rules.length; i < j; i++)
    {
      alert_msg += updated_rules[i].name + "is now: " + updated_rules[i].status + "\n";
      $("#rule-badge_" + updated_rules[i].id).html("" + updated_rules[i].status)
    }
    alert(alert_msg);

    //$.get('/smart_home/rule_list');
  }
}

set_properties = function(resource, model){
  switch(resource.model.resource_type){
    case "music":
      resource.possible_values = [0,1,2,3];
      break;
    case "heating":
      resource.possible_values = [0,1,2];
      break;
    default:
      resource.possible_values = [0,1];
      break;
  }
  resource.current_value = model.value;
  resource.possible_attributes = AttributeHandler.getAttribute(resource);
}

drag_function = function(object, person){
  object.odx = 0;
  object.animate({
    fill: "grey"
    }, 500);
  if(person.room_id) 
    paint_room("white", person.room_id);  
  return object.ody = 0;
}

drag_husband = function() {
  return drag_function(this, people[0]);
};

drag_wife = function() {
  return drag_function(this, people[1]);
};

drag_kid = function() {
  return drag_function(this, people[2]);
};

move = function(dx, dy) {
  var trans;
  trans = void 0;
  this.translate(dx - this.odx, dy - this.ody);
  this.odx = dx;
  return this.ody = dy;
};

drop_function = function(object, person){
  object.animate({
    fill: "white"
    }, 500);
  var room = get_room(person);
  if (room.id != 6){
    paint_room("green", room.id);
  }
  send_room_id(person, room);
}

up_husband = function() {
  drop_function(this, people[0]);
};

up_kid = function() {
  drop_function(this, people[2]);
};

up_wife = function() {
  drop_function(this, people[1]);
};


reprint_sensors = function(updated_sensors) {
  var i = 0;
  while (i < updated_sensors.length) {
    sensors[i].current_value = updated_sensors[i].value;
    sensors[i].reprint();
    i++;
  }
}

reprint_actors = function(updated_actors) {
  var i = 0;
  while (i < updated_actors.length) {
    actor = get_actor_for_reprinting(updated_actors[i]);
    actor.current_value = updated_actors[i].value;
    actor.model.last_user = updated_actors[i].last_user;
    actor.reprint();
    i++;
  }
}

get_actor_for_reprinting = function(updated_actor){
  for (var i = 0, j = actors.length; i < j; i++)
  {
    if(actors[i].model.id == updated_actor.id){
      return actors[i];
    }
  }
}

send_room_id = function(person, room){
    $.post( '/smart_home/set_room_id',
            {id: person.id, room_id: room.id},
            function(data) {
              person.room_id = data.room_id;
              reprint_actors(data.updated_actors);
              show_updated_rules(data.updated_rules);
            },
            "json"
          );
};

get_room = function(person) {
  //TODO schleife bauen
  if (in_room(rooms[0].path, person)){
    return rooms[0];
  }
  else if (in_room(rooms[1].path, person)){
    return rooms[1];
  }
  else if (in_room(rooms[2].path, person)){
    return rooms[2];
  }
  else if (in_room(rooms[3].path, person)){
    return rooms[3];
  }
  else if (!in_flat(person)){
    return rooms[5];
  }
  
  return rooms[4];
};

get_center_of_bbox = function(bbox){
  var center = {};
  center.x = bbox.x + (bbox.width/2);
  center.y = bbox.y + (bbox.height/2);

  return center;
};

in_room = function(room, person) {
  var center_of_person = get_center_of_bbox(person.path.getBBox());
  var bbox_room = room.getBBox();
  if( bbox_room.x < center_of_person.x &&
      center_of_person.x < bbox_room.x2 &&
      bbox_room.y < center_of_person.y &&
      center_of_person.y < bbox_room.y2){

    return true
  } else {

    return false
  }
};

in_flat = function(person) {
  var center_of_person = get_center_of_bbox(person.path.getBBox());
  var kitchen = rooms[0].path.getBBox();
  var bathroom = rooms[1].path.getBBox();

  if(kitchen.x < center_of_person.x &&
      center_of_person.x < bathroom.x2 &&
      kitchen.y < center_of_person.y &&
      center_of_person.y < bathroom.y2){

    return true;
  } else {

    return false;
  }
};

get_room_by_id = function(room_id){
  var i = 0
  while (i < rooms.length){
    if (rooms[i].id == room_id){
      return rooms[i];
    }
    i++;
  }
};

paint_room = function(color, room_id){
  room = get_room_by_id (room_id);
  if (room.id != 6){
    room.path.animate({fill: color}, 500);
  } 
};