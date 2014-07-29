class Sensor < Resource
  attr_accessible :room_id

end

class MovementSensor < Sensor
  has_many :persons

  def possible_values
    ["0","1"]
  end
end

class DayTime < Sensor
  def possible_values
    ["0","1"]
  end
end