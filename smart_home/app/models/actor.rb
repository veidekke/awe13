class Actor < Resource
  has_many :activities
end

class Led < Actor
  def possible_values
    ["0","1"]
  end
end

class Heating < Actor
  def possible_values
    ["0","1","2"]
  end
end

class Music < Actor
  def possible_values
    ["0","1","2","3"]
  end
end

class Lock < Actor
  def possible_values
    ["0","1"]
  end
end
