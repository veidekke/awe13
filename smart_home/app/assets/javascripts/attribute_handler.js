led_attributes = {
                    '0': { fill: "white", stroke: "#666" },
                    '1': { fill: "yellow", stroke: "#666" }
                  };

daytime_attributes = {
                        '0': { fill: "yellow", stroke: "none" },
                        '1': {fill: "blue", stroke: "none"}
                      };

heating_attributes = {
                        '0': { fill: "#aaa", stroke: "none" },
                        '1': { fill: "orange", stroke: "none" },
                        '2': { fill: "red", stroke: "none" }
                      };
default_attribute = { fill: "white", stroke: "#666" };

default_attributes = {
                        '0': default_attribute,
                        '1': default_attribute,
                        '2': default_attribute,
                        '3': default_attribute
                      };

var AttributeHandler = {
    getAttribute: function(actor){
    
    switch(actor.model.resource_type){
      case "led":
        return led_attributes;
      case "daytime":
        return daytime_attributes;
      case "heating":
        return heating_attributes;
      default:
        return default_attributes;
    }
  }   
};
